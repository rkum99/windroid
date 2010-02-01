package de.macsystems.windroid;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.identifyable.WindUnit;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class Util
{

	/**
	 * {@value #DEFAULT_WIND_UNIT}
	 */
	private final static String DEFAULT_WIND_UNIT = WindUnit.KNOTS.getId();
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
	 * Returns ID of selected WindUnit from <code>SharedPreferences</code>. If
	 * no selection was made the default Value will returned.
	 * 
	 * @param _pref
	 * @return
	 * @see #DEFAULT_WIND_UNIT
	 * @see WindUnit
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
	 * 
	 * @param aThrowable
	 * @return
	 */
	public static String getStackTrace(final Throwable aThrowable)
	{
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
	 */
	public final static void printCursorColumnNames(final Cursor _cursor)
	{
		if (_cursor == null)
		{
			return;
		}

		final String[] names = _cursor.getColumnNames();
		for (int i = 0; i < names.length; i++)
		{
			Log.d("Debug", "Column at index :" + i + "=" + names[i]);
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
			throw new IllegalArgumentException("ResultCode not valid. Must be RESULT or RESULT_CANCELED but was "
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
		return (_day < Calendar.SUNDAY || _day > Calendar.SATURDAY) ? true : false;
	}

}