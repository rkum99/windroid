package de.macsystems.windroid;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.SpotUpdater;

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

	private Timer timer;
	/**
	 * Holds state of Service
	 */
	private volatile boolean isServiceRunning;

	private TimerTask spotUpdateTask = new TimerTask()
	{

		@Override
		public void run()
		{

			if (!isServiceRunning())
			{
				Log.i(LOG_TAG, "Service stopped, nothing to do.");
				return;
			}

			final Intent intent = new Intent(DE_MACSYSTEMS_WINDROID_SPOT_UPDATE_ACTION);
			if (!Util.isSpotConfigured(SpotService.this))
			{
				Log.i(LOG_TAG, "No spot configured.");
				return;
			}

			if (!IOUtils.isNetworkReachable(SpotService.this))
			{
				Log.d(LOG_TAG, "Network not reachable. Waiting.");
				return;

			}
			final SpotConfigurationVO spot = Util.getSpotConfiguration(SpotService.this);
			Log.d(LOG_TAG, "--------------------------------------------------");
			Log.d(LOG_TAG, "Following Spot is Configured:");
			Log.d(LOG_TAG, "Spot Name:" + spot.getStation().getName());
			Log.d(LOG_TAG, "Spot ID:" + spot.getStation().getId());
			Log.d(LOG_TAG, "Spot Keyword:" + spot.getStation().getKeyword());

			Log.d(LOG_TAG, "has stats:" + spot.getStation().hasStatistic());
			Log.d(LOG_TAG, "has superforecast:" + spot.getStation().hasSuperforecast());

			Log.d(LOG_TAG, "Windspeed min:" + spot.getWindspeedMin());
			Log.d(LOG_TAG, "Windspeed max:" + spot.getWindspeedMax());

			Log.d(LOG_TAG, "Wind from::" + spot.getFromDirection());
			Log.d(LOG_TAG, "Wind to:" + spot.getToDirection());

			Log.d(LOG_TAG, "preferred Windunit :" + spot.getPreferredWindUnit());
			Log.d(LOG_TAG, "--------------------------------------------------");

			try
			{

				long start = System.currentTimeMillis();
				final Forecast update = SpotUpdater.getUpdate(spot);
				long end = System.currentTimeMillis();
				Log.d(LOG_TAG, "Brauchte " + (end - start) + "ms für das update.");
				// Log.d(LOG_TAG, "Recieved ForecastDetail : " +
				// update.toString());
				Log.d(LOG_TAG, "--------------------------------------------------");
				Log.d(LOG_TAG, "--------------------------------------------------");
				Log.d(LOG_TAG, "--------------------------------------------------");
				Log.d(LOG_TAG, "--------------------------------------------------");
			}
			catch (NullPointerException e)
			{
				Log.e(LOG_TAG, "Failed to update Spot.", e);
			}
			catch (IOException e)
			{
				Log.e(LOG_TAG, "Failed to update Spot.", e);
			}

			sendBroadcast(intent);

		}
	};

	/**
	 * @return the isServiceRunning
	 */
	private synchronized boolean isServiceRunning()
	{
		return isServiceRunning;
	}

	/**
	 * @param isServiceRunning
	 *            the isServiceRunning to set
	 */
	private synchronized void setServiceRunning(boolean isServiceRunning)
	{
		this.isServiceRunning = isServiceRunning;
	}

	private final ISpotService serviceBinder = new ISpotService.Stub()
	{

		@Override
		public boolean isRunning() throws RemoteException
		{
			Log.d(LOG_TAG, "ISpotService#isRunning called");
			return isServiceRunning;
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);

		if (timer != null)
		{
			timer.cancel();
		}
		timer = new Timer("Spot update timer");
		timer.scheduleAtFixedRate(spotUpdateTask, 0, 1000 * 5);
		Log.i(LOG_TAG, "Service started");
	}
}