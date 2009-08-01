/**
 * 
 */
package de.macsystems.windroid.io.task;

import java.util.concurrent.Callable;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.RetryLaterException;
import de.macsystems.windroid.io.SpotUpdater;

/**
 * @author mac
 * 
 */
public class UpdateSpotsTask implements Callable<Forecast>
{

	private final static String LOG_TAG = UpdateSpotsTask.class.getSimpleName();

	final SpotConfigurationVO spot;

	final private Context context;

	/**
	 * 
	 * 
	 * @param _spot
	 * @param _context
	 */
	public UpdateSpotsTask(final SpotConfigurationVO _spot, final Context _context)
	{
		if (_spot == null)
		{
			throw new NullPointerException();
		}
		if (_context == null)
		{
			throw new NullPointerException();
		}
		spot = _spot;
		context = _context;

	}

	@Override
	public Forecast call() throws Exception
	{
		if (!IOUtils.isNetworkReachable(context))
		{
			Log.d(LOG_TAG, "Skipping update, Network not reachable.");
			throw new RetryLaterException("Skipping update, Network not reachable.");
		}
		return SpotUpdater.getUpdate(spot);
	}
}