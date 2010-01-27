package de.macsystems.windroid.db.sqlite;

import de.macsystems.windroid.db.IScheduleDAO;
import de.macsystems.windroid.identifyable.Repeat;
import de.macsystems.windroid.identifyable.Schedule;
import de.macsystems.windroid.progress.IProgress;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class ScheduleImpl extends BaseImpl implements IScheduleDAO
{
	private static final String SCHEDULE = "schedule";

	/**
	 * @param database
	 * @param progress
	 */
	public ScheduleImpl(final Database database, final IProgress progress)
	{
		super(database, SCHEDULE, progress);
	}

	/**
	 * @param database
	 */
	public ScheduleImpl(final Database database)
	{
		super(database, SCHEDULE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IScheduleDAO#getRepeat(long)
	 */
	@Override
	public Repeat getRepeat(final long id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IScheduleDAO#getSchedule(long)
	 */
	@Override
	public Schedule getSchedule(final long id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IScheduleDAO#getTime(long)
	 */
	@Override
	public long getTime(final long id)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IScheduleDAO#isActiv(long)
	 */
	@Override
	public boolean isActiv(final long id)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
