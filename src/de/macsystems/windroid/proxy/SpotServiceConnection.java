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
import de.macsystems.windroid.ISpotService;
import de.macsystems.windroid.IntentConstants;

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
		Log.d(LOG_TAG, "public void onServiceConnected(ComponentName name, IBinder binder)");

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
		Log.d(LOG_TAG, "public void onServiceDisconnected(ComponentName name)");

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
	public void update(final long _id)
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
