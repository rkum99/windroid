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
package de.macsystems.windroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import de.macsystems.windroid.alarm.AlarmDetail;
import de.macsystems.windroid.common.IntentConstants;

/**
 * @author Jens Hohl
 * @version $Id: AlarmNotificationDetail.java 179 2010-01-28 02:55:59Z jens.hohl
 *          $
 * 
 */
public class AlarmNotificationDetail extends Activity
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarmnotificationdetail);

		final Intent intent = getIntent();
		if (intent != null)
		{
			final Parcelable pdetails = intent.getParcelableExtra(IntentConstants.ALARM_DETAIL);
			final AlarmDetail alarmDetail = (AlarmDetail) pdetails;
			cancelAlarm(this, alarmDetail.getId());
			new AlertDialog.Builder(this).setTitle("Windalarm").setMessage(
					"Alarm für " + alarmDetail.getName() + " wurde ausgelöst.").setCancelable(true).show();
		}
	}

	private final static void cancelAlarm(final Context _context, final int _alarmID)
	{
		final NotificationManager notificationManager = (NotificationManager) _context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(_alarmID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
	}
}