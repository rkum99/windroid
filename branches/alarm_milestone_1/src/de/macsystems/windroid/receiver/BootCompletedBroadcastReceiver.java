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
package de.macsystems.windroid.receiver;

import java.util.Collection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.OngoingUpdateActivity;
import de.macsystems.windroid.R;
import de.macsystems.windroid.Util;
import de.macsystems.windroid.alarm.AlarmUtil;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.ISelectedDAO;

/**
 * Broadcast Reciever which gets called when Boot is complete. When called it
 * starts the Background Service when configured.
 * 
 * @author Jens Hohl
 * @version $Id: BootCompletedBroadcastReceiver.java 2 2009-07-28 02:15:38Z
 *          jens.hohl $
 * 
 */
public class BootCompletedBroadcastReceiver extends BroadcastReceiver
{

	private final static String LOG_TAG = "BootCompletedReceiver";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(final Context _context, final Intent _intent)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "BootCompletedBroadcastReceiver#onRecieve(...) called.");
		}

		if (Intent.ACTION_BOOT_COMPLETED.equals(_intent.getAction()))
		{
			if (Logging.isLoggingEnabled())
			{
				Log.i(LOG_TAG, "Boot completed.");
			}

			try
			{
				final ISelectedDAO dao = DAOFactory.getSelectedDAO(_context);
				if (dao.isSpotActiv())
				{
					final Collection<SpotConfigurationVO> activeSpots = dao.getActivSpots();
					enqueueSpots(activeSpots, _context);
				}
				else
				{
					if (Logging.isLoggingEnabled())
					{
						Log.d(LOG_TAG, "No Spots to enqueue on reboot.");
					}
				}
			}
			catch (final DBException e)
			{
				Log.e(LOG_TAG, "Failed to fetch active spots.", e);
			}

			startService(_context);
		}
	}

	private static void startService(final Context _context)
	{
		// TODO Use DB!
		final SharedPreferences preferences = Util.getSharedPreferences(_context);
		final boolean isLaunchOnBoot = Util.isLaunchOnBoot(preferences);
		if (Logging.isLoggingEnabled())
		{
			Log.i(LOG_TAG, "Start SpotService on boot :" + isLaunchOnBoot);
		}
		if (isLaunchOnBoot)
		{
			final Intent startServiceIntent = new Intent();
			startServiceIntent.setAction(IntentConstants.DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION);
			final ComponentName name = _context.startService(startServiceIntent);
			if (name == null)
			{
				Log.e(LOG_TAG, "Failed to start SpotService.");
			}
			else
			{
				Toast.makeText(_context, "Windroid on boot started", Toast.LENGTH_LONG).show();
				if (Logging.isLoggingEnabled())
				{
					Log.i(LOG_TAG, "SpotService on boot launched.");
				}
			}
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
	private final static void enqueueSpots(final Collection<SpotConfigurationVO> _spots, final Context _context)
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
		for (SpotConfigurationVO vo : _spots)
		{
			AlarmUtil.createAlarmForSpot(vo, _context);
		}

		final String _notificationTitle = "Monitoring started";
		final String _notificationDetails = "" + _spots.size() + " spots scheduled.";

		final NotificationManager notificationManager = (NotificationManager) _context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		final long when = System.currentTimeMillis(); // notification time
		final Intent notificationIntent = new Intent(_context, OngoingUpdateActivity.class);
		final PendingIntent contentIntent = PendingIntent.getActivity(_context, 0, notificationIntent, 0);

		final Notification notification = new Notification(R.drawable.launcher, _notificationTitle, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(_context, _notificationTitle, _notificationDetails, contentIntent);
		//
		notificationManager.notify(777, notification);

	}
}