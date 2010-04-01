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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * "timestamp":"20090722_1800","stations":[{ "id": "nl48", "name":
 * "Wijk aan Zee", "timezone": 2, "forecasts":
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class Forecast
{
	private final List<ForecastDetail> forecasts;

	private final String name;

	private final int timezone;

	private final long timestamp;

	/**
	 * @param _name
	 * @param _timezone
	 * @param _timestamp
	 */
	public Forecast(final String _name, final int _timezone, final long _timestamp)
	{
		forecasts = new ArrayList<ForecastDetail>();
		name = _name;
		timezone = _timezone;
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
	 * @return the timezone
	 */
	public int getTimezone()
	{
		return timezone;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

}
