package de.macsystems.windroid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;

/**
 * A Generic BroadcastReciever which enables views whenever connection status
 * changes. Views that trigger network related processes can be disabled to
 * prevent failures relating in network code. <br>
 * <br>
 * <b>You have to unregister this in your Activity#onStop Method</b>
 * 
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class EnableViewConnectionBroadcastReciever extends BroadcastReceiver
{

	private final static String TAG = "UpdateViewReciever";

	private final List<View> viewsToEnable;

	/**
	 * Creates a generic BroadcastReciever which enables views whenever
	 * connection status changes. Views that trigger network related processes
	 * can be disabled to prevent failures relating in network code. <br>
	 * <b>You have to unregister this in your Activity#onStop Method:</b>
	 * 
	 * <pre>
	 * &#064;Override
	 * protected void onStop()
	 * {
	 * 	unregisterReceiver(broadcastReceiver);
	 * 	super.onStop();
	 * }
	 * </pre>
	 * 
	 * 
	 * @param _viewsToEnable
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 *             when empty collection was given.
	 */
	public EnableViewConnectionBroadcastReciever(final Collection<View> _viewsToEnable, final Context _contextToRegister)
	{
		super();
		if (_viewsToEnable == null)
		{
			throw new NullPointerException();
		}
		if (_viewsToEnable.isEmpty())
		{
			throw new IllegalArgumentException("No views given.");
		}
		if (_contextToRegister == null)
		{
			throw new NullPointerException("Need valid Context to register BroadcastReciever.");
		}
		// Create a Copy of the View List
		viewsToEnable = new ArrayList<View>(_viewsToEnable);
		_contextToRegister.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		if (Log.isLoggable(TAG, Log.DEBUG))
		{
			Log.d(TAG, "EnableViewConnectionBroadcastReciever created.");
		}
	}

	@Override
	public void onReceive(final Context ctxt, final Intent intent)
	{
		try
		{
			final boolean isConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			if (Log.isLoggable(TAG, Log.DEBUG))
			{
				Log.i(TAG, "EnableViewConnectionBroadcastReciever: "
						+ (isConnected ? "connection established" : "connection lost"));
			}

			updateViews(viewsToEnable, !isConnected);

		}
		catch (final Exception e)
		{
			Log.e(TAG, "Failed to update views", e);
		}
	}

	/**
	 * Enabled/Disables a list of Views
	 * 
	 * @param _views
	 * @param _enable
	 */
	private final static void updateViews(final List<View> _views, final boolean _enable)
	{
		for (final View view : _views)
		{
			if (view != null)
			{
				view.setEnabled(_enable);
			}
		}
	}
}