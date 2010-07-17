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
import java.net.URISyntaxException;
import java.util.Arrays;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.WindUtils;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.RetryLaterException;

/**
 * A Task which updates all Active Spots. This will be generally used when user
 * forces an update.
 *
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class UpdateAllActiveSpotReports extends AbstractNotificationTask<Void>
{
	private final static String LOG_TAG = UpdateAllActiveSpotReports.class.getSimpleName();

	/**
	 * @param context
	 * @throws NullPointerException
	 */
	public UpdateAllActiveSpotReports(final Context context) throws NullPointerException
	{
		super(context);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.macsystems.windroid.service.AbstractNotificationTask#execute()
	 */
	@Override
	public void execute() throws Exception
	{
		try
		{
			final ISelectedDAO dao = DAOFactory.getSelectedDAO(getContext());
			if (!dao.isSpotActiv())
			{
				Log.i(LOG_TAG, "No active spot configured.");
				return;
			}
			final int[] spots = dao.getActivSpotIDs();
			showStatus("Update " + spots.length + " Spots", "");

			if (Logging.isEnabled)
			{
				Log.i(LOG_TAG, "Found " + spots.length + " Spots to update. IDs : " + Arrays.toString(spots));
			}
			for (int i = 0; i < spots.length; i++)
			{
				loadSelectedForecast(spots[i]);
			}
		}
		finally
		{
			clearNotification();
		}

	}

	private void loadSelectedForecast(final int _selectedID) throws DBException, URISyntaxException, IOException,
			RetryLaterException, InterruptedException
	{
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "private void bla(final int _selectedID) : " + _selectedID);
		}

		final ISelectedDAO dao = DAOFactory.getSelectedDAO(getContext());
		final SpotConfigurationVO vo = dao.getSpotConfiguration(_selectedID);

		if (!vo.isActiv())
		{
			throw new DBException("Spot is not activ!");
		}

		final boolean available = isNetworkReachable();
		if (!available)
		{
			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "Cancel update as network not reachable.");
			}
			return;
		}
		final URI uri = WindUtils.getJSONForcastURL(vo.getStation().getId()).toURI();
		final ParseForecastTask task = new ParseForecastTask(uri);
		final Forecast forecast = task.execute(getContext());
		// Update Forecast in DB
		final IForecastDAO forecastDAO = DAOFactory.getForecast(getContext());
		forecastDAO.updateForecast(forecast, _selectedID);
		//
		// return forecast;

	}
}