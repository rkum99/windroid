package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class Pascal extends MeasureValue
{

	private Pascal(final float _value, final Measure _measure)
	{
		super(_value, _measure);
	}

	/**
	 * @param _value
	 * @param _unit
	 * @return
	 */
	public static Pascal create(final float _value, final String _unit)
	{
		return new Pascal(_value, getUnit(_unit));
	}
}
