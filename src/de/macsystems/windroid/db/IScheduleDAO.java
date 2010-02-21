package de.macsystems.windroid.db;

import de.macsystems.windroid.identifyable.Schedule;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IScheduleDAO extends IDAO
{

	public final static String COLUMN_REPEAT_ID = "repeatid";

	public final static String COLUMN_SELECTED_ID = "selectedid";

	public final static String COLUMN_ACTIV = "activ";

	public boolean isActiv(final int _id);

	public Schedule getSchedule(final int _id);

	public Schedule getScheduleByScheduleID(final int _selectedID);

	public long getTime(final int _id);

	public void insert(final Schedule _schedule, final int _selectedID);
}
