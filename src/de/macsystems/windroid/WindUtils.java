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
package de.macsystems.windroid;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.RetryLaterException;
import de.macsystems.windroid.io.task.MD5Task;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class WindUtils
{
	private WindUtils()
	{
	}

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
	 * Returns URL on server where MD5 Hash of stations.xml is located.
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
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Updating cache.");
		}

		if (!isCachedStationXmlValid(_context))
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Station file is not valid, force update.");
			}
			return true;
		}

		final Properties config = IOUtils.getConfigProperties(_context);
		final String md5 = config.getProperty(STATION_MD5);
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Cached MD5 " + md5);
		}

		final String serverMD5 = getLatestStationMD5(_context);
		if (Logging.isLoggingEnabled())
		{
			Log.i(LOG_TAG, "Server MD5 " + serverMD5);
		}

		final boolean result = md5 != null ? md5.equals(serverMD5) : true;
		if (Logging.isLoggingEnabled())
		{
			Log.i(LOG_TAG, result ? "Cache must be updated." + serverMD5 : "Cache is uptodate.");
		}

		return result;
	}

	/**
	 * 
	 * @param _context
	 * @return
	 */
	private static boolean isCachedStationXmlValid(final Context _context)
	{
		return IOUtils.existFile(IOUtils.stationsXMLFilePath, _context);
	}

	/**
	 * 
	 * @param _context
	 * @param _downloadProgress
	 * @throws RetryLaterException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public final static void updateStationList(final Context _context, final IProgress _downloadProgress)
			throws RetryLaterException, IOException, InterruptedException

	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Updating cache.");
		}
		final Properties config = IOUtils.getConfigProperties(_context);
		String md5 = config.getProperty(STATION_MD5);
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Cached MD5 " + md5);
		}

		final String latestMD5 = getLatestStationMD5(_context);
		if (Logging.isLoggingEnabled())
		{
			Log.i(LOG_TAG, "Server MD5 " + latestMD5);
		}
		if (md5 == null || !md5.equals(latestMD5))
		{
			if (Logging.isLoggingEnabled())
			{
				Log.i(LOG_TAG, "Station.xml will be updated.");
			}
			IOUtils.updateCachedStationXML(_context, getStationXMLUrl(), _downloadProgress);

			md5 = latestMD5;
			config.put(STATION_MD5, md5);
			// TODO: Use PreferenceDAO instead
			IOUtils.writeConfiguration(_context, config);
			if (Logging.isLoggingEnabled())
			{
				Log.i(LOG_TAG, "Stations list was updated.");
			}
		}
		else
		{
			if (Logging.isLoggingEnabled())
			{
				Log.i(LOG_TAG, "Cache is up to date.");
			}
		}
	}

	/**
	 * Returns MD5 checksum of Station.xml file on Server.
	 * 
	 * @param _context
	 * @return
	 * @throws IOException
	 */
	public final static String getLatestStationMD5(final Context _context) throws IOException
	{
		final MD5Task task;
		try
		{
			task = new MD5Task(getStationMD5URL().toURI(), NullProgressAdapter.INSTANCE);
		}
		catch (final URISyntaxException e)
		{
			final IOException exe = new IOException("Failed to launch task");
			exe.initCause(e);
			throw exe;
		}

		try
		{
			return task.execute(_context);
		}
		catch (final InterruptedException e)
		{
			final IOException exe = new IOException("Interrupted");
			exe.initCause(e);
			throw exe;
		}
		catch (final RetryLaterException e)
		{
			final IOException exe = new IOException("Failed to launch task");
			exe.initCause(e);
			throw exe;
		}

	}

	/**
	 * Return URL of JSON Forecast
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

		// http://www.windfinder.com/wind-cgi/xmlforecast.pl?CUSTOMER=windfox&FORMAT=JSON&VERSION=1&STATIONS=nl158
		return new URL("http://www.windfinder.com/wind-cgi/xmlforecast.pl?CUSTOMER=windfox&FORMAT=JSON&VERSION=1"
				+ "&STATIONS=" + _stationID);
	}

	/**
	 * Return URL of XML Forecast
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
	public static URL createSuperforecastURL(final String _stationID) throws IOException
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
}