package de.macsystems.windroid;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.io.IOUtils;

/**
 * Called when Alarm comes up. The primary key recieved is the Spot which needs
 * to be monitored.
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class AlarmBroadcastReciever extends BroadcastReceiver
{
	private final static String LOG_TAG = AlarmBroadcastReciever.class.getSimpleName();
	/**
	 * Lookup Key for primary key in a Intent {@value #SELECTED_PRIMARY_KEY}
	 */
	public final static String SELECTED_PRIMARY_KEY = "selected primary key";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		final long id = intent.getLongExtra(SELECTED_PRIMARY_KEY, -1);
		if (id == -1)
		{
			throw new IllegalArgumentException("missing id");
		}

		if (!IOUtils.isNetworkReachable(context))
		{
			throw new IllegalStateException("Skipping update, Network not reachable.");
		}

		final Intent startServiceIntent = new Intent();
		startServiceIntent.setAction(IntentConstants.DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION);
		final ComponentName name = context.startService(startServiceIntent);
		if (name == null)
		{
			Log.e(LOG_TAG, "Failed to start SpotService.");
		}
		else
		{
			Log.i(LOG_TAG, "SpotService launched.");
		}

		// final ISelectedDAO dao = DAOFactory.getSelectedDAO(context);
		// final SpotConfigurationVO vo = dao.getSpotConfiguration(id);
		//
		// Log.d(LOG_TAG, "Alarm for :" + vo.getStation().getName());
		//
		// /**
		// * If Spot is marked inactiv we skip.
		// */
		// if (!vo.isActiv())
		// {
		// return;
		// }
		//
		// try
		// {
		// final URI uri =
		// WindUtils.getJSONForcastURL(vo.getStation().getId()).toURI();
		// final ForecastTask task = new ForecastTask(uri);
		// final Forecast forecast = task.execute(context);
		// final Iterator<ForecastDetail> iter = forecast.iterator();
		// while (iter.hasNext())
		// {
		// final ForecastDetail detail = iter.next();
		// // final WindSpeed windspeed = detail.getWindSpeed();
		// // Log.i(LOG_TAG, windspeed.getValue() + " " +
		// // windspeed.getUnit());
		// }
		// }
		// catch (final IOException e)
		// {
		// Log.d(LOG_TAG, "", e);
		// }
		// catch (final RetryLaterException e)
		// {
		// Log.d(LOG_TAG, "", e);
		// }
		// catch (final Exception e)
		// {
		// Log.e(LOG_TAG, "", e);
		// }

	}

	private void update(final long _id)
	{
		final ISelectedDAO dao = DAOFactory.getSelectedDAO(null);
		final SpotConfigurationVO vo = dao.getSpotConfiguration(_id);
		if (!vo.isActiv())
		{
			return;
		}
		// A Spot is Configured, show Update Icon

	}
}
