package de.macsystems.windroid.db.sqlite;

import java.util.Date;
import java.util.Iterator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable.Creator;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.db.IForecastRelation;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.identifyable.Cavok;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.MeasureValue;
import de.macsystems.windroid.identifyable.Precipitation;
import de.macsystems.windroid.identifyable.Pressure;
import de.macsystems.windroid.identifyable.Temperature;
import de.macsystems.windroid.identifyable.WaveHeight;
import de.macsystems.windroid.identifyable.WavePeriod;
import de.macsystems.windroid.identifyable.WindDirection;
import de.macsystems.windroid.identifyable.WindSpeed;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;

/**
 * 
 * DAO for 'Forecast' relation
 * 
 * @author mac
 * @version $Id$
 */
public class ForecastImpl extends BaseImpl implements IForecastDAO, IForecastRelation
{

	private final static String LOG_TAG = ForecastImpl.class.getSimpleName();

	/**
	 * relation table name
	 */
	private final static String RELATION_TABLE = "forecast_releation";

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
		Cursor cursor = null;
		try
		{
			cursor = db.query(RELATION_TABLE, null, "", new String[]
			{ Integer.toString(_forecastID) }, null, null, null);
			moveToFirstOrThrow(cursor);

			do
			{
				final int primaryKey = getInt(cursor, COLUMN_FORECAST_ID);
			}
			while (cursor.moveToNext());

			return null;

		}
		finally
		{
			IOUtils.close(cursor);
			IOUtils.close(db);
		}
	}

	private ForecastDetail getForecastDetail(final int _primaryKey)
	{
		SQLiteDatabase db = null;
		Cursor cursor = null;

		try
		{
			db = getReadableDatabase();

			cursor = db.query(tableName, null, "_id=?", new String[]
			{ Integer.toString(_primaryKey) }, null, null, null);

			moveToFirstOrThrow(cursor);

			final Pressure airPressure = Pressure.create(getFloat(cursor, COLUMN_AIR_PRESSURE), getString(cursor,
					COLUMN_AIR_PRESSURE_UNIT));
			final Temperature airTemperature = Temperature.create(getFloat(cursor, COLUMN_AIR_TEMPERATURE), getString(
					cursor, COLUMN_AIR_TEMPERATURE_UNIT));
			final Temperature waterTemperature = Temperature.create(getFloat(cursor, COLUMN_WATER_TEMPERATURE),
					getString(cursor, COLUMN_WATER_TEMPERATURE_UNIT));
			final Precipitation precipitation = Precipitation.create(getFloat(cursor, COLUMN_PRECIPITATION), getString(
					cursor, COLUMN_PRECIPITATION_UNIT));
			final WaveHeight waveHeight = WaveHeight.create(getFloat(cursor, COLUMN_WAVE_HEIGHT), getString(cursor,
					COLUMN_WAVE_HEIGHT_UNIT));
			final WavePeriod wavePeriod = WavePeriod.create(getFloat(cursor, COLUMN_WAVE_PERIOD), getString(cursor,
					COLUMN_WAVE_PERIOD_UNIT));
			final WindSpeed windSpeed = WindSpeed.create(getFloat(cursor, COLUMN_WIND_SPEED), getString(cursor,
					COLUMN_WIND_SPEED_UNIT));
			final WindSpeed windGuest = WindSpeed.create(getFloat(cursor, COLUMN_WIND_GUSTS), getString(cursor,
					COLUMN_WIND_GUST_UNIT));
			final String waveDirectionString = getString(cursor, COLUMN_WAVE_DIRECTION);
			final String windDirectionString = getString(cursor, COLUMN_WIND_DIRECTION);

			final WindDirection windDirection = getDirection(windDirectionString);
			final WindDirection waveDirection = getDirection(waveDirectionString);

			//
			final String time = getString(cursor, COLUMN_TIME);
			final String date = getString(cursor, COLUMN_DATE);

			final ForecastDetail.Builder builder = new ForecastDetail.Builder("1");
			builder.setAirPressure(airPressure);
			builder.setAirTemperature(airTemperature);
			final String cavokName = getString(cursor, COLUMN_CLOUDS);
			final int index = IdentityUtil.indexOf(cavokName, Cavok.values());
			builder.setClouds(Cavok.values()[index]);
			builder.setPrecipitation(precipitation);
			builder.setWaterTemperature(waterTemperature);
			builder.setWindGusts(windGuest);
			builder.setWindSpeed(windSpeed);
			builder.setWavePeriod(wavePeriod);
			builder.setWaveHeight(waveHeight);
			// TODO Set Wave Direction
//			builder.setWaveDirection(waveDirection);
			builder.setWinddirection(windDirection);

			//
			// TODO: time need to be long/integer in Database!
			builder.setTime(Integer.valueOf(time));
			// TODO: Date need to be long in Database!
			builder.setDate(new Date());
		}
		finally
		{
			IOUtils.close(cursor);
			IOUtils.close(db);
		}
		return null;
	}

	private final static WindDirection getDirection(final String _direction)
	{
		final int index = IdentityUtil.indexOf(_direction, WindDirection.values());
		return WindDirection.values()[index];
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
				}

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
		finally
		{
			IOUtils.close(db);
		}
	}
}