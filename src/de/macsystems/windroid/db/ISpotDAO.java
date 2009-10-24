package de.macsystems.windroid.db;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public interface ISpotDAO
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
}
