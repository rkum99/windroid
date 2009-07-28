package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class WindSpeed
{
	private WindUnit unit;

	private float value;

	private WindSpeed(final float _value, final WindUnit _unit)
	{
		value = _value;
		unit = _unit;
	}

	/**
	 * 
	 * @param _value
	 * @param _windUnit
	 * @return
	 */
	public static WindSpeed create(float _value, final String _windUnit)
	{
		final WindUnit unit = WindUnit.getById(_windUnit);
		return new WindSpeed(_value, unit);
	}
}
