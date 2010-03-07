package de.macsystems.windroid.identifyable;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class WaveHeight extends MeasureValue
{

	private WaveHeight(final float _value, final Measure _measure)
	{
		super(_value, _measure);
	}

	/**
	 * 
	 * @param _value
	 * @param _unit
	 * @return
	 */
	public static WaveHeight create(final float _value, final String _unit)
	{
		return new WaveHeight(_value, getUnit(_unit));
	}
}
