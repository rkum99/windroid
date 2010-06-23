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
	private IntentConstants()
	{
	}

	/**
	 * Lookup Key which is used to get StationInfo eg. Intent or Bundle.
	 * {@value #SPOT_TO_CONFIGURE}
	 */
	public static final String SPOT_TO_CONFIGURE = "selectedStation";

	/**
	 * Used to recieve AlarmDetail Object notification
	 * {@value #ALARM_NOTIFICATION}
	 */
	public static final String ALARM_NOTIFICATION = "ALARM_NOTIFICATION";

	/**
	 * Used to retrieve the repeat id from an intent {@value #REPEAT_ID}
	 */
	public static final String REPEAT_ID = "repeat_id";

	/**
	 * Name of Action which will start this Service
	 * {@value #DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION}
	 */
	public static final String DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION = "de.macsystems.windroid.START_SPOT_SERVICE_ACTION";
	/**
	 * Name of Action for Alert {@value #DE_MACSYSTEMS_WINDROID_ALERT_TRIGGER}
	 */
	public static final String DE_MACSYSTEMS_WINDROID_ALERT_TRIGGER = "de.macsystems.windroid.ALERT_TRIGGER";

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
	 * Lookup Key for retry counter in a Intent {@value #SELECTED_RETRY_COUNTER}
	 */
	public final static String SELECTED_RETRY_COUNTER = "selected retry counter";

	/**
	 * Lookup Key for primary key of forecast to show.
	 * {@value #STORED_FORECAST_KEY}
	 */
	public final static String STORED_FORECAST_KEY = "show forecast key";
	/**
	 * Lookup Key for Intent which describe that after a reboot all activ spots
	 * should be enqueued again. {@value #ENQUEUE_ACTIV_SPOTS_AFTER_REBOOT}
	 */
	public final static String ENQUEUE_ACTIV_SPOTS_AFTER_REBOOT = "reboot.enqueue_activ_spots";

}
