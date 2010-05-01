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
public final class Preferences extends PreferenceActivity
{
	private final static String LOG_TAG = Preferences.class.getSimpleName();

	private final DAOManger daoManager = new DAOManger();

	private IPreferencesDAO prefDAO = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause()
	{
		// Commit changes to Database
		final Map<String, ?> prefs = Util.getSharedPreferences(this).getAll();
		prefDAO.update(prefs);
		super.onPause();
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
		prefDAO = DAOFactory.getPreferencesDAO(this);
		daoManager.addDAO(prefDAO);
		logSharedPreferences();
		addPreferencesFromResource(R.xml.preferencesdescription);
	}

	/**
	 * 
	 */
	private void logSharedPreferences()
	{

		if (!Logging.isLoggingEnabled())
		{
			return;
		}
		final SharedPreferences pref = Util.getSharedPreferences(this);
		Log.d(LOG_TAG, "SharedPreferences contain " + pref.getAll().size() + " Entry(s).");
		final Iterator<?> keyIter = pref.getAll().keySet().iterator();
		while (keyIter.hasNext())
		{
			final Object o = keyIter.next();
			Log.d(LOG_TAG, "Preference Key:" + o + "=" + pref.getAll().get(o));
		}
	}
}