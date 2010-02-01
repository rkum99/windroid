package de.macsystems.windroid;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.ISelectedDAO;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class SpotService extends Service
{

	private final static String LOG_TAG = SpotService.class.getSimpleName();
	/**
	 * Name of Action which will start this Service
	 */
	public static final String DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION = "de.macsystems.windroid.START_SPOT_SERVICE_ACTION";
	/**
	 * Name of Intent Action which will be broadcasted on Spot update (use a
	 * IntentFilter).
	 */
	public static final String DE_MACSYSTEMS_WINDROID_SPOT_UPDATE_ACTION = "de.macsystems.windroid.SPOT_UPDATE_ACTION";

	private static final int POOLSIZE = 2;

	private ScheduledExecutorService threadPool;

	/**
	 * Holds state of Service
	 */
	private final AtomicBoolean isServiceRunning = new AtomicBoolean(false);





	/**
	 * Task which checks for Updates on configured Spots.
	 */
	private final Runnable enqueConfiguredSpotsTask = new Runnable()
	{
		@Override
		public void run()
		{
			Log.d(LOG_TAG, "Entering enqueConfiguredSpotsTask.....................");
			if (!isServiceRunning())
			{
				Log.i(LOG_TAG, "Service stopped, nothing to do.");
				return;
			}

			final ISelectedDAO dao = DAOFactory.getSelectedDAO(SpotService.this);

			if (!dao.isSpotActiv())
			{
				Log.i(LOG_TAG, "No Activ Spot Configured.");
				return;
			}
			// A Spot is Configured, show Update Icon
			final Collection<SpotConfigurationVO> spots = dao.getActivSpots();// Util.getSpotConfiguration(SpotService.this);
			Log.i(LOG_TAG, "Found " + spots.size() + " Spots to update.");

			final Iterator<SpotConfigurationVO> iter = spots.iterator();
			while (iter.hasNext())
			{
				final SpotConfigurationVO spot = iter.next();
				Log.d(LOG_TAG, "Primary Key is: " + spot.getPrimaryKey());
				createAlarm(spot.getPrimaryKey());
			}

		}
	};

	/**
	 * @return the isServiceRunning
	 */
	private boolean isServiceRunning()
	{
		return isServiceRunning.get();
	}

	/**
	 * @param _isServiceRunning
	 *            the isServiceRunning to set
	 */
	private void setServiceRunning(final boolean _isServiceRunning)
	{
		isServiceRunning.set(_isServiceRunning);
	}

	private final ISpotService serviceBinder = new ISpotService.Stub()
	{

		@Override
		public boolean isRunning() throws RemoteException
		{
			Log.d(LOG_TAG, "ISpotService#isRunning called");
			return isServiceRunning.get();
		}

		@Override
		public void start() throws RemoteException
		{
			setServiceRunning(true);
			Log.d(LOG_TAG, "ISpotService#start called");
		}

		@Override
		public void stop() throws RemoteException
		{
			setServiceRunning(false);
			Log.d(LOG_TAG, "ISpotService#stop called");
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
		Log.i(LOG_TAG, "Service created");
		createThreadPool();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(final Intent intent, final int startId)
	{
		super.onStart(intent, startId);
		Log.i(LOG_TAG, "Service started");
		createThreadPool();
	}

	/**
	 * Checks if ThreadPool already instantiated, when not it creates it
	 */
	private synchronized void createThreadPool()
	{

		final long initialDelay = 5L;
		final long updateDelay = 15L;

		if (threadPool == null)
		{
			threadPool = Executors.newScheduledThreadPool(POOLSIZE);
			threadPool.scheduleAtFixedRate(enqueConfiguredSpotsTask, initialDelay, updateDelay, TimeUnit.SECONDS);
			Log.d(LOG_TAG, "Thread Pool Created.");
		}
	}

	public void createAlarm(final long _id)
	{

		final long now = System.currentTimeMillis();

		final Intent intent = new Intent(this, AlarmBroadcastReciever.class);
		intent.putExtra(AlarmBroadcastReciever.SELECTED_PRIMARY_KEY, _id);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 400, intent, 0);
		final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 1000),
				(AlarmManager.INTERVAL_DAY), pendingIntent);
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

}