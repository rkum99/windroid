package de.macsystems.windroid;

import java.io.Serializable;

import de.macsystems.windroid.identifyable.Schedule;
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

	private Schedule schedule;

	private boolean useWindirection;

	private WindDirection fromDirection = WindDirection.NO_DIRECTION;

	private WindDirection toDirection = WindDirection.NO_DIRECTION;

	private float windspeedMin;

	private float windspeedMax;

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
	public float getWindspeedMin()
	{
		return windspeedMin;
	}

	/**
	 * @param windspeedMin
	 *            the windspeedMin to set
	 */
	public void setWindspeedMin(final float windspeedMin)
	{
		this.windspeedMin = windspeedMin;
	}

	/**
	 * @return the windspeedMax
	 */
	public float getWindspeedMax()
	{
		return windspeedMax;
	}

	/**
	 * @param windspeedMax
	 *            the windspeedMax to set
	 */
	public void setWindspeedMax(final float windspeedMax)
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
	public void setActiv(final boolean isActiv)
	{
		this.isActiv = isActiv;
	}

	/**
	 * @return the schedule
	 */
	public Schedule getSchedule()
	{
		return schedule;
	}

	/**
	 * @param schedule
	 *            the schedule to set
	 */
	public void setSchedule(final Schedule schedule)
	{
		this.schedule = schedule;
	}

}
