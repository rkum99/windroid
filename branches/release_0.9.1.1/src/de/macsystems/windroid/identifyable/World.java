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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A root node
 * 
 * @author mac
 * @version $Id$
 */
public class World implements IdentifyAble, Node<Continent>
{

	private final Set<Continent> continents;

	public World()
	{
		continents = new TreeSet<Continent>(new Continent.ContinentComparator());
	}

	/**
	 * Returns an Iterator over all Country's of this Continent.
	 * 
	 * @return
	 */
	@Override
	public Iterator<Continent> iterator()
	{
		return continents.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.identifyable.IdentifyAble#getId()
	 */
	@Override
	public String getId()
	{
		return "World";
	}

	@Override
	public int getSize()
	{
		return continents.size();
	}

	@Override
	public void add(final Continent _aContinent)
	{
		if (_aContinent == null)
		{
			throw new NullPointerException("continent");
		}
		continents.add(_aContinent);
	}

	@Override
	public boolean isLeaf()
	{
		return continents.isEmpty();
	}

}
