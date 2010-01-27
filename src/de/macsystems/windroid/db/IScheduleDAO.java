package de.macsystems.windroid.db;

import de.macsystems.windroid.identifyable.Repeat;
import de.macsystems.windroid.identifyable.Schedule;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IScheduleDAO extends IDAO
{
	public boolean isActiv(final long _id);

	public Repeat getRepeat(final long _id);

	public Schedule getSchedule(final long _id);

	public long getTime(final long _id);
}
