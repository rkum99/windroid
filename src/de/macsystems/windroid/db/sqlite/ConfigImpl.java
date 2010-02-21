package de.macsystems.windroid.db.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.macsystems.windroid.db.IConfigDAO;
import de.macsystems.windroid.progress.IProgress;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public final class ConfigImpl extends BaseImpl implements IConfigDAO
{

	private static final String CONFIG = "config";

	public final String DB_STATUS = "db_status";

	/**
	 * 
	 * @param _database
	 */
	public ConfigImpl(final Database _database)
	{
		super(_database, CONFIG);
	}

	/**
	 * 
	 * @param _database
	 * @param _progress
	 */
	public ConfigImpl(final Database _database, final IProgress _progress)
	{
		super(_database, CONFIG, _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IConfigDAO#getDatabaseStatus()
	 */
	public String getStatus()
	{
		final SQLiteDatabase db = getDatabase().getReadableDatabase();
		final Cursor cursor = db.rawQuery("SELECT value from internal where id ='" + DB_STATUS + "'", null);
		cursor.moveToFirst();
		db.close();
		return "false";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IConfigDAO#setStatus(java.lang.String)
	 */
	public boolean setStatus(final String _status)
	{
		final SQLiteDatabase db = getDatabase().getWritableDatabase();
		final ContentValues args = new ContentValues();
		args.put(DB_STATUS, _status);
		// Cursor cursor = db.update("internal",args,);
		db.close();
		return false;
	}

}
