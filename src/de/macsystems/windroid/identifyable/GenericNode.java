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
