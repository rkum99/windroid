package de.macsystems.windroid.inventory.adapter;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * @author Jens Hohl
 * @version $Id: ContinentCursorAdapter.java 102 2009-11-07 01:39:30Z jens.hohl
 *          $
 */
public class ContinentCursorAdapter implements SimpleCursorAdapter.ViewBinder
{

	private static final int CONTINENT_ID_COLUMN = 1;
	private static final int CONTINENT_NAME_COLUMN = 2;
	boolean retval = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SimpleCursorAdapter.ViewBinder#setViewValue(android.view
	 * .View, android.database.Cursor, int)
	 */
	@Override
	public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex)
	{
		Log.d("", "columnIndex = " + columnIndex);
		Log.d("", "view = " + view);
		if (columnIndex == CONTINENT_ID_COLUMN)
		{
			final String name = cursor.getString(CONTINENT_ID_COLUMN);
			final TextView tv = (TextView) view;
			tv.setText(name);
		}
		else if (columnIndex == CONTINENT_NAME_COLUMN)
		{
			final String name = cursor.getString(CONTINENT_NAME_COLUMN);
			final TextView tv = (TextView) view;
			tv.setText("" + name);
		}
		return true;
	}
}