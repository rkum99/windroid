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
 * 
 * @author mac
 * @version $Id$
 */
public final class ThreadFactory implements java.util.concurrent.ThreadFactory
{

	private final static String LOG_TAG = ThreadFactory.class.getSimpleName();

	private final static String THREADNAME = "SpotService-";
	/**
	 * Reference Counter
	 */
	private final AtomicInteger count = new AtomicInteger(0);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	@Override
	public Thread newThread(final Runnable r)
	{
		final Thread thread = new Thread(r);
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.setUncaughtExceptionHandler(new ThreadLogger());
		final int counter = count.getAndIncrement();
		final String name = THREADNAME + counter;
		thread.setName(THREADNAME + counter);
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Thread created :" + name);
		}
		return thread;
	}
}