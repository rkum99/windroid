package de.macsystems.windroid.service;

import android.os.IBinder;
import android.os.RemoteException;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class NullServiceCallbackListener implements IServiceCallbackListener
{
	/**
	 * Instance to use.
	 */
	public final static NullServiceCallbackListener INSTANCE = new NullServiceCallbackListener();

	private IBinder binder = null;

	private NullServiceCallbackListener()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.service.IServiceCallbackListener#onTaskComplete()
	 */
	@Override
	public void onTaskComplete() throws RemoteException
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.service.IServiceCallbackListener#onTaskFailed()
	 */
	@Override
	public void onTaskFailed() throws RemoteException
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.service.IServiceCallbackListener#onTaskStatusChange
	 * (int, int)
	 */
	@Override
	public void onTaskStatusChange(int currentValue, int maxValue) throws RemoteException
	{
	}

	@Override
	public IBinder asBinder()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
