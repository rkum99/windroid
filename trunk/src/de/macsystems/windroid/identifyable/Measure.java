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

import java.util.Arrays;

/**
 * @author Jens Hohl
 * @version $Id$
 * @see MeasureValue
 * 
 */
public enum Measure implements IdentifyAble
{
	// Length
	METER("m", "meter", "m", 1), //
	MILLIMETER("mm", "millimeter", "mm", 1),
	// Time
	SECONDS("s", "seconds", "s", 1),
	// Pressure
	HEKTOPASCAL("hpa", "hectopascal", "hpa", 100),
	// Temp
	FAHRENHEIT("fahrenheit", "fahrenheit", "f", 1),
	CELSIUS("celsius", "celsius", "c", 99),
	// Wind
	BEAUFORT("bft", "Beaufort", "bft", 12), //
	KNOTS("kts", "Knots", "kts", 63),
	FPS("fps", "Feet per second (f/s)", "fps", 0),
	FTM("ftm", "Feet per minute (f/min)", "ftm", 0),
	MMI("mmi", "Meter per minute (m/min)", "mmi", 0),
	MPS("mps", "Meter per Second (m/s)", "mps", 37),
	MPH("mph", "Miles per Hour", "mph", 83),
	KMH("kmh", "Kilometer per Hour", "kmh", 133);

	private final String id;

	private final String description;

	private final String shortDisplayName;

	private final float maximum;

	/**
	 * 
	 * @param _id
	 *            unique id
	 * @param _description
	 * @param _shortDisplayName
	 *            name may be used to display name
	 * @param _max
	 * @throws NullPointerException
	 *             if any parameter passed in is <code>null</code>.
	 */
	private Measure(final String _id, final String _description, final String _shortDisplayName, final float _max)
			throws NullPointerException
	{
		if (_id == null || _description == null || _shortDisplayName == null)
		{
			throw new NullPointerException();
		}

		id = _id;
		shortDisplayName = _shortDisplayName;
		description = _description;
		maximum = _max;
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

	/**
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * 
	 * @return
	 */
	public float getMaximum()
	{
		return maximum;
	}

	/**
	 * Returns <code>Measure</code> by its id.
	 * 
	 * @param _id
	 * @return
	 * @throws IllegalArgumentException
	 * @see {@link Enum#name()}
	 */
	public final static Measure getById(final String _id) throws IllegalArgumentException
	{
		for (final Measure unit : values())
		{
			if (unit.id.equalsIgnoreCase(_id))
			{
				return unit;
			}
		}
		throw new IllegalArgumentException("unknown id \"" + _id + "\". Known Values :" + Arrays.toString(values()));
	}

}
