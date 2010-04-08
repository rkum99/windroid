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
package de.macsystems.windroid.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.OngoingUpdateActivity;
import de.macsystems.windroid.R;
import de.macsystems.windroid.alarm.AlarmUtil;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.ISelectedDAO;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class UpdateAlarmTask implements Runnable
{
	private final static String LOG_TAG = UpdateAlarmTask.class.getSimpleName();

	/**
	 * Thread save integer which can be used to count alarm id.
	 */
	private final static AtomicInteger notificationCounter = new AtomicInteger(1);

	private final Context context;

	public UpdateAlarmTask(final Context _context)
	{
		if (_context == null)
		{
			throw new NullPointerException("context");
		}
		context = _context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		final int alarmID = showStatus(context, (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE), context.getString(R.string.ongoing_update_title),
				context.getString(R.string.ongoing_update_text));

		try
		{
			final ISelectedDAO dao = DAOFactory.getSelectedDAO(context);

			if (!dao.isSpotActiv())
			{
				Log.i(LOG_TAG, "No active spot configured.");
				return;
			}
			final Collection<SpotConfigurationVO> spots = dao.getActivSpots();
			if (Logging.isLoggingEnabled())
			{
				Log.i(LOG_TAG, "Found " + spots.size() + " Spots to update.");
			}

			final Iterator<SpotConfigurationVO> iter = spots.iterator();
			while (iter.hasNext())
			{
				final SpotConfigurationVO spot = iter.next();
				AlarmUtil.createAlarmsForSpot(spot.getPrimaryKey(), context);
			}

		}
		finally
		{
			removeUpdateOnStatusBar((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE),
					alarmID);
		}

	}

	/**
	 * 
	 * @param _notificationManager
	 * @param _intentID
	 */
	private static void removeUpdateOnStatusBar(final NotificationManager _notificationManager,
			final int _cancelIntentID)
	{
		_notificationManager.cancel(_cancelIntentID);
	}

	private int showStatus(final Context context, final NotificationManager notificationManager,
			final String notificationTitle, final String notificationDetails)
	{

		final int notificationID = notificationCounter.incrementAndGet();

		final long when = System.currentTimeMillis(); // notification time
		final Intent notificationIntent = new Intent(context, OngoingUpdateActivity.class);
		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		final Notification notification = new Notification(R.drawable.icon_update, notificationTitle, when);
		notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(context, notificationTitle, notificationDetails, contentIntent);

		notificationManager.notify(notificationID, notification);
		return notificationID;
	}

}
