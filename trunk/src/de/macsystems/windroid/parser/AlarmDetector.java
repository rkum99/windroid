package de.macsystems.windroid.parser;

import java.util.Iterator;

import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.identifyable.WindSpeed;

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
	public static boolean isAlarm(final Forecast _forecast,final SpotConfigurationVO _spotConfigurationVO)
	{
		
		final Iterator<ForecastDetail> iter = _forecast.iterator();
		while(iter.hasNext())
		{
			final ForecastDetail detail = iter.next();
			final WindSpeed windspeed = detail.getWindSpeed();
			
			
			if(_spotConfigurationVO.isUseWindirection())
			{
				
			}
		}
		
		

		return false;
	}
}