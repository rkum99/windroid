package de.macsystems.windroid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.Station;
import de.macsystems.windroid.identifyable.WindDirection;
import de.macsystems.windroid.identifyable.WindUnit;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class Util
{
	private static int FAKE_STATION_ID = 0;

	public static final String SPOT_STATION_HAS_STATISTIC = "spot_station_has_statistic";
	public static final String SPOT_STATION_HAS_SUPERFORECAST = "spot_station_has_superforecast";

	public static final String SPOT_STATION_HAS_FORECAST = "spot_station_has_forecast";
	public static final String SPOT_STATION_HAS_WAVERFORECAST = "spot_station_has_waveforecast";
	public static final String SPOT_STATION_HAS_WAVEREPORT = "spot_station_has_wavereport";
	public static final String SPOT_STATION_HAS_REPORT = "spot_station_has_report";

	public static final String SPOT_PREFERRED_WIND_UNIT = "spot_preferred_unit";
	public static final String SPOT_WINDDIRECTION_TO_ID = "spot_winddirection_to";
	public static final String SPOT_WINDDIRECTION_FROM_ID = "spot_winddirection_from";
	public static final String SPOT_STATION_KEYWORD = "spot_station_keyword";
	public static final String SPOT_STATION_NAME = "spot_station_name";
	public static final String SPOT_STATION_ID = "spot_station_id";
	public static final String SPOT_WINDSPEED_MIN = "spot_windspeed_min";
	public static final String SPOT_WINDSPEED_MAX = "spot_windspeed_max";

	/**
	 * {@value #DEFAULT_WIND_UNIT}
	 */
	private final static String DEFAULT_WIND_UNIT = WindUnit.KNOTS.getId();
	/**
	 * {@value #DEFAULT_CONTINENT}
	 */
	private final static String DEFAULT_CONTINENT = "Europe";
	/**
	 * Start Application when boot completed {@value #DEFAULT_LAUNCH_ON_BOOT}
	 */
	private final static boolean DEFAULT_LAUNCH_ON_BOOT = true;
	/**
	 * Update when roaming activated
	 */
	private final static boolean DEFAULT_UPDATE_WHILE_ROAMING = false;

	/**
	 * Returns the <code>SharedPreferences</code> of given
	 * <code>ContextWrapper</code>.
	 * 
	 * @param wrapper
	 * @return
	 * @throws NullPointerException
	 *             if context is null
	 * 
	 */
	public static final SharedPreferences getSharedPreferences(final ContextWrapper wrapper)
	{
		if (wrapper == null)
		{
			throw new NullPointerException("ContextWrapper is null.");
		}
		return wrapper.getSharedPreferences(wrapper.getPackageName() + "_preferences", Context.MODE_PRIVATE);
	}

	/**
	 * 
	 * @param _context
	 * @return
	 * @throws NullPointerException
	 *             if context is null.
	 */
	public static final SharedPreferences getSharedPreferences(final Context _context) throws NullPointerException
	{
		if (_context == null)
		{
			throw new NullPointerException("Context is null.");
		}
		return _context.getSharedPreferences(_context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
	}

	/**
	 * Returns ID of selected WindUnit from <code>SharedPreferences</code>. If
	 * no selection was made the default Value will returned.
	 * 
	 * @param _pref
	 * @return
	 * @see #DEFAULT_WIND_UNIT
	 * @see WindUnit
	 */

	public final static String getSelectedUnitID(final SharedPreferences _pref)
	{
		return _pref.getString(PrefConstants.PREFERRED_WIND_UNIT_ID, DEFAULT_WIND_UNIT);
	}

	/**
	 * Returns ID of selected Continent from <code>SharedPreferences</code>. If
	 * no selection was made the default Value will returned.
	 * 
	 * @param _pref
	 * @return
	 * @see #DEFAULT_CONTINENT
	 * @see Continent
	 */
	public final static String getSelectedContinentID(final SharedPreferences _pref)
	{
		return _pref.getString(PrefConstants.PREFERRED_CONTINENT_ID, DEFAULT_CONTINENT);
	}

	/**
	 * Returns default values which indicates that application should launch
	 * after system boot completed.
	 * 
	 * @param _pref
	 * @return
	 * @see #DEFAULT_LAUNCH_ON_BOOT
	 */
	public final static boolean isLaunchOnBoot(final SharedPreferences _pref)
	{
		return _pref.getBoolean(PrefConstants.LAUNCH_ON_BOOT, DEFAULT_LAUNCH_ON_BOOT);
	}

	/**
	 * Returns default values which indicates that application should launch
	 * after system boot completed.
	 * 
	 * @param _pref
	 * @return
	 * @see #DEFAULT_LAUNCH_ON_BOOT
	 */
	public final static boolean isUpdateWhileRoaming(final SharedPreferences _pref)
	{
		return _pref.getBoolean(PrefConstants.UPDATE_WHILE_ROAMING, DEFAULT_UPDATE_WHILE_ROAMING);
	}

	/**
	 * Saves an <code>SpotConfigurationVO</code> using given Context
	 * SharedPreferences
	 * 
	 * @param _configuration
	 * @param _context
	 */
	public final static void persistSpotConfigurationVO(final SpotConfigurationVO _configuration, final Context _context)
	{
		if (_configuration == null)
		{
			throw new NullPointerException("SpotConfigurationVO is null.");
		}

		final SharedPreferences prefs = getSharedPreferences(_context);
		final Editor editor = prefs.edit();

		editor.putString(SPOT_STATION_ID, _configuration.getStation().getId());
		editor.putString(SPOT_STATION_NAME, _configuration.getStation().getName());
		editor.putString(SPOT_STATION_KEYWORD, _configuration.getStation().getKeyword());
		editor.putString(SPOT_WINDDIRECTION_FROM_ID, _configuration.getFromDirection().getShortName());
		editor.putString(SPOT_WINDDIRECTION_TO_ID, _configuration.getToDirection().getShortName());
		editor.putString(SPOT_PREFERRED_WIND_UNIT, _configuration.getPreferredWindUnit().getId());
		editor.putBoolean(SPOT_STATION_HAS_STATISTIC, _configuration.getStation().hasStatistic());
		editor.putBoolean(SPOT_STATION_HAS_SUPERFORECAST, _configuration.getStation().hasSuperforecast());
		editor.putInt(SPOT_WINDSPEED_MIN, _configuration.getWindspeedMin());
		editor.putInt(SPOT_WINDSPEED_MAX, _configuration.getWindspeedMax());

		editor.commit();
	}

	/**
	 * A Configured Spot has a id, name, keyword and preferred unit.
	 * 
	 * @param _context
	 * @return
	 */
	public static final boolean isSpotConfigured(final Context _context)
	{
		final SharedPreferences prefs = getSharedPreferences(_context);
		//
		final String stationID = prefs.getString(SPOT_STATION_ID, null);
		final String stationName = prefs.getString(SPOT_STATION_NAME, null);
		final String spotKeyword = prefs.getString(SPOT_STATION_KEYWORD, null);
		final String preferredUnit = prefs.getString(SPOT_PREFERRED_WIND_UNIT, null);

		// Actual we don't care about wind directions,statistics or
		// superforecast

		return stationID != null && stationName != null && spotKeyword != null && preferredUnit != null;
	}

	/**
	 * FAKE Method: Simulates the Configuration
	 * 
	 * @param _context
	 * @return
	 * @throws IllegalArgumentException
	 * @Deprecated
	 */
	public final static List<SpotConfigurationVO> FAKE_getSpotConfiguration(final Context _context)
			throws IllegalArgumentException
	{
		if (!isSpotConfigured(_context))
		{
			throw new IllegalArgumentException("No Spot Configured.");
		}

		final SharedPreferences prefs = getSharedPreferences(_context);
		//
		final SpotConfigurationVO spotConfiguration = new SpotConfigurationVO();
		//
		final String stationID = "za" + ++FAKE_STATION_ID;

		final String stationName = prefs.getString(SPOT_STATION_NAME, null);
		final String spotKeyword = prefs.getString(SPOT_STATION_KEYWORD, null);
		final String preferredUnit = prefs.getString(SPOT_PREFERRED_WIND_UNIT, null);

		final boolean hasSuperForecast = prefs.getBoolean(SPOT_STATION_HAS_SUPERFORECAST, false);
		final boolean hasStatistic = prefs.getBoolean(SPOT_STATION_HAS_STATISTIC, false);

		final String winddirectionFrom = prefs.getString(SPOT_WINDDIRECTION_FROM_ID, null);
		final String winddirectionTo = prefs.getString(SPOT_WINDDIRECTION_TO_ID, null);

		final boolean hasReport = prefs.getBoolean(SPOT_STATION_HAS_REPORT, false);
		final boolean hasForecast = prefs.getBoolean(SPOT_STATION_HAS_FORECAST, false);
		final boolean hasWavereport = prefs.getBoolean(SPOT_STATION_HAS_WAVEREPORT, false);
		final boolean hasWaveforecast = prefs.getBoolean(SPOT_STATION_HAS_WAVERFORECAST, false);
		//
		final Station station = new Station(stationName, stationID, spotKeyword, hasForecast, hasSuperForecast,
				hasStatistic, hasReport, hasWavereport, hasWaveforecast);
		spotConfiguration.setStation(station);
		//
		final int indexPreferredWindUnit = IdentityUtil.indexOf(preferredUnit, WindUnit.values());
		final WindUnit preferredWindUnit = WindUnit.values()[indexPreferredWindUnit];
		spotConfiguration.setPreferredWindUnit(preferredWindUnit);
		//
		final int indexFromWindID = IdentityUtil.indexOf(winddirectionFrom, WindDirection.values());
		final int indexToWindID = IdentityUtil.indexOf(winddirectionTo, WindDirection.values());

		final WindDirection fromWindDirection = WindDirection.values()[indexFromWindID];
		final WindDirection toWindDirection = WindDirection.values()[indexToWindID];

		spotConfiguration.setFromDirection(fromWindDirection);
		spotConfiguration.setToDirection(toWindDirection);
		//
		final List<SpotConfigurationVO> configurations = new ArrayList<SpotConfigurationVO>();
		configurations.add(spotConfiguration);
		return configurations;
	}

	/**
	 * 
	 * @param _context
	 * @return
	 * @throws IllegalArgumentException
	 *             if no spot configured
	 * @see #isSpotConfigured(Context)
	 */
	public final static List<SpotConfigurationVO> getSpotConfiguration(final Context _context)
			throws IllegalArgumentException
	{
		if (!isSpotConfigured(_context))
		{
			throw new IllegalArgumentException("No Spot Configured.");
		}

		final SharedPreferences prefs = getSharedPreferences(_context);
		//
		final SpotConfigurationVO spotConfiguration = new SpotConfigurationVO();
		//
		final String stationID = prefs.getString(SPOT_STATION_ID, null);
		final String stationName = prefs.getString(SPOT_STATION_NAME, null);
		final String spotKeyword = prefs.getString(SPOT_STATION_KEYWORD, null);
		final String preferredUnit = prefs.getString(SPOT_PREFERRED_WIND_UNIT, null);

		final boolean hasSuperForecast = prefs.getBoolean(SPOT_STATION_HAS_SUPERFORECAST, false);
		final boolean hasStatistic = prefs.getBoolean(SPOT_STATION_HAS_STATISTIC, false);

		final String winddirectionFrom = prefs.getString(SPOT_WINDDIRECTION_FROM_ID, null);
		final String winddirectionTo = prefs.getString(SPOT_WINDDIRECTION_TO_ID, null);

		final boolean hasReport = prefs.getBoolean(SPOT_STATION_HAS_REPORT, false);
		final boolean hasForecast = prefs.getBoolean(SPOT_STATION_HAS_FORECAST, false);
		final boolean hasWavereport = prefs.getBoolean(SPOT_STATION_HAS_WAVEREPORT, false);
		final boolean hasWaveforecast = prefs.getBoolean(SPOT_STATION_HAS_WAVERFORECAST, false);
		//
		// Some parameter not readed yet so we put default values
		//
		final Station station = new Station(stationName, stationID, spotKeyword, hasForecast, hasSuperForecast,
				hasStatistic, hasReport, hasWavereport, hasWaveforecast);
		spotConfiguration.setStation(station);
		//
		final int indexPreferredWindUnit = IdentityUtil.indexOf(preferredUnit, WindUnit.values());
		final WindUnit preferredWindUnit = WindUnit.values()[indexPreferredWindUnit];
		spotConfiguration.setPreferredWindUnit(preferredWindUnit);
		//
		final int indexFromWindID = IdentityUtil.indexOf(winddirectionFrom, WindDirection.values());
		final int indexToWindID = IdentityUtil.indexOf(winddirectionTo, WindDirection.values());

		final WindDirection fromWindDirection = WindDirection.values()[indexFromWindID];
		final WindDirection toWindDirection = WindDirection.values()[indexToWindID];

		spotConfiguration.setFromDirection(fromWindDirection);
		spotConfiguration.setToDirection(toWindDirection);
		//
		final List<SpotConfigurationVO> configurations = new ArrayList<SpotConfigurationVO>();
		configurations.add(spotConfiguration);
		return configurations;

	}
}
