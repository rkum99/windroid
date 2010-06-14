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

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class Country implements IdentifyAble, Node<Region>
{

	private final String id;

	private final String name;

	public static class CountryComparator implements Comparator<Country>, Serializable
	{
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final Country object1, final Country object2)
		{
			return object1.getId().compareTo(object2.getId());
		}
	}

	private final Set<Region> regions = new TreeSet<Region>(new Region.RegionComparator());

	public Country(final String _id, final String _name)
	{
		id = _id;
		name = _name;
	}

	public String getName()
	{
		return name;
	}

	public Iterator<Region> iterator()
	{
		return regions.iterator();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

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
		final Country other = (Country) obj;
		if (id == null)
		{
			if (other.id != null)
			{
				return false;
			}
		}
		else if (!id.equals(other.id))
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return name;
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

	@Override
	public void add(final Region _region)
	{
		if (_region == null)
		{
			throw new NullPointerException();
		}
		regions.add(_region);
	}

	@Override
	public int getSize()
	{
		return regions.size();
	}

	@Override
	public boolean isLeaf()
	{
		return regions.isEmpty();
	}
}
