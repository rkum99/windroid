package de.macsystems.windroid.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import de.macsystems.windroid.common.IntentConstants;

/**
 * A Reciever which gets called when an update/replacement of the application
 * held.
 * 
 * @author mac
 * @version $Id$
 */
public final class ReplaceReceiver extends BroadcastReceiver
{

	private final static String LOG_TAG = ReplaceReceiver.class.getSimpleName();
	/**
	 * Used to identify that this Application get replaced or updated.
	 */
	private final Uri uri = new Uri.Builder().scheme("package").encodedOpaquePart("de.macsystems.windroid").build();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(final Context _context, final Intent _intent)
	{
		if (uri.equals(_intent.getData()))
		{
			Log.i(LOG_TAG, "Enqueue Alerts, Action is :" + _intent.getAction());
			startService(_context);
		}
	}

	/**
	 * Starts the Service, this will trigger the service to enqueue alarms
	 * again.
	 * 
	 * @param _context
	 */
	private static void startService(final Context _context)
	{
		final Intent startServiceIntent = new Intent();
		startServiceIntent.setAction(IntentConstants.DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION);
		startServiceIntent.putExtra(IntentConstants.ENQUEUE_ACTIV_SPOTS_AFTER_REBOOT_OR_UPDATE, "dummy");
		_context.startService(startServiceIntent);
	}
}
