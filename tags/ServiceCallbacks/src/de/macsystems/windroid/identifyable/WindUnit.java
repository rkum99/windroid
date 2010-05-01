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
public enum WindUnit implements IdentifyAble
{

	BEAUFORT("bft", "Beaufort", "bft", 12), //
	KNOTS("kts", "Knots", "kts", 63),
	MPS("mps", "Meter per Second (m/s)", "mps", 37),
	MPH("mph", "Miles per Hour", "mph", 83),
	KMH("kmh", "Kilometer per Hour", "kmh", 133);

	private final String description;

	private final String shortDisplayName;

	private final int maximum;

	private final String id;

	/**
	 * 
	 * @param _id
	 * @param _description
	 * @param _max
	 */
	private WindUnit(final String _id, final String _description, final String _shortDisplayName, final int _max)
	{
		id = _id;
		description = _description;
		shortDisplayName = _shortDisplayName;
		maximum = _max;

	}

	/**
	 * Returns <code>WindUnit</code> by its id.
	 * 
	 * @param _id
	 * @return
	 * @throws IllegalArgumentException
	 */
	public final static WindUnit getById(final String _id) throws IllegalArgumentException
	{
		if (_id == null)
		{
			throw new IllegalArgumentException("ID is  null.");
		}
		for (final WindUnit unit : WindUnit.values())
		{
			if (_id.equalsIgnoreCase(unit.id))
			{
				return unit;
			}
		}
		throw new IllegalArgumentException("unknown id \"" + _id + "\".");
	}

	/**
	 * Returns maximum of this Unit.
	 * 
	 * @return
	 */
	public int getMaximum()
	{
		return maximum;
	}

	/**
	 * 
	 * @return
	 */
	public String getShortDisplayName()
	{
		return shortDisplayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.identifyable.IdentifyAble#getId()
	 */
	@Override
	public String getId()
	{
		return id;
	}
}