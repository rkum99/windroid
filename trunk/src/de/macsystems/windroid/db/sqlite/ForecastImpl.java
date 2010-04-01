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
package de.macsystems.windroid.db.sqlite;

import java.util.Iterator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.db.IForecastRelation;
import de.macsystems.windroid.db.ISpotDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.identifyable.CardinalDirection;
import de.macsystems.windroid.identifyable.Cavok;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.Precipitation;
import de.macsystems.windroid.identifyable.Pressure;
import de.macsystems.windroid.identifyable.Temperature;
import de.macsystems.windroid.identifyable.WaveHeight;
import de.macsystems.windroid.identifyable.WavePeriod;
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
	public Forecast getForecast(final int _selectedID)
	{
		final SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try
		{
			final String spotName = getSpotName(_selectedID, db);
			final Forecast forecast = new Forecast(spotName, 1, 77889966L);
			cursor = db.query(RELATION_TABLE, null, "selectedid=?", new String[]
			{ Integer.toString(_selectedID) }, null, null, null);
			moveToFirstOrThrow(cursor);

			do
			{
				final int forcastID = getInt(cursor, COLUMN_FORECAST_ID);
				final ForecastDetail detail = getForecastDetail(forcastID);
				forecast.add(detail);
			}
			while (cursor.moveToNext());

			//
			return forecast;
		}
		finally
		{
			IOUtils.close(cursor);
			IOUtils.close(db);
		}
	}

	private String getSpotName(final int _selectedID, final SQLiteDatabase _db)
	{
		Cursor cursor = null;
		try
		{
			cursor = _db.rawQuery("SELECT name FROM spot as a, selected as b WHERE a.spotid = b.spotid AND b._id=?",
					new String[]
					{ Integer.toString(_selectedID) });
			moveToFirstOrThrow(cursor);

			do
			{
				final String name = getString(cursor, ISpotDAO.COLUMN_NAME);
				return name;
			}
			while (cursor.moveToNext());
		}
		finally
		{
			IOUtils.close(cursor);
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

			final CardinalDirection windDirection = CardinalDirection.getDirection(windDirectionString);
			final CardinalDirection waveDirection = CardinalDirection.getDirection(waveDirectionString);

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
			builder.setWaveDirection(waveDirection);
			builder.setWinddirection(windDirection);

			//
			// TODO: time need to be long/integer in Database!
			builder.setTime(Integer.valueOf(time));
			// TODO: Date need to be long in Database!
			builder.setDate(112233445566L);
			//
			return builder.build();
		}
		finally
		{
			IOUtils.close(cursor);
			IOUtils.close(db);
		}
	}

	@Override
	public void setForecast(final Forecast forecast, final int _selectedID)
	{
		if (forecast == null)
		{
			throw new NullPointerException("forecast");
		}

		final SQLiteDatabase db = getWritableDatabase();
		try
		{
			final StringBuilder relationUpdateBuilder = new StringBuilder(128);

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
				values.put(COLUMN_DATE, detail.getDate());
				values.put(COLUMN_TIME, detail.getTime());
				//
				values.put(COLUMN_PRECIPITATION, detail.getPrecipitation().getValue());
				values.put(COLUMN_PRECIPITATION_UNIT, detail.getPrecipitation().getMeasure().getId());
				//
				values.put(COLUMN_WATER_TEMPERATURE, detail.getWaterTemperature().getValue());
				values.put(COLUMN_WATER_TEMPERATURE_UNIT, detail.getWaterTemperature().getMeasure().getId());
				//
				values.put(COLUMN_WAVE_DIRECTION, detail.getWaveDirection().getId());
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
				final long rowID = db.insert(tableName, null, values);
				// Update Forecast
				relationUpdateBuilder
						.append("replace into forecast_releation (updatefailed,selectedid,forecastid) values (");
				relationUpdateBuilder.append("1,");
				relationUpdateBuilder.append(_selectedID).append(",");
				relationUpdateBuilder.append(rowID);
				relationUpdateBuilder.append(")");
				db.execSQL(relationUpdateBuilder.toString());

				// clear buffer
				relationUpdateBuilder.setLength(0);
			}
		}
		finally
		{
			IOUtils.close(db);
		}
	}
}