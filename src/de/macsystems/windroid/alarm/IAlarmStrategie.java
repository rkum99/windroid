package de.macsystems.windroid.alarm;

import android.content.Context;
import android.content.Intent;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.receiver.AlarmBroadcastReciever;

public interface IAlarmStrategie
{

	/**
	 * Creates an Alerts for each <code>Repeat</code> which is active by using
	 * the <code>AlarmService</code>. Each Alarm will call
	 * {@link AlarmBroadcastReciever}.<br>
	 * The Intent will call the {@link AlarmBroadcastReciever} with intent data
	 * which describe an {@link Alert}.
	 * 
	 * @param _id
	 * @param _context
	 * @see Alert
	 * @see Alert#readAlertFormAlarmIntent(Intent) to get an {@link Alert}
	 *      from an {@link Intent}
	 * @see Alert#writeAlertToIntent(Alert, Intent) to write an {@link Alert}
	 *      into an {@link Intent}
	 */
	public abstract void createAlarmForSpot(final SpotConfigurationVO _vo, final Context _context);

	/**
	 * Creates an alarm if network not reachable that will be processed in the
	 * future using {@link BaseStrategie}.<br>
	 * It is possible that an alert will not be processed as the alert already
	 * reached max retrys.
	 * 
	 * @param _alert
	 * @param _context
	 * @return <code>true</code> if alarm is enqueued.
	 * @throws NullPointerException
	 */
	public abstract boolean enqueueRetryAlarm(final Alert _alert, final Context _context) throws NullPointerException;

	/**
	 * Cancels all regular alerts for all active spots. still be processed!
	 * 
	 * @param _context
	 * @TODO: retry alerts will still be processed
	 */
	public abstract void cancelAllAlerts(final Context _context);

	/**
	 * Cancels an alert!
	 * 
	 * @param _alert
	 * @param _context
	 * @throws NullPointerException
	 */
	public abstract void cancelAlarm(final Alert _alert, final Context _context) throws NullPointerException;

}