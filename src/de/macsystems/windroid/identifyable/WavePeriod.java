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
	 * @param _value
	 * @param _unit
	 * @return
	 */
	public static WavePeriod create(final float _value,final String _unit)
	{
		return new WavePeriod(_value, getUnit(_unit));
	}
}