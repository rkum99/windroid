package de.macsystems.windroid.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

/**
 * Creates Threads with some default name, which makes debugging a bit easier.
 * 
 * @author mac
 * @version $Id$
 */
public final class ThreadFactory implements java.util.concurrent.ThreadFactory
{

	private final static String LOG_TAG = ThreadFactory.class.getSimpleName();

	private final String threadName = "SpotService-";
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
		final int counter = count.getAndIncrement();
		final String name = threadName + counter;
		thread.setName(threadName + counter);
		Log.d(LOG_TAG, "Created Thread :" + name);
		return thread;
	}
}
