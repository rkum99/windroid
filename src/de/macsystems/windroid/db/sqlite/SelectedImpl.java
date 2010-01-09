package de.macsystems.windroid.db.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.db.ISelectedDAO;
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
			values.put(COLUMN_NAME, _vo.getStation().getName());
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
			{ "" + _id });
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
			{ "" + _id }, null, null, null);

			if (!c.moveToFirst())
			{
				throw new IllegalArgumentException("_id not found :" + _id);
			}

			final int index = c.getColumnIndexOrThrow(COLUMN_ACTIV);
			result = convertIntToBoolean(c.getInt(index));
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
		try
		{
			final String[] columns = new String[]
			{ COLUMN_ACTIV, COLUMN_SPOTID, COLUMN_NAME, COLUMN_ID, COLUMN_USEDIRECTION, COLUMN_STARTING, COLUMN_TILL,
					COLUMN_WINDMEASURE, COLUMN_MINWIND, COLUMN_MAXWIND };

			c = db.query(tableName, columns, "id=?", new String[]
			{ "" + _id }, null, null, null);

			final int activIndex = c.getColumnIndexOrThrow(COLUMN_ACTIV);
			final int spotIDIndex = c.getColumnIndexOrThrow(COLUMN_SPOTID);
			final int nameIndex = c.getColumnIndexOrThrow(COLUMN_NAME);
			final int spotidIndex = c.getColumnIndexOrThrow(COLUMN_ID);
			final int useDirectionIndex = c.getColumnIndexOrThrow(COLUMN_USEDIRECTION);
			final int startingIndex = c.getColumnIndexOrThrow(COLUMN_STARTING);
			final int tillIndex = c.getColumnIndexOrThrow(COLUMN_TILL);
			final int windmeasureIndex = c.getColumnIndexOrThrow(COLUMN_WINDMEASURE);
			final int minWindIndex = c.getColumnIndexOrThrow(COLUMN_MINWIND);
			final int maxWindIndex = c.getColumnIndexOrThrow(COLUMN_MAXWIND);

			final boolean activ = convertIntToBoolean(c.getInt(activIndex));
			final String spotID = c.getString(spotIDIndex);
			final String name = c.getString(nameIndex);
			final long id = c.getLong(spotidIndex);
			final boolean useDirection = convertIntToBoolean(c.getLong(useDirectionIndex));
			final float starting = c.getFloat(startingIndex);
			final float till = c.getFloat(tillIndex);
			final String windmeasure = c.getString(windmeasureIndex);
			final int minWind = c.getInt(minWindIndex);
			final int maxWind = c.getInt(maxWindIndex);

			final SpotConfigurationVO spotVO = new SpotConfigurationVO();
			spotVO.setActiv(activ);
			spotVO.setUseWindirection(useDirection);
			spotVO.setPreferredWindUnit(WindUnit.getById(windmeasure));
			spotVO.setToDirection(WindDirection.getByDegree(till));
			spotVO.setFromDirection(WindDirection.getByDegree(starting));

			// final Station station = new Station(_name,);

		}
		finally
		{
			IOUtils.close(db);
		}
		return null;
	}
}