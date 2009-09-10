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
	 * @param _windUnit
	 * @param _value
	 * @return
	 */
	public static WaveHeight create(final String _unit, float _value)
	{
		final int index = IdentityUtil.indexOf(_unit, Measure.values());
		final Measure unit = Measure.values()[index];
		return new WaveHeight(_value, unit);
	}
}