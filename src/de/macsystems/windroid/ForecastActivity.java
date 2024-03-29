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
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.identifyable.Measure;
import de.macsystems.windroid.identifyable.TempConverter;
import de.macsystems.windroid.identifyable.WindSpeedConverter;
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
	private final static DecimalFormat numberFormat = new DecimalFormat("##0.#");

	/**
	 * Used to format all decimals
	 */
	private final static DecimalFormat smallNumberFormat = new DecimalFormat("##0");

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
	 * Constant used by Option Menu
	 */
	private final static int OPTION_REFRESH = 1000;
	private final static int OPTION_NEXT = 1100;
	private final static int OPTION_PREVIOUS = 1200;
	private final static int OPTION_LEGEND = 1300;

	// Wind
	private final static int OPTION_GROUP_WIND = 50000;

	private final static int OPTION_GROUP_WIND_SHOW_BEAUFORT = 5000;
	private final static int OPTION_GROUP_WIND_SHOW_KNOTS = 5100;
	private final static int OPTION_GROUP_WIND_SHOW_KMH = 5200;
	private final static int OPTION_GROUP_WIND_SHOW_FPS = 5300;
	private final static int OPTION_GROUP_WIND_SHOW_FTM = 5400;
	private final static int OPTION_GROUP_WIND_SHOW_MMI = 5500;
	private final static int OPTION_GROUP_WIND_SHOW_MPH = 5600;
	private final static int OPTION_GROUP_WIND_SHOW_MPS = 5700;

	// Temp
	private final static int OPTION_GROUP_TEMP = 60000;

	private final static int OPTION_GROUP_TEMP_CELSIUS = 6000;
	private final static int OPTION_GROUP_TEMP_FAHRENHEIT = 6100;

	// Offset Days
	private final static int OFFSET_DAY_ONE = 0;
	private final static int OFFSET_DAY_TWO = 7;

	private int selectedID = -1;
	/**
	 * This Object is cached to be cached using
	 * {@link #onRetainNonConfigurationInstance()}
	 */
	private Forecast forecast = null;

	private ISpotService service = null;

	private ViewFlipper flipper = null;

	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;

	/**
	 * Detector we use to detect fling events
	 */
	private GestureDetector detector = null;
	/**
	 * Handler to send messages to service
	 */
	private final Handler handler = new Handler()
	{
		@Override
		public final void handleMessage(final Message _msg)
		{
			try
			{
				showDialog(UPDATE_SPOT_DIALOG);
				// createUpdateProgressDialog().show();
				service.update(_msg.what, callbackListener);
			}
			catch (final RemoteException e)
			{
				Log.e(LOG_TAG, "failed to call service", e);
			}
		}
	};

	private final ServiceConnection connection = new ServiceConnection()
	{

		@Override
		public final void onServiceDisconnected(final ComponentName _name)
		{
			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "onServiceDisconnected");
			}
			service = null;
		}

		@Override
		public final void onServiceConnected(final ComponentName _name, final IBinder _service)
		{
			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "onServiceConnected");
			}
			service = ISpotService.Stub.asInterface(_service);
		}
	};
	/**
	 * Callback Listener for the Activity
	 */
	private final IServiceCallbackListener.Stub callbackListener = new IServiceCallbackListener.Stub()
	{

		@Override
		public final void onTaskComplete() throws RemoteException
		{
			final Forecast aforecast;
			try
			{
				aforecast = forecastDAO.getForecast(selectedID);
				fillTable(aforecast);
			}
			catch (final DBException e)
			{
				Log.e(LOG_TAG, "failed to getForecast", e);
			}
			finally
			{
				removeDialog(UPDATE_SPOT_DIALOG);
			}
		}

		@Override
		public final void onTaskFailed() throws RemoteException
		{
			removeDialog(UPDATE_SPOT_DIALOG);
			Toast.makeText(ForecastActivity.this,
					ForecastActivity.this.getString(R.string.forecast_failed_to_load_forecast), Toast.LENGTH_LONG)
					.show();
		}
	};

	final SimpleOnGestureListener flingDetector = new GestureDetector.SimpleOnGestureListener()
	{
		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		@Override
		public final boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX,
				final float velocityY)
		{
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
			{
				return false;
			}
			// right to left swipe
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
			{
				flipper.setInAnimation(slideLeftIn);
				flipper.setOutAnimation(slideLeftOut);
				flipper.showNext();
			}
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
			{
				flipper.setInAnimation(slideRightIn);
				flipper.setOutAnimation(slideRightOut);
				flipper.showPrevious();
			}
			return false;
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

		timeFormat.getTimeZone().setRawOffset(0);
		dateFormat.getTimeZone().setRawOffset(0);

		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

		detector = new GestureDetector(this, flingDetector);
		flipper = (ViewFlipper) findViewById(R.id.forecast_flipper);

		final View page1 = findViewById(R.id.forecast_flipper_page_one);
		final View page2 = findViewById(R.id.forecast_flipper_page_two);
		page2.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(final View v, final MotionEvent _event)
			{
				if (detector != null)
				{
					return detector.onTouchEvent(_event);
				}
				return false;
			}
		});
		page1.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(final View v, final MotionEvent _event)
			{
				if (detector != null)
				{
					return detector.onTouchEvent(_event);
				}
				return false;
			}
		});

		forecastDAO = DAOFactory.getForecast(getApplicationContext());
		daoManager.addDAO(forecastDAO);

		final Intent intent = getIntent();
		if (!isForecastID(intent))
		{
			Toast.makeText(this, "Missing Forecast ID!", Toast.LENGTH_LONG).show();
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
		super.onResume();
		if (Logging.isEnabled)
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

	}

	@Override
	protected void onStart()
	{
		super.onStart();
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "onStart");
		}

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
			if (Logging.isEnabled)
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
		if (Logging.isEnabled)
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
		if (Logging.isEnabled)
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
		if (Logging.isEnabled)
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
		if (Logging.isEnabled)
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
		if (Logging.isEnabled)
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
		final MenuItem about = menu.add(Menu.NONE, OPTION_REFRESH, Menu.NONE, R.string.forecast_options_refresh);
		about.setIcon(R.drawable.refresh);
		//
		final MenuItem previous = menu.add(Menu.NONE, OPTION_PREVIOUS, Menu.NONE, R.string.forecast_options_previous);
		previous.setIcon(R.drawable.swipe_back);
		//
		final MenuItem next = menu.add(Menu.NONE, OPTION_NEXT, Menu.NONE, R.string.forecast_options_next);
		next.setIcon(R.drawable.swipe_next);

		menu.add(Menu.NONE, OPTION_LEGEND, Menu.NONE, R.string.forecast_options_legend);
		// Windspeed sub menu
		final SubMenu windspeed = menu.addSubMenu(R.string.forecast_options_windspeed);
		windspeed.add(OPTION_GROUP_WIND, OPTION_GROUP_WIND_SHOW_BEAUFORT, 0, Measure.BEAUFORT.getDescription());
		windspeed.add(OPTION_GROUP_WIND, OPTION_GROUP_WIND_SHOW_KNOTS, 1, Measure.KNOTS.getDescription());
		windspeed.add(OPTION_GROUP_WIND, OPTION_GROUP_WIND_SHOW_KMH, 2, Measure.KMH.getDescription());
		windspeed.add(OPTION_GROUP_WIND, OPTION_GROUP_WIND_SHOW_MPH, 3, Measure.MPH.getDescription());
		windspeed.add(OPTION_GROUP_WIND, OPTION_GROUP_WIND_SHOW_MPS, 4, Measure.MPS.getDescription());
		windspeed.add(OPTION_GROUP_WIND, OPTION_GROUP_WIND_SHOW_FPS, 5, Measure.FPS.getDescription());
		windspeed.add(OPTION_GROUP_WIND, OPTION_GROUP_WIND_SHOW_FTM, 6, Measure.FTM.getDescription());
		windspeed.add(OPTION_GROUP_WIND, OPTION_GROUP_WIND_SHOW_MMI, 7, Measure.MMI.getDescription());
		//
		// Windspeed sub menu
		final SubMenu tempMenu = menu.addSubMenu(getString(R.string.forecast_options_temp));
		tempMenu.add(OPTION_GROUP_TEMP, OPTION_GROUP_TEMP_CELSIUS, 1, Measure.CELSIUS.getDescription());
		tempMenu.add(OPTION_GROUP_TEMP, OPTION_GROUP_TEMP_FAHRENHEIT, 0, Measure.FAHRENHEIT.getDescription());

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
		if (item.getItemId() == OPTION_REFRESH)
		{
			handler.sendEmptyMessageDelayed(selectedID, 100L);
			result = true;
		}
		else if (item.getItemId() == OPTION_NEXT)
		{
			flipper.setInAnimation(slideLeftIn);
			flipper.setOutAnimation(slideLeftOut);
			flipper.showNext();
			result = true;
		}
		else if (item.getItemId() == OPTION_PREVIOUS)
		{
			flipper.setInAnimation(slideRightIn);
			flipper.setOutAnimation(slideRightOut);
			flipper.showPrevious();
			result = true;
		}
		else if (item.getItemId() == OPTION_LEGEND)
		{
			showLegend();
		}
		else if (item.getGroupId() == OPTION_GROUP_WIND)
		{
			handleWindSpeedClick(item);
		}
		else if (item.getGroupId() == OPTION_GROUP_TEMP)
		{
			handleTempClick(item);
		}

		return result;
	}

	private void handleTempClick(final MenuItem _item)
	{
		if (_item.getGroupId() != OPTION_GROUP_TEMP)
		{
			throw new IllegalArgumentException("wrong group id: " + _item.getGroupId());
		}

		final Measure measure;
		switch (_item.getItemId())
		{
			case OPTION_GROUP_TEMP_CELSIUS:
				measure = Measure.CELSIUS;
				break;
			case OPTION_GROUP_TEMP_FAHRENHEIT:
				measure = Measure.FAHRENHEIT;
				break;

			default:
				throw new IllegalArgumentException("unkown measure");

		}
		TempConverter.setPreferredMeasure(measure);
		try
		{
			loadForecast(selectedID);
		}
		catch (final DBException e)
		{
			Toast.makeText(this, "An error occured.", Toast.LENGTH_LONG);
			Log.e(LOG_TAG, "failed to load forcast", e);
		}
	}

	private final void handleWindSpeedClick(final MenuItem _item)
	{
		if (_item.getGroupId() != OPTION_GROUP_WIND)
		{
			throw new IllegalArgumentException("wrong group id: " + _item.getGroupId());
		}
		final Measure measure;
		switch (_item.getItemId())
		{
			case OPTION_GROUP_WIND_SHOW_BEAUFORT:
				Toast.makeText(this, "Beaufort", Toast.LENGTH_LONG).show();
				measure = Measure.BEAUFORT;
				break;
			case OPTION_GROUP_WIND_SHOW_FPS:
				Toast.makeText(this, "Feet per Second", Toast.LENGTH_LONG).show();
				measure = Measure.FPS;
				break;
			case OPTION_GROUP_WIND_SHOW_FTM:
				Toast.makeText(this, "Feet per minute", Toast.LENGTH_LONG).show();
				measure = Measure.FTM;
				break;
			case OPTION_GROUP_WIND_SHOW_KMH:
				Toast.makeText(this, "Kilometer per hour", Toast.LENGTH_LONG).show();
				measure = Measure.KMH;
				break;
			case OPTION_GROUP_WIND_SHOW_KNOTS:
				Toast.makeText(this, "Knot", Toast.LENGTH_LONG).show();
				measure = Measure.KNOTS;
				break;
			case OPTION_GROUP_WIND_SHOW_MMI:
				Toast.makeText(this, "meter per minute", Toast.LENGTH_LONG).show();
				measure = Measure.MMI;
				break;
			case OPTION_GROUP_WIND_SHOW_MPH:
				Toast.makeText(this, "Miles per hour", Toast.LENGTH_LONG).show();
				measure = Measure.MPH;
				break;
			case OPTION_GROUP_WIND_SHOW_MPS:
				Toast.makeText(this, "meter per second", Toast.LENGTH_LONG).show();
				measure = Measure.MPS;
				break;
			default:
				throw new IllegalArgumentException("unkown measure");

		}
		WindSpeedConverter.setPreferredMeasure(measure);
		try
		{
			loadForecast(selectedID);
		}
		catch (final DBException e)
		{
			Toast.makeText(this, "An error occured.", Toast.LENGTH_LONG);
			Log.e(LOG_TAG, "failed to load forcast", e);
		}
	}

	private void showLegend()
	{
		final Dialog alertDialog = new Dialog(this);
		alertDialog.setTitle(getString(R.string.forecast_dialog_legend_title));
		alertDialog.setContentView(R.layout.legend);
		alertDialog.show();

	}

	@Override
	public boolean onTouchEvent(final MotionEvent event)
	{
		if (detector.onTouchEvent(event))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private void fillTable(final Forecast _forecast)
	{
		final String serverTime = titleDateFormat.format(_forecast.getTimestamp());

		setTitle(_forecast.getName() + getString(R.string.forecast_title_layout) + serverTime);
		/**
		 * For each row in table we set the values
		 */
		final TableRow rowWindSpeed = (TableRow) findViewById(R.id.forecast_row_wind_speed);
		fillWindSpeedRow(rowWindSpeed, R.string.wind_speed, _forecast, TEXT_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowWindSpeed2 = (TableRow) findViewById(R.id.forecast_page_two_row_wind_speed);
		fillWindSpeedRow(rowWindSpeed2, R.string.wind_speed, _forecast, TEXT_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowDate = (TableRow) findViewById(R.id.forecast_row_date);
		fillDateRow(rowDate, R.string.date, _forecast, TEXT_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowDate2 = (TableRow) findViewById(R.id.forecast_page_two_row_date);
		fillDateRow(rowDate2, R.string.date, _forecast, TEXT_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowTime = (TableRow) findViewById(R.id.forecast_row_time);
		fillTimeRow(rowTime, R.string.time, _forecast, TEXT_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowTime2 = (TableRow) findViewById(R.id.forecast_page_two_row_time);
		fillTimeRow(rowTime2, R.string.time, _forecast, TEXT_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowAirPressure = (TableRow) findViewById(R.id.forecast_row_air_pressure);
		fillAirPressureRow(rowAirPressure, R.string.air_pressure, _forecast, TEXT_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowAirPressure2 = (TableRow) findViewById(R.id.forecast_page_two_row_air_pressure);
		fillAirPressureRow(rowAirPressure2, R.string.air_pressure, _forecast, TEXT_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowAirTemp = (TableRow) findViewById(R.id.forecast_row_air_temperature);
		fillAirTempRow(rowAirTemp, R.string.air_temperature, _forecast, TEXT_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowAirTemp2 = (TableRow) findViewById(R.id.forecast_row_page_two_air_temperature);
		fillAirTempRow(rowAirTemp2, R.string.air_temperature, _forecast, TEXT_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowWaterTemp = (TableRow) findViewById(R.id.forecast_row_water_temperature);
		fillWaterTempRow(rowWaterTemp, R.string.water_temperature, _forecast, TEXT_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowWaterTemp2 = (TableRow) findViewById(R.id.forecast_page_two_row_water_temperature);
		fillWaterTempRow(rowWaterTemp2, R.string.water_temperature, _forecast, TEXT_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowWaveHeight = (TableRow) findViewById(R.id.forecast_row_wave_height);
		fillWaveHeightRow(rowWaveHeight, R.string.wave_height, _forecast, TEXT_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowWaveHeight2 = (TableRow) findViewById(R.id.forecast_page_two_row_wave_height);
		fillWaveHeightRow(rowWaveHeight2, R.string.wave_height, _forecast, TEXT_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowWavePeriod = (TableRow) findViewById(R.id.forecast_row_wave_period);
		fillWavePeriodRow(rowWavePeriod, R.string.wave_period, _forecast, TEXT_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowWavePeriod2 = (TableRow) findViewById(R.id.forecast_page_two_row_wave_period);
		fillWavePeriodRow(rowWavePeriod2, R.string.wave_period, _forecast, TEXT_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowWindGust = (TableRow) findViewById(R.id.forecast_row_windgust);
		fillWindGustRow(rowWindGust, R.string.windgust, _forecast, TEXT_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowWindGust2 = (TableRow) findViewById(R.id.forecast_page_two_row_windgust);
		fillWindGustRow(rowWindGust2, R.string.windgust, _forecast, TEXT_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowClouds = (TableRow) findViewById(R.id.forecast_row_clouds);
		fillCloudRow(rowClouds, R.string.clouds, _forecast, IMAGE_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowClouds2 = (TableRow) findViewById(R.id.forecast_page_two_row_clouds);
		fillCloudRow(rowClouds2, R.string.clouds, _forecast, IMAGE_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowWindDirection = (TableRow) findViewById(R.id.forecast_row_wind_direction);
		fillWindDirectionRow(rowWindDirection, R.string.wind_direction, _forecast, IMAGE_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowWindDirection2 = (TableRow) findViewById(R.id.forecast_page_two_row_wind_direction);
		fillWindDirectionRow(rowWindDirection2, R.string.wind_direction, _forecast, IMAGE_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowWaveDirection = (TableRow) findViewById(R.id.forecast_row_wave_direction);
		fillWaveDirectionRow(rowWaveDirection, R.string.wave_direction, _forecast, IMAGE_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowWaveDirection2 = (TableRow) findViewById(R.id.forecast_page_two_row_wave_direction);
		fillWaveDirectionRow(rowWaveDirection2, R.string.wave_direction, _forecast, IMAGE_ROW_IDS, OFFSET_DAY_TWO);
		//
		final TableRow rowPrecipitationDirection = (TableRow) findViewById(R.id.forecast_row_precipitation);
		fillPrecipitationRow(rowPrecipitationDirection, R.string.precipitation, _forecast, TEXT_ROW_IDS, OFFSET_DAY_ONE);
		final TableRow rowPrecipitationDirection2 = (TableRow) findViewById(R.id.forecast_page_two_row_precipitation);
		fillPrecipitationRow(rowPrecipitationDirection2, R.string.precipitation, _forecast, TEXT_ROW_IDS,
				OFFSET_DAY_TWO);

	}

	private void fillWindDirectionRow(final TableRow _rowWind, final int _columNameResID, final Forecast _forecast,
			final int[] _rowIDs, final int _offset)
	{
		if (_rowWind == null)
		{
			throw new NullPointerException("row");
		}

		final TextView rowName = (TextView) _rowWind.findViewById(R.id.forecast_image_column_name);
		final String text = _rowWind.getResources().getString(_columNameResID);
		rowName.setText(text);
		//
		for (int i = 0; i < _rowIDs.length; i++)
		{
			final ImageView iv = (ImageView) _rowWind.findViewById(IMAGE_ROW_IDS[i]);
			final ForecastDetail detail = _forecast.get(_offset + i);
			iv.setImageResource(detail.getWinddirection().getImage());
		}
	}

	private void fillWaveDirectionRow(final TableRow _rowWave, final int _columNameResID, final Forecast _forecast,
			final int[] _rowIDs, final int _offset)
	{
		if (_rowWave == null)
		{
			throw new NullPointerException("row");
		}

		final TextView rowName = (TextView) _rowWave.findViewById(R.id.forecast_image_column_name);
		final String text = _rowWave.getResources().getString(_columNameResID);
		rowName.setText(text);
		//

		for (int i = 0; i < _rowIDs.length; i++)
		{
			final ImageView iv = (ImageView) _rowWave.findViewById(_rowIDs[i]);

			final ForecastDetail detail = _forecast.get(_offset + i);
			iv.setImageResource(detail.getWaveDirection().getImage());
		}
	}

	private final static void fillTimeRow(final TableRow _row, final int _columNameResID, final Forecast _forecast,
			final int[] _rowIDs, final int _offset)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}
		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text);

		//

		final Date aDate = new Date();

		for (int i = 0; i < _rowIDs.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(_rowIDs[i]);
			{
				final ForecastDetail detail = _forecast.get(_offset + i);
				aDate.setTime(detail.getTime());
				tv.setText(timeFormat.format(aDate));
			}
		}
	}

	private final static void fillPrecipitationRow(final TableRow _row, final int _columNameResID,
			final Forecast _forecast, final int[] _rowIDs, final int _offset)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}
		//
		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text + " (" + _forecast.get(0).getPrecipitation().getMeasure().getShortDisplayName() + ")");

		for (int i = 0; i < _rowIDs.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(_rowIDs[i]);

			final ForecastDetail detail = _forecast.get(_offset + i);
			tv.setText(numberFormat.format(detail.getPrecipitation().getValue()));
		}
	}

	private final static void fillDateRow(final TableRow _row, final int _columNameResID, final Forecast _forecast,
			final int[] rowIDs, final int _offset)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}
		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text);

		//
		// final Iterator<ForecastDetail> iter = _forecast.iterator();

		final Date aDate = new Date();

		for (int i = 0; i < rowIDs.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(rowIDs[i]);
			{
				final ForecastDetail detail = _forecast.get(_offset + i);
				aDate.setTime(detail.getDate() + (1000L * 60L));
				// Log.d(LOG_TAG, "Date " + aDate.toString());
				tv.setText(dateFormat.format(aDate));
			}
		}
	}

	private final static void fillWaveHeightRow(final TableRow _row, final int _columNameResID,
			final Forecast _forecast, final int[] _rowIDs, final int _offset)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text + " (" + _forecast.get(0).getWaveHeight().getMeasure().getShortDisplayName() + ")");

		//
		for (int i = 0; i < _rowIDs.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(_rowIDs[i]);
			final ForecastDetail detail = _forecast.get(_offset + i);
			tv.setText(numberFormat.format(detail.getWaveHeight().getValue()));
		}
	}

	private final static void fillAirPressureRow(final TableRow _row, final int _columNameResID,
			final Forecast _forecast, final int[] _rowIDs, final int _offset)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text + " (" + _forecast.get(0).getAirPressure().getMeasure().getShortDisplayName() + ")");
		//
		for (int i = 0; i < _rowIDs.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(_rowIDs[i]);
			final ForecastDetail detail = _forecast.get(_offset + i);
			tv.setText(smallNumberFormat.format(detail.getAirPressure().getValue()));
		}
	}

	private final static void fillWavePeriodRow(final TableRow _row, final int _columNameResID,
			final Forecast _forecast, final int[] _rowIDs, final int _offset)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		//
		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text + " (" + _forecast.get(0).getWavePeriod().getMeasure().getShortDisplayName() + ")");

		for (int i = 0; i < _rowIDs.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(_rowIDs[i]);
			final ForecastDetail detail = _forecast.get(_offset + i);
			tv.setText(numberFormat.format(detail.getWavePeriod().getValue()));
		}
	}

	private final static void fillWindSpeedRow(final TableRow _row, final int _columNameResID,
			final Forecast _forecast, final int[] _rowIDs, final int offset)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text + " (" + _forecast.get(0).getWindSpeed().getMeasure().getShortDisplayName() + ")");

		//
		// final Iterator<ForecastDetail> iter = _forecast.iterator();

		for (int i = 0; i < _rowIDs.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(_rowIDs[i]);

			final ForecastDetail detail = _forecast.get(offset + i);
			{
				tv.setText(numberFormat.format(detail.getWindSpeed().getValue()));
			}
		}
	}

	private final static void fillWindGustRow(final TableRow _row, final int _columNameResID, final Forecast _forecast,
			final int[] _rowIDs, final int _offset)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		//
		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text + " (" + _forecast.get(0).getWindGusts().getMeasure().getShortDisplayName() + ")");

		for (int i = 0; i < _rowIDs.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(_rowIDs[i]);
			final ForecastDetail detail = _forecast.get(_offset + i);
			tv.setText(smallNumberFormat.format(detail.getWindGusts().getValue()));
		}
	}

	private final static void fillAirTempRow(final TableRow _row, final int _columNameResID, final Forecast _forecast,
			final int[] _rowIDs, final int _offset)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text + " (" + _forecast.get(0).getAirTemperature().getMeasure().getShortDisplayName() + ")");
		//
		for (int i = 0; i < _rowIDs.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(_rowIDs[i]);
			final ForecastDetail detail = _forecast.get(_offset + i);
			tv.setText(smallNumberFormat.format(detail.getAirTemperature().getValue()));

		}
	}

	private final static void fillWaterTempRow(final TableRow _row, final int _columNameResID,
			final Forecast _forecast, final int[] _rowIDs, final int _offset)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}

		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text + " (" + _forecast.get(0).getWaterTemperature().getMeasure().getShortDisplayName() + ")");

		//

		for (int i = 0; i < _rowIDs.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(_rowIDs[i]);
			final ForecastDetail detail = _forecast.get(_offset + i);
			tv.setText(smallNumberFormat.format(detail.getWaterTemperature().getValue()));
		}
	}

	private final static void fillCloudRow(final TableRow _row, final int _columNameResID, final Forecast _forecast,
			final int[] _rowIDs, final int _offset)
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

		for (int i = 0; i < _rowIDs.length; i++)
		{
			final ImageView iv = (ImageView) _row.findViewById(_rowIDs[i]);
			final ForecastDetail detail = _forecast.get(_offset + i);
			final long time = detail.getTime();
			iv.setImageResource(detail.getClouds().getResIDByTime(time));
		}
	}

	/**
	 * 
	 * @param _intent
	 * @return
	 * @see #getForecastID(Intent)
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
	 * @return the id or -1 if not found or <code>Intent</code> was
	 *         <code>null</code>.
	 * @see #isForecastID(Intent)
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