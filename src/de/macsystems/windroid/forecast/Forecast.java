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

	private final Date timestamp;

	/**
	 * @param _name
	 * @param _timezone
	 * @param _timestamp
	 */
	public Forecast(final String _name, final int _timezone, final Date _timestamp)
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
	public Date getTimestamp()
	{
		return timestamp;
	}

}
