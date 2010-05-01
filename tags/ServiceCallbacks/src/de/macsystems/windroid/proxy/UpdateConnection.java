package de.macsystems.windroid.proxy;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
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

	public final int SUCCESS = 100;

	public final int FAILED = 200;

	private final static String LOG_TAG = UpdateConnection.class.getSimpleName();

	private final Context context;

	private ISpotService service;

	private final Object LOCK = new Object();

	private final int selectedID;

	private Dialog dialog = null;

	private final Handler handler;

	/**
	 * 
	 * @param _context
	 */
	public UpdateConnection(final Context _context, final Handler _handler, final int _selectedID)
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
		handler = _handler;
		selectedID = _selectedID;
		final boolean success = _context.bindService(new Intent(
				IntentConstants.DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION), this, Context.BIND_AUTO_CREATE);
		if (!success)
		{
			throw new AndroidRuntimeException("Failed to bind Service.");
		}
	}

	public void setDialog(final Dialog _dialog)
	{
		synchronized (LOCK)
		{
			if (dialog != null)
			{
				dialog.dismiss();
			}
			dialog = _dialog;
		}
	}

	/**
	 * Unbind this ServiceConnection.
	 */
	public void unbind()
	{
		context.unbindService(this);
		synchronized (LOCK)
		{
			service = null;
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
	public void onServiceConnected(final ComponentName _name, final IBinder _service)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onServiceConnected " + _name + " " + _service);
		}
		synchronized (LOCK)
		{
			service = (ISpotService) _service;
		}
		try
		{
			update(selectedID);
		}
		catch (final RemoteException e)
		{
			Log.e(LOG_TAG, "failed to update spot with selectedid : " + selectedID, e);
		}
	}

	/**
	 * Adds update task for Spot with selected ID into service
	 * 
	 * @param _selectedID
	 * @throws RemoteException
	 */
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
	public void onServiceDisconnected(final ComponentName _name)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "onServiceDisconnected");
		}
		service = null;
	}

	private final IServiceCallbackListener listener = new IServiceCallbackListener.Stub()
	{

		@Override
		public void onTaskComplete() throws RemoteException
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "onTaskComplete");
			}
			dialog.dismiss();
			handler.sendEmptyMessage(SUCCESS);
		}

		@Override
		public void onTaskFailed() throws RemoteException
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "onTaskFailed");
			}
			dialog.dismiss();
			handler.sendEmptyMessage(FAILED);
		}

		@Override
		public void onTaskStatusChange(final int currentValue, final int maxValue) throws RemoteException
		{
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "onTaskStatusChange");
			}
			dialog.dismiss();
			handler.sendEmptyMessage(FAILED);
		}

	};

}