package de.macsystems.windroid.io.task;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

/**
 * A Stream which counts read bytes and log them when closed.
 * 
 * @author mac
 * @version $Id$
 */
public final class CountInputStream extends FilterInputStream
{

	private final static String LOG_TAG = CountInputStream.class.getSimpleName();

	private long bytes = 0;

	private volatile boolean isClosed = false;

	/**
	 * 
	 * @param _inStream
	 */
	public CountInputStream(final InputStream _inStream)
	{
		super(_inStream);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterInputStream#read(byte[], int, int)
	 */
	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException
	{
		final int bytesRead = super.read(b, off, len);
		bytes += bytesRead;
		return bytesRead;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterInputStream#close()
	 */
	@Override
	public void close() throws IOException
	{
		if (isClosed)
		{
			return;
		}

		try
		{
			super.close();
		}
		finally
		{
			isClosed = true;
			final float kbs = bytes / 1024.0f;
			final float mb = kbs / 1024.0f;
			Log.i(LOG_TAG, "Read :" + bytes + " Byte, " + kbs + " kB, " + mb + " MB");
		}
	}

}
