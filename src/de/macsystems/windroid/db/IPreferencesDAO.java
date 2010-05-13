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
package de.macsystems.windroid.db;

import java.util.Map;

import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.identifyable.WindUnit;

/**
 * Dao Interface for general application preferences.
 * 
 * @author mac
 * @version $Id$
 */
public interface IPreferencesDAO extends IDAO
{
	/**
	 * {@value #COLUMN_KEY}
	 */
	public final static String COLUMN_KEY = "key";
	/**
	 * {@value #COLUMN_VALUE}
	 */
	public final static String COLUMN_VALUE = "value";
	/**
	 * {@value #KEY_ALARM_TONE}
	 */
	final static String KEY_ALARM_TONE = "alarmtone";
	/**
	 * {@value #KEY_VIBRATE_ON_ALARM} *
	 */
	final static String KEY_VIBRATE_ON_ALARM = "vibrate_on_alarm";
	/**
	 * {@value #KEY_PREFERRED_UNIT} *
	 */
	final static String KEY_PREFERRED_UNIT = "preferred_unit";
	/**
	 * {@value #KEY_UPDATE_WHILE_ROAMING}
	 */
	final static String KEY_UPDATE_WHILE_ROAMING = "update_while_roaming";
	/**
	 * {@value #KEY_LAUNCH_ON_BOOT}
	 */
	final static String KEY_LAUNCH_ON_BOOT = "launch_on_boot";
	/**
	 * {@value #KEY_PREFERRED_CONTINENT}
	 */
	final static String KEY_PREFERRED_CONTINENT = "preferred_continent";
	/**
	 * {@value #KEY_WARN_WHEN_UPDATE_FAILED}c
	 */
	final static String KEY_WARN_WHEN_UPDATE_FAILED = "warn_when_update_failed";
	/**
	 * {@value #KEY_MUSIC_ON_ALARM}
	 */
	final static String KEY_MUSIC_ON_ALARM = "music_on_alarm";
	/**
	 * {@value #KEY_IS_LICENCE_ACCEPTED}
	 */
	final static String KEY_IS_LICENCE_ACCEPTED = "isLicenceAccepted";

	/**
	 * Updates a preference
	 * 
	 * @param _key
	 * @param _value
	 */
	public void update(final String _key, String _value);

	/**
	 * 
	 * persists all entry's into database.
	 * 
	 * @param
	 */
	public void update(final Map<?, ?> sharedPreferences);

	/**
	 * Returns <code>true</code> if network can access the network while
	 * roaming.
	 * 
	 * @return
	 * @throws DBException
	 */
	public boolean useNetworkWhileRoaming() throws DBException;

	/**
	 * 
	 * @return
	 */
	public String getAlarmTone();

	/**
	 * 
	 * @return
	 */
	public boolean vibrateOnAlarm();

	/**
	 * 
	 * @return
	 */
	public boolean launchOnBoot();

	/**
	 * 
	 * @return
	 */
	public boolean warnWhenUpdateFailed();

	/**
	 * 
	 * @return
	 */
	public boolean playMusicOnAlarm();

	/**
	 * 
	 * @return
	 */
	public boolean isLicenceAccepted();

	/**
	 * 
	 * @return
	 */
	public WindUnit getPreferredWindUnit();

	/**
	 * 
	 * @return
	 */
	public Continent getPreferredContinent();

}
