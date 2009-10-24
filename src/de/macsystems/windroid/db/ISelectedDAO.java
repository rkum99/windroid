package de.macsystems.windroid.db;

import android.database.Cursor;
import de.macsystems.windroid.SpotConfigurationVO;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public interface ISelectedDAO
{
	/**
	 * Returns nr of Spots in selected table
	 * 
	 * @return
	 */
	public int fetchSize();

	/**
	 * Adds a Spot
	 * 
	 * @param _vo
	 */
	public void insertSpot(SpotConfigurationVO _vo);

	/**
	 * returns a coursor on all items
	 * 
	 * @return
	 */
	public Cursor fetchAll();
}
