package de.macsystems.windroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.RetryLaterException;
import de.macsystems.windroid.io.SpotUpdater;
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

	private ScheduledThreadPoolExecutor threadPool;

	/**
	 * Holds state of Service
	 */
	private volatile boolean isServiceRunning;

	private final Runnable enqueConfiguredSpotsTask = new Runnable()
	{
		@Override
		public void run()
		{

			if (!isServiceRunning())
			{
				Log.i(LOG_TAG, "Service stopped, nothing to do.");
				return;
			}

			if (!Util.isSpotConfigured(SpotService.this))
			{
				Log.i(LOG_TAG, "No spot configured.");
				return;
			}

			final List<SpotConfigurationVO> spots = Util.FAKEgetSpotConfiguration(SpotService.this);
			final List<ScheduledFuture<Forecast>> futures = new ArrayList<ScheduledFuture<Forecast>>(spots.size());
			for (final SpotConfigurationVO spot : spots)
			{
				// final FutureTask<Forecast> futureForecast = new
				// FutureTask<Forecast>(new UpdateSpotsTask(spot,
				// SpotService.this));

				final UpdateSpotsTask futureForecast = new UpdateSpotsTask(spot, SpotService.this);
				final ScheduledFuture<Forecast> future = threadPool.schedule(futureForecast, 1, TimeUnit.SECONDS);
				futures.add(future);
			}

			for (final ScheduledFuture<Forecast> future : futures)
			{
				try
				{
					final Forecast forecast = future.get();

				}
//				catch (final RetryLaterException e)
//				{
//					e.printStackTrace();					
//				}
				catch (final InterruptedException e)
				{
					e.printStackTrace();
				}
				catch (final ExecutionException e)
				{
					e.printStackTrace();
				}
			}

		}
	};

	/**
	 * 
	 * @param _spot
	 * @return Forecast
	 */
	private Forecast getUpdate(final SpotConfigurationVO _spot)
	{
		Log.d(LOG_TAG, "--------------------------------------------------");
		Log.d(LOG_TAG, "Following Spot is Configured:");
		Log.d(LOG_TAG, "Spot Name:" + _spot.getStation().getName());
		Log.d(LOG_TAG, "Spot ID:" + _spot.getStation().getId());
		Log.d(LOG_TAG, "Spot Keyword:" + _spot.getStation().getKeyword());

		Log.d(LOG_TAG, "has stats:" + _spot.getStation().hasStatistic());
		Log.d(LOG_TAG, "has superforecast:" + _spot.getStation().hasSuperforecast());

		Log.d(LOG_TAG, "Windspeed min:" + _spot.getWindspeedMin());
		Log.d(LOG_TAG, "Windspeed max:" + _spot.getWindspeedMax());

		Log.d(LOG_TAG, "Wind from::" + _spot.getFromDirection());
		Log.d(LOG_TAG, "Wind to:" + _spot.getToDirection());

		Log.d(LOG_TAG, "preferred Windunit :" + _spot.getPreferredWindUnit());
		Log.d(LOG_TAG, "--------------------------------------------------");
		final Forecast update;
		try
		{
			final long start = System.currentTimeMillis();
			update = SpotUpdater.getUpdate(_spot);
			final long end = System.currentTimeMillis();
			Log.d(LOG_TAG, "Brauchte " + (end - start) + " ms für das update.");
			// Log.d(LOG_TAG, "Recieved ForecastDetail : " +
			// update.toString());
			Log.d(LOG_TAG, "--------------------------------------------------");
			Log.d(LOG_TAG, "--------------------------------------------------");
			Log.d(LOG_TAG, "--------------------------------------------------");
			Log.d(LOG_TAG, "--------------------------------------------------");
		}
		catch (final RetryLaterException e)
		{
			// threadPool.schedule(command, delay, unit);
			Log.w(LOG_TAG, "Failed to update Spot.", e);
		}
		catch (final IOException e)
		{
			Log.e(LOG_TAG, "Failed to update Spot.", e);
		}
		catch (final NullPointerException e)
		{
			Log.e(LOG_TAG, "Failed to update Spot.", e);
		}
		return null;
	}

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
	private synchronized void setServiceRunning(final boolean isServiceRunning)
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
			threadPool = new ScheduledThreadPoolExecutor(POOLSIZE);
		}
	}
}
