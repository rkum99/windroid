/**
 This file is part of Windroid.

 Windroid is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Windroid is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Windroid.  If not, see <http://www.gnu.org/licenses/>.

 */
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
	public boolean equals(final Object obj)
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
		final MeasureValue other = (MeasureValue) obj;
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
	 * @throws IllegalArgumentException
	 *             if unit cannot be found.
	 */
	protected static Measure getUnit(final String _unit) throws IllegalArgumentException
	{
		final int index = IdentityUtil.indexOf(_unit, Measure.values());
		final Measure unit = Measure.values()[index];
		return unit;
	}

}