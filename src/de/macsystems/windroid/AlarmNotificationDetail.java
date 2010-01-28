package de.macsystems.windroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import de.macsystems.windroid.alarm.AlarmDetail;

/**
 * @author Jens Hohl
 * @version $Id$
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