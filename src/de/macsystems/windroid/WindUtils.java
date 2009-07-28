package de.macsystems.windroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.macsystems.windroid.io.IOUtils;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class WindUtils
{

	private static final String LOG_TAG = WindUtils.class.getSimpleName();;

	private static final String STATION_MD5 = "stationMd5";

	/**
	 * Creates an <code>Intent</code> which ready to invoke next activity. It
	 * stores <code>SpotConfigurationVO</code> in its extras.
	 * 
	 * 
	 * @param _source
	 * @param _activityToLaunch
	 * @param _spotConfiguration
	 * @return
	 * @see IntentConstants#SPOT_TO_CONFIGURE for lookup key of
	 *      SpotConfigurationVO
	 */
	public static final Intent createIntent(final Activity _source, final Class<? extends Activity> _activityToLaunch,
			final SpotConfigurationVO _spotConfiguration)
	{
		final Intent intent = new Intent(_source, _activityToLaunch);
		intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, _spotConfiguration);
		return intent;
	}

	/**
	 * Returns <code>true</code> when a SpotConfiguration can be found in given
	 * Intent.
	 * 
	 * @param _intent
	 * @return
	 */
	public final static boolean isSpotConfigured(final Intent _intent)
	{
		return getConfigurationFromIntent(_intent) != null;
	}

	/**
	 * Returns <code>SpotConfigurationVO</code> from given <code>Intent</code>s
	 * extras.
	 * 
	 * @param _intent
	 * @return a SpotConfigurationVO or <code>null</code> if nothing was found.
	 * @see #isSpotConfigured(Intent)
	 */
	public final static SpotConfigurationVO getConfigurationFromIntent(final Intent _intent)
	{
		return (SpotConfigurationVO) (_intent.getExtras() == null ? null : _intent.getExtras().get(
				IntentConstants.SPOT_TO_CONFIGURE));
	}

	/**
	 * Converts "1" into <code>true</code> else false will be returned.
	 * 
	 * @param _value
	 * @return
	 */
	public final static boolean toBoolean(final String _value)
	{
		return "1".equals(_value);
	}

	/**
	 * Returns URL where all Stations are defined.
	 * 
	 * @return
	 * @throws IOException
	 */
	public final static URL getStationXMLUrl() throws IOException
	{
		return new URL("http://www.windfinder.com/windfox/stations.xml");
	}

	/**
	 * Returns URL where MD5 Hash of stations.xml is located.
	 * 
	 * @return
	 * @throws IOException
	 */
	public final static URL getStationMD5URL() throws IOException
	{
		return new URL("http://www.windfinder.com/windfox/stations.md5");
	}

	/**
	 * Returns <code>true</code> whenever a new station list is available.
	 * 
	 * @param _context
	 * @return
	 * @throws IOException
	 */
	public final static boolean isStationListUpdateAvailable(final Context _context) throws IOException
	{
		Log.d(LOG_TAG, "Updating cache.");
		final Properties config = IOUtils.getConfigProperties(_context);
		final String md5 = config.getProperty(STATION_MD5);

		Log.d(LOG_TAG, "Cached MD5 " + md5);

		final String serverMD5 = getLatestStationMD5();
		Log.i(LOG_TAG, "Server MD5 " + serverMD5);

		final boolean result = md5 != null ? md5.equals(serverMD5) : true;

		Log.i(LOG_TAG, result ? "Cache must be updated." + serverMD5 : "Cache is uptodate.");

		return result;
	}

	public final static void updateStationList(final Context _context) throws IOException
	{
		Log.d(LOG_TAG, "Updating cache.");
		final Properties config = IOUtils.getConfigProperties(_context);
		String md5 = config.getProperty(STATION_MD5);

		Log.d(LOG_TAG, "Cached MD5 " + md5);

		final String latestMD5 = getLatestStationMD5();
		Log.i(LOG_TAG, "Server MD5 " + latestMD5);
		if (md5 == null || !md5.equals(latestMD5))
		{
			Log.i(LOG_TAG, "Station.xml will be updated.");
			IOUtils.updateCachedStationXML(_context, getStationXMLUrl());

			md5 = latestMD5;
			config.put(STATION_MD5, md5);
			IOUtils.writeConfiguration(_context, config);
			Log.i(LOG_TAG, "Stations list was updated.");
		}
		else
		{
			Log.i(LOG_TAG, "Cache is up to date.");
		}
	}

	/**
	 * Returns MD5 checksum of Station.xml file on Server.
	 * 
	 * @return
	 * @throws IOException
	 */
	public final static String getLatestStationMD5() throws IOException
	{

		final StringBuilder builder = new StringBuilder(128);
		final URL md5ChecksumURL = getStationMD5URL();
		InputStream inStream = null;

		try
		{
			inStream = md5ChecksumURL.openStream();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
			String line;
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}

		}
		finally
		{
			IOUtils.close(inStream);
		}
		//
		String md5Hash = "";
		try
		{
			final String md5Line = builder.toString();
			final int indexStart = md5Line.indexOf("<md5>") + "<md5>".length();
			final int indexEnd = md5Line.indexOf("</md5>");
			md5Hash = md5Line.substring(indexStart, indexEnd);
		}
		catch (final IndexOutOfBoundsException e)
		{
			Log.e(LOG_TAG, "Failed to parse MD5 String. String was \"" + builder.toString() + "\".", e);
		}
		return md5Hash;
	}

	/**
	 * Return JSON URL
	 * 
	 * @return
	 * @throws IOException
	 */
	public final static URL getJSONForcastURL(final String _stationID) throws IOException
	{
		if (_stationID == null || "".equals(_stationID.trim()))
		{
			throw new IllegalArgumentException("Illegal StationID \"" + _stationID + "\".");
		}

		return new URL("http://www.windfinder.com/wind-cgi/xmlforecast.pl?CUSTOMER=windfox&FORMAT=JSON&VERSION=1"
				+ "&STATIONS=" + _stationID);
	}

	/**
	 * Return JSON URL
	 * 
	 * @return
	 * @throws IOException
	 */
	public final static URL getXMLForcastURL(final String _stationID) throws IOException
	{
		return new URL("http://www.windfinder.com/wind-cgi/xmlforecast.pl?CUSTOMER=windfox&FORMAT=xml" + "&STATIONS="
				+ _stationID);
	}

	/*
	 * @param _stationID
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public static URL createWindreport(final String _stationID) throws IOException
	{
		if (_stationID == null)
		{
			throw new IllegalArgumentException("invalid station id");
		}

		final URL reportURL = new URL("http://www.windfinder.com/report/" + _stationID);
		return reportURL;
	}

	/*
	 * @param _stationID
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public static URL createForecast(final String _stationID) throws IOException
	{
		if (_stationID == null)
		{
			throw new IllegalArgumentException("invalid station id");
		}

		final URL reportURL = new URL("http://www.windfinder.com/forecast/" + _stationID);
		return reportURL;
	}

	/*
	 * @param _stationID
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public static URL createSuperforecast(final String _stationID) throws IOException
	{
		if (_stationID == null)
		{
			throw new IllegalArgumentException("invalid station id");
		}

		final URL reportURL = new URL("http://www.windfinder.com/weatherforecast/" + _stationID);
		return reportURL;
	}

	/**
	 * 
	 * @param _stationID
	 * @return
	 * @throws IOException
	 */
	public static URL createGetReportURL(final String _stationID) throws IOException
	{
		if (_stationID == null)
		{
			throw new IllegalArgumentException("invalid station id");
		}

		final URL reportURL = new URL("http://www.windfinder.com/wind-cgi/xmlreport.pl?CUSTOMER=windfox&STATIONS="
				+ _stationID);
		return reportURL;
	}

	public static URL createStatisicURL(final String _stationID) throws IOException
	{
		if (_stationID == null)
		{
			throw new IllegalArgumentException("invalid station id");
		}

		final StringBuilder urlBuffer = new StringBuilder(256);
		urlBuffer.append("http://www.windfinder.com/windstats/windstatistic_");
		urlBuffer.append(_stationID);
		urlBuffer.append(".htm");

		final URL statisticURL = new URL(urlBuffer.toString());
		return statisticURL;
	}

	/**
	 * 
	 * @param bft
	 * @return
	 */
	public static int convertBftToKnots(final int bft)
	{
		int knots = -1;

		if (bft >= 12)
		{
			knots = 63;
		}
		else if (bft == 11)
		{
			knots = 56;
		}
		else if (bft == 10)
		{
			knots = 48;
		}
		else if (bft == 9)
		{
			knots = 41;
		}
		else if (bft == 8)
		{
			knots = 34;
		}
		else if (bft == 7)
		{
			knots = 28;
		}
		else if (bft == 6)
		{
			knots = 22;
		}
		else if (bft == 5)
		{
			knots = 16;
		}
		else if (bft == 4)
		{
			knots = 11;
		}
		else if (bft == 3)
		{
			knots = 7;
		}
		else if (bft == 2)
		{
			knots = 4;
		}
		else if (bft == 1)
		{
			knots = 1;
		}
		else
		{
			knots = 0;
		}

		return knots;

	}

	/**
	 * 
	 * @param knots
	 * @return
	 */
	public static int convertKnotsToBft(final double knots)
	{
		int bft = -1;

		if (knots >= 63)
		{
			bft = 12;
		}
		if (knots < 63)
		{
			bft = 11;
		}
		if (knots < 56)
		{
			bft = 10;
		}
		if (knots < 48)
		{
			bft = 9;
		}
		if (knots < 41)
		{
			bft = 8;
		}
		if (knots < 34)
		{
			bft = 7;
		}
		if (knots < 28)
		{
			bft = 6;
		}
		if (knots < 22)
		{
			bft = 5;
		}
		if (knots < 16)
		{
			bft = 4;
		}
		if (knots < 11)
		{
			bft = 3;
		}
		if (knots < 7)
		{
			bft = 2;
		}
		if (knots < 4)
		{
			bft = 1;
		}
		if (knots == 0)
		{
			bft = 0;
		}
		return bft;
	}

	/**
	 * 
	 * @param temperature
	 * @param unit_in
	 *            c or f
	 * @param unit_out
	 *            c or f
	 * @return
	 */
	public static String convertTemperature(final float temperature, final String unit_in, final String unit_out)
	{
		String result = "999";

		if (unit_in.equals(unit_out))
		{
			return "" + temperature;
		}

		if (temperature != 999)
		{
			if ("f".equals(unit_in))
			{
				result = String.valueOf(Math.floor((temperature - 32) * 0.5556));
			}
			else if ("c".equals(unit_in))
			{
				result = String.valueOf(Math.floor(temperature * 1.8 + 32));
			}
		}
		return result;
	}

	/**
	 * 
	 * @param windspeed
	 * @param unit_in
	 * @param unit_out
	 * @return
	 */
	public static double convertWindspeed(final double windspeed, final String unit_in, final String unit_out)
	{
		double tmp = 999;
		double result = 999d;

		if (unit_in == unit_out)
		{
			return windspeed;
		}

		if (windspeed != 999)
		{
			// convert to knots first
			if ("kts".equals(unit_in))
			{
				tmp = windspeed;
			}
			else if ("bft".equals(unit_in))
			{
				tmp = convertBftToKnots((int) windspeed);
			}
			else if ("m/s".equals(unit_in))
			{
				tmp = windspeed * 1.9438;
			}
			else if ("kmh".equals(unit_in))
			{
				tmp = windspeed / 1.852;
			}
			else if ("mph".equals(unit_in))
			{
				tmp = windspeed * 0.868;
			}

			// convert to output value
			if ("kts".equals(unit_out))
			{
				result = Math.floor(tmp + 0.5);
			}
			else if ("bft".equals(unit_out))
			{
				result = convertKnotsToBft(tmp);
			}
			else if ("m/s".equals(unit_out))
			{
				result = Math.floor(tmp * 0.5144 + 0.5);
			}
			else if ("kmh".equals(unit_out))
			{
				result = Math.floor(tmp * 1.852 + 0.5);
			}
			else if ("mph".equals(unit_out))
			{
				result = Math.floor(tmp * 1.15 + 0.5);
			}
		}

		return result;
	}
}
