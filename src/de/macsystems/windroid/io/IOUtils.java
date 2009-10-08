package de.macsystems.windroid.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.util.Log;
import de.macsystems.windroid.R;
import de.macsystems.windroid.io.task.StationXMLUpdateTask;
import de.macsystems.windroid.progress.IProgress;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class IOUtils
{

	private static final String LOG_TAG = IOUtils.class.getSimpleName();

	public static final int BIG_BUFFER_SIZE = 1024 * 32;

	public static final int DEFAULT_BUFFER_SIZE = 1024;

	public static final String MOZILLA_5_0 = "Mozilla/5.0";

	/**
	 * Complete Path to configuration file.
	 */
	private final static String configFilePath = "windroid.config";

	/**
	 * Complete Path to Cached Stations.xml.
	 */
	public final static String stationsXMLFilePath = "stations.xml";

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
	 * @param _progress
	 * @throws RetryLaterException
	 * @throws IOException
	 */
	public static void updateCachedStationXML(final Context _context, final URL _stationXMLURL, IProgress _progress)
			throws RetryLaterException, IOException
	{

		try
		{
			final StationXMLUpdateTask task = new StationXMLUpdateTask(_stationXMLURL.toURI(), stationsXMLFilePath,
					_progress);
			task.execute(_context);
		}
		catch (final URISyntaxException e)
		{
			final IOException ioe = new IOException();
			ioe.initCause(e);
			throw ioe;
		}

		// final HttpClient httpclient = new DefaultHttpClient();
		//
		// try
		// {
		// final HttpGet httpGet = new HttpGet(_stationXMLURL.toExternalForm());
		// httpGet.addHeader("User-Agent", "MOZILLA_5_0");
		//
		// final HttpResponse response = httpclient.execute(httpGet);
		// Log.d(LOG_TAG, "Server Response Code:" +
		// response.getStatusLine().getStatusCode());
		// if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
		// {
		// throw new RetryLaterException("Server response was : " +
		// response.getStatusLine().getStatusCode() + " "
		// + response.getStatusLine().getReasonPhrase());
		// }
		//
		// FileOutputStream fout = null;
		// final BufferedInputStream inStream = new
		// BufferedInputStream(response.getEntity().getContent(), 8192);
		// try
		// {
		//
		// fout = _context.openFileOutput(stationsXMLFilePath,
		// Context.MODE_PRIVATE);
		// final byte[] buffer = new byte[BIG_BUFFER_SIZE];
		// int bytesRead = -1;
		// while ((bytesRead = inStream.read(buffer)) > -1)
		// {
		// fout.write(buffer, 0, bytesRead);
		// }
		// if (Log.isLoggable(LOG_TAG, Log.DEBUG))
		// {
		// Log.d(LOG_TAG, stationsXMLFilePath + " updated");
		// }
		// }
		// catch (final IOException e)
		// {
		// final IOException ioe = new IOException("Failed to update " +
		// stationsXMLFilePath);
		// ioe.initCause(e);
		// throw ioe;
		// }
		// finally
		// {
		// close(fout);
		// close(inStream);
		// }
		//
		// }
		// finally
		// {
		// httpclient.getConnectionManager().shutdown();
		// }

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
	 * Checks if a file exists in directory returned by
	 * {@link Context#getFilesDir()} with given name.
	 * 
	 * @param _filename
	 * @param _context
	 * @return
	 */
	public static boolean existFile(final String _filename, final Context _context)
	{
		final File root = _context.getFilesDir();
		final File neededFile = new File(root, _filename);

		Log.d(LOG_TAG, "checking File exist " + neededFile.getAbsolutePath());

		return neededFile.exists();
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
	public final static void close(final SQLiteDatabase _db)
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
	public static boolean renameFile(final Context _context) throws SecurityException
	{
		return _context.getFileStreamPath("test").renameTo(new File("echterfilname"));
	}

	/**
	 * @param _instream
	 * @return
	 * @throws IOException
	 */
	public static StringBuilder asString(final InputStream _instream) throws IOException
	{
		final BufferedReader reader = new BufferedReader(new InputStreamReader(_instream), 4000);

		try
		{
			String line;
			final StringBuilder builder = new StringBuilder(1024);
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}
			return builder;

		}
		finally
		{
			IOUtils.close(reader);
		}

	}
}