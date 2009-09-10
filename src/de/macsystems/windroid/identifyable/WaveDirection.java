package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class WaveDirection extends MeasureValue
{
	/**
	 * @param _value
	 * @param _measure
	 */
	private WaveDirection(final float _value, final Measure _measure)
	{
		super(_value, _measure);
	}

	/**
	 * 
	 * @param _unit
	 * @param _value
	 * @return
	 */
	public static WaveDirection create(final String _unit, float _value)
	{
		final int index = IdentityUtil.indexOf(_unit, Measure.values());
		final Measure unit = Measure.values()[index];
		return new WaveDirection(_value, unit);
	}
}
