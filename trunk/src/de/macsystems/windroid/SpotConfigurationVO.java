package de.macsystems.windroid;

import java.io.Serializable;

import de.macsystems.windroid.identifyable.Station;
import de.macsystems.windroid.identifyable.WindDirection;
import de.macsystems.windroid.identifyable.WindUnit;

/**
 * Holds Information about a Station which user configures
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 * 
 */
public final class SpotConfigurationVO implements Serializable
{

	private static final long serialVersionUID = 1L;

	private Station station;

	private boolean useWindirection;

	private WindDirection fromDirection = WindDirection.NO_DIRECTION;

	private WindDirection toDirection = WindDirection.NO_DIRECTION;

	private int windspeedMin;

	private int windspeedMax;

	private boolean isActiv = true;

	/**
	 * Preferred Windunit of User.
	 */
	private WindUnit preferredWindUnit = WindUnit.KNOTS;

	/**
	 * @return the station
	 */
	public Station getStation()
	{
		return station;
	}

	/**
	 * @return the preferredWindUnit
	 */
	public WindUnit getPreferredWindUnit()
	{
		return preferredWindUnit;
	}

	/**
	 * @param preferredWindUnit
	 *            the preferredWindUnit to set
	 */
	public void setPreferredWindUnit(final WindUnit preferredWindUnit)
	{
		this.preferredWindUnit = preferredWindUnit;
	}

	/**
	 * @param station
	 *            the station to set
	 */
	public void setStation(final Station station)
	{
		this.station = station;
	}

	/**
	 * @return the useWindirection
	 */
	public boolean isUseWindirection()
	{
		return useWindirection;
	}

	/**
	 * @param useWindirection
	 *            the useWindirection to set
	 */
	public void setUseWindirection(final boolean useWindirection)
	{
		this.useWindirection = useWindirection;
	}

	/**
	 * @return the fromDirection
	 */
	public WindDirection getFromDirection()
	{
		return fromDirection;
	}

	/**
	 * @param fromDirection
	 *            the fromDirection to set
	 */
	public void setFromDirection(final WindDirection fromDirection)
	{
		this.fromDirection = fromDirection;
	}

	/**
	 * @return the toDirection
	 */
	public WindDirection getToDirection()
	{
		return toDirection;
	}

	/**
	 * @param toDirection
	 *            the toDirection to set
	 */
	public void setToDirection(final WindDirection toDirection)
	{
		this.toDirection = toDirection;
	}

	/**
	 * @return the windspeedMin
	 */
	public int getWindspeedMin()
	{
		return windspeedMin;
	}

	/**
	 * @param windspeedMin
	 *            the windspeedMin to set
	 */
	public void setWindspeedMin(final int windspeedMin)
	{
		this.windspeedMin = windspeedMin;
	}

	/**
	 * @return the windspeedMax
	 */
	public int getWindspeedMax()
	{
		return windspeedMax;
	}

	/**
	 * @param windspeedMax
	 *            the windspeedMax to set
	 */
	public void setWindspeedMax(final int windspeedMax)
	{
		this.windspeedMax = windspeedMax;
	}

	/**
	 * @return the isActiv
	 */
	public boolean isActiv()
	{
		return isActiv;
	}

	/**
	 * @param isActiv
	 *            the isActiv to set
	 */
	public void setActiv(boolean isActiv)
	{
		this.isActiv = isActiv;
	}

}
