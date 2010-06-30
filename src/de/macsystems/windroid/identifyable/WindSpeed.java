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
public final class WindSpeed extends MeasureValue implements IMeasureValue
{

	/**
	 * 
	 * @param _value
	 * @param _unit
	 */
	private WindSpeed(final float _value, final Measure _unit)
	{
		super(_value, _unit);
	}

	/**
	 * @return the value
	 */
	@Override
	public float getValue()
	{
		return value;
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

	/**
	 * 
	 * @param _value
	 * @param _measure
	 * @return
	 */
	static <E extends IMeasureValue> IMeasureValue create(final float _value, final Measure _measure)
	{
		return new WindSpeed(_value, _measure);
	}

	public static int knotToBeaufort(final float _knots)
	{

		if (_knots <= 1)
		{
			return 0;
		}

		if (_knots > 1 && _knots < 3.5)
		{
			return 1;
		}

		if (_knots >= 3.5 && _knots < 6.5)
		{
			return 2;
		}

		if (_knots >= 6.5 && _knots < 10.5)
		{
			return 3;
		}

		if (_knots >= 10.5 && _knots < 16.5)
		{
			return 4;
		}

		if (_knots >= 16.5 && _knots < 21.5)
		{
			return 5;
		}

		if (_knots >= 21.5 && _knots < 27.5)
		{
			return 6;
		}

		if (_knots >= 27.5 && _knots < 33.5)
		{
			return 7;
		}

		if (_knots >= 33.5 && _knots < 40.5)
		{
			return 8;
		}

		if (_knots >= 40.5 && _knots < 47.5)
		{
			return 9;
		}

		if (_knots >= 47.5 && _knots < 55.5)
		{
			return 10;
		}

		if (_knots >= 55.5 && _knots < 63.5)
		{
			return 11;
		}

		if (_knots >= 63.5 && _knots < 74.5)
		{
			return 12;
		}

		if (_knots >= 74.5 && _knots < 80.5)
		{
			return 13;
		}

		if (_knots >= 80.5 && _knots < 89.5)
		{
			return 14;
		}
		else if (_knots >= 89.5)
		{
			return 15;
		}

		return -1;
	}

}
