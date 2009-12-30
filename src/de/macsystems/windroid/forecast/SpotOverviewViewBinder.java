package de.macsystems.windroid.forecast;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.WindDirection;

/**
 * @author Jens Hohl
 * @version $Id: SpotOverviewCursorAdapter.java 73 2009-10-25 16:01:54Z
 *          jens.hohl $
 */
public class SpotOverviewViewBinder implements SimpleCursorAdapter.ViewBinder
{

	private static final int SPOT_NAME = 0;
	private static final int SPOT_ID = 2;

	private static final int WIND_TO = 5;
	private static final int WIND_FROM = 6;

	// { ISelectedDAO.COLUMN_NAME,
	// ISelectedDAO.COLUMN_ID,ISelectedDAO.COLUMN_STARTING,ISelectedDAO.COLUMN_TILL
	// };

	// CREATE TABLE IF NOT EXISTS selected (_id INTEGER PRIMARY KEY
	// AUTOINCREMENT, spotid text NOT NULL, activ BOOLEAN, usedirection BOOLEAN,
	// starting TEXT, till TEXT, windmeasure TEXT NOT NULL, minwind INTEGER,
	// maxwind INTEGER);

	boolean retval = false;

	@Override
	public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex)
	{
		Log.d("Test", "Column Index :" + columnIndex);
		if (columnIndex == SPOT_NAME)
		{
			final String name = cursor.getString(columnIndex);
			final TextView tv = (TextView) view;
			tv.setText(name);
		}
		else if (columnIndex == SPOT_ID)
		{
			final TextView tv = (TextView) view;
			tv.setText(cursor.getString(columnIndex));
		}
		else if (columnIndex == WIND_FROM)
		{
			final ImageView tv = (ImageView) view;
			final String id = cursor.getString(columnIndex);
			final int index = IdentityUtil.indexOf(id, WindDirection.values());
			final int resID = WindDirection.values()[index].getImage();
			tv.setImageResource(resID);
		}
		else if (columnIndex == WIND_TO)
		{
			final ImageView tv = (ImageView) view;
			final String id = cursor.getString(columnIndex);
			final int index = IdentityUtil.indexOf(id, WindDirection.values());
			final int resID = WindDirection.values()[index].getImage();
			tv.setImageResource(resID);
		}

		return true;
	}
}
