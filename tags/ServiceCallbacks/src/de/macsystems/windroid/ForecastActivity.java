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
package de.macsystems.windroid;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.proxy.UpdateConnection;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 * 
 * @TODO: 
 *        http://android-developers.blogspot.com/2009/02/faster-screen-orientation
 *        -change.html
 */
public final class ForecastActivity extends DBActivity
{

	private final static String LOG_TAG = ForecastActivity.class.getSimpleName();
	/**
	 * Used to format Time
	 */
	private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	/**
	 * Used to format Date
	 */
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");
	/**
	 * Used in window title
	 */
	private final static SimpleDateFormat titleDateFormat = new SimpleDateFormat("dd.MM HH:mm");
	/**
	 * Used to format all decimals
	 */
	private final static DecimalFormat numberFormat = new DecimalFormat("##0.0");
	/**
	 * All res id used to set text on a table row
	 */
	private final static int[] TEXT_ROW_IDS = new int[]
	{ R.id.forecast_text_column_1, R.id.forecast_text_column_2, R.id.forecast_text_column_3,
			R.id.forecast_text_column_4, R.id.forecast_text_column_5, R.id.forecast_text_column_6,
			R.id.forecast_text_column_7 };
	/**
	 * All res id used to set images on a table row
	 */
	private final static int[] IMAGE_ROW_IDS = new int[]
	{ R.id.forecast_image_column_1, R.id.forecast_image_column_2, R.id.forecast_image_column_3,
			R.id.forecast_image_column_4, R.id.forecast_image_column_5, R.id.forecast_image_column_6,
			R.id.forecast_image_column_7 };

	private IForecastDAO forecastDAO = null;
	/**
	 * 
	 */
	private final static int UPDATE_SPOT_DIALOG = 500;

	private ProgressDialog updateDialog = null;

	private static UpdateConnection connection = null;

	private final static String IS_PROGRESS_DIALOG_SHOWN = "show_update_dialog";
	private static final int MENU_REFRESH_ID = 1000;

	private int selectedID = -1;

	private Forecast forecast = null;

	private final Handler handler = new Handler()
	{
		@Override
		public void handleMessage(final Message msg)
		{
			Log.d(LOG_TAG, "Recieved Message :" + msg);
			try
			{
				forecast = forecastDAO.getForecast(selectedID);
				fillTable(forecast);
			}
			catch (final DBException e)
			{
				Log.e(LOG_TAG, "Failed to fetch Forecast.", e);
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forecast);

		forecastDAO = DAOFactory.getForecast(getApplicationContext());
		daoManager.addDAO(forecastDAO);

		final Intent intent = getIntent();
		if (!isForecastID(intent))
		{
			Toast.makeText(this, "Missing Forecast ID", Toast.LENGTH_LONG).show();
			return;
		}
		// cache selected id
		selectedID = getForecastID(intent);
		try
		{
			loadForecast(selectedID, savedInstanceState);
		}
		catch (final DBException e)
		{
			Log.e(LOG_TAG, "", e);
		}
	}

	private void loadForecast(final int _selectedID, final Bundle _savedInstanceState) throws DBException
	{
		final Object obj = getLastNonConfigurationInstance();
		if (obj != null)
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Using cached forecast");
			}
			forecast = (Forecast) obj;
			fillTable(forecast);
		}
		//
		if (forecastDAO.isForecastAvailable(selectedID))
		{
			forecast = forecastDAO.getForecast(_selectedID);
			fillTable(forecast);
		}
		else
		{
			if (connection == null)
			{
				// TODO: On second call the selectedID is cached which is wrong and nothing get loaded!
				connection = new UpdateConnection(getApplicationContext(), handler, selectedID);
			}

			if (_savedInstanceState != null)
			{
				final boolean isShown = _savedInstanceState.getBoolean(IS_PROGRESS_DIALOG_SHOWN);
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, IS_PROGRESS_DIALOG_SHOWN + " : "
							+ _savedInstanceState.getBoolean(IS_PROGRESS_DIALOG_SHOWN));
				}
				if (isShown)
				{
					showDialog(UPDATE_SPOT_DIALOG);
				}
			}
			else
			{
				showDialog(UPDATE_SPOT_DIALOG);
			}
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance()
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Saving cached forecast.");
		}

		return forecast;
	}

	@Override
	protected void onStop()
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onStop");
		}

		super.onStop();
	}

	@Override
	protected void onPause()
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onPause");
		}

		super.onPause();
	}

	@Override
	public void onSaveInstanceState(final Bundle outState)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onSaveInstanceState");
		}
		//
		if (updateDialog != null)
		{
			final boolean isShowing = updateDialog.isShowing();
			outState.putBoolean(IS_PROGRESS_DIALOG_SHOWN, isShowing);
			removeDialog(UPDATE_SPOT_DIALOG);
		}
		super.onSaveInstanceState(outState);
	}

	private Dialog createUpdateProgressDialog()
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "createUpdateProgressDialog");
		}
		updateDialog = new ProgressDialog(this);
		updateDialog.setTitle("Updating");
		updateDialog.setMessage("Please wait while loading...");
		updateDialog.setIndeterminate(true);
		updateDialog.setCancelable(true);
		if (connection != null)
		{
			connection.setDialog(updateDialog);
		}
		return updateDialog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(final int _id)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onCreateDialog :" + _id);
		}
		Dialog dialog;
		switch (_id)
		{
			case (UPDATE_SPOT_DIALOG):
				dialog = createUpdateProgressDialog();
				break;
			default:
				dialog = null;
				break;
		}

		return dialog;
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		final MenuItem about = menu.add(Menu.NONE, MENU_REFRESH_ID, Menu.NONE, R.string.forecast_refresh);
		about.setIcon(R.drawable.refresh);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		super.onOptionsItemSelected(item);

		boolean result = false;
		if (item.getItemId() == MENU_REFRESH_ID)
		{
			if (connection == null)
			{
				connection = new UpdateConnection(this, handler, selectedID);
			}
			else
			{
				try
				{
					showDialog(UPDATE_SPOT_DIALOG);
					connection.update(selectedID);
				}
				catch (final RemoteException e)
				{
					Log.e(LOG_TAG, "failed to update by user request", e);
				}
			}
			result = true;
		}
		return result;
	}

	private void fillTable(final Forecast _forecast)
	{
		final String serverTime = titleDateFormat.format(_forecast.getTimestamp());
		setTitle(_forecast.getName() + " update " + serverTime);
		/**
		 * For each row in table we set the values
		 */
		final TableRow rowWindSpeed = (TableRow) findViewById(R.id.forecast_row_wind_speed);
		fillWindSpeedRow(rowWindSpeed, R.string.wind_speed, _forecast);
		final TableRow rowDate = (TableRow) findViewById(R.id.forecast_row_date);
		fillDateRow(rowDate, R.string.date, _forecast);
		final TableRow rowTime = (TableRow) findViewById(R.id.forecast_row_time);
		fillTimeRow(rowTime, R.string.time, _forecast);
		final TableRow rowAirPressure = (TableRow) findViewById(R.id.forecast_row_air_pressure);
		fillAirPressureRow(rowAirPressure, R.string.air_pressure, _forecast);
		final TableRow rowAirTemp = (TableRow) findViewById(R.id.forecast_row_air_temperature);
		fillAirTempRow(rowAirTemp, R.string.air_temperature, _forecast);
		final TableRow rowWaterTemp = (TableRow) findViewById(R.id.forecast_row_water_temperature);
		fillWaterTempRow(rowWaterTemp, R.string.water_temperature, _forecast);
		final TableRow rowWaveHeight = (TableRow) findViewById(R.id.forecast_row_wave_height);
		fillWaveHeightRow(rowWaveHeight, R.string.wave_height, _forecast);
		final TableRow rowWavePeriod = (TableRow) findViewById(R.id.forecast_row_wave_period);
		fillWavePeriodRow(rowWavePeriod, R.string.wave_period, _forecast);
		final TableRow rowWindGust = (TableRow) findViewById(R.id.forecast_row_windgust);
		fillWindGustRow(rowWindGust, R.string.windgust, _forecast);
		final TableRow rowClouds = (TableRow) findViewById(R.id.forecast_row_clouds);
		fillCloudRow(rowClouds, R.string.clouds, _forecast);
		final TableRow rowWindDirection = (TableRow) findViewById(R.id.forecast_row_wind_direction);
		fillWindDirectionRow(rowWindDirection, R.string.wind_direction, _forecast);
		final TableRow rowWaveDirection = (TableRow) findViewById(R.id.forecast_row_wave_direction);
		fillWaveDirectionRow(rowWaveDirection, R.string.wave_direction, _forecast);

		final TableRow rowPrecipitationDirection = (TableRow) findViewById(R.id.forecast_row_precipitation);
		fillPrecipitationRow(rowPrecipitationDirection, R.string.precipitation, _forecast);
	}

	private void fillWindDirectionRow(final TableRow _rowWind, final int _columNameResID, final Forecast _forecast)
	{
		if (_rowWind == null)
		{
			throw new NullPointerException("row");
		}

		final TextView rowName = (TextView) _rowWind.findViewById(R.id.forecast_image_column_name);
		final String text = _rowWind.getResources().getString(_columNameResID);
		rowName.setText(text);
		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < IMAGE_ROW_IDS.length; i++)
		{
			final ImageView iv = (ImageView) _rowWind.findViewById(IMAGE_ROW_IDS[i]);

			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				iv.setImageResource(detail.getWinddirection().getImage());
			}
			else
			{
				iv.setImageDrawable(null);
			}
		}
	}

	private void fillWaveDirectionRow(final TableRow _rowWave, final int _columNameResID, final Forecast _forecast)
	{
		if (_rowWave == null)
		{
			throw new NullPointerException("row");
		}

		final TextView rowName = (TextView) _rowWave.findViewById(R.id.forecast_image_column_name);
		final String text = _rowWave.getResources().getString(_columNameResID);
		rowName.setText(text);
		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < IMAGE_ROW_IDS.length; i++)
		{
			final ImageView iv = (ImageView) _rowWave.findViewById(IMAGE_ROW_IDS[i]);

			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				iv.setImageResource(detail.getWaveDirection().getImage());
			}
			else
			{
				iv.setImageDrawable(null);
			}
		}
	}

	private final static void fillTimeRow(final TableRow _row, final int _columNameResID, final Forecast _forecast)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}
		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text);

		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < TEXT_ROW_IDS.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(TEXT_ROW_IDS[i]);
			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				final Date date = new Date(detail.getTime());
				tv.setText(timeFormat.format(date));
			}
		}
	}

	private final static void fillPrecipitationRow(final TableRow _row, final int _columNameResID,
			final Forecast _forecast)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}
		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < TEXT_ROW_IDS.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(TEXT_ROW_IDS[i]);
			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				tv.setText(numberFormat.format(detail.getPrecipitation().getValue()));

				if (i == 1)
				{
					final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
					final String text = _row.getResources().getString(_columNameResID);
					rowName.setText(text + " (" + detail.getPrecipitation().getMeasure().getShortDisplayName() + ")");
				}

			}
		}
	}

	private final static void fillDateRow(final TableRow _row, final int _columNameResID, final Forecast _forecast)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}
		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text);

		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();
		for (int i = 0; i < TEXT_ROW_IDS.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(TEXT_ROW_IDS[i]);
			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				tv.setText(dateFormat.format(detail.getDate()));
			}
		}
	}

	private final static void fillWaveHeightRow(final TableRow _row, final int _columNameResID, final Forecast _forecast)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < TEXT_ROW_IDS.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(TEXT_ROW_IDS[i]);
			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				tv.setText(numberFormat.format(detail.getWaveHeight().getValue()));
				if (i == 1)
				{
					final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
					final String text = _row.getResources().getString(_columNameResID);
					rowName.setText(text + " (" + detail.getWaveHeight().getMeasure().getShortDisplayName() + ")");
				}
			}
		}
	}

	private final static void fillAirPressureRow(final TableRow _row, final int _columNameResID,
			final Forecast _forecast)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < TEXT_ROW_IDS.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(TEXT_ROW_IDS[i]);
			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				tv.setText(numberFormat.format(detail.getAirPressure().getValue()));
				if (i == 1)
				{
					final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
					final String text = _row.getResources().getString(_columNameResID);
					rowName.setText(text + " (" + detail.getAirPressure().getMeasure().getShortDisplayName() + ")");
				}
			}
		}
	}

	private final static void fillWavePeriodRow(final TableRow _row, final int _columNameResID, final Forecast _forecast)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < TEXT_ROW_IDS.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(TEXT_ROW_IDS[i]);
			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				tv.setText(numberFormat.format(detail.getWavePeriod().getValue()));
				if (i == 1)
				{
					final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
					final String text = _row.getResources().getString(_columNameResID);
					rowName.setText(text + " (" + detail.getWavePeriod().getMeasure().getShortDisplayName() + ")");
				}
			}
		}
	}

	private final static void fillWindSpeedRow(final TableRow _row, final int _columNameResID, final Forecast _forecast)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < TEXT_ROW_IDS.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(TEXT_ROW_IDS[i]);
			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				tv.setText(numberFormat.format(detail.getWindSpeed().getValue()));
				if (i == 1)
				{
					final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
					final String text = _row.getResources().getString(_columNameResID);
					rowName.setText(text + " (" + detail.getWindSpeed().getUnit().getShortDisplayName() + ")");
				}
			}
		}
	}

	private final static void fillWindGustRow(final TableRow _row, final int _columNameResID, final Forecast _forecast)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < TEXT_ROW_IDS.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(TEXT_ROW_IDS[i]);
			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				tv.setText(numberFormat.format(detail.getWindGusts().getValue()));
				if (i == 1)
				{
					final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
					final String text = _row.getResources().getString(_columNameResID);
					rowName.setText(text + " (" + detail.getWindGusts().getUnit().getShortDisplayName() + ")");
				}
			}
		}
	}

	private final static void fillAirTempRow(final TableRow _row, final int _columNameResID, final Forecast _forecast)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < TEXT_ROW_IDS.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(TEXT_ROW_IDS[i]);
			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				tv.setText(numberFormat.format(detail.getAirTemperature().getValue()));
				if (i == 1)
				{
					final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
					final String text = _row.getResources().getString(_columNameResID);
					rowName.setText(text + " (" + detail.getAirTemperature().getMeasure().getShortDisplayName() + ")");
				}
			}
		}
	}

	private final static void fillWaterTempRow(final TableRow _row, final int _columNameResID, final Forecast _forecast)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		//
		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < TEXT_ROW_IDS.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(TEXT_ROW_IDS[i]);
			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				tv.setText(numberFormat.format(detail.getWaterTemperature().getValue()));
				if (i == 1)
				{
					final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
					final String text = _row.getResources().getString(_columNameResID);
					rowName
							.setText(text + " (" + detail.getWaterTemperature().getMeasure().getShortDisplayName()
									+ ")");
				}
			}
		}
	}

	private final static void fillCloudRow(final TableRow _row, final int _columNameResID, final Forecast _forecast)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}
		if (_forecast == null)
		{
			throw new NullPointerException("forecast");
		}

		//
		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_image_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text);

		final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < IMAGE_ROW_IDS.length; i++)
		{
			final ImageView iv = (ImageView) _row.findViewById(IMAGE_ROW_IDS[i]);

			if (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				iv.setImageResource(detail.getClouds().getResId());
			}
			else
			{
				iv.setImageDrawable(null);
			}
		}
	}

	/**
	 * 
	 * @param _intent
	 * @return
	 */
	private static final boolean isForecastID(final Intent _intent)
	{
		final int INVALID_VALUE = -1;

		if (_intent == null)
		{
			return false;
		}

		return _intent.getIntExtra(IntentConstants.STORED_FORECAST_KEY, INVALID_VALUE) != INVALID_VALUE;
	}

	/**
	 * 
	 * @param _intent
	 * @return
	 */
	private static final int getForecastID(final Intent _intent)
	{
		final int INVALID_VALUE = -1;
		if (_intent == null)
		{
			return INVALID_VALUE;
		}
		return _intent.getIntExtra(IntentConstants.STORED_FORECAST_KEY, INVALID_VALUE);
	}

}