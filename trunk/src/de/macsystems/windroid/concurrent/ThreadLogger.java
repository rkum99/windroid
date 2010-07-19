package de.macsystems.windroid.concurrent;

import android.util.Log;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class ThreadLogger implements Thread.UncaughtExceptionHandler
{
	private final static String LOG_TAG = ThreadLogger.class.getSimpleName();

	ThreadLogger()
	{
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang
	 * .Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final Thread _thread, final Throwable _ex)
	{
		Log.e(LOG_TAG, "uncaughtException, Name" + _thread.getName() + " prio " + _thread.getPriority(), _ex);
	}

}
