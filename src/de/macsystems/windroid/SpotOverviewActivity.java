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
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.ISelectedDAO;

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
	private static final int ENABLE_ITEM_ID = 0;
	private static final int DISABLE_ITEM_ID = 1;
	private static final int EDIT_ITEM_ID = 2;
	private static final int FORECAST_ITEM_ID = 3;
	private static final int DELETE_ITEM_ID = 4;

	SimpleCursorAdapter shows = null;

	ISelectedDAO dao = null;

	private Cursor oldCursor = null;

	/**
	 * We cache the ID as when focus lost on context menu to use it.
	 */
	private int selectedID = -1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		// final ISelectedDAO dao = DAOFactory.getSelectedDAO(this);
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
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, "Selected item for Context :" + ((AdapterContextMenuInfo) menuInfo).id);
				}

				final boolean isActiv = dao.isActiv(selectedID);
				menu.add(0, EDIT_ITEM_ID, 0, R.string.spot_overview_spot_edit);
				if (isActiv)
				{
					menu.add(0, FORECAST_ITEM_ID, 0, R.string.spot_overview_spot_forecast);
					menu.add(0, DISABLE_ITEM_ID, 0, R.string.spot_overview_spot_monitoring_disable);
				}
				else
				{
					menu.add(0, ENABLE_ITEM_ID, 0, R.string.spot_overview_spot_monitoring_enable);
				}
				menu.add(0, DELETE_ITEM_ID, 0, R.string.spot_overview_spot_delete);
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
		dao = DAOFactory.getSelectedDAO(this);
		onCreateDAO(dao);
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
		case DISABLE_ITEM_ID:
			setActive(false);
			break;
		case ENABLE_ITEM_ID:
			setActive(true);
			break;
		case EDIT_ITEM_ID:
			editSpot(selectedID);
			break;
		case FORECAST_ITEM_ID:
			showForcastForSpot(selectedID);
			break;
		case DELETE_ITEM_ID:
			deleteSpot(selectedID);
			break;

		default:
			throw new IllegalArgumentException("Unknown Item ID " + _item.getItemId());
		}

		return true;
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
		// final ISelectedDAO dao = DAOFactory.getSelectedDAO(this);
		final SpotConfigurationVO vo = dao.getSpotConfiguration(_id);

		final Intent intent = new Intent(SpotOverviewActivity.this, SpotConfigurationActivity.class);
		intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, vo);
		startActivityForResult(intent, EDIT_SPOT_REQUEST_CODE);
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
			if (Logging.isLoggingEnabled())
			{
				Log.d(LOG_TAG, "Recieved requestcode EDIT_SPOT_REQUEST_CODE");
			}
			// --
			if (resultCode == Activity.RESULT_OK)
			{
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, "Recieved resultcode RESULT_OK");
				}
				updateSpot(data);
			}
			else if (resultCode == Activity.RESULT_CANCELED)
			{
				if (Logging.isLoggingEnabled())
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
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Updating Spot in Database");
		}

		// final ISelectedDAO dao = DAOFactory.getSelectedDAO(this);
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