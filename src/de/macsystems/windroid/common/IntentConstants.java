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

package de.macsystems.windroid.common;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class IntentConstants
{
	/**
	 * Lookup Key which is used to get StationInfo eg. Intent or Bundle.
	 * {@value #SPOT_TO_CONFIGURE}
	 */
	public static final String SPOT_TO_CONFIGURE = "selectedStation";

	/**
	 * Used to recieve AlarmDetail Object {@value #ALARM_DETAIL}
	 */
	public static final String ALARM_DETAIL = "AlarmDetail";

	/**
	 * Name of Action which will start this Service
	 * {@value #DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION}
	 */
	public static final String DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION = "de.macsystems.windroid.START_SPOT_SERVICE_ACTION";

	/**
	 * Name of Intent Action which will be broadcasted on Spot update (use a
	 * IntentFilter). {@value #DE_MACSYSTEMS_WINDROID_SPOT_UPDATE_ACTION}
	 */
	public static final String DE_MACSYSTEMS_WINDROID_SPOT_UPDATE_ACTION = "de.macsystems.windroid.SPOT_UPDATE_ACTION";

	/**
	 * Lookup Key for primary key in a Intent {@value #SELECTED_PRIMARY_KEY}
	 */
	public final static String SELECTED_PRIMARY_KEY = "selected primary key";

	/**
	 * Lookup Key for primary key of forecast to show.
	 * {@value #STORED_FORECAST_KEY}
	 */
	public final static String STORED_FORECAST_KEY = "show forecast key";

	/**
	 * Type of event which
	 */
	public final static String ALARM_ON_SPOT = "alarm on spot";

}
