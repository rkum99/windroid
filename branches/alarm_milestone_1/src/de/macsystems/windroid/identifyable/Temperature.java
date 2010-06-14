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
 * @see http://de.wikipedia.org/wiki/Grad_Fahrenheit
 */
public final class Temperature extends MeasureValue
{
	/**
	 * 
	 * @param _value
	 * @param _measure
	 */
	private Temperature(final float _value, final Measure _measure)
	{
		super(_value, _measure);
	}

	/**
	 * 
	 * @param _value
	 * @param _unit
	 * @return
	 */
	public static Temperature create(final float _value, final String _unit)
	{

		return new Temperature(_value, getUnit(_unit));
	}

	/**
	 * creates Celsius.
	 * 
	 * @param _value
	 * @return
	 */
	public static Temperature asCelsius(final float _value)
	{
		return new Temperature(_value, Measure.CELSIUS);
	}

	/**
	 * Creates Fahrenheit.
	 * 
	 * @param _value
	 * @return
	 */
	public static Temperature asFahrenheit(final float _value)
	{
		return new Temperature(_value, Measure.FAHRENHEIT);
	}

	/**
	 * Calculates celsius into fahrenheit.
	 * 
	 * @param _value
	 * @return
	 */
	public static Temperature asFahrenheit(final Temperature _celsius)
	{
		return new Temperature(_celsius.getValue() * 1.8f + 32, Measure.FAHRENHEIT);
	}

}
