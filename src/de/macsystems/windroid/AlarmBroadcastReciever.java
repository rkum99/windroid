package de.macsystems.windroid;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.identifyable.WindSpeed;
import de.macsystems.windroid.io.RetryLaterException;
import de.macsystems.windroid.io.task.ForecastTask;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * Called when Alarm comes up. The primary key recieved is the Spot which needs
 * to be monitored.
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class AlarmBroadcastReciever extends BroadcastReceiver
{
	private final static String LOG_TAG = AlarmBroadcastReciever.class.getSimpleName();
	/**
	 * Lookup Key for primary key in a Intent {@value #SELECTED_PRIMARY_KEY}
	 */
	public final static String SELECTED_PRIMARY_KEY = "selected primary key";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		final long id = intent.getLongExtra(SELECTED_PRIMARY_KEY, -1);
		Log.d(LOG_TAG, " Alarm selected spot id:" + id);
		if (id == -1)
		{
			Log.e(LOG_TAG, "No selected id found");
		}
		else
		{
			final ISelectedDAO dao = DAOFactory.getSelectedDAO(context);
			final int alarmID = showUpdateOnStatusBar(context, (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE), 999, context
					.getString(R.string.ongoing_update_title), context.getString(R.string.ongoing_update_text));

			final SpotConfigurationVO vo = dao.getSpotConfiguration(id);
			if (!vo.isActiv())
			{
				// TODO: What to do if Spot is inactive ? Cancel pending Alarms
				// ?
				return;
			}

			try
			{
				final URI uri = WindUtils.getJSONForcastURL(vo.getStation().getId()).toURI();
				final ForecastTask task = new ForecastTask(uri, NullProgressAdapter.INSTANCE);
				final Forecast forecast = task.execute(context);
				final Iterator<ForecastDetail> iter = forecast.iterator();
				while (iter.hasNext())
				{
					final ForecastDetail detail = iter.next();
					final WindSpeed windspeed = detail.getWindSpeed();
					Log.i(LOG_TAG, windspeed.getValue() + " " + windspeed.getUnit());
				}
			}
			catch (IOException e)
			{
				Log.d(LOG_TAG, "", e);
			}
			catch (RetryLaterException e)
			{
				Log.d(LOG_TAG, "", e);
			}
			catch (Exception e)
			{
				Log.e(LOG_TAG, "", e);
			}

			removeUpdateOnStatusBar((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE),
					alarmID);
		}

	}

	private static int showUpdateOnStatusBar(final Context context, final NotificationManager notificationManager,
			final int alarmID, final String notificationTitle, final String notificationDetails)
	{
		final long when = System.currentTimeMillis(); // notification time
		final Intent notificationIntent = new Intent(context, OngoingUpdate.class);
		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		final Notification notification = new Notification(R.drawable.icon_update, notificationTitle, when);
		notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(context, notificationTitle, notificationDetails, contentIntent);

		notificationManager.notify(alarmID, notification);
		return alarmID;
	}

	/**
	 * 
	 * @param _notificationManager
	 * @param _intentID
	 */
	private static void removeUpdateOnStatusBar(final NotificationManager _notificationManager,
			final int _cancelIntentID)
	{
		_notificationManager.cancel(_cancelIntentID);
	}

	private void update(final long _id)
	{
		final ISelectedDAO dao = DAOFactory.getSelectedDAO(null);
		final SpotConfigurationVO vo = dao.getSpotConfiguration(_id);
		if (!vo.isActiv())
		{
			return;
		}
		// A Spot is Configured, show Update Icon

	}
}
