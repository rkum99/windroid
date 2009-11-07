package de.macsystems.windroid.identifyable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class Region implements IdentifyAble, Node<Station>
{

	public static class RegionComparator implements Comparator<Region>, Serializable
	{
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final Region object1, final Region object2)
		{
			return object1.getName().compareTo(object2.getName());
		}
	}

	private final String id;
	private final String name;

	private final Set<Station> stations = new TreeSet<Station>(new Station.StationComparator());

	public Region(final String _regionID, final String _name) throws NullPointerException
	{
		if (_regionID == null || _name == null)
		{
			throw new NullPointerException("parameters");
		}

		id = _regionID;
		name = _name;
	}

	public Iterator<Station> iterator()
	{
		return stations.iterator();
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

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public void add(Station _bean)
	{
		if (_bean == null)
		{
			throw new NullPointerException();
		}
		stations.add(_bean);
	}

	@Override
	public int getSize()
	{
		return stations.size();
	}

	@Override
	public boolean isLeaf()
	{
		return stations.isEmpty();
	}

}