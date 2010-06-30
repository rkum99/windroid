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
package de.macsystems.windroid.parser;

import java.util.Iterator;

import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.identifyable.IMeasureValue;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class AlarmDetector
{
	private AlarmDetector()
	{
	}

	/**
	 * 
	 * @param _forecast
	 * @return
	 */
	public static boolean isAlarm(final Forecast _forecast, final SpotConfigurationVO _spotConfigurationVO)
	{

		final Iterator<ForecastDetail> iter = _forecast.iterator();
		while (iter.hasNext())
		{
			final ForecastDetail detail = iter.next();
			final IMeasureValue windspeed = detail.getWindSpeed();

			if (_spotConfigurationVO.isUseWindirection())
			{

			}
		}

		return false;
	}
}