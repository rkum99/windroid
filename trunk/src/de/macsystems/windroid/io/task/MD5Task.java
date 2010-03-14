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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;

/**
 * IO Task which returns MD5 Checksum on Server
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public class MD5Task extends IOTask<String, InputStream>
{

	private final static String LOG_TAG = MD5Task.class.getSimpleName();

	/**
	 * 
	 * @param _uri
	 * @param _progress
	 * @throws NullPointerException
	 */
	public MD5Task(final URI _uri, final IProgress _progress) throws NullPointerException
	{
		super(_uri, _progress);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.io.task.IOTask#process(android.content.Context,
	 * java.io.InputStream)
	 */
	@Override
	public String process(final Context _context, final InputStream _instream) throws IOException, Exception
	{
		final StringBuilder builder = new StringBuilder(128);
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(_instream));
			String line;
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}

		}
		finally
		{
			IOUtils.close(reader);
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

}
