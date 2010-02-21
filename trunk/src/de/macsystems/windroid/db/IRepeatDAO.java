package de.macsystems.windroid.db;

import de.macsystems.windroid.identifyable.Repeat;

/**
 * @author mac
 * @version $Id$
 */
public interface IRepeatDAO extends IDAO
{
	public final static String COLUMN_SCHEDULE_ID = "scheduleid";

	public final static String COLUMN_WEEKDAY = "weekday";

	public final static String COLUMN_DAYTIME = "daytime";

	public final static String COLUMN_ACTIV = "activ";

	/**
	 * 
	 * @param _id
	 * @return
	 */
	public Repeat getRepeat(final int _id);

	/**
	 * Updates a repeat
	 * 
	 * @param _repeat
	 */
	public void update(final Repeat _repeat);

	/**
	 * Inserts a newly Repeat
	 * 
	 * @param _repeat
	 */
	public void insert(final Repeat _repeat);
}
