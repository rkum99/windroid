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

import java.io.IOException;

import android.content.Context;
import de.macsystems.windroid.io.RetryLaterException;

/**
 * Interface for common tasks.
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public interface Task<V, I>
{
	/**
	 * Executes the Task.
	 * 
	 * @param _context
	 * @return
	 * @throws RetryLaterException
	 * @throws IOException
	 */
	public V execute(final Context _context) throws RetryLaterException, IOException, InterruptedException;

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
