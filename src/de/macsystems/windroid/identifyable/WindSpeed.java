package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class WindSpeed
{
	private final WindUnit unit;

	private final float value;

	/**
	 * 
	 * @param _value
	 * @param _unit
	 */
	private WindSpeed(final float _value, final WindUnit _unit)
	{
		value = _value;
		unit = _unit;
	}

	/**
	 * @return the unit
	 */
	public WindUnit getUnit()
	{
		return unit;
	}

	/**
	 * @return the value
	 */
	public float getValue()
	{
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{

		return "" + value + unit.toString();
	}

	/**
	 * 
	 * @param _value
	 * @param _windUnit
	 * @return
	 */
	public static WindSpeed create(final float _value, final String _windUnit)
	{
		final WindUnit unit = WindUnit.getById(_windUnit);
		return new WindSpeed(_value, unit);
	}
}
