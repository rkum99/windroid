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

/**
 * Represents an alarm queued in AlarmManager
 * 
 * @author mac
 * @version $Id$
 */
public final class Alert
{

	public final static String RETRYS = "alert.retrys";
	public final static String REPEAT_ID = "alert.repeatid";
	public final static String SELECTED_ID = "alert.selectedid";
	public final static String TIME = "alert.time";
	public final static String WEEKDAY = "alert.weekday";
	public final static String SPOTNAME = "alert.spotname";

	private final static int MAX_RETRYS = 3;

	private final int repeatID;

	private final int selectedID;

	private int retryCounter = 0;

	private final long time;

	private final int dayOfWeek;

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
	 */
	Alert(final String _spotName, final int _selectedID, final int _repeatId, final int _retryCounter,
			final long _time, final int _dayOfWeek)
	{
		selectedID = _selectedID;
		repeatID = _repeatId;
		retryCounter = _retryCounter;
		time = _time;
		dayOfWeek = _dayOfWeek;
		spotName = _spotName;
	}

	/**
	 * Retrieve repeat ID
	 * 
	 * @return
	 */
	public int getRepeatID()
	{
		return repeatID;
	}

	/**
	 * Retrieve selected id
	 * 
	 * @return
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
	 * returns daytime this alert happen first.
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
		return "Alert [dayOfWeek=" + dayOfWeek + ", repeatID=" + repeatID + ", retryCounter=" + retryCounter
				+ ", selectedID=" + selectedID + ", spotName=" + spotName + ", time=" + time + "]";
	}

}
