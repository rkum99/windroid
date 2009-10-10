package de.macsystems.windroid.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.macsystems.windroid.SpotConfigurationVO;

/**
 * @author mac
 * @version $Id$
 */
public class SelectedDAO implements ISelectedDAO
{
	private final Database database;

	/**
	 * 
	 */
	public SelectedDAO(final Database _database)
	{
		database = _database;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISelectedDAO#fetchAll()
	 */
	@Override
	public Cursor fetchAll()
	{
		final SQLiteDatabase db = database.getReadableDatabase();
		return db.rawQuery("select * from selected", null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISelectedDAO#fetchSize()
	 */
	@Override
	public int fetchSize()
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.db.ISelectedDAO#insertSpot(de.macsystems.windroid
	 * .SpotConfigurationVO)
	 */
	@Override
	public void insertSpot(SpotConfigurationVO _vo)
	{
		throw new UnsupportedOperationException();
	}

}
