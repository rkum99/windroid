package de.macsystems.windroid.io.task;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

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
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.RetryLaterException;
import de.macsystems.windroid.service.AbstractNotificationTask;
import de.macsystems.windroid.service.UpdateAlarmTask;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class UpdateAllActiveSpotReports extends AbstractNotificationTask
{
	private final static String LOG_TAG = UpdateAlarmTask.class.getSimpleName();

	/**
	 * @param context
	 * @throws NullPointerException
	 */
	public UpdateAllActiveSpotReports(Context context) throws NullPointerException
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
			try
			{
				final int[] spots = dao.getActivSpotIDs();
				showStatus("Update " + spots.length + " Spots", "");

				if (Logging.isLoggingEnabled())
				{
					Log.i(LOG_TAG, "Found " + spots.length + " Spots to update. IDs : " + Arrays.toString(spots));
				}
				for (int i = 0; i < spots.length; i++)
				{
					loadSelectedForecast(spots[i]);
				}
			}
			catch (final DBException e)
			{
				Log.e(LOG_TAG, "Failed to fetch Spot", e);
			}
		}
		finally
		{
			clearNotification();
		}

	}

	private void loadSelectedForecast(final int _selectedID) throws DBException
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "private void bla(final int _selectedID) : " + _selectedID);
		}

		final ISelectedDAO dao = DAOFactory.getSelectedDAO(getContext());
		final SpotConfigurationVO vo = dao.getSpotConfiguration(_selectedID);

		if (!vo.isActiv())
		{
			throw new DBException("Spot is not activ!");
		}

		final boolean available = IOUtils.isNetworkReachable(getContext());
		if (!available)
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Cancel update as network not reachable.");
			}
			return;
		}
		try
		{
			final URI uri = WindUtils.getJSONForcastURL(vo.getStation().getId()).toURI();
			final ParseForecastTask task = new ParseForecastTask(uri);
			final Forecast forecast = task.execute(getContext());
			// Update Forecast in DB
			final IForecastDAO forecastDAO = DAOFactory.getForecast(getContext());
			forecastDAO.updateForecast(forecast, _selectedID);
			//
			// return forecast;
		}
		catch (final IOException e)
		{
			Log.e(LOG_TAG, "Failed to create uri", e);
		}
		catch (final RetryLaterException e)
		{
			Log.e(LOG_TAG, "", e);
		}
		catch (final Throwable e)
		{
			Log.e(LOG_TAG, "", e);
		}

	}
}
