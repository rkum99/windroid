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

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.R;
import de.macsystems.windroid.WindUtils;
import de.macsystems.windroid.alarm.AlarmUtil;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.RetryLaterException;

/**
 * A Task which is only called from windroid itself as a Alarm happened. It
 * provides audio feedback to indicate what is going on (testing).
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class AlarmUpdateTask extends AudioFeedbackTask
{
	private final static String LOG_TAG = AlarmUpdateTask.class.getSimpleName();

	private final int selectedID;

	/**
	 * 
	 * @param _context
	 * @param _selectedID
	 * @throws NullPointerException
	 */
	public AlarmUpdateTask(final Context _context, final int _selectedID) throws NullPointerException
	{
		super(_context);
		selectedID = _selectedID;
	}

	@Override
	public void execute() throws Exception
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

			final boolean available = isNetworkReachable();
			if (!available)
			{
				AlarmUtil.createRetryAlarm(selectedID, getContext());
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, "Cancel update as network not reachable.");
				}
				return;
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
		catch (final DBException e)
		{
			Log.e(LOG_TAG, "", e);
		}
		finally
		{
			clearNotification();
		}

	}

}
