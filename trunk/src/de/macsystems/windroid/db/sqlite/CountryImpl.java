package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.macsystems.windroid.db.ICountryDAO;
import de.macsystems.windroid.progress.IProgress;

/**
 * DAO for table 'country'
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class CountryImpl extends BaseImpl implements ICountryDAO
{

	/**
	 * @param database
	 * @param progress
	 */
	public CountryImpl(Database database, IProgress progress)
	{
		super(database, progress);
	}

	/**
	 * @param database
	 */
	public CountryImpl(Database database)
	{
		super(database);
	}

	@Override
	public Cursor fetchAll()
	{
		final SQLiteDatabase db = getDatabase().getReadableDatabase();
		return db.rawQuery("SELECT * from country", null);
	}

	@Override
	public Cursor fetchByContinentID(String id)
	{
		final SQLiteDatabase db = getDatabase().getReadableDatabase();
		return db.rawQuery("SELECT * from country ", null);
	}

	@Override
	public int getSize()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
