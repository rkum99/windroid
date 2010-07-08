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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.alarm.Alert;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.service.SpotService;

/**
 * Called when Alarm comes up. Expect an {@link Alert} is serialized into
 * Intent.
 * 
 * @author mac
 * @version $Id: AlarmBroadcastReciever.java 190 2010-02-06 01:04:09Z jens.hohl
 *          $
 */
public final class AlarmBroadcastReciever extends BroadcastReceiver
{
	private final static String LOG_TAG = AlarmBroadcastReciever.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(final Context _context, final Intent _intent)
	{
		if (Logging.isEnabled())
		{
			Log.d(LOG_TAG, "AlarmBroadcastReciever::onReceive");
		}
		if (Alert.isAlertIntent(_intent))
		{
			final Alert alert = Alert.read(_intent);
			handleAlert(_context, alert);
		}
		else
		{
			Log.e(LOG_TAG, "Expected intent which contains alert, skipping!");
		}
	}

	/**
	 * Passing the alert to the {@link SpotService}.
	 * 
	 * @param _context
	 * @param _alert
	 * @see SpotService
	 */
	private static void handleAlert(final Context _context, final Alert _alert)
	{
		final Intent startServiceIntent = new Intent();
		Alert.write(_alert, startServiceIntent);
		startServiceIntent.setAction(IntentConstants.DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION);
		final ComponentName name = _context.startService(startServiceIntent);
		if (name == null)
		{
			Log.e(LOG_TAG, "Failed to start SpotService.");
		}
	}
}