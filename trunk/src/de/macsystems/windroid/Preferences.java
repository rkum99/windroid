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
import android.preference.RingtonePreference;
import android.provider.Settings.System;
import android.util.Log;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.IPreferencesDAO;
import de.macsystems.windroid.identifyable.WindSpeedConverter;

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

	private final static String RINGTONE_MANAGER_LOOKUP_KEY = "alarmtone";

	private final DAOManger daoManager = new DAOManger();

	private IPreferencesDAO prefDAO = null;

	// private RingtonePreference ringtonePreference = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause()
	{
		// Commit changes to Database

		// ringtonePreference = (RingtonePreference)
		// findPreference(RINGTONE_MANAGER_LOOKUP_KEY);

		final Map<String, ?> prefs = Util.getSharedPreferences(this).getAll();
		prefDAO.update(prefs);
		logSharedPreferences();

		WindSpeedConverter.setPreferredMeasure(prefDAO.getPreferredWindUnit());

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

		final RingtonePreference ringtonePreference = (RingtonePreference) findPreference(RINGTONE_MANAGER_LOOKUP_KEY);
		ringtonePreference.setDefaultValue(System.DEFAULT_NOTIFICATION_URI);
		if (Logging.isEnabled())
		{
			Log.d(LOG_TAG, "DEFAULT_NOTIFICATION_URI : " + System.DEFAULT_NOTIFICATION_URI.toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop()
	{
		daoManager.onStop();
		super.onStop();
	}

	/**
	 * 
	 */
	private void logSharedPreferences()
	{

		if (!Logging.isEnabled())
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