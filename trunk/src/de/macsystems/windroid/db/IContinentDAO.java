package de.macsystems.windroid.db;

import de.macsystems.windroid.identifyable.Continent;

/**
 * DAO for table 'continent'
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public interface IContinentDAO extends IDAO
{

	/**
	 * Returns Continent by id
	 * 
	 * @param id
	 * @return
	 */
	public Continent get(final int id);

	/**
	 * Returns index of continent by its continent id
	 * 
	 * @param _id
	 * @return
	 */
	public int getIndexByID(final String _id);
}
