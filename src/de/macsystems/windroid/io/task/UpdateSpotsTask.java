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
package de.macsystems.windroid.io.task;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Callable;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.WindUtils;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.RetryLaterException;

/**
 * Callable which will update the current forecast of a spot and updates the
 * Database.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class UpdateSpotsTask implements Callable<Forecast>
{

	private final static String LOG_TAG = UpdateSpotsTask.class.getSimpleName();

	final long primaryKey;

	final private Context context;

	/**
	 * 
	 * 
	 * @param _primaryKey
	 * @param _context
	 */
	public UpdateSpotsTask(final long _primaryKey, final Context _context) throws NullPointerException
	{
		if (_context == null)
		{
			throw new NullPointerException("context");
		}
		primaryKey = _primaryKey;
		context = _context;
	}

	@Override
	public Forecast call() throws Exception
	{
		final ISelectedDAO dao = DAOFactory.getSelectedDAO(context);
		final SpotConfigurationVO vo = dao.getSpotConfiguration(primaryKey);

		if (!vo.isActiv())
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Cancel update as spot is not active: " + vo.getStation().getName());
			}
			return null;
		}

		final boolean available = IOUtils.isNetworkReachable(context);
		if (!available)
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Cancel update as network not reachable.");
			}
			// createRetryAlarm(primaryKey);
			return null;
		}
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Alarm for: " + vo.getStation().getName());
		}
		try
		{
			final URI uri = WindUtils.getJSONForcastURL(vo.getStation().getId()).toURI();
			final ParseForecastTask task = new ParseForecastTask(uri);
			final Forecast forecast = task.execute(context);
			// Update Forecast in DB
			final IForecastDAO forecastDAO = DAOFactory.getForecast(context);
			forecastDAO.setForecast(forecast);
			//
			return forecast;
		}
		catch (final IOException e)
		{
			Log.e(LOG_TAG, "Failed to create uri", e);
		}
		catch (final RetryLaterException e)
		{
			Log.e(LOG_TAG, "", e);
		}
		catch (final Exception e)
		{
			Log.e(LOG_TAG, "", e);
		}
		return null;

	}
}