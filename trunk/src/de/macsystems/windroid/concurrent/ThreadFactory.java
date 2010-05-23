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

package de.macsystems.windroid.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;
import de.macsystems.windroid.Logging;

/**
 * Creates Threads with some default name, which makes debugging a bit easier.
 * This Thread are created with normal priority.
 * 
 * @author mac
 * @version $Id$
 * @TODO: Android seems to ignore the uncaught Exception Handler
 */
public final class ThreadFactory implements java.util.concurrent.ThreadFactory
{

	private final static String LOG_TAG = ThreadFactory.class.getSimpleName();

	private final String threadname;

	private final int threadPriority;
	/**
	 * Reference Counter
	 */
	private final AtomicInteger count = new AtomicInteger(1);

	/**
	 * Creates a threadfactory which produces Threads with given name and
	 * priority. Each created threadname will include a number starting with
	 * '-1'
	 * 
	 * @param _threadName
	 * @param _priority
	 * @throws IllegalArgumentException
	 *             if thread priority is illegal or thread name is null
	 */
	public ThreadFactory(final String _threadName, final int _priority) throws IllegalArgumentException
	{
		if (_priority != Thread.MAX_PRIORITY || _priority != Thread.MIN_PRIORITY || _priority != Thread.NORM_PRIORITY)
		{
			throw new IllegalArgumentException("Illegal Thread priority :" + _priority);
		}
		if (_threadName == null)
		{
			throw new IllegalArgumentException("Thread name must be given");
		}

		threadname = _threadName;
		threadPriority = _priority;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	@Override
	public Thread newThread(final Runnable r)
	{
		final Thread thread = new Thread(r);
		thread.setPriority(threadPriority);
		thread.setUncaughtExceptionHandler(new ThreadLogger());
		final int counter = count.getAndIncrement();
		thread.setName(threadname + "-" + Integer.toString(counter));
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Thread created :" + thread.getName());
		}
		return thread;
	}
}