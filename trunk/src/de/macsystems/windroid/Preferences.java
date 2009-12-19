package de.macsystems.windroid;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.IPreferencesDAO;

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
		final Iterator<?> iterNew = prefs.entrySet().iterator();
		//
		while (iterNew.hasNext())
		{
			final Entry<String, ?> entry = (Entry<String, ?>) iterNew.next();
			final String key = entry.getKey();
			final Object value = entry.getValue();
			Log.d(LOG_TAG, "updating database from preferences with key " + key + " value = " + value);
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

		logSharedPreferences();

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferencesdescription);
	}

	/**
	 * 
	 */
	private void logSharedPreferences()
	{
		final SharedPreferences pref = Util.getSharedPreferences(this);
		Log.d(LOG_TAG, "SharedPreferences contain " + pref.getAll().size() + " Entries.");
		final Iterator<?> keyIter = pref.getAll().keySet().iterator();
		while (keyIter.hasNext())
		{
			final Object o = keyIter.next();
			Log.d(LOG_TAG, "Preference Key:" + o + "=" + pref.getAll().get(o));
		}
	}

}
