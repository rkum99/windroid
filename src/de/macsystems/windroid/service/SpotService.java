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
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.R;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.io.task.AlarmUpdateTask;
import de.macsystems.windroid.io.task.UpdateAlarmTask;
import de.macsystems.windroid.io.task.UpdateAllActiveSpotReports;
import de.macsystems.windroid.io.task.UpdateSpotForecastTask;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class SpotService extends Service
{

	private final static String LOG_TAG = SpotService.class.getSimpleName();

	protected static final int REPORT_MSG = 0;

	/**
	 * The Threadpool used to schedule all kind of tasks.
	 */
	private ExecutorService threadPool;

	/**
	 * This is a list of callbacks that have been registered with the service.
	 * Note that this is package scoped (instead of private) so that it can be
	 * accessed more efficiently from inner classes.
	 */
	final RemoteCallbackList<IServiceCallbackListener> mCallbacks = new RemoteCallbackList<IServiceCallbackListener>();

	private final ISpotService serviceBinder = new ISpotService.Stub()
	{
		@Override
		public void initAlarms() throws RemoteException
		{
			addTask(new SpotAlarmTask(SpotService.this));
		}

		@Override
		public void updateAll() throws RemoteException
		{
			addTask(new UpdateAlarmTask(SpotService.this));
		}

		@Override
		public void update(final int _selectedID, final IServiceCallbackListener _listener)
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Insert Task to update spot with selectedID:" + _selectedID + " into scheduler");
			}

			final UpdateSpotForecastTask task = new UpdateSpotForecastTask(_selectedID, SpotService.this);

			addTask(task, _listener);
		}

		@Override
		public void updateActiveReports(final IServiceCallbackListener listener) throws RemoteException
		{
			final UpdateAllActiveSpotReports task = new UpdateAllActiveSpotReports(SpotService.this);
			addTask(task, listener);
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
		createThreadPool();
		if (Logging.isLoggingEnabled())
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
		createThreadPool();
		if (Logging.isLoggingEnabled())
		{
			Log.i(LOG_TAG, "onStart");
		}
		final boolean found = isSelectedID(_intent);
		if (found)
		{
			final int selectedID = getSelectedID(_intent);
			createAlarmTask(selectedID);
		}
		else
		{
			if (Logging.isLoggingEnabled())
			{
				Log.w(LOG_TAG, "Service started without any selected id!");
			}
		}
	}

	/**
	 * Creates an Task when Alarm for a spot with given id is on.
	 * 
	 * @param _selectedID
	 */
	private final void createAlarmTask(final int _selectedID)
	{

		// adding Task which updates this SPOT on Alarm
		addTask(new AlarmUpdateTask(this, _selectedID));
	}

	/**
	 * Returns <code>true</code> if a id is present
	 * 
	 * @param _intent
	 * @return
	 * @see #getSelectedID(Intent)
	 */
	private final static boolean isSelectedID(final Intent _intent)
	{
		if (_intent == null)
		{
			return false;
		}
		return -1 != _intent.getIntExtra(IntentConstants.SELECTED_PRIMARY_KEY, -1);
	}

	/**
	 * Returns primary key of selected spot which needs to be updated.
	 * 
	 * @param _intent
	 * @return
	 * @see #isSelectedID(Intent)
	 */
	private final static int getSelectedID(final Intent _intent)
	{
		if (_intent == null)
		{
			throw new NullPointerException("Intent");
		}
		return _intent.getIntExtra(IntentConstants.SELECTED_PRIMARY_KEY, -1);
	}

	/**
	 * Checks if ThreadPool already created, if not it creates it
	 */
	private synchronized void createThreadPool()
	{
		if (threadPool == null)
		{
			final int poolSize = getResources().getInteger(R.integer.schedule_threadpool_size);
			final BlockingQueue<? super Runnable> queue = new PriorityBlockingQueue<Runnable>(poolSize,
					new PriorizedFutureTaskComparator());
			//
			threadPool = new ThreadPoolExecutor(1, 1, 1L, TimeUnit.SECONDS, (BlockingQueue<Runnable>) queue);
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
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Service#onDestroy");
			}
			//
			if (threadPool != null)
			{
				// First we try a 'soft' shutdown
				threadPool.shutdown();
				// waiting for termination.
				final boolean isTerminated = threadPool.awaitTermination(4L, TimeUnit.SECONDS);
				if (!isTerminated)
				{
					if (Logging.isLoggingEnabled())
					{
						Log.d(LOG_TAG, "soft shutdown failed, trying hard shutdown.");
					}
					// Do a hard termination.
					final List<Runnable> uncompletedTasks = threadPool.shutdownNow();
					logUncompletedTask(uncompletedTasks);
				}
				if (Logging.isLoggingEnabled())
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
			threadPool.execute(task);
		}
		catch (final Throwable e)
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
			threadPool.execute(task);
		}
		catch (final Throwable e)
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
		if (Logging.isLoggingEnabled())
		{
			for (final Runnable task : tasks)
			{
				Log.d(LOG_TAG, " Task not completed: " + task.toString());
			}
		}
	}
}