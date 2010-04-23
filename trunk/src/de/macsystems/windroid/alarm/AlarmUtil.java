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

import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.receiver.AlarmBroadcastReciever;

/**
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class AlarmUtil
{
	private final static String LOG_TAG = AlarmUtil.class.getSimpleName();

	private static final long INITIAL_DELAY = 100L;

	private final static AtomicInteger REQUEST_COUNTER = new AtomicInteger(1);

	private AlarmUtil()
	{
	}

	/**
	 * Creates an Alarm by using the <code>AlarmService</code>. Each Alarm will
	 * call {@link AlarmBroadcastReciever}. Lookup Intent data to lookup primary
	 * key of selected spot to update.
	 * 
	 * @param _id
	 * @param _context
	 * @see IntentConstants#SELECTED_PRIMARY_KEY can be used to lookup primary
	 *      key of spot to monitor
	 */
	public static void createAlarmForSpot(final int _id, final Context _context)
	{
		if (_context == null)
		{
			throw new NullPointerException("context");
		}

		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Creating alarm for spot with id:" + _id);
		}
		final long now = System.currentTimeMillis();
		//
		final Intent intent = new Intent(_context, AlarmBroadcastReciever.class);
		intent.putExtra(IntentConstants.SELECTED_PRIMARY_KEY, _id);

		final int requestID = REQUEST_COUNTER.incrementAndGet();

		final PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, requestID, intent,
				PendingIntent.FLAG_ONE_SHOT);
		final AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		// TODO Fix initial delay
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now + INITIAL_DELAY, (10L * 1000L), pendingIntent);

		// alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now +
		// INITIAL_DELAY, AlarmManager.INTERVAL_DAY,
		// pendingIntent);
	}

	/**
	 * 
	 * @param _selectedID
	 * @param _context
	 */
	public static void createRetryAlarm(final int _selectedID, final Context _context)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Creating retry alarm for selected with id :" + _selectedID);
		}
		final long now = System.currentTimeMillis();
		//
		final Intent intent = new Intent(_context, AlarmBroadcastReciever.class);
		intent.putExtra(IntentConstants.SELECTED_PRIMARY_KEY, _selectedID);
		final int requestID = REQUEST_COUNTER.incrementAndGet();
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, requestID, intent,
				PendingIntent.FLAG_ONE_SHOT);
		final AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, now + (15L * 1000L), pendingIntent);
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
			Log.d(LOG_TAG, "Canceling Alarm for selected with id :" + _selectedID);
		}
		//

		final Intent intent = new Intent(_context, AlarmBroadcastReciever.class);
		intent.putExtra(IntentConstants.SELECTED_PRIMARY_KEY, _selectedID);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, _requestID, intent,
				PendingIntent.FLAG_ONE_SHOT);
		final AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

}