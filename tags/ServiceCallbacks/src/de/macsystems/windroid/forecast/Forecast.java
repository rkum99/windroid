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
package de.macsystems.windroid.forecast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parses the JSON result into a forecast object containing details.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class Forecast
{

	private final static int INITIAL_CAPACITY = 16;

	private final List<ForecastDetail> forecasts;

	private final String name;

	private final long timestamp;

	/**
	 * @param _name
	 * @param _timestamp
	 */
	public Forecast(final String _name, final long _timestamp)
	{
		forecasts = new ArrayList<ForecastDetail>(INITIAL_CAPACITY);
		name = _name;
		timestamp = _timestamp;
	}

	/**
	 * 
	 * @param _forecast
	 */
	public void add(final ForecastDetail _forecast)
	{
		if (_forecast == null)
		{
			throw new NullPointerException("Forecast is null.");
		}

		forecasts.add(_forecast);
	}

	/**
	 * 
	 * @return
	 */
	public Iterator<ForecastDetail> iterator()
	{
		return forecasts.iterator();
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Forecast [name=" + name + ", timestamp=" + timestamp + "]";
	}

}
