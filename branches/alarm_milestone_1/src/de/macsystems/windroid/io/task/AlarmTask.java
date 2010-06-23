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
import de.macsystems.windroid.alarm.Alert;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.RetryLaterException;

/**
 * A Task which is only called from windroid-service itself. This Alarm is a
 * regular Alarm which got invoked by the AlarmManager enqueued PendingIntent
 * instances.<br>
 * 
 * 
 * @author mac
 * @version $Id$
 */
public class AlarmTask extends AudioFeedbackTask
{
	private final static String LOG_TAG = AlarmTask.class.getSimpleName();

	private final Alert alert;

	/**
	 * 
	 * @param _context
	 * @param _alert
	 * @throws NullPointerException
	 */
	public AlarmTask(final Context _context, final Alert _alert) throws NullPointerException
	{
		super(_context);
		if (_alert == null)
		{
			throw new NullPointerException("alert");
		}
		alert = _alert;
	}

	@Override
	public void execute() throws Exception
	{
		boolean showNotification = true;
		try
		{

			final ISelectedDAO dao = DAOFactory.getSelectedDAO(getContext());
			final SpotConfigurationVO vo = dao.getSpotConfiguration(alert.getSelectedID());

			showStatus(getContext().getString(R.string.recieve_forecast_update_title) + vo.getStation().getName(), "");
			if (!vo.isActiv())
			{
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, "Cancel update as spot is not active: " + vo.getStation().getName());
				}
				// Quit
				return;
			}

			final boolean available = isNetworkReachable();
			if (!available)
			{
				AlarmUtil.enqueueRetryAlarm(alert, getContext());
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
				forecastDAO.updateForecast(forecast, alert.getSelectedID());
				// for Audio output
				setAsSuccessfull();
			}
			catch (final IOException e)
			{
				Log.e(LOG_TAG, "Failed to create uri", e);
				AlarmUtil.enqueueRetryAlarm(alert, getContext());
			}
			catch (final RetryLaterException e)
			{
				Log.e(LOG_TAG, "", e);
				AlarmUtil.enqueueRetryAlarm(alert, getContext());
			}

		}
		catch (final DBException e)
		{
			Log.e(LOG_TAG, "", e);
		}
		finally
		{
			play();
			clearNotification();
		}

	}
}
