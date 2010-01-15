package de.macsystems.windroid.forecast;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.SimpleCursorAdapter;
import de.macsystems.windroid.IntentConstants;
import de.macsystems.windroid.R;
import de.macsystems.windroid.SpotConfiguration;
import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.WindUtils;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.ISelectedDAO;

/**
 * Shows list of spots configured.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class SpotOverview extends ListActivity
{

	private final static int EDIT_SPOT_REQUEST_CODE = 400;

	private final static String LOG_TAG = SpotOverview.class.getSimpleName();
	private static final int ENABLE_ITEM_ID = 0;
	private static final int DISABLE_ITEM_ID = 1;
	private static final int EDIT_ITEM_ID = 2;

	SimpleCursorAdapter shows = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		final ISelectedDAO dao = DAOFactory.getSelectedDAO(this);
		final Cursor c = dao.fetchAll();
		setupMapping(c);
		//
		getListView().setOnCreateContextMenuListener(new OnCreateContextMenuListener()
		{
			@Override
			public final void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo)
			{
				final long selectedItemID = getSelectedItemId();
				final boolean isActiv = dao.isActiv(selectedItemID);
				menu.add(0, EDIT_ITEM_ID, 0, R.string.spot_overview_spot_edit);
				if (isActiv)
				{
					menu.add(0, DISABLE_ITEM_ID, 0, R.string.spot_overview_spot_monitoring_disable);
				}
				else
				{
					menu.add(0, ENABLE_ITEM_ID, 0, R.string.spot_overview_spot_monitoring_enable);
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(final MenuItem __item)
	{
		switch (__item.getItemId())
		{
		case DISABLE_ITEM_ID:
			setActive(false);
			break;
		case ENABLE_ITEM_ID:
			setActive(true);
			break;
		case EDIT_ITEM_ID:
			editSpot(getSelectedItemId());
			break;
		default:
			throw new IllegalArgumentException("Unkown Item ID " + __item.getItemId());
		}

		return true;
	}

	private void setActive(final boolean _state)
	{
		final ISelectedDAO dao = DAOFactory.getSelectedDAO(this);
		dao.setActiv(getSelectedItemId(), _state);
		final Cursor c = dao.fetchAll();
		setupMapping(c);
		// Log.d(LOG_TAG, "Updating View");
	}

	/**
	 * Code has some problems, but at the moment its the only solution i found
	 * as i have to recreate a cursor the binder/cursoradapter to see updates in
	 * database in view.
	 * 
	 * @param _cursor
	 */
	private void setupMapping(Cursor _cursor)
	{
		if (_cursor == null)
		{
			throw new NullPointerException("Cursor");
		}
		startManagingCursor(_cursor);
		final String[] from = new String[]
		{ ISelectedDAO.COLUMN_ACTIV, ISelectedDAO.COLUMN_NAME, ISelectedDAO.COLUMN_ID, ISelectedDAO.COLUMN_STARTING,
				ISelectedDAO.COLUMN_TILL };
		final int[] to = new int[]
		{ R.id.custom_spotoverview_activ, R.id.custom_spotoverview_name, R.id.custom_spotoverview_detail,
				R.id.custom_spotoverview_wind_from, R.id.custom_spotoverview_wind_to };
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
		final ISelectedDAO dao = DAOFactory.getSelectedDAO(this);
		final SpotConfigurationVO vo = dao.getSpotConfiguration(_id);

		final Intent intent = new Intent(SpotOverview.this, SpotConfiguration.class);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == EDIT_SPOT_REQUEST_CODE)
		{
			Log.d(LOG_TAG, "Recieved requestcode EDIT_SPOT_REQUEST_CODE");
			if (resultCode == Activity.RESULT_OK)
			{
				Log.d(LOG_TAG, "Recieved resultcode RESULT_OK");
				updateSpot(data);
			}
			else if (resultCode == Activity.RESULT_CANCELED)
			{
				Log.d(LOG_TAG, "Recieved resultCode RESULT_CANCELED");
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

		Log.d(LOG_TAG, "Updating Spot in Database");

		final ISelectedDAO dao = DAOFactory.getSelectedDAO(this);
		final SpotConfigurationVO vo = WindUtils.getConfigurationFromIntent(_intent);
		dao.update(vo);

	}

}