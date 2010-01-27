package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class WavePeriod extends MeasureValue
{

	private WavePeriod(final float _value, final Measure _measure)
	{
		super(_value, _measure);
	}

	/**
	 * 
	 * @param _unit
	 * @param _value
	 * @return
	 */
	public static WavePeriod create(final String _unit, final float _value)
	{
		return new WavePeriod(_value, getUnit(_unit));
	}
}