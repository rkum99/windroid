package de.macsystems.windroid.forecast;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import de.macsystems.windroid.R;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.ISelectedDAO;

/**
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
		{ "spotid", "activ" };
		final int[] to = new int[]
		{ R.id.custom_spotoverview_name, R.id.custom_spotoverview_detail };
		final SimpleCursorAdapter shows = new SimpleCursorAdapter(this, R.layout.custom_listview_spotoverview, c, from,
				to);
		shows.setViewBinder(new SpotOverviewCursorAdapter());

		setListAdapter(shows);

	}
}