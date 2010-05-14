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
package de.macsystems.windroid.io.task;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.macsystems.windroid.OngoingUpdateActivity;
import de.macsystems.windroid.R;
import de.macsystems.windroid.io.IOUtils;

/**
 * Abstract base implementation to show the user 'whats going on' by using the
 * statusbar.
 * 
 * @author mac
 * @version $Id: AbstractNotificationTask.java 314 2010-04-15 11:50:03Z
 *          jens.hohl $
 */
public abstract class AbstractNotificationTask<V> implements Callable<V>
{

	private final String LOG_TAG = AbstractNotificationTask.class.getSimpleName();
	/**
	 * Thread save integer which can be used to count alarm id.
	 */
	private final static AtomicInteger notificationCounter = new AtomicInteger(1);

	private int statusID = -1;

	private final Context context;

	/**
	 * 
	 * @param _context
	 * @throws NullPointerException
	 */
	public AbstractNotificationTask(final Context _context) throws NullPointerException
	{
		if (_context == null)
		{
			throw new NullPointerException("context");
		}
		context = _context;
	}

	/**
	 * 
	 * @return
	 */
	protected final Context getContext()
	{
		return context;
	}

	/**
	 * Returns <code>true</code> when network is reachable.
	 * 
	 * @return
	 */
	protected final boolean isNetworkReachable()
	{
		return IOUtils.isNetworkReachable(context);
	}

	/**
	 * Implement your Task there
	 */
	public abstract void execute() throws Exception;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public final V call()
	{
		try
		{
			execute();
		}
		catch (final Exception e)
		{
			Log.e(LOG_TAG, "Failed to execute", e);
		}
		return null;
	}

	/**
	 * 
	 * @param _notificationTitle
	 * @param _notificationDetails
	 */
	protected void showStatus(final String _notificationTitle, final String _notificationDetails)
	{

		final NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		statusID = notificationCounter.incrementAndGet();

		final long when = System.currentTimeMillis(); // notification time
		final Intent notificationIntent = new Intent(context, OngoingUpdateActivity.class);
		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		final Notification notification = new Notification(R.drawable.icon, _notificationTitle, when);
		notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(context, _notificationTitle, _notificationDetails, contentIntent);
		//
		notificationManager.notify(statusID, notification);
	}

	/**
	 * 
	 */
	protected void clearNotification()
	{
		final NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(statusID);

	}
}
