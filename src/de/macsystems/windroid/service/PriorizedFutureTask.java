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
import java.util.concurrent.FutureTask;

import android.os.RemoteException;
import android.util.Log;

/**
 * @author mac
 * @version $Id$
 */
final class PriorizedFutureTask extends FutureTask<Void>
{
	final PRIORITY prio;

	private final IServiceCallbackListener listener;

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
	 * @throws NullPointerException
	 * 
	 */
	PriorizedFutureTask(final PRIORITY _prio, final Callable<Void> _task, IServiceCallbackListener _listener)
			throws NullPointerException
	{
		super(_task);
		if (_prio == null)
		{
			throw new NullPointerException("prio");
		}
		prio = _prio;
		listener = _listener;
		if (listener == null)
		{
			Log.d("PriorizedFutureTask", "callbackListener is null.");
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
		Log.d("PriorizedFutureTask", "protected void done()");
		if (listener != null)
		{
			try
			{
				listener.onTaskComplete();
			}
			catch (RemoteException e)
			{
				Log.e("PriorizedFutureTask", "failed to call CallbackListener", e);
			}
		}
		Log.d("PriorizedFutureTask", "protected void done() finished");
		super.done();

	}

}
