package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class AirPressure extends MeasureValue
{

	private AirPressure(final float _value, final Measure _measure)
	{
		super(_value, _measure);
	}

	/**
	 * 
	 * @param _value
	 * @param _unit
	 * @return
	 */
	public static AirPressure create(float _value, final String _unit)
	{
		return new AirPressure(_value, getUnit(_unit));
	}
}
