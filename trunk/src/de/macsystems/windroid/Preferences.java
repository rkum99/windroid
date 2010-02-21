package de.macsystems.windroid;

import java.util.Iterator;
import java.util.Map;

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
		// Commit changes to Database
		final IPreferencesDAO dao = DAOFactory.getPreferencesDAO(this);
		final Map<String, ?> prefs = Util.getSharedPreferences(this).getAll();
		dao.update(prefs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		logSharedPreferences();
		addPreferencesFromResource(R.xml.preferencesdescription);
	}

	/**
	 * 
	 */
	private void logSharedPreferences()
	{
		final SharedPreferences pref = Util.getSharedPreferences(this);
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "SharedPreferences contain " + pref.getAll().size() + " Entry(s).");
		}
		final Iterator<?> keyIter = pref.getAll().keySet().iterator();
		while (keyIter.hasNext())
		{
			final Object o = keyIter.next();
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Preference Key:" + o + "=" + pref.getAll().get(o));
			}
		}
	}
}