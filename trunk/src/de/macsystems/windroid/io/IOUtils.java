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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.IPreferencesDAO;
import de.macsystems.windroid.io.task.StationXMLUpdateTask;
import de.macsystems.windroid.progress.IProgress;

/**
 * Utility Class for IO related tasks.
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public class IOUtils
{

	private IOUtils()
	{
	}

	private static final String LOG_TAG = IOUtils.class.getSimpleName();

	public static final int BIG_BUFFER_SIZE = 1024 * 32;

	public static final int DEFAULT_BUFFER_SIZE = 1024;

	/**
	 * Complete Path to configuration file.
	 * 
	 * @deprecated used to store md5 which should be stored in Database
	 */
	@Deprecated
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
			Log.i(LOG_TAG, "Network not reachable");
			return false;
		}

		final State networkState = systemService.getActiveNetworkInfo().getState();
		final boolean isRoamingNow = systemService.getActiveNetworkInfo().isRoaming();
		// Query User Configuration
		final IPreferencesDAO dao = DAOFactory.getPreferencesDAO(_context);
		boolean userWantIOWhileRoaming = false;
		try
		{
			userWantIOWhileRoaming = dao.useNetworkWhileRoaming();
		}
		catch (final DBException e)
		{
			Log.e(LOG_TAG, "Failed to Query DB", e);
		}

		if (State.CONNECTED == networkState)
		{
			if (userWantIOWhileRoaming)
			{
				return true;
			}
			else
			{
				return isRoamingNow ? false : true;
			}
		}
		return false;
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
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Attempt updating configuration.");
		}
		if (_properties == null)
		{
			throw new NullPointerException("Properties are null.");
		}
		final StringBuffer comments = new StringBuffer(256);
		comments.append("Updated: ");
		comments.append(System.currentTimeMillis());
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
		if (Logging.isLoggingEnabled())
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
	 * @throws InterruptedException 
	 */
	public static void updateCachedStationXML(final Context _context, final URL _stationXMLURL,
			final IProgress _progress) throws RetryLaterException, IOException, InterruptedException
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
	 * closes inputstream, parameter can be null.
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
	 * disconnects connection, parameter can be null.
	 * 
	 * @param _connection
	 */
	public final static void disconnect(final HttpURLConnection _connection)
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
	 * closes reader, parameter can be null.
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
	 * closes stream, parameter can be null.
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
	 * Returns an <code>Uri</code> to an Resource in form:
	 * android.resource://de.macsystems.windroid//id
	 * 
	 * @param _context
	 * @param _resourceId
	 * @return
	 * @see Uri
	 */
	public static Uri getResourceURI(final Context _context, final int _resourceId)
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
		builder.append(_resourceId);

		return Uri.parse(builder.toString());
	}

	/**
	 * closes database, parameter can be null.
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
	 * closes statement, parameter can be null.
	 * 
	 * @param _statement
	 */
	public final static void close(final SQLiteStatement _statement)
	{
		if (_statement != null)
		{
			_statement.close();
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
	 * Reads from given <code>InputStream</code> into a
	 * <code>StringBuffer</code>.
	 * 
	 * @param _instream
	 * @return
	 * @throws IOException
	 */
	public static StringBuilder asString(final InputStream _instream) throws IOException
	{
		if (_instream == null)
		{
			throw new NullPointerException("InputStream");
		}

		final BufferedReader reader = new BufferedReader(new InputStreamReader(_instream), 4000);

		try
		{
			String line;
			final StringBuilder builder = new StringBuilder(DEFAULT_BUFFER_SIZE);
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}
			return builder;

		}
		finally
		{
			close(reader);
		}

	}

	/**
	 * Reads a text file line by line.
	 * 
	 * @param _context
	 * @param _resourceId
	 * @return
	 * @throws Resources.NotFoundException
	 * @throws IOException
	 */
	public static List<String> readTextfile(final Context _context, final int _resourceId)
			throws Resources.NotFoundException,
			IOException
	{
		BufferedReader bufReader = null;
		InputStream inStream = null;
		InputStreamReader inReader = null;
		final List<String> list = new ArrayList<String>();
		try
		{
			inStream = _context.getResources().openRawResource(_resourceId);
			inReader = new InputStreamReader(inStream);
			bufReader = new BufferedReader(inReader);

			String line;
			while ((line = bufReader.readLine()) != null)
			{
				list.add(line);
			}
		}
		finally
		{
			close(inStream);
			close(inReader);
			close(bufReader);
		}
		return list;

	}

	/**
	 * Closes Cursor.
	 * 
	 * @param _cursor
	 */
	public static void close(final Cursor _cursor)
	{
		if (_cursor != null)
		{
			_cursor.close();
		}

	}
}