package de.macsystems.windroid.io.task;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public class StationXMLUpdateTask extends IOTask<Void, InputStream>
{

	private final static String LOG_TAG = StationXMLUpdateTask.class.getSimpleName();

	private final String stationsXMLFilePath;

	/**
	 * 
	 * @param _uri
	 * @param _progress
	 * @param _stationsXMLFilePath
	 * @throws NullPointerException
	 */
	public StationXMLUpdateTask(final URI _uri, final String _stationsXMLFilePath, final IProgress _progress)
	{
		super(_uri, _progress);
		if (_stationsXMLFilePath == null)
		{
			throw new NullPointerException("stationsXMLFilePath");
		}
		stationsXMLFilePath = _stationsXMLFilePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.io.task.IOTask#process(android.content.Context,
	 * java.io.InputStream)
	 */
	@Override
	public Void process(final Context _context, final InputStream _instream) throws IOException, Exception
	{
		FileOutputStream fout = null;
		BufferedInputStream inStream = null;
		try
		{
			inStream = new BufferedInputStream(_instream, IOUtils.BIG_BUFFER_SIZE);
			fout = _context.openFileOutput(stationsXMLFilePath, Context.MODE_PRIVATE);
			final byte[] buffer = new byte[IOUtils.BIG_BUFFER_SIZE];
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
			IOUtils.close(fout);
			IOUtils.close(inStream);
		}

		return null;

	}

}
