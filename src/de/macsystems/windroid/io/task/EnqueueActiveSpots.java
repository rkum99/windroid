package de.macsystems.windroid.io.task;

import java.util.Collection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.OngoingUpdateActivity;
import de.macsystems.windroid.R;
import de.macsystems.windroid.alarm.AlarmManagerFactory;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.ISelectedDAO;

/**
 * Will run after an reboot to create alarms for active spots again. The User
 * gets informed about the scheduling.
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class EnqueueActiveSpots extends AbstractNotificationTask<Void>
{

	private final static String LOG_TAG = EnqueueActiveSpots.class.getSimpleName();

	public EnqueueActiveSpots(final Context context) throws NullPointerException
	{
		super(context);
	}

	@Override
	public void execute() throws Exception
	{
		try
		{
			final ISelectedDAO dao = DAOFactory.getSelectedDAO(getContext());
			if (dao.isSpotActiv())
			{
				final Collection<SpotConfigurationVO> activeSpots = dao.getActivSpots();
				if (Logging.isEnabled())
				{
					Log.d(LOG_TAG, "Scheduling " + activeSpots.size() + " activ spot(s) after reboot to monitor.");
				}
				enqueueSpots(activeSpots, getContext());
				showScheduleNotification(getContext(), activeSpots.size());
			}
			else
			{
				if (Logging.isEnabled())
				{
					Log.d(LOG_TAG, "No Spots to enqueue after reboot.");
				}
			}
		}
		catch (final DBException e)
		{
			Log.e(LOG_TAG, "Failed to fetch active spots.", e);
		}

	}

	/**
	 * Enqueues all spots again after reboot.
	 * 
	 * @param _spots
	 *            can be null
	 * @param _context
	 * @throws NullPointerException
	 *             if context is null
	 */
	private static void enqueueSpots(final Collection<SpotConfigurationVO> _spots, final Context _context)
			throws NullPointerException
	{
		if (_spots == null)
		{
			Log.w(LOG_TAG, "No spots to enqueue as collection is null!");
			return;
		}
		if (_context == null)
		{
			throw new NullPointerException("Context");
		}

		for (final SpotConfigurationVO vo : _spots)
		{
			AlarmManagerFactory.getAlarmManager().createAlarmForSpot(vo, _context, true);
		}

	}

	private static void showScheduleNotification(final Context _context, final int _nrOfSpots)
	{
		final NotificationManager mNotificationManager = (NotificationManager) _context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		final int icon = R.drawable.launcher;
		final CharSequence tickerText = _context.getString(R.string.on_reboot_monitored_spots_tickertext);
		final long time = System.currentTimeMillis();

		final Notification notification = new Notification(icon, tickerText, time);
		notification.flags |= Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

		final Context context = _context.getApplicationContext();
		final String contentTitle = _context.getString(R.string.on_reboot_monitored_spots_title);
		final String temp = _context.getString(R.string.on_reboot_monitored_spots_text);
		final String contentText = temp.replace("$1", Integer.toString(_nrOfSpots));
		final Intent notificationIntent = new Intent(context, OngoingUpdateActivity.class);
		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		mNotificationManager.notify(1, notification);
	}
}