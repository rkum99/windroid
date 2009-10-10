package de.macsystems.windroid.forecast;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import de.macsystems.windroid.R;
import de.macsystems.windroid.db.Database;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.db.SelectedDAO;

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

		// final List<SpotConfigurationVO> spotConfigurations = new
		// ArrayList<SpotConfigurationVO>();
		//
		// for (int i = 0; i < 10; i++)
		// {
		// final SpotConfigurationVO vo = new SpotConfigurationVO();
		// vo.setFromDirection(WindDirection.E);
		// vo.setToDirection(WindDirection.W);
		// vo.setStation(new Station("Test Station", "id", "keyword", true,
		// true,true,true,true,true));
		// vo.setPreferredWindUnit(WindUnit.KNOTS);
		// spotConfigurations.add(vo);
		// }

		// final SpotOverviewAdapter adapter = new SpotOverviewAdapter(this,
		// R.layout.custom_listview_spotoverview,
		// spotConfigurations);

		final ISelectedDAO dao = new SelectedDAO(new Database(this));
		final Cursor c = dao.fetchAll();
		startManagingCursor(c);
		final String[] from = new String[]
		{ "spotid", "activ" };
		final int[] to = new int[]
		{ R.id.custom_spotoverview_name, R.id.custom_spotoverview_tralala };
		final SimpleCursorAdapter shows = new SimpleCursorAdapter(this, R.layout.custom_listview_spotoverview, c, from,
				to);
		shows.setViewBinder(new SpotOverviewCursorAdapter());

		setListAdapter(shows);

	}
}