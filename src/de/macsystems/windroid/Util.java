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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.Log;
import de.macsystems.windroid.alarm.Alert;
import de.macsystems.windroid.common.PrefConstants;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.identifyable.Measure;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class Util
{

	private final static String LOG_TAG = Util.class.getSimpleName();

	private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	/**
	 * Used for debug output
	 */
	public final static Map<Integer, String> dayMap = new HashMap<Integer, String>();

	static
	{
		dayMap.put(Calendar.MONDAY, "Monday");
		dayMap.put(Calendar.TUESDAY, "Tuesday");
		dayMap.put(Calendar.WEDNESDAY, "Wednesday");
		dayMap.put(Calendar.THURSDAY, "Thusday");
		dayMap.put(Calendar.FRIDAY, "Friday");
		dayMap.put(Calendar.SATURDAY, "Saturday");
		dayMap.put(Calendar.SUNDAY, "Sunday");
	}

	private Util()
	{
	}

	/**
	 * {@value #DEFAULT_WIND_UNIT}
	 */
	private final static String DEFAULT_WIND_UNIT = Measure.KNOTS.getId();
	/**
	 * {@value #DEFAULT_CONTINENT}
	 */
	private final static String DEFAULT_CONTINENT = "Europe";
	/**
	 * Start Application when boot completed {@value #DEFAULT_LAUNCH_ON_BOOT}
	 */
	private final static boolean DEFAULT_LAUNCH_ON_BOOT = true;
	/**
	 * Update when roaming activated
	 */
	private final static boolean DEFAULT_UPDATE_WHILE_ROAMING = false;

	private final static boolean DEFAUL_LICENCE_ACCEPTED = false;

	private final static String LICENCE_ACCEPTED = "isLicenceAccepted";

	/**
	 * Returns the <code>SharedPreferences</code> of given
	 * <code>ContextWrapper</code>.
	 * 
	 * @param wrapper
	 * @return
	 * @throws NullPointerException
	 *             if context is null
	 * 
	 */
	public static final SharedPreferences getSharedPreferences(final ContextWrapper wrapper)
	{
		if (wrapper == null)
		{
			throw new NullPointerException("ContextWrapper is null.");
		}
		return wrapper.getSharedPreferences(wrapper.getPackageName() + "_preferences", Context.MODE_PRIVATE);
	}

	/**
	 * 
	 * @param _context
	 * @return
	 * @throws NullPointerException
	 *             if context is null.
	 */
	public static final SharedPreferences getSharedPreferences(final Context _context) throws NullPointerException
	{
		if (_context == null)
		{
			throw new NullPointerException("Context is null.");
		}
		return _context.getSharedPreferences(_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
	}

	/**
	 * Returns ID of selected Wind Unit from <code>SharedPreferences</code>. If
	 * no selection was made the default Value will returned.
	 * 
	 * @param _pref
	 * @return
	 * @see #DEFAULT_WIND_UNIT
	 * @see Measure
	 */

	public final static String getSelectedUnitID(final SharedPreferences _pref)
	{
		return _pref.getString(PrefConstants.PREFERRED_WIND_UNIT_ID, DEFAULT_WIND_UNIT);
	}

	/**
	 * Returns ID of selected Continent from <code>SharedPreferences</code>. If
	 * no selection was made the default Value will returned.
	 * 
	 * @param _pref
	 * @return
	 * @see #DEFAULT_CONTINENT
	 * @see Continent
	 */
	public final static String getSelectedContinentID(final SharedPreferences _pref)
	{
		return _pref.getString(PrefConstants.PREFERRED_CONTINENT_ID, DEFAULT_CONTINENT);
	}

	/**
	 * Returns default values which indicates that application should launch
	 * after system boot completed.
	 * 
	 * @param _pref
	 * @return
	 * @see #DEFAULT_LAUNCH_ON_BOOT
	 */
	public final static boolean isLaunchOnBoot(final SharedPreferences _pref)
	{
		return _pref.getBoolean(PrefConstants.LAUNCH_ON_BOOT, DEFAULT_LAUNCH_ON_BOOT);
	}

	/**
	 * Returns <code>true</code> when user already accepts the licence.
	 * 
	 * @param _context
	 * @return
	 */
	public final static boolean isLicenceAccepted(final Context _context)
	{
		final SharedPreferences prefs = getSharedPreferences(_context);
		return prefs.getBoolean(LICENCE_ACCEPTED, DEFAUL_LICENCE_ACCEPTED);
	}

	/**
	 * Sets the status of end user for licence.
	 * 
	 */
	public synchronized final static void setLicenceAccepted(final Context _context, final boolean _accepted)
	{
		final SharedPreferences prefs = getSharedPreferences(_context);
		final Editor editor = prefs.edit();
		try
		{
			editor.putBoolean(LICENCE_ACCEPTED, _accepted);
		}
		finally
		{
			editor.commit();
		}
	}

	/**
	 * Returns default values which indicates that application should launch
	 * after system boot completed.
	 * 
	 * @param _pref
	 * @return
	 * @see #DEFAULT_LAUNCH_ON_BOOT
	 */
	public final static boolean isUpdateWhileRoaming(final SharedPreferences _pref)
	{
		return _pref.getBoolean(PrefConstants.UPDATE_WHILE_ROAMING, DEFAULT_UPDATE_WHILE_ROAMING);
	}

	/**
	 * Returns a Throwable as a String, if Throwable is null the String "null"
	 * will be returned.
	 * 
	 * @param aThrowable
	 * @return
	 */
	public static String getStackTrace(final Throwable aThrowable)
	{
		if (aThrowable == null)
		{
			return "null";
		}
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	/**
	 * Debug Method which prints column names at index. If Cursor is null method
	 * will do nothing.
	 * 
	 * @param _cursor
	 * @see Logging#isEnabled()
	 */
	public final static void printCursorColumnNames(final Cursor _cursor)
	{
		if (!Logging.isEnabled)
		{
			return;
		}
		if (_cursor == null)
		{
			return;
		}

		final String[] names = _cursor.getColumnNames();
		for (int i = 0; i < names.length; i++)
		{
			Log.d(LOG_TAG, "Column at index :" + i + "=" + names[i]);
		}
	}

	/**
	 * Checks if result code is valid, else a {@link IllegalArgumentException}
	 * will be thrown.
	 * 
	 * @param _resultCode
	 * @throws IllegalArgumentException
	 * @see Activity#RESULT_OK
	 * @see Activity#RESULT_CANCELED
	 */
	public static void checkResultCode(final int _resultCode) throws IllegalArgumentException
	{
		if (_resultCode != Activity.RESULT_OK && _resultCode != Activity.RESULT_CANCELED)
		{
			throw new IllegalArgumentException("ResultCode not valid. Must be RESULT_OK or RESULT_CANCELED but was "
					+ _resultCode);
		}
	}

	/**
	 * Returns amount of days to roll to get to next Weekday depending on given
	 * time (mill).
	 * 
	 * @param _now
	 * @param dayInWeek
	 * @return
	 * @see {@link Calendar.DAY_OF_YEAR}
	 */
	public static int getDayToRoll(final long _now, final int dayInWeek)
	{
		if (dayInWeek < 0 || dayInWeek > 7)
		{
			throw new IllegalArgumentException("Days not in range");
		}
		final Calendar now = new GregorianCalendar();
		now.setTimeInMillis(_now);
		final int dayNow = (now.get(Calendar.DAY_OF_WEEK) - 1);
		//
		final Calendar future = Calendar.getInstance();
		future.setTimeInMillis(_now);
		future.set(Calendar.DAY_OF_WEEK, dayInWeek);

		final int dayFuture = (future.get(Calendar.DAY_OF_WEEK) - 1);
		final int daysToRoll;
		if (dayNow < dayFuture)
		{
			daysToRoll = dayFuture - dayNow;
		}
		else if (dayNow > dayFuture)
		{
			daysToRoll = 7 - (dayNow - dayFuture);
		}
		else
		{
			daysToRoll = 0;
		}

		now.roll(Calendar.DAY_OF_YEAR, daysToRoll);
		return daysToRoll;
	}

	/**
	 * Checks if day of week is valid.
	 * 
	 * @param _day
	 * @return
	 * @see Calendar
	 */
	public static boolean isValidDayOfWeek(final int _day)
	{
		return (_day >= Calendar.SUNDAY && _day <= Calendar.SATURDAY) ? true : false;
	}

	/**
	 * Logs Display Metrics using Log if Logging is enabled.
	 * 
	 * @param _activity
	 *            needed to read display settings from activity.
	 * @throws NullPointerException
	 * @see {@link Logging#isEnabled}
	 */
	public static void logDisplayMetrics(final Activity _activity) throws NullPointerException
	{
		if (!Logging.isEnabled)
		{
			return;
		}
		if (_activity == null)
		{
			throw new NullPointerException("Activity");
		}

		final DisplayMetrics metrics = new DisplayMetrics();
		_activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		final StringBuilder metricsBuffer = new StringBuilder(128);
		metricsBuffer.append("DisplayMetrics :");
		metricsBuffer.append("widthPixels=");
		metricsBuffer.append(metrics.widthPixels);
		metricsBuffer.append(",heightPixels=");
		metricsBuffer.append(metrics.heightPixels);
		metricsBuffer.append(",xdpi=");
		metricsBuffer.append(metrics.xdpi);
		metricsBuffer.append(",ydpi=");
		metricsBuffer.append(metrics.ydpi);
		metricsBuffer.append(",scaledDensity=");
		metricsBuffer.append(metrics.scaledDensity);
		metricsBuffer.append(",density=");
		metricsBuffer.append(metrics.density);
		Log.d(LOG_TAG, metricsBuffer.toString());
	}

	/**
	 * Debug Method.<br>
	 * Returns integer as a human readable String. <code>null</code> returned if
	 * nothing found.
	 * 
	 * @param _day
	 * @return
	 */
	public static String getDayAsString(final int _day)
	{
		return Util.dayMap.get(_day);
	}

	/**
	 * Returns an String which is human readable. Use it to log debug output.
	 * 
	 * @param _alert
	 * @return
	 */
	public static String getAlertAsDebugString(final Alert _alert)
	{
		// avoid wrong output using a offset!
		timeFormat.getTimeZone().setRawOffset(0);
		final StringBuffer buffer = new StringBuffer(64);
		//
		final Date time = new Date(_alert.getTime());

		final String nameOfWeekDay = getDayAsString(_alert.getDayOfWeek());

		buffer.append("Alert for Spot ").append(_alert.getSpotName()).append(" get invoked every ").append(
				nameOfWeekDay).append(" at ").append(timeFormat.format(time)).append(". retrys=").append(
				_alert.getRetryCounter()).append(" alertID=").append(_alert.getAlertID());
		return buffer.toString();
	}

}