package de.macsystems.windroid.db.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.db.ISpotDAO;
import de.macsystems.windroid.identifyable.Station;
import de.macsystems.windroid.identifyable.WindDirection;
import de.macsystems.windroid.identifyable.WindUnit;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public class SelectedImpl extends BaseImpl implements ISelectedDAO
{
	/**
	 * 
	 * @param _database
	 */
	public SelectedImpl(final Database _database)
	{
		super(_database, "selected");
	}

	/**
	 * 
	 * @param _database
	 * @param _progress
	 */
	public SelectedImpl(final Database _database, final IProgress _progress)
	{
		super(_database, "selected", _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.db.ISelectedDAO#insertSpot(de.macsystems.windroid
	 * .SpotConfigurationVO)
	 */
	@Override
	public long insertSpot(final SpotConfigurationVO _vo)
	{
		if (_vo == null)
		{
			throw new NullPointerException("SpotConfigurationVO");
		}

		long newID = -1;
		final SQLiteDatabase db = getWritableDatabase();
		try
		{
			final ContentValues values = new ContentValues();
			values.put(COLUMN_SPOTID, _vo.getStation().getId());
			values.put(COLUMN_ACTIV, _vo.isActiv());
			values.put(COLUMN_USEDIRECTION, _vo.isUseWindirection());
			values.put(COLUMN_STARTING, _vo.getFromDirection().getId());
			values.put(COLUMN_TILL, _vo.getToDirection().getId());
			values.put(COLUMN_WINDMEASURE, _vo.getPreferredWindUnit().name());
			values.put(COLUMN_MINWIND, _vo.getWindspeedMin());
			values.put(COLUMN_MAXWIND, _vo.getWindspeedMax());
			//
			newID = db.insert(tableName, null, values);
		}
		finally
		{
			IOUtils.close(db);
		}

		return newID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISelectedDAO#setActiv(long, boolean)
	 */
	public void setActiv(final long _id, final boolean isActiv)
	{
		final SQLiteDatabase db = getWritableDatabase();
		try
		{
			final ContentValues values = new ContentValues();
			values.put(COLUMN_ACTIV, isActiv);

			db.update(tableName, values, "_id=?", new String[]
			{ Long.toString(_id) });
		}
		finally
		{
			IOUtils.close(db);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISelectedDAO#isActiv(long)
	 */
	@Override
	public boolean isActiv(final long _id)
	{
		final SQLiteDatabase db = getReadableDatabase();
		Cursor c = null;
		boolean result = false;
		try
		{
			c = db.query(tableName, null, "_id=?", new String[]
			{ Long.toString(_id) }, null, null, null);

			moveToFirstOrThrow(c);
			result = getBoolean(c, COLUMN_ACTIV);
		}
		finally
		{
			IOUtils.close(c);
			IOUtils.close(db);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISelectedDAO#getSpotConfiguration(int)
	 */
	@Override
	public SpotConfigurationVO getSpotConfiguration(final long _id)
	{
		final SQLiteDatabase db = getReadableDatabase();
		Cursor c = null;

		SpotConfigurationVO spotVO = null;

		try
		{
			final String tempID = getSpotIdByID(_id);

			Log.d("SelectedImpl", "Searched SpotID is :" + tempID);

			c = db.rawQuery("SELECT B.name, B.spotid ,B.keyword, B.report ,B.superforecast, "
					+ "B.forecast, B.statistic, B.wavereport, B.waveforecast, "
					+ "A.activ, A.usedirection, A.starting, A.till, A.windmeasure, "
					+ "A.minwind, A.maxwind FROM selected AS A, spot AS B WHERE B.spotid=? AND A.spotid=?",
					new String[]
					{ tempID, tempID });

			if (!c.moveToFirst())
			{
				throw new IllegalStateException("Cursor ist empty. Id was " + _id + ", spotid was " + tempID);
			}

			final boolean activ = getBoolean(c, COLUMN_ACTIV);
			final String keyword = getString(c, ISpotDAO.COLUMN_KEYWORD);
			final String spotID = getString(c, COLUMN_SPOTID);
			final String name = getString(c, COLUMN_NAME);
			final boolean useDirection = getBoolean(c, COLUMN_USEDIRECTION);

			final boolean superforecast = getBoolean(c, ISpotDAO.COLUMN_SUPERFORECAST);
			final boolean forecast = getBoolean(c, ISpotDAO.COLUMN_FORECAST);
			final boolean report = getBoolean(c, ISpotDAO.COLUMN_REPORT);
			final boolean statistic = getBoolean(c, ISpotDAO.COLUMN_STATISTIC);
			final boolean wavereport = getBoolean(c, ISpotDAO.COLUMN_WAVEREPORT);
			final boolean waveforecast = getBoolean(c, ISpotDAO.COLUMN_WAVEFORECAST);

			final float starting = getFloat(c, COLUMN_STARTING);
			final float till = getFloat(c, COLUMN_TILL);
			final String windmeasure = getString(c, COLUMN_WINDMEASURE);
			final float minWind = getFloat(c, COLUMN_MINWIND);
			final float maxWind = getFloat(c, COLUMN_MAXWIND);

			spotVO = new SpotConfigurationVO();
			spotVO.setActiv(activ);
			spotVO.setUseWindirection(useDirection);
			spotVO.setPreferredWindUnit(WindUnit.getById(windmeasure));
			spotVO.setToDirection(WindDirection.getByDegree(till));
			spotVO.setFromDirection(WindDirection.getByDegree(starting));
			spotVO.setWindspeedMin(minWind);
			spotVO.setWindspeedMin(maxWind);

			final Station station = new Station(name, spotID, keyword, forecast, superforecast, statistic, report,
					wavereport, waveforecast);
			spotVO.setStation(station);
		}
		finally
		{
			IOUtils.close(db);
		}
		return spotVO;
	}

	/**
	 * Returns spotid by its _id.
	 * 
	 * @param _id
	 * @return
	 */
	protected final String getSpotIdByID(final long _id)
	{
		Cursor c = null;
		SQLiteDatabase db = null;
		String spotid = null;
		try
		{
			db = getReadableDatabase();
			c = db.rawQuery("SELECT spotid FROM " + tableName + " WHERE _id=?", new String[]
			{ Long.toString(_id) });

			moveToFirstOrThrow(c);

			spotid = getString(c, COLUMN_SPOTID);
		}
		finally
		{
			IOUtils.close(c);
		}
		return spotid;
	}

	@Override
	public void update(final SpotConfigurationVO _vo)
	{
		if (_vo == null)
		{
			throw new NullPointerException("SpotConfigurationVO");
		}

	}

	@Override
	public Cursor getSpots()
	{
		final SQLiteDatabase db = getReadableDatabase();
		return db.rawQuery("SELECT B._id, A.name, B.windmeasure, B.starting, B.till,B.activ FROM selected as B, "
				+ "spot as A where A.spotid=B.spotid", null);
	}

}