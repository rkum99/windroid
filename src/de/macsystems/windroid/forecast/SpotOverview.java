package de.macsystems.windroid.forecast;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
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
	}
}