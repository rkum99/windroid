package de.macsystems.windroid.forecast;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.SimpleCursorAdapter;
import de.macsystems.windroid.R;
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

	private final static String LOG_TAG = SpotOverview.class.getSimpleName();
	private static final int ENABLE_ITEM_ID = 0;
	private static final int DISABLE_ITEM_ID = 1;
	private static final int EDIT_ITEM_ID = 2;

	Cursor c = null;
	SimpleCursorAdapter shows = null;

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

		final ISelectedDAO dao = DAOFactory.getSelectedDAO(this);
		c = dao.fetchAll();
		startManagingCursor(c);
		setupMapping();
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
			editSpot();
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
		c = dao.fetchAll();
		startManagingCursor(c);

		setupMapping();
		// Log.d(LOG_TAG, "Updating View");
	}

	/**
	 * Code has some problems but at the moment its the only solution i found as
	 * i have to recreate a cursor the binder/cursoradapter.
	 */
	private void setupMapping()
	{
		final String[] from = new String[]
		{ ISelectedDAO.COLUMN_ACTIV, ISelectedDAO.COLUMN_NAME, ISelectedDAO.COLUMN_ID, ISelectedDAO.COLUMN_STARTING,
				ISelectedDAO.COLUMN_TILL };
		final int[] to = new int[]
		{ R.id.custom_spotoverview_activ, R.id.custom_spotoverview_name, R.id.custom_spotoverview_detail,
				R.id.custom_spotoverview_wind_from, R.id.custom_spotoverview_wind_to };
		shows = new SimpleCursorAdapter(this, R.layout.custom_listview_spotoverview, c, from, to);
		shows.setViewBinder(new SpotOverviewViewBinder());
		setListAdapter(shows);
	}

	private void editSpot()
	{
	}

}