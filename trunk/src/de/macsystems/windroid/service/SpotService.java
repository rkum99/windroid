/**
 This file is part of Windroid.

 Windroid is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Windroid is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Windroid.  If not, see <http://www.gnu.org/licenses/>.

 */
package de.macsystems.windroid.service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.R;
import de.macsystems.windroid.alarm.Alert;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.concurrent.WindroidThreadFactory;
import de.macsystems.windroid.io.task.AlarmTask;
import de.macsystems.windroid.io.task.EnqueueActiveSpots;
import de.macsystems.windroid.io.task.UpdateAlarmTask;
import de.macsystems.windroid.io.task.UpdateAllActiveSpotReports;
import de.macsystems.windroid.io.task.UpdateSpotForecastTask;

/**
 * The background service which inits all tasks thru the AIDL interface. The
 * Service manages a ThreadPool. Each containing thread can be priorized using a
 * comparator see {@link PriorizedFutureTaskComparator}. Its possible to receive
 * a callback from this service if task finished, see
 * {@link IServiceCallbackListener}.
 *
 * @author Jens Hohl
 * @version $Id$
 *
 */
public class SpotService extends Service
{

	private final static String LOG_TAG = SpotService.class.getSimpleName();

	/**
	 * The Threadpool/Queue used to schedule all kind of tasks.
	 */
	private ExecutorService taskQueue;

	private final ISpotService serviceBinder = new ISpotService.Stub()
	{
		@Override
		public void initAlarms() throws RemoteException
		{
			addTask(new SpotAlarmTask(SpotService.this));
		}

		/**
		 * Call method if you want to update all activ spots
		 */
		@Override
		public void updateAll() throws RemoteException
		{
			addTask(new UpdateAlarmTask(SpotService.this));
		}

		/**
		 * Call Method if you want to update a Spot only once.
		 */
		@Override
		public void update(final int _selectedID, final IServiceCallbackListener _listener)
		{
			if (_listener == null)
			{
				throw new NullPointerException("IServiceCallbackListener is null");
			}

			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "Insert Task to update spot with selectedID:" + _selectedID + " into scheduler");
			}

			final UpdateSpotForecastTask task = new UpdateSpotForecastTask(_selectedID, SpotService.this);
			addTask(task, _listener);
		}

		@Override
		public void updateActiveReports(final IServiceCallbackListener _listener) throws RemoteException
		{
			if (_listener == null)
			{
				throw new NullPointerException("IServiceCallbackListener is null");
			}
			final UpdateAllActiveSpotReports task = new UpdateAllActiveSpotReports(SpotService.this);
			addTask(task, _listener);
		}

	};

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(final Intent intent)
	{
		return serviceBinder.asBinder();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
		createTaskQueue();
		if (Logging.isEnabled)
		{
			Log.i(LOG_TAG, "Service created");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	// TODO Deprecated from Android >= 2.0
	public void onStart(final Intent _intent, final int _startId)
	{
		super.onStart(_intent, _startId);
		createTaskQueue();
		if (Logging.isEnabled)
		{
			Log.i(LOG_TAG, "onStart");
		}
		/**
		 * Check if we invoked because of an Reboot or Update (market or
		 * install)
		 */
		if (SpotService.isRestartActiveSpotsIntent(_intent))
		{
			addTask(new EnqueueActiveSpots(this));
		}
		else if (Alert.isAlertIntent(_intent))
		{
			final Alert alert = Alert.read(_intent);
			createAlarmTask(alert);
		}
		else
		{
			Log.w(LOG_TAG, "Service started without any Alert infos!");
		}
	}

	/**
	 * Creates an Task for Alarm.
	 *
	 * @param _repeatID
	 */
	private final void createAlarmTask(final Alert _alert)
	{
		// adding Task which updates this SPOT on Alarm
		addTask(new AlarmTask(this, _alert));
	}

	/**
	 * Checks if Queue already created, if not it creates it
	 */
	private synchronized void createTaskQueue()
	{
		if (taskQueue == null)
		{
			final int poolSize = getResources().getInteger(R.integer.schedule_threadpool_size);
			final int poolMaxSize = getResources().getInteger(R.integer.schedule_threadpool_max_size);
			// Seems items cannot represent float, double or long ?
			final long keepAlive = (long) getResources().getInteger(
					R.integer.schedule_threadpool_thread_alive_time_in_seconds);

			final BlockingQueue<? super Runnable> queue = new PriorityBlockingQueue<Runnable>(poolSize,
					new PriorizedFutureTaskComparator());
			//
			final WindroidThreadFactory factory = new WindroidThreadFactory("SpotService", Thread.NORM_PRIORITY);
			taskQueue = new ThreadPoolExecutor(poolSize, poolMaxSize, keepAlive, TimeUnit.SECONDS,
					(BlockingQueue<Runnable>) queue, factory);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		try
		{
			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "Service#onDestroy");
			}
			//
			if (taskQueue != null)
			{
				// First we try a 'soft' shutdown
				taskQueue.shutdown();
				// waiting for termination.
				final boolean isTerminated = taskQueue.awaitTermination(4L, TimeUnit.SECONDS);
				if (!isTerminated)
				{
					if (Logging.isEnabled)
					{
						Log.d(LOG_TAG, "soft shutdown failed, trying hard shutdown.");
					}
					// Do a hard termination.
					final List<Runnable> uncompletedTasks = taskQueue.shutdownNow();
					logUncompletedTask(uncompletedTasks);
				}
				if (Logging.isEnabled)
				{
					Log.d(LOG_TAG, "shutdown completed.");
				}
			}
		}
		catch (final SecurityException e)
		{
			Log.e(LOG_TAG, "shutdown failed", e);
		}
		catch (final InterruptedException e)
		{
			Log.e(LOG_TAG, "Failed to shutdown threadpool");
		}
		finally
		{
			super.onDestroy();
		}
	}

	/**
	 *
	 * @param _task
	 */
	private void addTask(final Callable<Void> _task)
	{
		try
		{
			final PriorizedFutureTask task = new PriorizedFutureTask(PRIORITY.NORMAL, _task);
			taskQueue.execute(task);
		}
		catch (final Exception e)
		{
			Log.e(LOG_TAG, "Failed to queue task", e);
		}
	}

	/**
	 *
	 * @param _task
	 */
	private void addTask(final Callable<Void> _task, final IServiceCallbackListener _listener)
	{
		try
		{
			final PriorizedFutureTask task = new PriorizedFutureTask(PRIORITY.NORMAL, _task, _listener);
			taskQueue.execute(task);
		}
		catch (final Exception e)
		{
			Log.e(LOG_TAG, "Failed to queue task", e);
		}
	}

	/**
	 * Lists all uncompleted task.<br>
	 * Method is more a debug method than very useful yet.
	 *
	 * @param tasks
	 */
	private void logUncompletedTask(final List<Runnable> tasks)
	{
		if (tasks == null)
		{
			return;
		}
		if (Logging.isEnabled)
		{
			for (final Runnable task : tasks)
			{
				Log.d(LOG_TAG, " Task not completed: " + task.toString());
			}
		}
	}

	/**
	 *
	 * @param _intent
	 * @return
	 */
	public static boolean isRestartActiveSpotsIntent(final Intent _intent)
	{
		if (_intent != null)
		{
			return _intent.getStringExtra(IntentConstants.ENQUEUE_ACTIV_SPOTS_AFTER_REBOOT_OR_UPDATE) != null;
		}
		return false;
	}
}