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

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.OngoingUpdateActivity;
import de.macsystems.windroid.R;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.concurrent.ThreadFactory;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.task.UpdateSpotsTask;
import de.macsystems.windroid.receiver.AlarmBroadcastReciever;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class SpotService extends Service
{

	private static final long INITIAL_DELAY = 100L;
	private final static String LOG_TAG = SpotService.class.getSimpleName();
	/**
	 * The Threadpool used to schedule all kind of tasks.
	 */
	private ScheduledThreadPoolExecutor threadPool;
	/**
	 * Thread save integer which can be used to count alarm id.
	 */
	private final AtomicInteger notificationCounter = new AtomicInteger(1);

	private final AtomicInteger alarmCounter = new AtomicInteger(1);

	private final ISpotService serviceBinder = new ISpotService.Stub()
	{
		@Override
		public void initAlarms() throws RemoteException
		{
			threadPool.schedule(createAlarmsOnAllSpots(), INITIAL_DELAY, TimeUnit.MILLISECONDS);
		}

		@Override
		public void updateAll() throws RemoteException
		{
			threadPool.schedule(createUpdateAllSpotsTask(), INITIAL_DELAY, TimeUnit.MILLISECONDS);
		}

		@Override
		public void stop() throws RemoteException
		{
			if(Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "ISpotService#stop called");
			}
		}

		@Override
		public void update(final int _id)
		{
			final UpdateSpotsTask task = new UpdateSpotsTask(_id, SpotService.this);
			final ScheduledFuture<Forecast> future = threadPool.schedule(task, INITIAL_DELAY, TimeUnit.MILLISECONDS);
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
		Log.i(LOG_TAG, "Service created");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(final Intent _intent, final int _startId)
	{
		super.onStart(_intent, _startId);
		createThreadPool();
		if (Logging.isLoggingEnabled())
		{
			Log.i(LOG_TAG, "Service started ");
		}
		final boolean found = isSelectedID(_intent);
		if (!found)
		{
			if (Logging.isLoggingEnabled())
			{
				Log.i(LOG_TAG, "Found no spot to update!");
			}
			return;
		}
		//
		final int selectedID = getSelectedID(_intent);
		try
		{
			serviceBinder.update(selectedID);
		}
		catch (final RemoteException e)
		{
			Log.e(LOG_TAG, "Failed to call Service Stub", e);
		}
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
	 * Creates an Alarm which should be executed whenever the network is not
	 * reachable
	 * 
	 * @param _id
	 *            TODO : Alarms that fail cause of not reachable network, should
	 *            run again.
	 */
	private void createRetryAlarm(final long _id)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Creating retry alarm for selected with id :" + _id);
		}
		final long now = System.currentTimeMillis();

		final Intent intent = new Intent(this, AlarmBroadcastReciever.class);
		intent.putExtra(IntentConstants.SELECTED_PRIMARY_KEY, _id);
		final int requestID = alarmCounter.incrementAndGet();
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestID, intent, 0);
		final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, now + (AlarmManager.INTERVAL_HALF_HOUR), pendingIntent);
	}

	private void createAlarmsForSpot(final int _id)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Creating alarm");
		}
		final long now = System.currentTimeMillis();
		//
		final Intent intent = new Intent(this, AlarmBroadcastReciever.class);
		intent.putExtra(IntentConstants.SELECTED_PRIMARY_KEY, _id);
		final int requestID = alarmCounter.incrementAndGet();
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestID, intent, 0);
		final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now + INITIAL_DELAY, AlarmManager.INTERVAL_DAY,
				pendingIntent);
		//
		// final NotificationManager notificationManager = (NotificationManager)
		// getSystemService(Context.NOTIFICATION_SERVICE);
		// Notification notification = new Notification(R.drawable.icon,
		// "Wake up alarm", System.currentTimeMillis());
		// final PendingIntent contentIntent = PendingIntent.getActivity(this,
		// 0, new Intent(this, SpotService.class), 0);
		// notification.setLatestEventInfo(this, "Context Title",
		// "Context text", contentIntent);
		// notification.flags = Notification.FLAG_INSISTENT;

		// notification.sound = (Uri) intent.getParcelableExtra("Ringtone");
		// Log.d(LOG_TAG, "Ringtone URI:" + notification.sound);
		// notification.vibrate = (long[])
		// intent.getExtras().get("vibrationPatern");

		// The PendingIntent to launch our activity if the user selects this
		// notification
		// notificationManager.notify(777, notification);

	}

	private int showStatus(final Context context, final NotificationManager notificationManager,
			final String notificationTitle, final String notificationDetails)
	{

		final int notificationID = notificationCounter.incrementAndGet();

		final long when = System.currentTimeMillis(); // notification time
		final Intent notificationIntent = new Intent(context, OngoingUpdateActivity.class);
		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		final Notification notification = new Notification(R.drawable.icon_update, notificationTitle, when);
		notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(context, notificationTitle, notificationDetails, contentIntent);

		notificationManager.notify(notificationID, notification);
		return notificationID;
	}

	protected Runnable createAlarmsOnAllSpots()
	{
		final Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				final ISelectedDAO dao = DAOFactory.getSelectedDAO(SpotService.this);

				if (!dao.isSpotActiv())
				{
					if (Logging.isLoggingEnabled())
					{
						Log.i(LOG_TAG, "No active spot configured.");
					}
					return;
				}
				final Collection<SpotConfigurationVO> spots = dao.getActivSpots();// Util.getSpotConfiguration(SpotService.this);
				if (Logging.isLoggingEnabled())
				{
					Log.i(LOG_TAG, "Found " + spots.size() + " spot(s) to install alarm trigger(s).");
				}

				final Iterator<SpotConfigurationVO> iter = spots.iterator();
				while (iter.hasNext())
				{
					final SpotConfigurationVO spot = iter.next();
					if (Logging.isLoggingEnabled())
					{
						Log.d(LOG_TAG, "Primary Key is: " + spot.getPrimaryKey());
					}
					createAlarmsForSpot(spot.getPrimaryKey());
				}
			}
		};
		return task;
	}

	/**
	 * Creates an task which checks for updates on all active configured Spots.
	 * 
	 * @returns {@link Runnable}
	 */
	private Runnable createUpdateAllSpotsTask()
	{
		final Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				final int alarmID = SpotService.this.showStatus(SpotService.this,
						(NotificationManager) SpotService.this.getSystemService(Context.NOTIFICATION_SERVICE),
						getString(R.string.ongoing_update_title), getString(R.string.ongoing_update_text));

				try
				{
					final ISelectedDAO dao = DAOFactory.getSelectedDAO(SpotService.this);

					if (!dao.isSpotActiv())
					{
						Log.i(LOG_TAG, "No active spot configured.");
						return;
					}
					final Collection<SpotConfigurationVO> spots = dao.getActivSpots();// Util.getSpotConfiguration(SpotService.this);
					if (Logging.isLoggingEnabled())
					{
						Log.i(LOG_TAG, "Found " + spots.size() + " Spots to update.");
					}

					final Iterator<SpotConfigurationVO> iter = spots.iterator();
					while (iter.hasNext())
					{
						final SpotConfigurationVO spot = iter.next();
						if (Logging.isLoggingEnabled())
						{
							Log.d(LOG_TAG, "Primary Key is: " + spot.getPrimaryKey());
						}
						createAlarmsForSpot(spot.getPrimaryKey());
					}

				}
				finally
				{
					removeUpdateOnStatusBar((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE),
							alarmID);
				}
			}
		};
		return task;
	}

	/**
	 * 
	 * @param _notificationManager
	 * @param _intentID
	 */
	private static void removeUpdateOnStatusBar(final NotificationManager _notificationManager,
			final int _cancelIntentID)
	{
		_notificationManager.cancel(_cancelIntentID);
	}

	/**
	 * Checks if ThreadPool already created, if not it creates it
	 */
	private synchronized void createThreadPool()
	{
		if (threadPool == null)
		{
			final int poolSize = getResources().getInteger(R.integer.schedule_threadpool_size);
			threadPool = new ScheduledThreadPoolExecutor(poolSize, new ThreadFactory());
		}
	}
}