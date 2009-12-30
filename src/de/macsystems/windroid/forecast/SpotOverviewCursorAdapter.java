package de.macsystems.windroid.forecast;

import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * @author Jens Hohl
 * @version $Id: SpotOverviewCursorAdapter.java 73 2009-10-25 16:01:54Z
 *          jens.hohl $
 */
public class SpotOverviewCursorAdapter implements SimpleCursorAdapter.ViewBinder
{

	private static final int SPOTID_COLUMN = 3;
	private static final int ACTIVE_COLUMN = 2;

	// CREATE TABLE IF NOT EXISTS selected (_id INTEGER PRIMARY KEY
	// AUTOINCREMENT, spotid text NOT NULL, activ BOOLEAN, usedirection BOOLEAN,
	// starting TEXT, till TEXT, windmeasure TEXT NOT NULL, minwind INTEGER,
	// maxwind INTEGER);

	boolean retval = false;

	@Override
	public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex)
	{
		if (columnIndex == SPOTID_COLUMN)
		{
			final String name = cursor.getString(SPOTID_COLUMN);
			final TextView tv = (TextView) view;
			tv.setText(name);
			return true;

		}
		else if (columnIndex == ACTIVE_COLUMN)
		{
			;
			final TextView tv = (TextView) view;
			tv.setText(cursor.getString(ACTIVE_COLUMN));
			return true;
		}
		return true;
	}
}
