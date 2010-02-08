package de.macsystems.windroid.io.task;

import java.net.URI;
import java.util.concurrent.Callable;

import android.content.Context;
import de.macsystems.windroid.WindUtils;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.RetryLaterException;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * Callable which will update the current forecast of a spot.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class UpdateSpotsTask implements Callable<Forecast>
{

	@SuppressWarnings("unused")
	private final static String LOG_TAG = UpdateSpotsTask.class.getSimpleName();

	final SpotConfigurationVO spot;

	final private Context context;

	/**
	 * 
	 * 
	 * @param _spot
	 * @param _context
	 */
	public UpdateSpotsTask(final SpotConfigurationVO _spot, final Context _context) throws NullPointerException
	{
		if (_spot == null)
		{
			throw new NullPointerException("spot");
		}
		if (_context == null)
		{
			throw new NullPointerException("context");
		}
		spot = _spot;
		context = _context;
	}

	@Override
	public Forecast call() throws Exception
	{
		if (!IOUtils.isNetworkReachable(context))
		{
			throw new RetryLaterException("Skipping update, Network not reachable.");
		}

		final URI uri = WindUtils.getJSONForcastURL(spot.getStation().getId()).toURI();
		final ForecastTask task = new ForecastTask(uri, NullProgressAdapter.INSTANCE);
		return task.execute(context);
	}
}