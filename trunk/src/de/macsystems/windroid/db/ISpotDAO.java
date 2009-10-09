package de.macsystems.windroid.db;

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
