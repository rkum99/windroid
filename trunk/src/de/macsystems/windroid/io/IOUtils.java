package de.macsystems.windroid.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.util.Log;
import de.macsystems.windroid.R;
import de.macsystems.windroid.WindUtils;
import de.macsystems.windroid.parser.ForecastParser;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class IOUtils
{

	private static final String LOG_TAG = IOUtils.class.getSimpleName();

	private static final int BUFFER_SIZE = 1024 * 32;

	/**
	 * Complete Path to configuration file.
	 */
	private final static String configFilePath = "windroid.config";

	/**
	 * Complete Path to Cached Stations.xml.
	 */
	private final static String stationsXMLFilePath = "stations.xml";

	/**
	 * Returns <code>true</code> when Network is reachable and connected.
	 * 
	 * @param _context
	 * @return
	 * @todo also check configuration if roaming needs to be checked.
	 */
	public static boolean isNetworkReachable(final Context _context)
	{
		final ConnectivityManager systemService = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		/**
		 * Avoid NullPointerException when offline
		 */
		if (systemService.getActiveNetworkInfo() == null)
		{
			return false;
		}

		final State networkState = systemService.getActiveNetworkInfo().getState();
		return State.CONNECTED == networkState ? true : false;
	}

	/**
	 * Returns the Name of the Network Connection eg. WIFI, MOBILE.
	 * 
	 * @param _context
	 * @return
	 * @see #isNetworkReachable(Context)
	 */
	public static String getNetworkName(final Context _context)
	{
		final ConnectivityManager systemService = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		String networkTypeName = systemService.getActiveNetworkInfo().getTypeName();
		networkTypeName = networkTypeName + " [" + systemService.getActiveNetworkInfo().getSubtypeName() + "]";
		return networkTypeName;
	}

	/**
	 * Writes <code>Properties</code> as configuration file.
	 * 
	 * @param _properties
	 * @throws IOException
	 */
	public final static void writeConfiguration(final Context _context, final Properties _properties)
			throws IOException
	{
		if (Log.isLoggable(LOG_TAG, Log.DEBUG))
		{
			Log.d(LOG_TAG, "Attempt updating configuration.");
		}
		if (_properties == null)
		{
			throw new NullPointerException("Properties are null.");
		}
		final StringBuffer comments = new StringBuffer(256);
		comments.append("Updated: " + new Date());
		OutputStream outStream = null;

		try
		{
			outStream = getConfigFileOutputStream(_context);
			_properties.store(outStream, comments.toString());

		}
		finally
		{
			close(outStream);
		}
		if (Log.isLoggable(LOG_TAG, Log.DEBUG))
		{
			Log.d(LOG_TAG, "Attempt updating configuration successfully.");
		}
	}

	/**
	 * Stores 'station.xml' on local file system.
	 * 
	 * @param _context
	 * @param _stationXMLURL
	 * @throws IOException
	 * @deprecated Use HttpClient instead
	 */
	public static void updateCachedStationXML(final Context _context, final URL _stationXMLURL) throws IOException
	{
		/**
		 * if (!IOUtils.isNetworkReachable(context)) { Log.d(LOG_TAG,
		 * "Skipping update, Network not reachable."); throw new
		 * RetryLaterException("Skipping update, Network not reachable."); }
		 * 
		 * final HttpClient httpclient = new DefaultHttpClient();
		 * 
		 * try { final HttpGet httpGet = new
		 * HttpGet(WindUtils.getJSONForcastURL(
		 * spot.getStation().getId()).toExternalForm());
		 * httpGet.addHeader("User-Agent", MOZILLA_5_0);
		 * 
		 * final HttpResponse response = httpclient.execute(httpGet);
		 * Log.d(LOG_TAG, "Server Response Code:" +
		 * response.getStatusLine().getStatusCode()); if
		 * (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		 * throw new RetryLaterException("Server reponse was : " +
		 * response.getStatusLine().getStatusCode()); } final StringBuilder
		 * builder = readResult(response.getEntity().getContent()); return
		 * ForecastParser.parse(builder); } finally {
		 * httpclient.getConnectionManager().shutdown(); }
		 */

		FileOutputStream fout = null;
		BufferedInputStream inStream = null;
		try
		{

			fout = _context.openFileOutput(stationsXMLFilePath, Context.MODE_PRIVATE);
			inStream = new BufferedInputStream(_stationXMLURL.openStream(), BUFFER_SIZE * 2);
			final byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = inStream.read(buffer)) > -1)
			{
				fout.write(buffer, 0, bytesRead);
			}
			if (Log.isLoggable(LOG_TAG, Log.DEBUG))
			{
				Log.d(LOG_TAG, stationsXMLFilePath + " updated");
			}
		}
		catch (final IOException e)
		{
			final IOException ioe = new IOException("Failed to update " + stationsXMLFilePath);
			ioe.initCause(e);
			throw ioe;
		}
		finally
		{
			close(fout);
			close(inStream);
		}
	}

	/**
	 * Returns <code>InputStream</code> of local cached 'station.xml' file
	 * 
	 * @param _context
	 * @return
	 * @throws IOException
	 */
	public final static InputStream getStationXML(final Context _context) throws IOException
	{
		return _context.openFileInput(stationsXMLFilePath);
	}

	/**
	 * Returns <code>URL</code> to local cached station.xml file
	 * 
	 * @return
	 * @throws IOException
	 */
	public final static URL getCachedStationURL() throws IOException
	{
		return new URL("file", null, stationsXMLFilePath);
	}

	/**
	 * Returns <code>OutputStream</code> to configuration file.
	 * 
	 * @param _context
	 * @return
	 * @throws IOException
	 */
	public final static OutputStream getConfigFileOutputStream(final Context _context) throws IOException
	{
		if (Log.isLoggable(LOG_TAG, Log.DEBUG))
		{
			Log.d(LOG_TAG, "Files were created at: \"" + _context.getFilesDir() + "\".");
		}
		return _context.openFileOutput(configFilePath, Context.MODE_PRIVATE);
	}

	/**
	 * Returns a <code>Properties</code> Object which contains current
	 * configuration properties.
	 * 
	 * @return
	 * @throws IOException
	 */
	public final static Properties getConfigProperties(final Context _context) throws IOException
	{
		createFileIfNotExists(_context, configFilePath);

		final InputStream inStream = _context.openFileInput(configFilePath);
		final Properties properties = new Properties();

		try
		{
			properties.load(inStream);

		}
		finally
		{
			IOUtils.close(inStream);
		}

		return properties;

	}

	/**
	 * Checks if File already exists else it creates an empty file.
	 * 
	 * @param _context
	 * @param filename
	 * @return true if file already existed
	 * @throws IOException
	 */
	private static void createFileIfNotExists(final Context _context, final String filename) throws IOException
	{
		final File root = _context.getFilesDir();
		final File neededFile = new File(root, filename);
		if (neededFile.exists())
		{
			if (Log.isLoggable(LOG_TAG, Log.DEBUG))
			{
				Log.d(LOG_TAG, "File \"" + filename + "\" already exists.");
			}
		}
		else
		{
			final OutputStream output = _context.openFileOutput(configFilePath, Context.MODE_PRIVATE);
			IOUtils.close(output);
			if (Log.isLoggable(LOG_TAG, Log.DEBUG))
			{
				Log.d(LOG_TAG, "Created File \"" + filename + "\".");
			}
		}
	}

	/**
	 * 
	 * @param _inputStream
	 */
	public final static void close(final InputStream _inputStream)
	{
		if (_inputStream != null)
		{
			try
			{
				_inputStream.close();
			}
			catch (final IOException e)
			{
				Log.e(LOG_TAG, "Failed to close stream", e);
			}
		}
	}

	/**
	 * 
	 * @param _connection
	 */
	public final static void close(final HttpURLConnection _connection)
	{
		if (_connection != null)
		{
			try
			{
				_connection.disconnect();
			}
			catch (final Exception e)
			{
				Log.e(LOG_TAG, "Failed to close HttpURLConnection", e);
			}
		}
	}

	/**
	 * 
	 * @param _reader
	 */
	public final static void close(final Reader _reader)
	{
		if (_reader != null)
		{

			try
			{
				_reader.close();
			}
			catch (final IOException e)
			{
				Log.e(LOG_TAG, "Failed to close stream", e);
			}
		}
	}

	/**
	 * 
	 * @param _outputStream
	 */
	public final static void close(final OutputStream _outputStream)
	{
		if (_outputStream != null)
		{
			try
			{
				_outputStream.close();
			}
			catch (final IOException e)
			{
				Log.e(LOG_TAG, "Failed to close stream", e);
			}
		}
	}

	/**
	 * Returns an <code>Uri</code> to an Sound
	 * 
	 * @param _context
	 * @return
	 * @see Uri
	 */
	public static Uri getResourceURI(final Context _context)
	{
		if (_context == null)
		{
			throw new NullPointerException("Context is null.");
		}

		final StringBuilder builder = new StringBuilder(128);
		builder.append("android.resource://");
		builder.append(_context.getPackageName());
		builder.append("/");
		builder.append("/");
		builder.append(R.raw.wind_chime);

		return Uri.parse(builder.toString());
	}

	/**
	 * 
	 * @param _db
	 */
	public final static void close(SQLiteDatabase _db)
	{
		if (_db != null)
		{
			_db.close();
		}
	}

	/**
	 * Untested yet.
	 * 
	 * @param _context
	 * @return
	 * @throws SecurityException
	 */
	public static boolean renameFile(Context _context) throws SecurityException
	{
		return _context.getFileStreamPath("test").renameTo(new File("echterfilname"));
	}
}