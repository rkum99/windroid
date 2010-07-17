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
package de.macsystems.windroid.io.task;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.Logging;
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
			if (Logging.isEnabled)
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
