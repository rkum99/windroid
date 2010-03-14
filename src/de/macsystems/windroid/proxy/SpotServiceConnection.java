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
package de.macsystems.windroid.proxy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.service.ISpotService;

/**
 * Proxy Class with implements the ISpotService Interface to control the
 * SpotService Implementation.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class SpotServiceConnection implements ServiceConnection, ISpotService
{

	private final static String LOG_TAG = SpotServiceConnection.class.getSimpleName();

	private final View viewToEnable;

	private volatile ISpotService delegate;

	/**
	 * 
	 * @param _viewToEnable
	 *            typical a Button which toggle service on/off
	 * @param _context
	 * @throws NullPointerException
	 */
	public SpotServiceConnection(final View _viewToEnable, final Context _context) throws NullPointerException
	{
		if (_viewToEnable == null)
		{
			throw new NullPointerException();
		}
		if (_context == null)
		{
			throw new NullPointerException();
		}

		viewToEnable = _viewToEnable;

		final boolean success = _context.bindService(new Intent(
				IntentConstants.DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION), this, Context.BIND_AUTO_CREATE);
		if (!success)
		{
			throw new AndroidRuntimeException("Failed to bind Service.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.ServiceConnection#onServiceConnected(android.content.
	 * ComponentName, android.os.IBinder)
	 */
	@Override
	public void onServiceConnected(final ComponentName name, final IBinder binder)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "public void onServiceConnected(ComponentName name, IBinder binder)");
		}

		delegate = ISpotService.Stub.asInterface(binder);
		viewToEnable.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.ServiceConnection#onServiceDisconnected(android.content
	 * .ComponentName)
	 */
	@Override
	public void onServiceDisconnected(final ComponentName name)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "public void onServiceDisconnected(ComponentName name)");
		}

		delegate = null;

		viewToEnable.setEnabled(false);
	}

	@Override
	public void initAlarms() throws RemoteException
	{
		if (delegate != null)
		{
			delegate.initAlarms();
		}
	}

	@Override
	public void updateAll() throws RemoteException
	{
		if (delegate != null)
		{
			delegate.updateAll();
		}
	}

	@Override
	public void update(final int _id)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void stop() throws RemoteException
	{
		if (delegate != null)
		{
			delegate.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.IInterface#asBinder()
	 */
	@Override
	public IBinder asBinder()
	{
		return delegate.asBinder();
	}

}
