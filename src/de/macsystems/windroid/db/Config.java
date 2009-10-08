package de.macsystems.windroid.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author mac
 * @version $Id$
 */
public class Config
{
	private final Database database;

	public final String DB_STATUS = "db_status";

	public Config(Database _dataDatabase)
	{
		if (_dataDatabase == null)
		{
			throw new NullPointerException("database");
		}
		database = _dataDatabase;
	}

	public String getDatabaseStatus()
	{
		SQLiteDatabase db = database.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT value from internal where id ='" + DB_STATUS + "'", null);
		cursor.moveToFirst();
		db.close();
		return "false";
	}

	public boolean setDatabaseStatus(final String status)
	{
		SQLiteDatabase db = database.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DB_STATUS, status);
		// Cursor cursor = db.update("internal",args,);
		db.close();
		return false;
	}

}
