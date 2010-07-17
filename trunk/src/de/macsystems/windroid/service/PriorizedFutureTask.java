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
package de.macsystems.windroid.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import de.macsystems.windroid.Logging;

/**
 * A Task with a priority. The Task is able to call back after using
 * {@link IServiceCallbackListener}.
 *
 * @author mac
 * @version $Id$
 */
final class PriorizedFutureTask extends FutureTask<Void>
{

	private final static String LOG_TAG = PriorizedFutureTask.class.getSimpleName();

	protected final PRIORITY prio;

	private final static int TASK_FAILED = 100;
	private final static int TASK_COMPLETED = 200;
	private final static int TASK_CANCELLED = 300;

	private Throwable exception = null;

	private final RemoteCallbackList<IServiceCallbackListener> callbackListener = new RemoteCallbackList<IServiceCallbackListener>();

	/**
	 *
	 * @param _prio
	 * @param _task
	 * @throws NullPointerException
	 *
	 */
	PriorizedFutureTask(final PRIORITY _prio, final Callable<Void> _task) throws NullPointerException
	{
		this(_prio, _task, null);
	}

	/**
	 *
	 * @param _prio
	 * @param _task
	 * @param _listener
	 *            <code>null</code> allowed
	 * @throws NullPointerException
	 * @see {@link FutureTask}
	 *
	 */
	PriorizedFutureTask(final PRIORITY _prio, final Callable<Void> _task, final IServiceCallbackListener _listener)
			throws NullPointerException
	{
		super(_task);
		if (_prio == null)
		{
			throw new NullPointerException("prio");
		}
		prio = _prio;
		if (_listener != null)
		{
			callbackListener.register(_listener);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.concurrent.FutureTask#done()
	 */
	@Override
	protected void done()
	{
		super.done();

		// Dispatch Message
		final Message message = Message.obtain();
		try
		{
			get();
			message.what = isCancelled() ? TASK_CANCELLED : TASK_COMPLETED;
		}
		catch (final InterruptedException e)
		{
			message.what = TASK_FAILED;
		}
		catch (final ExecutionException e)
		{
			Log.d(LOG_TAG, "Done :", e);
			message.what = TASK_FAILED;
		}
		finally
		{
			mHandler.sendMessage(message);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.concurrent.FutureTask#setException(java.lang.Throwable)
	 */
	@Override
	protected void setException(final Throwable _t)
	{
		super.setException(_t);
		exception = _t;

		// if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "sniffed exception :" + exception.getMessage());
		}
	}

	/**
	 * Our Handler used to execute operations on the main thread. This is used
	 * to schedule increments of our value.<br>
	 */
	// TODO: create handler only if any listener exists!
	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(final Message msg)
		{
			// Broadcast to all clients the new value.
			final int nrOfCallbacks = callbackListener.beginBroadcast();
			final int what = msg.what;
			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "broadcast " + what + " to " + nrOfCallbacks + " callback listeners");
			}
			try
			{
				for (int i = 0; i < nrOfCallbacks; i++)
				{
					try
					{
						if (what == TASK_COMPLETED)
						{
							callbackListener.getBroadcastItem(i).onTaskComplete();
						}
						else if (what == TASK_CANCELLED)
						{
							callbackListener.getBroadcastItem(i).onTaskFailed();
						}
						else
						{
							callbackListener.getBroadcastItem(i).onTaskFailed();
						}

					}
					catch (final RemoteException e)
					{
						// The RemoteCallbackList will take care of
						// removing
						// the dead object for us.
					}
				}
			}
			finally
			{
				callbackListener.finishBroadcast();
				callbackListener.kill();
			}
		}

	};
}