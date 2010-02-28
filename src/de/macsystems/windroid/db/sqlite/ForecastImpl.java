package de.macsystems.windroid.db.sqlite;

import java.util.Iterator;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.identifyable.MeasureValue;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;

/**
 * 
 * DAO for 'Forecast' relation
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class ForecastImpl extends BaseImpl implements IForecastDAO
{

	private final static String FORECAST = "forecast";

	private final static String LOG_TAG = ForecastImpl.class.getSimpleName();

	/**
	 * @param _database
	 */
	public ForecastImpl(final Database _database, final IProgress _progress)
	{
		super(_database, FORECAST, _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IForecast#getForecast(int)
	 */
	@Override
	public Forecast getForecast(final int _forecastID)
	{
		final SQLiteDatabase db = getReadableDatabase();
		try
		{
			return null;
		}
		finally
		{
			IOUtils.close(db);
		}
	}

	@Override
	public void setForecast(final Forecast forecast)
	{
		if (forecast == null)
		{
			throw new NullPointerException("forecast");
		}

		// testInsertForecast();

		final SQLiteDatabase db = getWritableDatabase();
		try
		{
			final Iterator<ForecastDetail> iter = forecast.iterator();
			while (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, "Insert a forecast into Database " + detail.toString());

					final ContentValues values = new ContentValues();
					values.put(COLUMN_AIR_PRESSURE, detail.getAirPressure().getValue());
					values.put(COLUMN_AIR_PRESSURE_UNIT, detail.getAirPressure().getMeasure().getId());
					values.put(COLUMN_AIR_TEMPERATURE, detail.getAirTemperature().getValue());
					values.put(COLUMN_AIR_TEMPERATURE_UNIT, detail.getAirTemperature().getMeasure().getId());
					//
					values.put(COLUMN_CLOUDS, detail.getClouds().getId());
					//
					values.put(COLUMN_DATE, detail.getDate().toString());
					values.put(COLUMN_TIME, detail.getTime());
					//
					values.put(COLUMN_PRECIPITATION, detail.getPrecipitation().getValue());
					values.put(COLUMN_PRECIPITATION_UNIT, detail.getPrecipitation().getMeasure().getId());
					// TODO Precipitation Unit needed.
					//
					values.put(COLUMN_WATER_TEMPERATURE, detail.getWaterTemperature().getValue());
					values.put(COLUMN_WATER_TEMPERATURE_UNIT, detail.getWaterTemperature().getMeasure().getId());
					//
					values.put(COLUMN_WAVE_DIRECTION, detail.getWaterTemperature().getValue());
					//
					values.put(COLUMN_WAVE_HEIGHT, detail.getWaveHeight().getValue());
					values.put(COLUMN_WAVE_HEIGHT_UNIT, detail.getWaveHeight().getMeasure().getId());
					//
					values.put(COLUMN_WAVE_PERIOD, detail.getWavePeriod().getValue());
					values.put(COLUMN_WAVE_PERIOD_UNIT, detail.getWavePeriod().getMeasure().getId());
					//
					values.put(COLUMN_WIND_DIRECTION, detail.getWinddirection().getId());
					//
					values.put(COLUMN_WIND_GUSTS, detail.getWindGusts().getValue());
					values.put(COLUMN_WIND_GUST_UNIT, detail.getWindGusts().getUnit().getId());
					//
					values.put(COLUMN_WIND_SPEED, detail.getWindSpeed().getValue());
					values.put(COLUMN_WIND_SPEED_UNIT, detail.getWindSpeed().getUnit().getId());
					//
					db.insert(tableName, "99", values);
				}
			}
		}
		finally
		{
			IOUtils.close(db);
		}

	}

	/**
	 *CREATE TABLE IF NOT EXISTS forecast (_id INTEGER PRIMARY KEY, date TEXT
	 * NOT NULL, time TEXT NOT NULL, wave_period FLOAT,wave_period_unit TEXT,
	 * wind_direction TEXT NOT NULL, wave_direction TEXT NOT NULL, precipitation
	 * FLOAT, air_pressure FLOAT, air_pressure_unit TEXT, wind_gusts FLOAT,
	 * wind_gusts_unit TEXT, water_temperature FLOAT,water_temperature_unit
	 * TEXT, air_temperature FLOAT, air_temperature_unit TEXT, wave_height
	 * FLOAT, wave_height_unit TEXT, clouds TEXT, wind_speed
	 * FLOAT,wind_speed_unit TEXT);
	 */
	private void testInsertForecast()
	{
		final SQLiteDatabase db = getWritableDatabase();
		try
		{
			final ContentValues values = new ContentValues();
			values.put(COLUMN_AIR_PRESSURE, 10.0f);
			values.put(COLUMN_AIR_PRESSURE_UNIT, "hpa");

			db.insert(tableName, null, values);
		}
		finally
		{
			IOUtils.close(db);
		}

	}

	private void insertValue(ContentValues _v, String _column, MeasureValue _mv)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Updating MeasureValue for Column:" + _column);
		}
		if (_mv.getMeasure() == null)
		{
			_v.put(_column, 0);
		}
		else
		{
			Log.d(LOG_TAG, "MeasureValue for " + _column + " = " + _mv.getValue());
			_v.put(_column, _mv.getValue());
		}
	}

}