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
import de.macsystems.windroid.R;
import de.macsystems.windroid.WindUtils;
import de.macsystems.windroid.alarm.AlarmUtil;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.RetryLaterException;
import de.macsystems.windroid.service.AbstractNotificationTask;

/**
 * Callable which will update the current forecast of a spot and updates the
 * Database.
 * 
 * @author Jens Hohl
 * @version $Id: UpdateSpotForecastTask.java 314 2010-04-15 11:50:03Z jens.hohl
 *          $
 * 
 */
public class UpdateSpotForecastTask extends AbstractNotificationTask implements Callable<Void>

{

	private final static String LOG_TAG = UpdateSpotForecastTask.class.getSimpleName();

	final int selectedID;

	/**
	 * 
	 * @param _selectedID
	 * @param _context
	 * @throws NullPointerException
	 */
	public UpdateSpotForecastTask(final int _selectedID, final Context _context) throws NullPointerException
	{
		super(_context);
		selectedID = _selectedID;
	}

	@Override
	public Void call() throws Exception
	{

		try
		{
			final ISelectedDAO dao = DAOFactory.getSelectedDAO(getContext());
			final SpotConfigurationVO vo = dao.getSpotConfiguration(selectedID);

			showStatus(getContext().getString(R.string.recieve_forecast_update_title) + vo.getStation().getName(), "");
			if (!vo.isActiv())
			{
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, "Cancel update as spot is not active: " + vo.getStation().getName());
				}
			}

			final boolean available = IOUtils.isNetworkReachable(getContext());
			if (!available)
			{
				AlarmUtil.createRetryAlarm(selectedID, getContext());
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, "Cancel update as network not reachable.");
				}
				// return Void
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
				final Forecast forecast = task.execute(getContext());
				// Update Forecast in DB
				final IForecastDAO forecastDAO = DAOFactory.getForecast(getContext());
				forecastDAO.updateForecast(forecast, selectedID);
				//
				// return forecast;
			}
			catch (final IOException e)
			{
				Log.e(LOG_TAG, "Failed to create uri", e);
				AlarmUtil.createRetryAlarm(selectedID, getContext());
			}
			catch (final RetryLaterException e)
			{
				Log.e(LOG_TAG, "", e);
				AlarmUtil.createRetryAlarm(selectedID, getContext());
			}
			catch (final Throwable e)
			{
				Log.e(LOG_TAG, "", e);
			}

		}
		finally
		{
			clearNotification();
		}
		// Void
		return null;
	}
}