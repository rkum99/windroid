package de.macsystems.windroid.io.task;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import de.macsystems.windroid.io.RetryLaterException;

/**
 * Interface for io tasks.
 * 
 * @author mac
 * @version $Id$
 */
public interface Task<V>
{
	/**
	 * 
	 * @param _context
	 * @return
	 * @throws RetryLaterException
	 * @throws IOException
	 */
	public V execute(final Context _context) throws RetryLaterException, IOException;

	/**
	 * @param _instream
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public V process(final Context _context, final InputStream _instream) throws IOException, Exception;
}
