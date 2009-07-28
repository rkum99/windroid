package de.macsystems.windroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class BootCompletedBroadcastReceiver extends BroadcastReceiver
{

	private final static String LOG_TAG = "BootCompleted";

	@Override
	public void onReceive(final Context _context, final Intent _intent)
	{

		Log.d(LOG_TAG, "BootCompletedBroadcastReceiver#onRecieve(...) called.");

		if (Intent.ACTION_BOOT_COMPLETED.equals(_intent.getAction()))
		{
			Log.i(LOG_TAG, "Boot completed.");

			final SharedPreferences preferences = Util.getSharedPreferences(_context);
			final boolean isLaunchOnBoot = Util.isLaunchOnBoot(preferences);

			Log.i(LOG_TAG, "Launch on boot :" + isLaunchOnBoot);

			if (isLaunchOnBoot)
			{
				final Intent startServiceIntent = new Intent();
				startServiceIntent.setAction("de.macsystems.windroid.START_SPOT_SERVICE_ACTION");
				_context.startService(startServiceIntent);
				Log.i(LOG_TAG, "Service on boot launched through Action :" + startServiceIntent.getAction());
			}
		}

	}
}
