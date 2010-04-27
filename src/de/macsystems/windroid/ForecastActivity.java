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
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
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
import de.macsystems.windroid.proxy.SpotServiceConnection;
import de.macsystems.windroid.proxy.UpdateConnection;
import de.macsystems.windroid.service.IServiceCallbackListener;

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

	UpdateConnection connection = null;

	private final static String IS_PROGRESS_DIALOG_SHOWN = "show_update_dialog";

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

		final Intent intent = getIntent();
		if (!isForecastID(intent))
		{
			Toast.makeText(this, "Missing Forecast ID", Toast.LENGTH_LONG).show();
			return;
		}

		if (connection == null)
		{
			connection = new UpdateConnection(getApplicationContext());
		}

		final int selectedID = getForecastID(intent);
		forecastDAO = DAOFactory.getForecast(getApplicationContext());
		daoManager.addDAO(forecastDAO);

		try
		{
			if (forecastDAO.isForecastAvailable(selectedID))
			{
				fillTable(selectedID);
			}
			else
			{
				/**
				 * First time the activity gets called the savedInstanceState is
				 * null!
				 */
				if (savedInstanceState != null)
				{
					Log.d(LOG_TAG, IS_PROGRESS_DIALOG_SHOWN + " : "
							+ savedInstanceState.getBoolean(IS_PROGRESS_DIALOG_SHOWN));
				}
				showDialog(UPDATE_SPOT_DIALOG);
//				try
//				{
//					connection.update(selectedID);
//				}
//				catch (RemoteException e)
//				{
//					Log.e(LOG_TAG, "Failed to call service", e);
//
//				}
			}
		}
		catch (final DBException e)
		{
			Log.e(LOG_TAG, "Failed to determine forecast status for selectedid: " + selectedID, e);
		}
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
	public void onSaveInstanceState(Bundle outState)
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

		outState.putString("ID", "1234567890");
		super.onSaveInstanceState(outState);
	}

	private Dialog createUpdateProgressDialog()
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "createUpdateProgressDialog");
		}
		updateDialog = new ProgressDialog(this);
		updateDialog.setTitle("Indeterminate");
		updateDialog.setMessage("Please wait while loading...");
		updateDialog.setIndeterminate(true);
		updateDialog.setCancelable(true);
		return updateDialog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int _id)
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

	private void fillTable(final int selectedID) throws DBException
	{
		final Forecast forecast = forecastDAO.getForecast(selectedID);
		final String serverTime = titleDateFormat.format(forecast.getTimestamp());
		setTitle(forecast.getName() + " update " + serverTime);
		/**
		 * For each row in table we set the values
		 */
		final TableRow rowWindSpeed = (TableRow) findViewById(R.id.forecast_row_wind_speed);
		fillWindSpeedRow(rowWindSpeed, R.string.wind_speed, forecast);
		final TableRow rowDate = (TableRow) findViewById(R.id.forecast_row_date);
		fillDateRow(rowDate, R.string.date, forecast);
		final TableRow rowTime = (TableRow) findViewById(R.id.forecast_row_time);
		fillTimeRow(rowTime, R.string.time, forecast);
		final TableRow rowAirPressure = (TableRow) findViewById(R.id.forecast_row_air_pressure);
		fillAirPressureRow(rowAirPressure, R.string.air_pressure, forecast);
		final TableRow rowAirTemp = (TableRow) findViewById(R.id.forecast_row_air_temperature);
		fillAirTempRow(rowAirTemp, R.string.air_temperature, forecast);
		final TableRow rowWaterTemp = (TableRow) findViewById(R.id.forecast_row_water_temperature);
		fillWaterTempRow(rowWaterTemp, R.string.water_temperature, forecast);
		final TableRow rowWaveHeight = (TableRow) findViewById(R.id.forecast_row_wave_height);
		fillWaveHeightRow(rowWaveHeight, R.string.wave_height, forecast);
		final TableRow rowWavePeriod = (TableRow) findViewById(R.id.forecast_row_wave_period);
		fillWavePeriodRow(rowWavePeriod, R.string.wave_period, forecast);
		final TableRow rowWindGust = (TableRow) findViewById(R.id.forecast_row_windgust);
		fillWindGustRow(rowWindGust, R.string.windgust, forecast);
		final TableRow rowClouds = (TableRow) findViewById(R.id.forecast_row_clouds);
		fillCloudRow(rowClouds, R.string.clouds, forecast);
		final TableRow rowWindDirection = (TableRow) findViewById(R.id.forecast_row_wind_direction);
		fillWindDirectionRow(rowWindDirection, R.string.wind_direction, forecast);
		final TableRow rowWaveDirection = (TableRow) findViewById(R.id.forecast_row_wave_direction);
		fillWaveDirectionRow(rowWaveDirection, R.string.wave_direction, forecast);

		final TableRow rowPrecipitationDirection = (TableRow) findViewById(R.id.forecast_row_precipitation);
		fillPrecipitationRow(rowPrecipitationDirection, R.string.precipitation, forecast);
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