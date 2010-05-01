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
 * @see MeasureValue
 * 
 */
public enum Measure implements IdentifyAble
{

	METER("m", "m"), //
	MILLIMETER("mm", "mm"),
	SECONDS("s", "s"),
	FAHRENHEIT("fahrenheit", "f"),
	CELSIUS("celsius", "c"),
	HEKTOPASCAL("hpa", "hpa");

	private final String id;

	private final String shortDisplayName;

	/**
	 * 
	 * @param _id
	 */
	private Measure(final String _id, final String _shortDisplayName)
	{
		id = _id;
		shortDisplayName = _shortDisplayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.identifyable.IdentifyAble#getId()
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * returns a shortcut to display
	 * 
	 * @return
	 */
	public String getShortDisplayName()
	{
		return shortDisplayName;
	}

}
