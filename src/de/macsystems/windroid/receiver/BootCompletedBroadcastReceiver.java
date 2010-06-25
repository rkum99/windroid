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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.Util;
import de.macsystems.windroid.common.IntentConstants;

/**
 * Broadcast Reciever which gets called when Boot is complete. When called it
 * starts the Background Service when configured.
 * 
 * @author Jens Hohl
 * @version $Id: BootCompletedBroadcastReceiver.java 2 2009-07-28 02:15:38Z
 *          jens.hohl $
 * 
 */
public final class BootCompletedBroadcastReceiver extends BroadcastReceiver
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
			startService(_context);
		}
	}

	/**
	 * Starts the Service
	 * 
	 * @param _context
	 */
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
			startServiceIntent.putExtra(IntentConstants.ENQUEUE_ACTIV_SPOTS_AFTER_REBOOT, "dummy");
			_context.startService(startServiceIntent);
		}
	}
}