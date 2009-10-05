package de.macsystems.windroid.io.task;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.io.IOUtils;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class StationXMLUpdateTask extends IOTask<Void>
{

	private final static String LOG_TAG = StationXMLUpdateTask.class.getSimpleName();

	private final String stationsXMLFilePath;

	/**
	 * 
	 * @param _uri
	 * @param _stationsXMLFilePath
	 */
	public StationXMLUpdateTask(final URI _uri, final String _stationsXMLFilePath)
	{
		super(_uri);
		if (_stationsXMLFilePath == null)
		{
			throw new NullPointerException();
		}
		stationsXMLFilePath = _stationsXMLFilePath;
	}

	@Override
	public Void process(final Context _context, final InputStream _instream) throws IOException, Exception
	{
		FileOutputStream fout = null;
		final BufferedInputStream inStream = new BufferedInputStream(_instream, IOUtils.DEFAULT_BUFFER_SIZE);
		try
		{
			fout = _context.openFileOutput(stationsXMLFilePath, Context.MODE_PRIVATE);
			final byte[] buffer = new byte[IOUtils.DEFAULT_BUFFER_SIZE];
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
