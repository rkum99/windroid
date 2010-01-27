package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class Temperature extends MeasureValue
{
	/**
	 * 
	 * @param _value
	 * @param _measure
	 */
	private Temperature(final float _value, final Measure _measure)
	{
		super(_value, _measure);
	}

	/**
	 * 
	 * @param _value
	 * @param _unit
	 * @return
	 */
	public static Temperature create(final float _value, final String _unit)
	{
		return new Temperature(_value, getUnit(_unit));
	}

	/**
	 * 
	 * @param _value
	 * @return
	 */
	public static Temperature asCelsius(final float _value)
	{
		return new Temperature(_value, Measure.CELSIUS);
	}

	/**
	 * 
	 * @param _value
	 * @return
	 */
	public static Temperature asFahrenheit(final float _value)
	{
		return new Temperature(_value, Measure.FAHRENHEIT);
	}

}
