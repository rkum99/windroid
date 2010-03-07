package de.macsystems.windroid;

import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class ForecastActivity extends Activity
{

	private final static String LOG_TAG = ForecastActivity.class.getSimpleName();

	private final static int[] TEXT_ROW_IDS = new int[]
	{ R.id.forecast_text_column_1, R.id.forecast_text_column_2, R.id.forecast_text_column_3,
			R.id.forecast_text_column_4, R.id.forecast_text_column_5, R.id.forecast_text_column_6,
			R.id.forecast_text_column_7 };

	private final static int[] IMAGE_ROW_IDS = new int[]
	{ R.id.forecast_image_column_1, R.id.forecast_image_column_2, R.id.forecast_image_column_3,
			R.id.forecast_image_column_4, R.id.forecast_image_column_5, R.id.forecast_image_column_6,
			R.id.forecast_image_column_7 };

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

		final int forecastID = getForecastID(intent);
		Toast.makeText(this, "Showing Forecast for ID " + forecastID, Toast.LENGTH_LONG).show();

		final IForecastDAO dao = DAOFactory.getForecast(this);
		final Forecast forecast = dao.getForecast(forecastID);

		/**
		 * For each row in table we set the values
		 */
		final TableRow rowWindSpeed = (TableRow) findViewById(R.id.forecast_row_wind_speed);
		fillTextRow(rowWindSpeed, R.string.wind_speed);
		final TableRow rowDate = (TableRow) findViewById(R.id.forecast_row_date);
		fillTextRow(rowDate, R.string.date);
		final TableRow rowTime = (TableRow) findViewById(R.id.forecast_row_time);
		fillTextRow(rowTime, R.string.time);
		final TableRow rowAirPressure = (TableRow) findViewById(R.id.forecast_row_air_pressure);
		fillTextRow(rowAirPressure, R.string.air_pressure);
		final TableRow rowAirTemp = (TableRow) findViewById(R.id.forecast_row_air_temperature);
		fillTextRow(rowAirTemp, R.string.air_temperature);
		final TableRow rowWaterTemp = (TableRow) findViewById(R.id.forecast_row_water_temperature);
		fillTextRow(rowWaterTemp, R.string.water_temperature);
		final TableRow rowWaveHeight = (TableRow) findViewById(R.id.forecast_row_wave_height);
		fillTextRow(rowWaveHeight, R.string.wave_height);
		final TableRow rowWavePeriod = (TableRow) findViewById(R.id.forecast_row_wave_period);
		fillTextRow(rowWavePeriod, R.string.wave_period);
		final TableRow rowWindGust = (TableRow) findViewById(R.id.forecast_row_windgust);
		fillTextRow(rowWindGust, R.string.windgust);
		//
		final TableRow rowClouds = (TableRow) findViewById(R.id.forecast_row_clouds);
		fillWindRow(rowClouds, R.string.wave_direction);
		final TableRow rowWindDirection = (TableRow) findViewById(R.id.forecast_row_wind_direction);
		fillCloudRow(rowWindDirection, R.string.wind_direction, forecast);
		final TableRow rowWaveDirection = (TableRow) findViewById(R.id.forecast_row_wave_direction);
		fillCloudRow(rowWaveDirection, R.string.wave_direction, forecast);
	}

	private void fillWindRow(final TableRow rowClouds,final int clouds)
	{
		// TODO Auto-generated method stub

	}

	private final static void fillTextRow(final TableRow _row, final int _columNameResID)
	{
		if (_row == null)
		{
			throw new NullPointerException("row");
		}
		//
		final TextView rowName = (TextView) _row.findViewById(R.id.forecast_text_column_name);
		final String text = _row.getResources().getString(_columNameResID);
		rowName.setText(text);

		for (int i = 0; i < TEXT_ROW_IDS.length; i++)
		{
			final TextView tv = (TextView) _row.findViewById(TEXT_ROW_IDS[i]);
			tv.setText("a text");
		}
	}

	private final static void fillCloudRow(final TableRow _row, int _columNameResID, final Forecast _forecast)
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
				Log.d(LOG_TAG, "Cavok " + detail.getClouds());
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