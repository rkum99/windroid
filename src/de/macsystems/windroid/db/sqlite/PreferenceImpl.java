package de.macsystems.windroid.db.sqlite;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.db.IPreferencesDAO;
import de.macsystems.windroid.io.IOUtils;

/**
 * 
 * @author mac
 * @version $Id$
 */
public class PreferenceImpl extends BaseImpl implements IPreferencesDAO
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

	@Override
	public String fetchBy(final String _key)
	{
		String result = null;
		final SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = fetchBy("key", _key);
			cursor.moveToFirst();
			final int index = cursor.getColumnIndexOrThrow("value");
			result = cursor.getString(index);
		}
		catch (final Exception e)
		{
			Log.e(LOG_TAG, "failed to fetch \"" + _key + "\".", e);
		}
		finally
		{
			IOUtils.close(cursor);
			IOUtils.close(db);
		}
		return result;

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
		builder.append("UPDATE preferences SET value='");
		builder.append(_value);
		builder.append("' WHERE key='");
		builder.append(_key);
		builder.append("'");

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

	@Override
	public void update(final Map<?, ?> sharedPreferences)
	{

		if (sharedPreferences == null)
		{
			Log.w(LOG_TAG, "SharedPreferences were null, ignoring it.");
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

				builder.append("UPDATE preferences SET value='");
				builder.append(entry.getValue());
				builder.append("' WHERE key='");
				builder.append(entry.getKey());
				builder.append("'");

				try
				{
					db.execSQL(builder.toString());
					// TODO: uncomment logging for production version
					Log.d(LOG_TAG, "INSERT INTO preferences ('key','value') VALUES ('" + entry.getKey() + "','"
							+ entry.getValue() + "');");
				}
				catch (final Exception e)
				{
					Log.e(LOG_TAG, "Failed to update. SQL was :" + builder.toString(), e);
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
}