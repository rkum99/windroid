package de.macsystems.windroid.identifyable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A Continent has country's and a display name id is DB id not primary key..
 * 
 * @author mac
 * @version $Id: Continent.java 172 2010-01-27 02:09:20Z jens.hohl $
 */
public class Continent implements IdentifyAble, Node<Country>
{

	private final String id;

	private final String name;

	private final Set<Country> countrys;

	public static class ContinentComparator implements Comparator<Continent>, Serializable
	{
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final Continent object1, final Continent object2)
		{
			return object1.getId().compareTo(object2.getId());
		}
	}

	/**
	 * 
	 * @param _key
	 * @param _name
	 */
	public Continent(final String _key, final String _name)
	{
		if (_name == null)
		{
			throw new NullPointerException("_name");
		}
		countrys = new TreeSet<Country>(new Country.CountryComparator());
		id = _key;
		name = _name;
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

	/**
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns an Iterator over all Country's of this Continent.
	 * 
	 * @return
	 */
	@Override
	public Iterator<Country> iterator()
	{
		return countrys.iterator();
	}

	@Override
	public void add(final Country _country)
	{
		if (_country == null)
		{
			throw new NullPointerException("country");
		}

		countrys.add(_country);
	}

	@Override
	public int getSize()
	{
		return countrys.size();
	}

	@Override
	public boolean isLeaf()
	{
		return countrys.isEmpty();
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
		result = prime * result + ((countrys == null) ? 0 : countrys.hashCode());
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
	public boolean equals(Object obj)
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
		Continent other = (Continent) obj;
		if (countrys == null)
		{
			if (other.countrys != null)
			{
				return false;
			}
		}
		else if (!countrys.equals(other.countrys))
		{
			return false;
		}
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
