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
			return object1.getId().compareTo(object2.getId());
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
	public void add(final Station _bean)
	{
		if (_bean == null)
		{
			throw new NullPointerException("station");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		final Region other = (Region) obj;
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

}