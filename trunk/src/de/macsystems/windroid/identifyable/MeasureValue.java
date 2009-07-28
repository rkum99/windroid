package de.macsystems.windroid.identifyable;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 * @see Measure
 * 
 */
public abstract class MeasureValue
{

	protected final Measure measure;
	protected final float value;

	/**
	 * 
	 * @param _value
	 * @param _measure
	 */
	public MeasureValue(final float _value, final Measure _measure)
	{
		if (_measure == null)
		{
			throw new IllegalArgumentException("Measure is null.");
		}
		value = _value;
		measure = _measure;
	}

	/**
	 * 
	 * @return
	 */
	public float getValue()
	{
		return value;
	}

	/**
	 * 
	 * @return
	 */
	public Measure getMeasure()
	{
		return measure;
	}

}