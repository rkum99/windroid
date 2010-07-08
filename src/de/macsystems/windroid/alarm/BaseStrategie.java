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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.identifyable.Repeat;
import de.macsystems.windroid.identifyable.Schedule;
import de.macsystems.windroid.receiver.AlarmBroadcastReciever;

/**
 * Util class for Alert/Alarm handling
 * 
 * @author mac
 * @version $Id$
 */
abstract class BaseStrategie implements IAlarmManager
{
	private final static String LOG_TAG = BaseStrategie.class.getSimpleName();
	/**
	 * Interval used when some error detected like Network problems.
	 */
	private final long RETRY_INTERVAL;
	/**
	 * Interval used for a normal update period
	 */
	private final long NORMAL_INTERVAL;
	/**
	 * Delay which is added to current time before first alarm get invoked.
	 */
	private final long ON_REBOOT_DELAY;

	private final static AtomicInteger RETRY_REQUEST_COUNTER = new AtomicInteger(1);

	/**
	 * 
	 * @param _normalInterval
	 *            Interval which is used regularly
	 * @param _retryInterval
	 *            delay when an failure occurred before next try
	 * @param _onRebootDelay
	 *            delay before first attempt made to get info
	 */
	BaseStrategie(final long _normalInterval, final long _retryInterval, final long _onRebootDelay)
	{
		NORMAL_INTERVAL = _normalInterval;
		RETRY_INTERVAL = _retryInterval;
		ON_REBOOT_DELAY = _onRebootDelay;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.alarm.IAlarmStrategie#createAlarmForSpot(de.macsystems
	 * .windroid.common.SpotConfigurationVO, android.content.Context)
	 */
	public void createAlarmForSpot(final SpotConfigurationVO _vo, final Context _context, final boolean _isReboot)
	{
		if (_context == null)
		{
			throw new NullPointerException("context");
		}

		if (_vo == null)
		{
			throw new NullPointerException("vo");
		}

		if (Logging.isEnabled())
		{
			Log.d(LOG_TAG, "Creating alarm for spot " + _vo.getStation().getName());
		}

		final List<Alert> alertsToEnqueue = getAlertsFromSpotConfiguration(_vo);

		final AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		//

		for (int i = 0; i < alertsToEnqueue.size(); i++)
		{
			final Alert alert = alertsToEnqueue.get(i);
			final Intent intent = new Intent(_context, AlarmBroadcastReciever.class);
			intent.setAction(IntentConstants.DE_MACSYSTEMS_WINDROID_ALERT_TRIGGER);
			Alert.write(alert, intent);
			//
			final PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, alert.getAlertID(), intent, 0);

			final long millsToAdd = _isReboot ? ON_REBOOT_DELAY : 0L;
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millsToAdd,
					NORMAL_INTERVAL, pendingIntent);

			if (Logging.isEnabled())
			{
				Log.d(LOG_TAG, Util.getAlertAsDebugString(alert));
			}
		}
		if (Logging.isEnabled())
		{
			Log.d(LOG_TAG, "Creating alarms for spot :" + _vo.getStation().getName() + " finished.");
		}
	}

	/**
	 * Returns all alerts which are active for spot
	 * 
	 * @param _vo
	 * @return
	 */
	private static List<Alert> getAlertsFromSpotConfiguration(final SpotConfigurationVO _vo)
	{
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
		return alertsToEnqueue;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.alarm.IAlarmStrategie#enqueueRetryAlarm(de.macsystems
	 * .windroid.alarm.Alert, android.content.Context)
	 */
	public boolean enqueueRetryAlarm(final Alert _alert, final Context _context) throws NullPointerException
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

		// final long retryInMS = 1000L * 3;
		_alert.incrementRetryCounter();
		if (_alert.isExpired())
		{
			if (Logging.isEnabled())
			{
				Log.d(LOG_TAG, "Alert expired for Spot :\"" + _alert.getSpotName() + "\", retrys : "
						+ _alert.getRetryCounter() + ", skipping!");

			}
			return false;
		}

		//
		final Intent intent = new Intent(_context, AlarmBroadcastReciever.class);
		Alert.write(_alert, intent);
		final int requestID = RETRY_REQUEST_COUNTER.incrementAndGet();
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, requestID, intent, 0);
		final AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + RETRY_INTERVAL, pendingIntent);
		//
		if (Logging.isEnabled())
		{
			Log.d(LOG_TAG, "Created Retry alert :" + Util.getAlertAsDebugString(_alert));
		}
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.alarm.IAlarmStrategie#cancelAllAlerts(android.
	 * content.Context)
	 */
	public void cancelAllAlerts(final Context _context)
	{
		final ISelectedDAO dao = DAOFactory.getSelectedDAO(_context);
		if (!dao.isSpotActiv())
		{
			Log.i(LOG_TAG, "No active spot configured, skipping!");
			return;
		}
		try
		{
			final Collection<SpotConfigurationVO> spots = dao.getActivSpots();
			if (Logging.isEnabled())
			{
				Log.i(LOG_TAG, "Found " + spots.size() + " Spots which are active.");
			}

			final Iterator<SpotConfigurationVO> iter = spots.iterator();
			while (iter.hasNext())
			{
				final SpotConfigurationVO spot = iter.next();
				final List<Alert> alertsToCancel = getAlertsFromSpotConfiguration(spot);
				for (final Alert alert : alertsToCancel)
				{
					cancelAlarm(alert, _context);
				}
			}
		}
		catch (final DBException e)
		{
			Log.e(LOG_TAG, "Failure while fetch active spots.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.alarm.IAlarmStrategie#cancelAlarm(de.macsystems
	 * .windroid.alarm.Alert, android.content.Context)
	 */
	public void cancelAlarm(final Alert _alert, final Context _context) throws NullPointerException
	{
		if (_alert == null)
		{
			throw new NullPointerException("Alert");
		}
		if (_context == null)
		{
			throw new NullPointerException("Context");
		}

		final Intent intent = new Intent(_context, AlarmBroadcastReciever.class);
		intent.setAction(IntentConstants.DE_MACSYSTEMS_WINDROID_ALERT_TRIGGER);
		Alert.write(_alert, intent);

		final AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, _alert.getAlertID(), intent, 0);
		alarmManager.cancel(pendingIntent);

		Log.i(LOG_TAG, "Cancel Alert :" + Util.getAlertAsDebugString(_alert));
	}

}
