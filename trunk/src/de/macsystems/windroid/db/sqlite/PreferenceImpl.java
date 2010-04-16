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
import java.util.Map;
import java.util.Map.Entry;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.db.IPreferencesDAO;
import de.macsystems.windroid.io.IOUtils;

/**
 * 
 * @author mac
 * @version $Id$
 */
public final class PreferenceImpl extends BaseImpl implements IPreferencesDAO
{
	private final static String LOG_TAG = PreferenceImpl.class.getSimpleName();

	/**
	 * 
	 * @param database
	 */
	public PreferenceImpl(final Database database)
	{
		super(database, "preferences");
	}

	protected static boolean isKeyAvailable(final String _key, final SQLiteDatabase _db)
	{
		Cursor cursor = null;
		boolean returnValue = false;
		try
		{
			cursor = _db.rawQuery("SELECT * FROM PREFERENCES WHERE key=?", new String[]
			{ _key });
			returnValue = cursor.moveToFirst();
		}
		catch (final Exception e)
		{
			Log.e(LOG_TAG, "Failed to update \"" + _key, e);
		}
		finally
		{
			IOUtils.close(cursor);
		}
		return returnValue;
	}

	@Override
	public void update(final String _key, final String _value)
	{
		if (_key == null || _key.length() == 0)
		{
			throw new IllegalArgumentException("invalid key");
		}
		final SQLiteDatabase db = getWritableDatabase();

		try
		{
			if (isKeyAvailable(_key, db))
			{
				Log.d(LOG_TAG, "Found Preference :" + _key);
			}
			else
			{
				Log.d(LOG_TAG, "Preference missing :" + _key);
			}
		}
		catch (final Exception e)
		{
			Log.e(LOG_TAG, "Failed to update \"" + _key + "\" with \"" + _value + "\"", e);
		}

		final StringBuilder builder = new StringBuilder(128);
		builder.append("REPLACE INTO preferences ('key','value') VALUES ('");
		builder.append(_key);
		builder.append("','");
		builder.append(_key);
		builder.append("')");

		try
		{

			db.execSQL(builder.toString());
		}
		catch (final Exception e)
		{
			Log.e(LOG_TAG, "Failed to update \"" + _key + "\" with \"" + _value + "\". SQL was :" + builder.toString(),
					e);
		}
		finally
		{
			IOUtils.close(db);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(final Map<?, ?> sharedPreferences)
	{

		if (sharedPreferences == null)
		{
			throw new NullPointerException("Map");
		}

		SQLiteDatabase db = null;
		try
		{
			db = getWritableDatabase();
			final Iterator<?> iterNew = sharedPreferences.entrySet().iterator();
			//
			final StringBuilder builder = new StringBuilder(128);
			while (iterNew.hasNext())
			{
				final Entry<String, ?> entry = (Entry<String, ?>) iterNew.next();
				try
				{
					builder.append("REPLACE INTO preferences ('key','value') VALUES ('");
					builder.append(entry.getKey());
					builder.append("','");
					builder.append(entry.getValue().toString());
					builder.append("')");

					db.execSQL(builder.toString());
					if (Logging.isLoggingEnabled())
					{
						Log.d(LOG_TAG, "Execute SQL : " + builder.toString());
					}
				}
				catch (final Exception e)
				{
					Log.e(LOG_TAG, "Failed to update. SQL was :" + builder.toString(), e);
					Log.e(LOG_TAG, "Map Key was :" + entry.getKey());
					Log.e(LOG_TAG, "Map Entry was :" + entry.getValue());
				}
				finally
				{
					builder.setLength(0);
				}
			}
		}
		finally
		{
			IOUtils.close(db);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IPreferencesDAO#useNetworkWhileRoaming()
	 */
	@Override
	public boolean useNetworkWhileRoaming()
	{
		boolean result = false;
		final SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.rawQuery("SELECT * FROM PREFERENCES WHERE key=?", new String[]
			{ "update_while_roaming" });

			moveToFirstOrThrow(cursor);
			result = getBoolean(cursor, COLUMN_VALUE);
		}
		catch (final SQLException e)
		{
			Log.e(LOG_TAG, "failed to query roaming:", e);
		}
		finally
		{
			IOUtils.close(cursor);
		}
		return result;
	}
}