package de.macsystems.windroid.db.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.db.ISelectedDAO;
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
	public void insertSpot(final SpotConfigurationVO _vo)
	{
		if (_vo == null)
		{
			throw new NullPointerException("SpotConfigurationVO");
		}

		// TODO : At the moment all spots are activ !!!

		// CREATE TABLE IF NOT EXISTS selected (_id INTEGER PRIMARY KEY
		// AUTOINCREMENT, spotid text NOT NULL, activ BOOLEAN, usedirection
		// BOOLEAN, starting TEXT, till TEXT, windmeasure TEXT NOT NULL, minwind
		// INTEGER, maxwind INTEGER);
		final SQLiteDatabase db = getWritableDatabase();
		try
		{
			final ContentValues values = new ContentValues();
			values.put("spotid", _vo.getStation().getId());
			values.put("name", _vo.getStation().getName());
			values.put("activ", true);
			values.put("usedirection", _vo.isUseWindirection());
			values.put("starting", _vo.getFromDirection().getId());
			values.put("till", _vo.getToDirection().getId());
			values.put("windmeasure", _vo.getPreferredWindUnit().name());
			values.put("minwind", _vo.getWindspeedMin());
			values.put("maxwind", _vo.getWindspeedMax());

			final long newID = db.insert(tableName, null, values);
			Log.d("Bla", "New ID = " + newID);
		}
		finally
		{
			IOUtils.close(db);
		}
	}
}