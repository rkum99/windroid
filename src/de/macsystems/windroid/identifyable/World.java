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
		continents = new TreeSet<Continent>(new Continent.NewContinentComparator());
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
