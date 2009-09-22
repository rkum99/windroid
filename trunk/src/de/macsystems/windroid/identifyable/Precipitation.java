package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class Precipitation extends MeasureValue
{

	private Precipitation(final float _value, final Measure _measure)
	{
		super(_value, _measure);
	}

	/**
	 * @param _value
	 * @param _unit
	 * @return
	 */
	public static Precipitation create(final float _value, final String _unit)
	{
		return new Precipitation(_value, getUnit(_unit));
	}

}
