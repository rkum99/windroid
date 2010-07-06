package de.macsystems.windroid.alarm;

import android.app.AlarmManager;

/**
 * @author mac
 * @version $Id$
 */
public final class DebugStrategie extends BaseStrategie
{
	DebugStrategie()
	{
		super(AlarmManager.INTERVAL_FIFTEEN_MINUTES * 2, AlarmManager.INTERVAL_FIFTEEN_MINUTES / 3);
	}
}
