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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A generic node has children a size and allows to iterate over children.
 * 
 * @author mac
 * @version $Id$
 * @param <T>
 */
public abstract class GenericNode<T> implements Node<T>
{
	private final Set<T> collection;

	public GenericNode(final Comparator<T> aComparator)
	{
		if (aComparator == null)
		{
			throw new NullPointerException("comparator");
		}
		collection = new TreeSet<T>(aComparator);
	}

	@Override
	public void add(final T t)
	{
		if (t == null)
		{
			throw new NullPointerException();
		}
		collection.add(t);
	}

	@Override
	public int getSize()
	{
		return collection.size();
	}

	@Override
	public boolean isLeaf()
	{
		return collection.isEmpty();
	}

	@Override
	public Iterator<T> iterator()
	{
		return collection.iterator();
	}

}
