package de.macsystems.windroid.identifyable;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class Station implements IdentifyAble, Serializable
{
	private static final long serialVersionUID = 1L;

	public static class StationComparator implements Comparator<Station>, Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(final Station _station1, final Station _station2)
		{
			return _station1.getId().compareTo(_station2.getId());
		}
	}

	private final String name;
	private final String id;
	private final String keyWord;
	private final boolean hasForecast;
	private final boolean hasSuperforecast;
	private final boolean hasStatistic;
	private final boolean hasReport;
	private final boolean hasWaveReport;

	private final boolean hasWaveforecast;

	/**
	 * 
	 * @param _name
	 * @param _id
	 * @param _keyWord
	 * @param _hasForecast
	 * @param _hasSuperforecast
	 * @param _hasStatistic
	 * @param _hasWaveforecast
	 * @param _hasWaveReport
	 * @param _hasReport
	 */
	public Station(final String _name, final String _id, final String _keyWord, final boolean _hasForecast,
			final boolean _hasSuperforecast, final boolean _hasStatistic, final boolean _hasReport,
			final boolean _hasWaveReport, final boolean _hasWaveforecast)
	{
		if (_name == null || _id == null || _keyWord == null)
		{
			throw new NullPointerException("check arguments name=" + _name + " id=" + _id + " keyword=" + _keyWord);
		}

		name = _name;
		id = _id;
		keyWord = _keyWord;
		hasForecast = _hasForecast;
		hasSuperforecast = _hasSuperforecast;
		hasStatistic = _hasStatistic;
		hasReport = _hasReport;
		hasWaveforecast = _hasWaveforecast;
		hasWaveReport = _hasWaveReport;
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

	/**
	 * @return the hasForecast
	 */
	public boolean hasForecast()
	{
		return hasForecast;
	}

	/**
	 * @return the hasReport
	 */
	public boolean hasReport()
	{
		return hasReport;
	}

	/**
	 * @return the hasWaveReport
	 */
	public boolean hasWaveReport()
	{
		return hasWaveReport;
	}

	/**
	 * @return the hasWaveforecast
	 */
	public boolean hasWaveforecast()
	{
		return hasWaveforecast;
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
