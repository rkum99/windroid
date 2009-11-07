package de.macsystems.windroid.identifyable;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A root node
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class World implements IdentifyAble, Node<NewContinent>
{

	private final Set<NewContinent> continents = new TreeSet<NewContinent>();

	/**
	 * Returns an Iterator over all Country's of this Continent.
	 * 
	 * @return
	 */
	public Iterator<NewContinent> iterator()
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
	public void add(final NewContinent _aContinent)
	{
		if (_aContinent == null)
		{
			throw new NullPointerException();
		}
		continents.add(_aContinent);
	}

	@Override
	public boolean isLeaf()
	{
		return continents.isEmpty();
	}

}
