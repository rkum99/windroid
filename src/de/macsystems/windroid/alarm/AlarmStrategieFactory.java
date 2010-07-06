package de.macsystems.windroid.alarm;

/**
 * @author mac
 * @version $Id$
 */
public class AlarmStrategieFactory
{
	private final static IAlarmStrategie strategie = new ReleaseStrategie();

	/**
	 * Allows a flexible testing facility.
	 * 
	 * @return
	 */
	public final static IAlarmStrategie getAlarmManager()
	{
		return strategie;
	}
}
