package de.macsystems.windroid.db.sqlite;

import java.util.Iterator;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
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

	private final static String LOG_TAG = ForecastImpl.class.getSimpleName();

	/**
	 * @param _database
	 */
	public ForecastImpl(final Database _database, final IProgress _progress)
	{
		super(_database, "forecast", _progress);
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
					db.insert(tableName, null, values);
				}
			}
		}
		finally
		{
			IOUtils.close(db);
		}
	}
}