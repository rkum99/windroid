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
	 * Type of event which
	 */
	public final static String ALARM_ON_SPOT = "alarm on spot";

}
