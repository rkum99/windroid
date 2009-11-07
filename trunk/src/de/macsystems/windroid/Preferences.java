package de.macsystems.windroid;

import java.util.Iterator;
import java.util.Map;

import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.IPreferencesDAO;
import de.macsystems.windroid.io.IOUtils;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * Main Preferences Screen of Windroid
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class Preferences extends PreferenceActivity
{
	private final static String LOG_TAG = Preferences.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
		Log.d(LOG_TAG, "onPause");
		// Commit Changes to Database
		final IPreferencesDAO dao = DAOFactory.getPreferencesDAO(this);
		final Map<String, ?> prefs = Util.getSharedPreferences(this).getAll();
		final Iterator<String> iter = prefs.keySet().iterator();
		while (iter.hasNext())
		{
			final String key = iter.next();
			final Object value = prefs.get(key).toString();
			Log.d(LOG_TAG, "updating database from preferences with key " + key + " value = " + value.toString());
			dao.update(key, value.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(LOG_TAG, "onResume");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		Log.d(LOG_TAG, "onCreate");

		final SharedPreferences pref = Util.getSharedPreferences(this);
		Log.i(LOG_TAG, "Preferences contains " + pref.getAll().size() + " Entries.");
		final Iterator<?> keyIter = pref.getAll().keySet().iterator();
		while (keyIter.hasNext())
		{
			final Object o = keyIter.next();
			Log.i(LOG_TAG, "Preference Key:" + o + "=" + pref.getAll().get(o));
		}

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferencesdescription);
	}

}
