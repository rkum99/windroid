package de.macsystems.windroid.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
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
		if(Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "BootCompletedBroadcastReceiver#onRecieve(...) called.");
		}

		if (Intent.ACTION_BOOT_COMPLETED.equals(_intent.getAction()))
		{
			if(Logging.isLoggingEnabled())
			{
				Log.i(LOG_TAG, "Boot completed.");
			}

			// TODO Use DB!
			final SharedPreferences preferences = Util.getSharedPreferences(_context);
			final boolean isLaunchOnBoot = Util.isLaunchOnBoot(preferences);
			if(Logging.isLoggingEnabled())
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
					if(Logging.isLoggingEnabled())
					{
						Log.i(LOG_TAG, "SpotService on boot launched.");
					}
				}
			}
		}
	}
}