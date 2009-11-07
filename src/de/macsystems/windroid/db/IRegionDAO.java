package de.macsystems.windroid.db;

import android.graphics.Region;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public interface IRegionDAO extends IDAO
{
	/**
	 * 
	 * @param _id
	 * @return
	 */
	public Region getById(final int _id);

}
