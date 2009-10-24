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
public class Region implements IdentifyAble
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

	public Station[] getStations()
	{
		return stations.toArray(new Station[stations.size()]);
	}

	public void addStation(final Station _bean) throws NullPointerException
	{
		if (_bean == null)
		{
			throw new NullPointerException();
		}
		stations.add(_bean);
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

	public Station getStationById(final String _stationID)
	{
		for (final Station bean : stations)
		{
			if (bean.getId().equals(_stationID))
			{
				return bean;
			}
		}
		throw new IllegalArgumentException("Station with ID " + _stationID + " not found");
	}

	@Override
	public String toString()
	{
		return name;
	}

}