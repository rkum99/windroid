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
package de.macsystems.windroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.service.IServiceCallbackListener;
import de.macsystems.windroid.service.ISpotService;

/**
 * Shows list of spots configured.
 *
 * @author Jens Hohl
 * @version $Id$
 *
 */
public final class SpotOverviewActivity extends DBListActivity
{

	private final static int EDIT_SPOT_REQUEST_CODE = 400;

	private final static String LOG_TAG = SpotOverviewActivity.class.getSimpleName();
	/**
	 *
	 */
	private static final int CONTEXT_ENABLE_ITEM_ID = 0;
	private static final int CONTEXT_DISABLE_ITEM_ID = 1;
	private static final int CONTEXT_EDIT_ITEM_ID = 2;
	private static final int CONTEXT_FORECAST_ITEM_ID = 3;
	private static final int CONTEXT_DELETE_ITEM_ID = 4;
	/**
	 * Option Menu Items
	 */
	private final static int OPTION_MENU_REFRESH_ID = 500;
	private final static int OPTION_MENU_DISABLE_ALL = 510;
	private final static int OPTION_MENU_ENABLE_ALL = 520;

	SimpleCursorAdapter shows = null;

	ISelectedDAO dao = null;

	private Cursor oldCursor = null;

	/**
	 * We cache the ID as when focus lost on context menu to use it.
	 */
	private int selectedID = -1;

	private ISpotService service = null;
	/**
	 * Constant used to display Progress Dialog
	 */
	private final static int UPDATE_SPOT_DIALOG = 1000;

	private final Handler handler = new Handler()
	{
		@Override
		public void handleMessage(final Message _msg)
		{
			try
			{
				showDialog(UPDATE_SPOT_DIALOG);
				service.updateActiveReports(callbackListener);
			}
			catch (final RemoteException e)
			{
				Log.e(LOG_TAG, "failed to call service", e);
			}
		}
	};

	private final ServiceConnection connection = new ServiceConnection()
	{

		@Override
		public void onServiceDisconnected(final ComponentName _name)
		{
			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "onServiceConnected");
			}
			service = null;
		}

		@Override
		public void onServiceConnected(final ComponentName _name, final IBinder _service)
		{
			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "onServiceConnected");
			}
			service = ISpotService.Stub.asInterface(_service);
		}
	};

	/**
	 * Callback Listener for the Activity
	 */
	private final IServiceCallbackListener.Stub callbackListener = new IServiceCallbackListener.Stub()
	{

		@Override
		public void onTaskComplete() throws RemoteException
		{
			removeDialog(UPDATE_SPOT_DIALOG);
		}

		@Override
		public void onTaskFailed() throws RemoteException
		{
			removeDialog(UPDATE_SPOT_DIALOG);
			Toast.makeText(SpotOverviewActivity.this,
					SpotOverviewActivity.this.getString(R.string.forecast_failed_to_load_forecast), Toast.LENGTH_LONG)
					.show();
		}
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		final boolean isBound = bindService(
				new Intent(IntentConstants.DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION), connection,
				Context.BIND_AUTO_CREATE);

		if (!isBound)
		{
			throw new AndroidRuntimeException("Failed to bind service");
		}

		final Cursor c = dao.getConfiguredSpots();
		setupMapping(c);
		//
		getListView().setOnCreateContextMenuListener(new OnCreateContextMenuListener()
		{
			@Override
			public final void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo)
			{
				/**
				 * Cache id as when context menu shows up the focus is lost and
				 * the i returned is invalid.
				 */
				selectedID = (int) ((AdapterContextMenuInfo) menuInfo).id;
				if (Logging.isEnabled)
				{
					Log.d(LOG_TAG, "Selected item for Context :" + ((AdapterContextMenuInfo) menuInfo).id);
				}

				try
				{
					final boolean isActiv = dao.isActiv(selectedID);
					menu.add(0, CONTEXT_EDIT_ITEM_ID, 0, R.string.spot_overview_spot_edit);
					if (isActiv)
					{
						menu.add(0, CONTEXT_FORECAST_ITEM_ID, 0, R.string.spot_overview_spot_forecast);
						menu.add(0, CONTEXT_DISABLE_ITEM_ID, 0, R.string.spot_overview_spot_monitoring_disable);
					}
					else
					{
						menu.add(0, CONTEXT_ENABLE_ITEM_ID, 0, R.string.spot_overview_spot_monitoring_enable);
					}
					menu.add(0, CONTEXT_DELETE_ITEM_ID, 0, R.string.spot_overview_spot_delete);
				}
				catch (final DBException e)
				{
					Log.e(LOG_TAG, "Cant determine if Spot is activ, selected id:" + selectedID, e);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spotoverview);
		Background.apply(this);
		dao = DAOFactory.getSelectedDAO(this);
		daoManager.addDAO(dao);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause()
	{
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "onPause");
		}
		unbindService(connection);
		super.onPause();
	}

	/**
	 *
	 * @return
	 */
	private Dialog createUpdateProgressDialog()
	{
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "createUpdateProgressDialog");
		}
		final ProgressDialog updateDialog = new ProgressDialog(this);
		updateDialog.setTitle(getString(R.string.forecast_progressdialog_title));
		updateDialog.setMessage(getString(R.string.forecast_progressdialog_message));
		updateDialog.setIndeterminate(true);
		updateDialog.setCancelable(true);
		return updateDialog;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(final int _id)
	{
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "onCreateDialog :" + _id);
		}
		Dialog dialog;
		switch (_id)
		{
			case (UPDATE_SPOT_DIALOG):
				dialog = createUpdateProgressDialog();
				break;
			default:
				dialog = null;
				break;
		}

		return dialog;
	}

	/**
	 *
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		final MenuItem about = menu.add(Menu.NONE, OPTION_MENU_REFRESH_ID, Menu.NONE,
				R.string.spot_overview_option_update_all_spots);
		about.setIcon(R.drawable.refresh);

		final MenuItem disableAll = menu.add(Menu.NONE, OPTION_MENU_DISABLE_ALL, Menu.NONE,
				R.string.spot_overview_option_disable_all_spots);
		disableAll.setIcon(R.drawable.disable_all);

		final MenuItem enableAll = menu.add(Menu.NONE, OPTION_MENU_ENABLE_ALL, Menu.NONE,
				R.string.spot_overview_option_enable_all_spots);
		enableAll.setIcon(R.drawable.enable_all);

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(final MenuItem _item)
	{

		switch (_item.getItemId())
		{
			case CONTEXT_DISABLE_ITEM_ID:
				setActive(false);
				break;
			case CONTEXT_ENABLE_ITEM_ID:
				setActive(true);
				break;
			case CONTEXT_EDIT_ITEM_ID:
				editSpot(selectedID);
				break;
			case CONTEXT_FORECAST_ITEM_ID:
				showForcastForSpot(selectedID);
				break;
			case CONTEXT_DELETE_ITEM_ID:
				deleteSpot(selectedID);
				break;

			default:
				throw new IllegalArgumentException("Unknown Item ID " + _item.getItemId());
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		super.onOptionsItemSelected(item);

		boolean result = false;
		if (item.getItemId() == OPTION_MENU_REFRESH_ID)
		{
			handler.sendEmptyMessageDelayed(selectedID, 100L);
			result = true;
		}
		else if (item.getItemId() == OPTION_MENU_DISABLE_ALL)
		{
			dao.setAllActiv(false);
			final Cursor c = dao.getConfiguredSpots();
			setupMapping(c);
			result = true;
		}
		else if (item.getItemId() == OPTION_MENU_ENABLE_ALL)
		{
			dao.setAllActiv(true);
			final Cursor c = dao.getConfiguredSpots();
			setupMapping(c);
			result = true;
		}

		return result;
	}

	private void deleteSpot(final int _id)
	{
		final DialogInterface.OnClickListener listener = new OnClickListener()
		{

			@Override
			public final void onClick(final DialogInterface _dialog, final int _which)
			{
				if (_which == DialogInterface.BUTTON_POSITIVE)
				{
					//
					// final ISelectedDAO dao =
					// DAOFactory.getSelectedDAO(SpotOverviewActivity.this);
					dao.delete(_id);
					final Cursor c = dao.getConfiguredSpots();
					setupMapping(c);
				}
				else
				{
					// do nothing
				}
			}
		};

		final Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.spot_overview_spot_delete_dialog_title);
		builder.setMessage(R.string.spot_overview_spot_delete_dialog_message);
		builder.setCancelable(false);
		builder.setPositiveButton(android.R.string.ok, listener);
		builder.setNegativeButton(android.R.string.cancel, listener);
		final AlertDialog dialog = builder.show();
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}

	/**
	 *
	 * @param _state
	 */
	private void setActive(final boolean _state)
	{
		// final ISelectedDAO dao = DAOFactory.getSelectedDAO(this);
		dao.setActiv((selectedID), _state);
		final Cursor c = dao.getConfiguredSpots();
		setupMapping(c);
	}

	/**
	 * Code has some problems, but at the moment its the only solution i found
	 * as i have to recreate a cursor the binder/cursoradapter to see updates in
	 * database in view.
	 *
	 * @param _cursor
	 */
	private void setupMapping(final Cursor _cursor)
	{
		if (_cursor == null)
		{
			throw new NullPointerException("Cursor");
		}
		// hack
		if (oldCursor != null)
		{
			oldCursor.close();
		}

		startManagingCursor(_cursor);
		oldCursor = _cursor;

		final String[] from = new String[]
		{ "name", "minwind", "maxwind", "windmeasure", "starting", "till", "activ" };
		final int[] to = new int[]
		{ R.id.custom_spotoverview_name, R.id.custom_spotoverview_wind_start, R.id.custom_spotoverview_wind_end,
				R.id.custom_spotoverview_wind_details, R.id.custom_spotoverview_wind_from,
				R.id.custom_spotoverview_wind_to, R.id.custom_spotoverview_activ };
		shows = new SimpleCursorAdapter(this, R.layout.custom_listview_spotoverview, _cursor, from, to);
		shows.setViewBinder(new SpotOverviewViewBinder());
		setListAdapter(shows);
	}

	/**
	 * Creates an {@link SpotConfigurationVO}from primary key and starts
	 * configuration activity.
	 *
	 * @param _id
	 */
	private void editSpot(final long _id)
	{
		try
		{
			final SpotConfigurationVO vo = dao.getSpotConfiguration(_id);
			final Intent intent = new Intent(SpotOverviewActivity.this, SpotConfigurationActivity.class);
			intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, vo);
			startActivityForResult(intent, EDIT_SPOT_REQUEST_CODE);
		}
		catch (final DBException e)
		{
			Log.e(LOG_TAG, "Failed to get configuration for " + _id, e);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == EDIT_SPOT_REQUEST_CODE)
		{
			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "Recieved requestcode EDIT_SPOT_REQUEST_CODE");
			}
			// --
			if (resultCode == Activity.RESULT_OK)
			{
				if (Logging.isEnabled)
				{
					Log.d(LOG_TAG, "Recieved resultcode RESULT_OK");
				}
				updateSpot(data);
			}
			else if (resultCode == Activity.RESULT_CANCELED)
			{
				if (Logging.isEnabled)
				{
					Log.d(LOG_TAG, "Recieved resultCode RESULT_CANCELED");
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("Wrong request recieved. Expected EDIT_SPOT_REQUEST_CODE");
		}
	}

	/**
	 * Updates a Spot
	 *
	 * @param _intent
	 * @throws NullPointerException
	 */
	private void updateSpot(final Intent _intent)
	{
		if (_intent == null)
		{
			throw new NullPointerException("Intent");
		}
		if (!WindUtils.isSpotConfigured(_intent))
		{
			throw new IllegalArgumentException("No SpotConfiguration found.");
		}
		// --
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "Updating Spot in Database");
		}
		// If Edit Spot was chosen we update the Database.
		final SpotConfigurationVO vo = WindUtils.getConfigurationFromIntent(_intent);
		dao.update(vo);
	}

	/**
	 * Shows Forecast of selected Spot
	 *
	 * @param _id
	 */
	private void showForcastForSpot(final int _id)
	{
		final Intent intent = new Intent(SpotOverviewActivity.this, ForecastActivity.class);
		intent.putExtra(IntentConstants.STORED_FORECAST_KEY, _id);
		startActivity(intent);
	}

}