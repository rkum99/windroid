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

	public static Precipitation create(final float _value, final String _unit)
	{
		final int index = IdentityUtil.indexOf(_unit, Measure.values());
		final Measure unit = Measure.values()[index];
		return new Precipitation(_value, unit);
	}
}
