package de.macsystems.windroid;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class IntentConstants
{
	/**
	 * Lookup Key which is used to get StationInfo eg. Intent or Bundle.
	 */
	public static final String SPOT_TO_CONFIGURE = "selectedStation";

	/**
	 * Used to recieve AlarmDetail Object
	 */
	static final String ALARM_DETAIL = "AlarmDetail";

	/**
	 * Name of Action which will start this Service
	 */
	public static final String DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION = "de.macsystems.windroid.START_SPOT_SERVICE_ACTION";

	/**
	 * Name of Intent Action which will be broadcasted on Spot update (use a
	 * IntentFilter).
	 */
	public static final String DE_MACSYSTEMS_WINDROID_SPOT_UPDATE_ACTION = "de.macsystems.windroid.SPOT_UPDATE_ACTION";
}
