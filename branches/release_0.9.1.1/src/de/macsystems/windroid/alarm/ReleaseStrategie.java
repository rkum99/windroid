package de.macsystems.windroid.alarm;

import android.app.AlarmManager;

/**
 * @author mac
 * @version $Id$
 */
public final class ReleaseStrategie extends BaseStrategie
{

	ReleaseStrategie()
	{
		super(7L * AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_FIFTEEN_MINUTES);
	}
}
