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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.IForecastDAO;
import de.macsystems.windroid.db.IForecastRelationDAO;
import de.macsystems.windroid.db.ISelectedDAO;
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
public class ForecastImpl extends BaseImpl implements IForecastDAO, IForecastRelationDAO
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
	 * @see de.macsystems.windroid.db.IForecastRelation#isForecastAvailable(int)
	 */
	public boolean isForecastAvailable(final int _selectedID) throws DBException
	{
		boolean result = false;
		final SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query(RELATION_TABLE, new String[]
			{ IForecastRelationDAO.COLUMN_SELECTED_ID }, "selectedid=?", new String[]
			{ Integer.toString(_selectedID) }, null, null, null);
			result = cursor.moveToFirst();
		}
		catch (final SQLException e)
		{
			throw new DBException(e);
		}
		finally
		{
			IOUtils.close(cursor);
			IOUtils.close(db);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IForecast#getForecast(int)
	 */
	@Override
	public Forecast getForecast(final int _selectedID) throws DBException
	{
		final SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try
		{
			final String spotName = getSpotName(_selectedID, db);
			// TODO: Use Server Timestamp there
			long timeStamp = -1L;
			try
			{
				cursor = db.query("selected", new String[]
				{ ISelectedDAO.COLUMN_LASTUPATE }, "_id=?", new String[]
				{ Integer.toString(_selectedID) }, null, null, null);
				moveToFirstOrThrow(cursor);
				timeStamp = getLong(cursor, ISelectedDAO.COLUMN_LASTUPATE);
			}
			finally
			{
				IOUtils.close(cursor);
			}
			final Forecast forecast = new Forecast(spotName, timeStamp);

			cursor = db.query(RELATION_TABLE, null, "selectedid=?", new String[]
			{ Integer.toString(_selectedID) }, null, null, null);
			moveToFirstOrThrow(cursor);

			do
			{
				final int forcastID = getInt(cursor, COLUMN_FORECAST_ID);
				final ForecastDetail detail = getForecastDetail(forcastID, db);
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

	/**
	 * 
	 * @param _selectedID
	 * @param _db
	 * @return
	 */
	private String getSpotName(final int _selectedID, final SQLiteDatabase _db) throws DBException
	{
		Cursor cursor = null;
		try
		{
			cursor = _db.rawQuery("SELECT name FROM spot AS a, selected AS b WHERE a.spotid = b.spotid AND b._id=?",
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

	private ForecastDetail getForecastDetail(final int _primaryKey, final SQLiteDatabase _db) throws DBException
	{

		if (_db == null)
		{
			throw new NullPointerException("SQLiteDatabase");
		}
		Cursor cursor = null;
		try
		{

			cursor = _db.query(tableName, null, "_id=?", new String[]
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
			final int time = getInt(cursor, COLUMN_TIME);
			final long date = getLong(cursor, COLUMN_DATE);

			final ForecastDetail.Builder builder = new ForecastDetail.Builder();
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
			builder.setWaveDirection(waveDirection);
			builder.setWinddirection(windDirection);

			//
			// TODO: time need to be long/integer in Database!
			builder.setTime(time);
			// TODO: Date need to be long in Database!
			builder.setDate(date);
			//
			return builder.build();
		}
		finally
		{
			IOUtils.close(cursor);
		}
	}

	@Override
	public void updateForecast(final Forecast forecast, final int _selectedID)
	{
		if (forecast == null)
		{
			throw new NullPointerException("forecast");
		}

		final SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();

		try
		{
			final Set<Integer> columnIdsToDelete = getRowsToDelete(db, _selectedID);

			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Found Columns to delete:" + columnIdsToDelete.toString());
			}

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
				relationUpdateBuilder.append("REPLACE INTO forecast_releation (selectedid,forecastid) VALUES (");
				relationUpdateBuilder.append(_selectedID).append(",");
				relationUpdateBuilder.append(rowID).append(")");
				db.execSQL(relationUpdateBuilder.toString());

				// clear buffer
				relationUpdateBuilder.setLength(0);
			}
			// remove old forecast_releation entries, the trigger will do the
			// rest!
			{
				final StringBuilder builder = new StringBuilder(64);
				final Iterator<Integer> deleteIterator = columnIdsToDelete.iterator();
				while (deleteIterator.hasNext())
				{
					builder.append("DELETE FROM forecast_releation WHERE forecastid=");
					builder.append(deleteIterator.next());
					db.execSQL(builder.toString());
					builder.setLength(0);
				}
			}
			{
				// Update last update Flag, mark spot as update not failed
				final ContentValues values = new ContentValues();
				values.put(ISelectedDAO.COLUMN_LASTUPATE, forecast.getTimestamp());
				values.put(ISelectedDAO.COLUMN_UPDATEFAILED, false);
				db.update("selected", values, "_id=?", new String[]
				{ Integer.toString(_selectedID) });
			}

			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			IOUtils.close(db);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.db.IForecastRelation#getRowsToDelete(android.database
	 * .sqlite.SQLiteDatabase, int)
	 */
	public Set<Integer> getRowsToDelete(final SQLiteDatabase _db, final int _selectedID)
	{
		final Set<Integer> columns = new HashSet<Integer>();
		Cursor c = null;
		try
		{

			c = _db.query(RELATION_TABLE, new String[]
			{ COLUMN_FORECAST_ID }, "selectedid=?", new String[]
			{ Integer.toString(_selectedID) }, null, null, null);
			// If nothing found pass back an empty collection
			if (c.moveToFirst())
			{
				do
				{
					columns.add(getInt(c, COLUMN_FORECAST_ID));
				}
				while (c.moveToNext());
			}
		}
		finally
		{
			IOUtils.close(c);
		}
		return columns;
	}
}