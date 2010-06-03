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
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class WindSpeed
{
	private final Measure unit;

	private final float value;

	/**
	 * 
	 * @param _value
	 * @param _unit
	 */
	private WindSpeed(final float _value, final Measure _unit)
	{
		value = _value;
		unit = _unit;
	}

	/**
	 * @return the unit
	 */
	public Measure getUnit()
	{
		return unit;
	}

	/**
	 * @return the value
	 */
	public float getValue()
	{
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{

		return "" + value + unit.toString();
	}

	/**
	 * 
	 * @param _value
	 * @param _windUnit
	 * @return
	 */
	public static WindSpeed create(final float _value, final String _windUnit)
	{
		final Measure unit = Measure.getById(_windUnit);
		return new WindSpeed(_value, unit);
	}
}
