package de.macsystems.windroid.identifyable;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class Station implements IdentifyAble, Serializable
{
	private static final long serialVersionUID = 1L;

	public static class StationComparator implements Comparator<Station>
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final Station object1, final Station object2)
		{

			return object1.getName().compareTo(object2.getName());
		}
	}

	private final String name;
	private final String id;
	private final String keyWord;
	private final boolean hasSuperforecast;
	private final boolean hasStatistic;

	/**
	 * 
	 * @param _name
	 * @param _id
	 * @param _keyWord
	 * @param _hasSuperforecast
	 * @param _hasStatistic
	 */
	public Station(final String _name, final String _id, final String _keyWord, final boolean _hasSuperforecast,
			final boolean _hasStatistic)
	{
		name = _name;
		id = _id;
		keyWord = _keyWord;
		hasSuperforecast = _hasSuperforecast;
		hasStatistic = _hasStatistic;
	}

	public String getName()
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

	public String getKeyword()
	{
		return keyWord;
	}

	public boolean hasSuperforecast()
	{
		return hasSuperforecast;
	}

	public boolean hasStatistic()
	{
		return hasStatistic;
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
		final Station other = (Station) obj;
		if (hasStatistic != other.hasStatistic)
		{
			return false;
		}
		if (hasSuperforecast != other.hasSuperforecast)
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
		if (keyWord == null)
		{
			if (other.keyWord != null)
			{
				return false;
			}
		}
		else if (!keyWord.equals(other.keyWord))
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
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasStatistic ? 1231 : 1237);
		result = prime * result + (hasSuperforecast ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((keyWord == null) ? 0 : keyWord.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public String toString()
	{
		return name + " (" + id + ")";
	}
}
