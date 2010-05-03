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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Represents a Spot
 * 
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

	private final double latitude;
	private final double longitude;

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
		// Dummy Values there
		longitude = 1234567d;
		latitude = 1234567d;
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

	public double getLongitude()
	{
		return longitude;
	}

	public double getLatitude()
	{
		return latitude;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasForecast ? 1231 : 1237);
		result = prime * result + (hasReport ? 1231 : 1237);
		result = prime * result + (hasStatistic ? 1231 : 1237);
		result = prime * result + (hasSuperforecast ? 1231 : 1237);
		result = prime * result + (hasWaveReport ? 1231 : 1237);
		result = prime * result + (hasWaveforecast ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((keyWord == null) ? 0 : keyWord.hashCode());
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
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
		if (hasForecast != other.hasForecast)
		{
			return false;
		}
		if (hasReport != other.hasReport)
		{
			return false;
		}
		if (hasStatistic != other.hasStatistic)
		{
			return false;
		}
		if (hasSuperforecast != other.hasSuperforecast)
		{
			return false;
		}
		if (hasWaveReport != other.hasWaveReport)
		{
			return false;
		}
		if (hasWaveforecast != other.hasWaveforecast)
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
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
		{
			return false;
		}
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
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
	public String toString()
	{
		return "Station [hasForecast=" + hasForecast + ", hasReport=" + hasReport + ", hasStatistic=" + hasStatistic
				+ ", hasSuperforecast=" + hasSuperforecast + ", hasWaveReport=" + hasWaveReport + ", hasWaveforecast="
				+ hasWaveforecast + ", id=" + id + ", keyWord=" + keyWord + ", latitude=" + latitude + ", longitude="
				+ longitude + ", name=" + name + "]";
	}

}
