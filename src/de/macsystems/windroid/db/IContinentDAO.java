package de.macsystems.windroid.db;

import de.macsystems.windroid.identifyable.Continent;

/**
 * DAO for table 'continent'
 * 
 * @author Jens Hohl
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
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
