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
package de.macsystems.windroid.alarm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.content.Intent;
import de.macsystems.windroid.identifyable.Repeat;
import de.macsystems.windroid.identifyable.Station;

/**
 * Represents an alarm queued in AlarmManager. This Class is designed to be
 * serialized into an {@link Intent}.
 * 
 * @author mac
 * @version $Id$
 */
public final class Alert
{
	/**
	 * Lookup value
	 */
	public final static String RETRYS = "alert.retrys";
	/**
	 * Lookup value
	 */

	public final static String REPEAT_ID = "alert.repeatid";

	/**
	 * Lookup value
	 */
	public final static String SELECTED_ID = "alert.selectedid";
	/**
	 * Lookup value
	 */

	public final static String TIME = "alert.time";
	/**
	 * Lookup value
	 */

	public final static String WEEKDAY = "alert.weekday";
	/**
	 * Lookup value
	 */

	public final static String SPOTNAME = "alert.spotname";
	/**
	 * Constant which describes how often the alert will be enqueued using
	 * {@link AlarmManager#enqueueRetryAlarm(Alert, android.content.Context)}
	 * before marked as failed/expired.
	 */
	private final static int MAX_RETRYS = 3;
	/**
	 * @see #REPEAT_ID
	 */
	private final int alarmID;
	/**
	 * @see #SELECTED_ID
	 */
	private final int selectedID;
	/**
	 * @see #RETRYS
	 */
	private volatile int retryCounter = 0;
	/**
	 * @see #TIME
	 */
	private final long time;
	/**
	 * @see #WEEKDAY
	 */
	private final int dayOfWeek;
	/**
	 * @see #SPOTNAME
	 */
	private final String spotName;

	/**
	 * 
	 * @param _spotName
	 * @param _selectedID
	 * @param _repeatId
	 * @param _retryCounter
	 * @param _time
	 * @param _dayOfWeek
	 * @see Calendar#DAY_OF_WEEK
	 * @see Repeat#getId()
	 * @see Station#getId()
	 */
	Alert(final String _spotName, final int _selectedID, final int _repeatId, final int _retryCounter,
			final long _time, final int _dayOfWeek)
	{
		selectedID = _selectedID;
		alarmID = _repeatId;
		retryCounter = _retryCounter;
		time = _time;
		dayOfWeek = _dayOfWeek;
		spotName = _spotName;
	}

	/**
	 * ID which is used to create or cancel Alerts using {@link AlarmManager}
	 * 
	 * @return
	 * @see Repeat#getId()
	 */
	public int getAlertID()
	{
		return alarmID;
	}

	/**
	 * Retrieve selected id
	 * 
	 * @return
	 * @see Station#getId()
	 */
	public int getSelectedID()
	{
		return selectedID;
	}

	/**
	 * retry counter for this alarm.
	 * 
	 * @return
	 */
	public int getRetryCounter()
	{
		return retryCounter;
	}

	/**
	 * returns daytime this alert happen first in mills.
	 * 
	 * @return
	 */
	public long getTime()
	{
		return time;
	}

	/**
	 * Returns day of week this alert happen
	 * 
	 * @return
	 * @see Calendar#DAY_OF_WEEK
	 */
	public int getDayOfWeek()
	{
		return dayOfWeek;
	}

	/**
	 * Call this method if you want to re-queue this alert as a retry.
	 * 
	 * @see #isRetry()
	 */
	void incrementRetryCounter()
	{
		retryCounter++;
	}

	/**
	 * Returns <code>true</code> if this alert represents a retry.
	 * 
	 * @return
	 * @see #incrementRetryCounter()
	 */
	public boolean isRetry()
	{
		return retryCounter > 0;
	}

	/**
	 * Returns <code>true</code> if alert is expired (too many retrys).
	 * 
	 * @return
	 */
	public boolean isExpired()
	{
		return retryCounter >= MAX_RETRYS;
	}

	/**
	 * Returns <code>true</code> if no retry are made for this alert.
	 * 
	 * @return
	 */
	public boolean isNormalAlert()
	{
		return retryCounter == 0;
	}

	/**
	 * @return the spotName
	 */
	public String getSpotName()
	{
		return spotName;
	}

	@Override
	public String toString()
	{
		return "Alert [dayOfWeek=" + dayOfWeek + ", repeatID=" + alarmID + ", retryCounter=" + retryCounter
				+ ", selectedID=" + selectedID + ", spotName=" + spotName + ", time=" + time + "]";
	}

	/**
	 * Writes all info needed to recreate an alert from an intent (Serialize).
	 * 
	 * @param _alert
	 * @param _intent
	 * @throws NullPointerException
	 * @see {@link readAlertFormAlarmIntent}
	 */
	public static void writeAlertToIntent(final Alert _alert, final Intent _intent) throws NullPointerException
	{
		if (_intent == null)
		{
			throw new NullPointerException("intent");
		}
		if (_alert == null)
		{
			throw new NullPointerException("alert");
		}
		_intent.putExtra(SPOTNAME, _alert.getSpotName());
		_intent.putExtra(REPEAT_ID, _alert.getAlertID());
		_intent.putExtra(RETRYS, _alert.getRetryCounter());
		_intent.putExtra(SELECTED_ID, _alert.getSelectedID());
		_intent.putExtra(TIME, _alert.getTime());
		_intent.putExtra(WEEKDAY, _alert.getDayOfWeek());
	}

	/**
	 * 
	 * @param _intent
	 * @return
	 * @throws NullPointerException
	 */
	public static boolean isAlertIntent(final Intent _intent) throws NullPointerException
	{
		if (_intent == null)
		{
			throw new NullPointerException("Intent");
		}
		final int NOT_FOUND = -1;
		final int NOT_FOUND_LONG = -1;
		//
		final String spotName = _intent.getStringExtra(SPOTNAME);
		final int selectedID = _intent.getIntExtra(SELECTED_ID, NOT_FOUND);
		final int repeatID = _intent.getIntExtra(REPEAT_ID, NOT_FOUND);
		final int retryCounter = _intent.getIntExtra(RETRYS, NOT_FOUND);
		final long time = _intent.getLongExtra(TIME, NOT_FOUND_LONG);
		final int weekday = _intent.getIntExtra(WEEKDAY, NOT_FOUND);

		final boolean result = (spotName != null || selectedID != NOT_FOUND || repeatID != NOT_FOUND
				|| retryCounter != NOT_FOUND || weekday != NOT_FOUND || time != NOT_FOUND_LONG);
		return result;

	}

	/**
	 * Recreats an <code>Alert</code> from an Intent. (Deserialize)
	 * 
	 * @param _intent
	 * @return
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 * @see Alert#writeAlertToIntent(Alert, Intent)
	 * @see Alert#isAlertIntent(Intent)
	 */
	public static Alert readAlertFormAlarmIntent(final Intent _intent) throws NullPointerException,
			IllegalArgumentException
	{
		if (_intent == null)
		{
			throw new NullPointerException("Intent");
		}
		final int NOT_FOUND = -1;
		final int NOT_FOUND_LONG = -1;
		//
		final String stationName = _intent.getStringExtra(SPOTNAME);
		final int selectedID = _intent.getIntExtra(SELECTED_ID, NOT_FOUND);
		final int repeatID = _intent.getIntExtra(REPEAT_ID, NOT_FOUND);
		final int retryCounter = _intent.getIntExtra(RETRYS, NOT_FOUND);
		final long time = _intent.getLongExtra(TIME, NOT_FOUND_LONG);
		final int weekday = _intent.getIntExtra(WEEKDAY, NOT_FOUND);

		if (stationName == null || selectedID == NOT_FOUND || repeatID == NOT_FOUND || retryCounter == NOT_FOUND
				|| weekday == NOT_FOUND || time == NOT_FOUND_LONG)
		{
			throw new IllegalArgumentException("Intent missing alert entrys! Entrys : stationName=" + stationName
					+ " selectedID=" + selectedID + " repeatID=" + repeatID + " retryCounter=" + retryCounter
					+ " time=" + time + " weekday=" + weekday);
		}

		final Alert alert = new Alert(stationName, selectedID, repeatID, retryCounter, time, weekday);
		return alert;
	}

}
