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

	@Override
	public String toString()
	{
		return "[MeasureValue measure=" + measure.name() + ", value=" + value + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((measure == null) ? 0 : measure.hashCode());
		result = prime * result + Float.floatToIntBits(value);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		MeasureValue other = (MeasureValue) obj;
		if (measure == null)
		{
			if (other.measure != null)
			{
				return false;
			}
		}
		else if (!measure.equals(other.measure))
		{
			return false;
		}
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
		{
			return false;
		}
		return true;
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
	
	/**
	 * @param _unit
	 * @return
	 */
	protected static Measure getUnit(final String _unit)
	{
		final int index = IdentityUtil.indexOf(_unit, Measure.values());
		final Measure unit = Measure.values()[index];
		return unit;
	}


}