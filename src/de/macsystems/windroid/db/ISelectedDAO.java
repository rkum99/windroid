package de.macsystems.windroid.db;

import de.macsystems.windroid.SpotConfigurationVO;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public interface ISelectedDAO extends IDAO
{
	/**
	 * Adds a Spot
	 * 
	 * @param _vo
	 */
	public void insertSpot(SpotConfigurationVO _vo);

}
