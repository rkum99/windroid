package de.macsystems.windroid.forecast;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
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
	private static final int EDIT_ID = 0;
	private static final int DELETE_ID = 1;

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
		final Cursor c = dao.fetchAll();
		startManagingCursor(c);
		final String[] from = new String[]
		{ ISelectedDAO.COLUMN_NAME, ISelectedDAO.COLUMN_ID, ISelectedDAO.COLUMN_STARTING, ISelectedDAO.COLUMN_TILL };
		final int[] to = new int[]
		{ R.id.custom_spotoverview_name, R.id.custom_spotoverview_detail, R.id.custom_spotoverview_wind_from,
				R.id.custom_spotoverview_wind_to };
		final SimpleCursorAdapter shows = new SimpleCursorAdapter(this, R.layout.custom_listview_spotoverview, c, from,
				to);
		shows.setViewBinder(new SpotOverviewViewBinder());
		setListAdapter(shows);
		//
		getListView().setOnCreateContextMenuListener(new OnCreateContextMenuListener()
		{
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
			{
				Log.d(LOG_TAG, "public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)");
//				menu.setHeaderTitle("ContextMenu");

				final long selectedItemID = getSelectedItemId();
				Log.d(LOG_TAG, "selected ID = " + selectedItemID);
				boolean isActiv = dao.isActiv(selectedItemID);
				menu.add(0, 0, 0, "Bearbeiten");
				if (isActiv)
				{
					menu.add(0, 1, 0, R.string.spot_overview_spot_monitoring_disable);
				}
				else
				{
					menu.add(0, 1, 0, R.string.spot_overview_spot_monitoring_enable);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Log.d(LOG_TAG, "On Create Options Menu");
		menu.add("Disable");
		menu.add("Ensable");
		return super.onCreateOptionsMenu(menu);
	}

}