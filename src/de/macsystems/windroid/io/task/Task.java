package de.macsystems.windroid.io.task;

import java.io.IOException;

import android.content.Context;
import de.macsystems.windroid.io.RetryLaterException;

/**
 * Interface for io tasks.
 * 
 * @author mac
 * @version $Id$
 */
public interface Task<V, I>
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
	 * 
	 * @param _context
	 * @param _input
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	V process(final Context _context, final I _input) throws IOException, Exception;
}
