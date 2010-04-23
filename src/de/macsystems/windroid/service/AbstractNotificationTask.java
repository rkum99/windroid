package de.macsystems.windroid.service;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import de.macsystems.windroid.OngoingUpdateActivity;
import de.macsystems.windroid.R;

/**
 * Abstract base implementation to show the user 'whats going on' by using the
 * statusbar.
 * 
 * @author mac
 * @version $Id: AbstractNotificationTask.java 314 2010-04-15 11:50:03Z
 *          jens.hohl $
 */
public abstract class AbstractNotificationTask implements ITaskPriority
{
	/**
	 * Thread save integer which can be used to count alarm id.
	 */
	private final static AtomicInteger notificationCounter = new AtomicInteger(1);

	private int statusID = -1;

	private final Context context;
	/**
	 * Default Priority {@value #AbstractNotificationTask#prio}
	 */
	private final PRIORITY prio;

	/**
	 * 
	 * @param _context
	 * @param _prio
	 * @throws NullPointerException
	 */
	public AbstractNotificationTask(final Context _context, final PRIORITY _prio) throws NullPointerException
	{
		if (_context == null)
		{
			throw new NullPointerException("context");
		}
		context = _context;
		prio = _prio;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.service.ITaskPriority#getPriority()
	 */
	public final PRIORITY getPriority()
	{
		return prio;
	}

	/**
	 * 
	 * @return
	 */
	protected Context getContext()
	{
		return context;
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
