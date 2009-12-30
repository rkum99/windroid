package de.macsystems.windroid.db;

import de.macsystems.windroid.SpotConfigurationVO;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public interface ISelectedDAO extends IDAO
{
	public final static String COLUMN_ACTIV = "activ";

	public final static String COLUMN_SPOTID = "spotid";

	public final static String COLUMN_ID = "_id";

	/**
	 * Adds a Spot
	 * 
	 * @param _vo
	 */
	public void insertSpot(SpotConfigurationVO _vo);

}
