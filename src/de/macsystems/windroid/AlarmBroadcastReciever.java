package de.macsystems.windroid;

import de.macsystems.windroid.common.IntentConstants;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Called when Alarm comes up. The primary key received is the Spot which needs
 * to be monitored.
 * 
 * @author mac
 * @version $Id: AlarmBroadcastReciever.java 190 2010-02-06 01:04:09Z jens.hohl
 *          $
 */
public class AlarmBroadcastReciever extends BroadcastReceiver
{
	private final static String LOG_TAG = AlarmBroadcastReciever.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		final long id = intent.getLongExtra(IntentConstants.SELECTED_PRIMARY_KEY, -1);
		if (id == -1)
		{
			throw new IllegalArgumentException("missing id");
		}

		Log.d(LOG_TAG, "Alarm for Station " + id + " triggered.");
		final Intent startServiceIntent = new Intent();
		startServiceIntent.putExtra(IntentConstants.SELECTED_PRIMARY_KEY, id);
		startServiceIntent.setAction(IntentConstants.DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION);
		final ComponentName name = context.startService(startServiceIntent);
		if (name == null)
		{
			Log.e(LOG_TAG, "Failed to start SpotService.");
		}
		else
		{
			Log.i(LOG_TAG, "SpotService launched : " + name.toString());
		}

	}
}