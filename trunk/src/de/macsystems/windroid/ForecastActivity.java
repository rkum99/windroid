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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.AndroidRuntimeException;
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
import de.macsystems.windroid.service.IServiceCallbackListener;
import de.macsystems.windroid.service.ISpotService;

/**
 * Displays a Forecast
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
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
	 * Constant for DIALOG
	 */
	private final static int UPDATE_SPOT_DIALOG = 500;
	/**
	 * Constant used by Context Menu
	 */
	private final static int CONTEXT_MENU_REFRESH_ID = 1000;

	private int selectedID = -1;
	/**
	 * This Object is cached to be cached using
	 * {@link #onRetainNonConfigurationInstance()}
	 */
	private Forecast forecast = null;

	private ISpotService service = null;

	private final Handler handler = new Handler()
	{
		@Override
		public void handleMessage(final Message _msg)
		{
			try
			{
				showDialog(UPDATE_SPOT_DIALOG);
				service.update(_msg.what, callbackListener);
			}
			catch (final RemoteException e)
			{
				Log.e(LOG_TAG, "failed to call service", e);
			}
			finally
			{
				removeDialog(UPDATE_SPOT_DIALOG);
			}
		}
	};

	private final ServiceConnection connection = new ServiceConnection()
	{

		@Override
		public void onServiceDisconnected(final ComponentName _name)
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "onServiceConnected");
			}
			service = null;
		}

		@Override
		public void onServiceConnected(final ComponentName _name, final IBinder _service)
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "onServiceConnected");
			}
			service = ISpotService.Stub.asInterface(_service);
		}
	};

	private final IServiceCallbackListener.Stub callbackListener = new IServiceCallbackListener.Stub()
	{

		@Override
		public void onTaskComplete() throws RemoteException
		{
			Forecast aforecast;
			try
			{
				aforecast = forecastDAO.getForecast(selectedID);
				fillTable(aforecast);
			}
			catch (final DBException e)
			{
				Log.e(LOG_TAG, "failed to getForecast", e);
			}
		}

		@Override
		public void onTaskFailed() throws RemoteException
		{
			removeDialog(UPDATE_SPOT_DIALOG);
			Toast.makeText(ForecastActivity.this, ForecastActivity.this
					.getString(R.string.forecast_failed_to_load_forecast), Toast.LENGTH_LONG);
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
			loadForecast(selectedID);
		}
		catch (final DBException e)
		{
			Log.e(LOG_TAG, "Failed to fetch forecast", e);
		}
	}

	@Override
	protected void onResume()
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onResume");
		}
		final boolean isBound = bindService(
				new Intent(IntentConstants.DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION), connection,
				Context.BIND_AUTO_CREATE);

		if (!isBound)
		{
			throw new AndroidRuntimeException("Failed to bind service");
		}

		super.onResume();
	}

	@Override
	protected void onStart()
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onStart");
		}

		super.onStart();
	}

	/**
	 * Checks if Forecast is already loaded by using
	 * {@link #getLastNonConfigurationInstance()} if not we check the Database
	 * and if there is nothing found we insert a message in the handler.
	 * 
	 * @param _selectedID
	 * @throws DBException
	 */
	private void loadForecast(final int _selectedID) throws DBException
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
			handler.sendEmptyMessageDelayed(selectedID, 1000L);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance()
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Saving cached forecast.");
		}

		return forecast;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.DBActivity#onStop()
	 */
	@Override
	protected void onStop()
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onStop");
		}
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause()
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onPause");
		}
		unbindService(connection);
		super.onPause();
	}

	/**
	 * 
	 * @return
	 */
	private Dialog createUpdateProgressDialog()
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "createUpdateProgressDialog");
		}
		final ProgressDialog updateDialog = new ProgressDialog(this);
		updateDialog.setTitle(getString(R.string.forecast_progressdialog_title));
		updateDialog.setMessage(getString(R.string.forecast_progressdialog_message));
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
		final MenuItem about = menu.add(Menu.NONE, CONTEXT_MENU_REFRESH_ID, Menu.NONE,
				R.string.forecast_contextdialog_refresh);
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
		if (item.getItemId() == CONTEXT_MENU_REFRESH_ID)
		{
			handler.sendEmptyMessageDelayed(selectedID, 100L);
			result = true;
		}
		return result;
	}

	private void fillTable(final Forecast _forecast)
	{
		final String serverTime = titleDateFormat.format(_forecast.getTimestamp());
		setTitle(_forecast.getName() + getString(R.string.forecast_title_layout) + serverTime);
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