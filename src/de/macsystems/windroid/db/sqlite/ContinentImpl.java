package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.macsystems.windroid.db.IContinentDAO;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.progress.IProgress;

/**
 * DAO for Table Continent
 * 
 * @author Jens Hohl
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class ContinentImpl extends BaseImpl implements IContinentDAO
{
	public ContinentImpl(final Database database)
	{
		super(database);
	}

	/**
	 * 
	 * @param database
	 * @param _progress
	 */
	public ContinentImpl(final Database database, final IProgress _progress)
	{
		super(database, _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IContinentDAO#fetchAll()
	 */
	@Override
	public Cursor fetchAll()
	{
		final SQLiteDatabase db = getDatabase().getReadableDatabase();
		return db.rawQuery("SELECT * FROM CONTINENT", null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IContinentDAO#get(int)
	 */
	@Override
	public Continent get(final int id)
	{
		throw new UnsupportedOperationException();
	}

}
