package de.macsystems.windroid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.identifyable.WindSpeed;
import de.macsystems.windroid.io.task.UpdateSpotsTask;

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
	private AtomicBoolean isServiceRunning = new AtomicBoolean(false);

	private static int showUpdateOnStatusBar(final Context context, final NotificationManager notificationManager,
			final int alarmID, final String notificationTitle, final String notificationDetails)
	{
		final CharSequence tickerText = "Hello"; // ticker-text
		long when = System.currentTimeMillis(); // notification time
		final CharSequence contentTitle = "My notification"; // expanded message
		// title
		final CharSequence contentText = "Hello World!"; // expanded message
		// text

		final Intent notificationIntent = new Intent(context, OngoingUpdate.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		final Notification notification = new Notification(R.drawable.icon_update, tickerText, when);
		notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		notificationManager.notify(alarmID, notification);
		return alarmID;
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

			if (!Util.isSpotConfigured(SpotService.this))
			{
				Log.i(LOG_TAG, "No Spot Configured.");
				return;
			}
			// A Spot is Configured, show Update Icon

			final List<SpotConfigurationVO> spots = Util.getSpotConfiguration(SpotService.this);

			final List<ScheduledFuture<Forecast>> futures = new ArrayList<ScheduledFuture<Forecast>>(spots.size());
			for (final SpotConfigurationVO spot : spots)
			{
				final UpdateSpotsTask futureForecast = new UpdateSpotsTask(spot, SpotService.this);
				final ScheduledFuture<Forecast> future = threadPool.schedule(futureForecast, 1, TimeUnit.SECONDS);
				futures.add(future);
			}

			final int alarmID = showUpdateOnStatusBar(SpotService.this, (NotificationManager) SpotService.this
					.getSystemService(Context.NOTIFICATION_SERVICE), 999, "", "");
			for (final ScheduledFuture<Forecast> future : futures)
			{
				try
				{
					final Forecast forecast = future.get();
					final Iterator<ForecastDetail> iter = forecast.iterator();
					while (iter.hasNext())
					{
						final ForecastDetail detail = iter.next();
						final WindSpeed windspeed = detail.getWindSpeed();
						Log.i(LOG_TAG, windspeed.getValue() + " " + windspeed.getUnit());
					}
				}
				catch (final InterruptedException e)
				{
					e.printStackTrace();
				}
				catch (final ExecutionException e)
				{
					e.printStackTrace();
				}
			}
			removeUpdateOnStatusBar((NotificationManager) SpotService.this
					.getSystemService(Context.NOTIFICATION_SERVICE), alarmID);
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
		if (threadPool == null)
		{
			threadPool = Executors.newScheduledThreadPool(POOLSIZE);
			threadPool.scheduleAtFixedRate(enqueConfiguredSpotsTask, 5L, 15L, TimeUnit.SECONDS);
			Log.d(LOG_TAG, "Thread Pool Created.");
		}
	}
}