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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.Util;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.identifyable.Repeat;
import de.macsystems.windroid.identifyable.Schedule;
import de.macsystems.windroid.receiver.AlarmBroadcastReciever;

/**
 * Util class for Alert/Alarm handling
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class AlarmUtil
{
	private final static String LOG_TAG = AlarmUtil.class.getSimpleName();

	private final static long INITIAL_DELAY = 100L;

	private final static AtomicInteger REQUEST_COUNTER = new AtomicInteger(1);

	private final static int MAX_RETRYS = 3;

	private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	/**
	 * Used for debug output
	 */
	private final static Map<Integer, String> dayMap = new HashMap<Integer, String>();
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

	/**
	 * Util class
	 */
	private AlarmUtil()
	{
	}

	/**
	 * Creates an Alerts for each <code>Repeat</code> which is active by using
	 * the <code>AlarmService</code>. Each Alarm will call
	 * {@link AlarmBroadcastReciever}.<br>
	 * The Intent will call the {@link AlarmBroadcastReciever} with intent data
	 * which describe an {@link Alert}.
	 * 
	 * @param _id
	 * @param _context
	 * @see Alert
	 * @see AlarmUtil#readAlertFormAlarmIntent(Intent) to get an {@link Alert}
	 *      from an {@link Intent}
	 * @see AlarmUtil#writeAlertToIntent(Alert, Intent) to write an
	 *      {@link Alert} into an {@link Intent}
	 */
	public static void createAlarmForSpot(final SpotConfigurationVO _vo, final Context _context)
	{
		if (_context == null)
		{
			throw new NullPointerException("context");
		}

		if (_vo == null)
		{
			throw new NullPointerException("vo");
		}

		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Creating alarm for spot " + _vo.getStation().getName());
		}

		final Schedule schedule = _vo.getSchedule();
		final Iterator<Integer> iter = schedule.getRepeatIterator();

		final List<Alert> alertsToEnqueue = new ArrayList<Alert>();
		while (iter.hasNext())
		{
			final Repeat repeat = schedule.getRepeat(iter.next());
			if (repeat.isActiv())
			{
				alertsToEnqueue.add(new Alert(_vo.getStation().getName(), _vo.getPrimaryKey(), repeat.getId(), 0,
						repeat.getDayTime(), repeat.getDayOfWeek()));
			}
		}

		final AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		//

		for (int i = 0; i < alertsToEnqueue.size(); i++)
		{
			final Alert alert = alertsToEnqueue.get(i);
			final Intent intent = new Intent(_context, AlarmBroadcastReciever.class);
			intent.setAction(IntentConstants.DE_MACSYSTEMS_WINDROID_ALERT_TRIGGER);
			AlarmUtil.writeAlertToIntent(alert, intent);
			//
			final PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, REQUEST_COUNTER.incrementAndGet(),
					intent, 0);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (15L * 1000L),
					AlarmManager.INTERVAL_HOUR * 2, pendingIntent);

			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, alert.toString());
				Log.d(LOG_TAG, getAlertAsDebugString(alert));
			}
		}
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Creating alarms for spot :" + _vo.getStation().getName() + " finished.");
		}
	}

	private static long calcSchedulingStartTime(final Alert _alert)
	{
		final Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 1);

		Log.d(LOG_TAG, "Now set to:" + now.toString());
		final int daysToRoll = Util.getDayToRoll(now.getTimeInMillis(), _alert.getDayOfWeek());

		final long dayToRollInMills = daysToRoll * AlarmManager.INTERVAL_HOUR;
		final long alertTime = dayToRollInMills + _alert.getTime();

		Log.d(LOG_TAG, "Alert time is : " + new Date(alertTime));

		return alertTime;
	}

	/**
	 * Creates an alarm if network not reachable that will be processed in the
	 * future using {@link AlarmManager}.<br>
	 * It is possible that an alert will not be processed as the alert already
	 * reached max retrys.
	 * 
	 * @return <code>true</code> if alarm is enqueued.
	 * @param _alert
	 * @param _context
	 * @throws NullPointerException
	 */
	public static boolean enqueueRetryAlarm(final Alert _alert, final Context _context) throws NullPointerException
	{
		if (_alert == null)
		{
			throw new NullPointerException("Alert");
		}
		if (_context == null)
		{
			throw new NullPointerException("Context");
		}
		// TODO: Change to control wake up retry time
		final long retryInMS = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
		// final long retryInMS = 1000L * 3;
		_alert.incrementRetryCounter();
		if (_alert.isExpired())
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Alert expired for Spot :\"" + _alert.getSpotName() + "\", retrys : "
						+ _alert.getRetryCounter() + ", skipping!");
				return false;

			}
		}

		final long now = System.currentTimeMillis();
		//
		final Intent intent = new Intent(_context, AlarmBroadcastReciever.class);
		writeAlertToIntent(_alert, intent);
		final int requestID = REQUEST_COUNTER.incrementAndGet();
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, requestID, intent,
				PendingIntent.FLAG_ONE_SHOT);
		final AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, now + retryInMS, pendingIntent);
		//
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Enqueued retry alert :" + _alert.toString());
		}
		return true;

	}

	/**
	 * 
	 * @param _selectedID
	 * @param _requestID
	 * @param _context
	 */
	public static void cancelAlarm(final int _selectedID, final int _requestID, final Context _context)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Canceling Alert for selected with id :" + _selectedID);
		}
		//

		final Intent intent = new Intent(_context, AlarmBroadcastReciever.class);
		intent.putExtra(IntentConstants.SELECTED_PRIMARY_KEY, _selectedID);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, _requestID, intent,
				PendingIntent.FLAG_ONE_SHOT);
		final AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	/**
	 * Recreats an <code>Alert</code> from an Intent. (Deserialize)
	 * 
	 * @param _intent
	 * @return
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 * @see #writeAlertToIntent(Alert, Intent)
	 * @see #isAlertIntent(Intent)
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
		final String stationName = _intent.getStringExtra(Alert.SPOTNAME);
		final int selectedID = _intent.getIntExtra(Alert.SELECTED_ID, NOT_FOUND);
		final int repeatID = _intent.getIntExtra(Alert.REPEAT_ID, NOT_FOUND);
		final int retryCounter = _intent.getIntExtra(Alert.RETRYS, NOT_FOUND);
		final long time = _intent.getLongExtra(Alert.TIME, NOT_FOUND_LONG);
		final int weekday = _intent.getIntExtra(Alert.WEEKDAY, NOT_FOUND);

		if (stationName == null || selectedID == NOT_FOUND || repeatID == NOT_FOUND || repeatID == NOT_FOUND
				|| retryCounter == NOT_FOUND || weekday == NOT_FOUND || time == NOT_FOUND_LONG)
		{
			throw new IllegalArgumentException("Intent missing alert entrys! Entrys : stationName=" + stationName
					+ " selectedID=" + selectedID + " repeatID=" + repeatID + " retryCounter=" + retryCounter
					+ " time=" + time + " weekday=" + weekday);
		}

		final Alert alert = new Alert(stationName, selectedID, repeatID, retryCounter, time, weekday);
		return alert;
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
		final String spotName = _intent.getStringExtra(Alert.SPOTNAME);
		final int selectedID = _intent.getIntExtra(Alert.SELECTED_ID, NOT_FOUND);
		final int repeatID = _intent.getIntExtra(Alert.REPEAT_ID, NOT_FOUND);
		final int retryCounter = _intent.getIntExtra(Alert.RETRYS, NOT_FOUND);
		final long time = _intent.getLongExtra(Alert.TIME, NOT_FOUND_LONG);
		final int weekday = _intent.getIntExtra(Alert.WEEKDAY, NOT_FOUND);

		final boolean result = (spotName != null || selectedID != NOT_FOUND || repeatID != NOT_FOUND
				|| repeatID != NOT_FOUND || retryCounter != NOT_FOUND || weekday != NOT_FOUND || time != NOT_FOUND_LONG);
		return result;

	}

	/**
	 * Cancels an alert which occured if nessesary!
	 * 
	 * @param _alert
	 * @param _context
	 */
	public static void cancelAlarm(final Alert _alert, final Context _context)
	{
		if (_alert == null)
		{
			throw new NullPointerException("Alert");
		}
		if (_context == null)
		{
			throw new NullPointerException("Context");
		}

		if (_alert.isRetry() && _alert.getRetryCounter() >= MAX_RETRYS)
		{

		}

		throw new UnsupportedOperationException("Cancel Alarm not supported yet.");

	}

	/**
	 * Writes all info needed to recreate an alert from an intent (Serialize).
	 * 
	 * @param _alert
	 * @param _intent
	 * @throws NullPointerException
	 * @see {@link #readAlertFormAlarmIntent(Intent)}
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
		_intent.putExtra(Alert.SPOTNAME, _alert.getSpotName());
		_intent.putExtra(Alert.REPEAT_ID, _alert.getRepeatID());
		_intent.putExtra(Alert.RETRYS, _alert.getRetryCounter());
		_intent.putExtra(Alert.SELECTED_ID, _alert.getSelectedID());
		_intent.putExtra(Alert.TIME, _alert.getTime());
		_intent.putExtra(Alert.WEEKDAY, _alert.getDayOfWeek());
	}

	/**
	 * 
	 * @param _intent
	 * @return
	 */
	public static boolean isRestartActiveSpotsIntent(final Intent _intent)
	{
		if (_intent != null)
		{
			return _intent.getStringExtra(IntentConstants.ENQUEUE_ACTIV_SPOTS_AFTER_REBOOT) != null;
		}
		return false;
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

		final String nameOfWeekDay = dayMap.get(_alert.getDayOfWeek());

		buffer.append("Alert for Spot ").append(_alert.getSpotName()).append(" get invoked every ").append(
				nameOfWeekDay).append(" at ").append(timeFormat.format(time)).append(".");

		return buffer.toString();
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
		return dayMap.get(_day);
	}

}
