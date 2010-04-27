package de.macsystems.windroid.proxy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.AndroidRuntimeException;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.service.IServiceCallbackListener;
import de.macsystems.windroid.service.ISpotService;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class UpdateConnection implements ServiceConnection
{

	private final static String LOG_TAG = UpdateConnection.class.getSimpleName();

	final Context context;

	private ISpotService service;

	public UpdateConnection(final Context _context)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "UpdateConnection::UpdateConnection");
		}

		if (_context == null)
		{
			throw new NullPointerException("context");
		}
		context = _context;
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
	public void onServiceConnected(final ComponentName name, final IBinder _service)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onServiceConnected " + name + " " + _service);
		}
		service = (ISpotService) _service;
	}

	public void update(final int _selectedID) throws RemoteException
	{
		if (service == null)
		{
			throw new AndroidRuntimeException("Service not bound");
		}

		service.update(_selectedID, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.ServiceConnection#onServiceDisconnected(android.content
	 * .ComponentName)
	 */
	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onServiceDisconnected");
		}
		context.unbindService(this);
		service = null;
	}

	private IServiceCallbackListener listener = new IServiceCallbackListener.Stub()
	{

		@Override
		public void onTaskComplete() throws RemoteException
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "onTaskComplete");
			}

		}

		@Override
		public void onTaskFailed() throws RemoteException
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "onTaskFailed");
			}

		}

		@Override
		public void onTaskStatusChange(int currentValue, int maxValue) throws RemoteException
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "onTaskStatusChange");
			}
		}

	};

}