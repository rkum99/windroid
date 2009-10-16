package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class Pressure extends MeasureValue
{

	private Pressure(final float _value, final Measure _measure)
	{
		super(_value, _measure);
	}

	/**
	 * @param _value
	 * @param _unit
	 * @return
	 */
	public static Pressure create(final float _value, final String _unit)
	{
		return new Pressure(_value, getUnit(_unit));
	}
}
