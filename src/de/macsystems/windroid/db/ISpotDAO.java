package de.macsystems.windroid.db;

import android.database.Cursor;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public interface ISpotDAO extends IDAO
{
	public final static int SPOT_INDEX = 0;

	/**
	 * 
	 */
	public void insertSpots();

	/**
	 * Returns true if spots available
	 * 
	 * @return
	 */
	public boolean hasSpots();

	public Cursor fetchBy(final String continentid, final String countryid, final String regionid);
}
