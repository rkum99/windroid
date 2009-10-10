package de.macsystems.windroid.forecast;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class SpotOverviewCursorAdapter implements SimpleCursorAdapter.ViewBinder
{

	private static final int SPOTID_COLUMN = 1;
	private static final int ACTIVE_COLUMN = 2;
	boolean retval = false;

	@Override
	public boolean setViewValue(final View view, final Cursor cursor, final int columnIndex)
	{
		Log.d("", "columnIndex = " + columnIndex);
		Log.d("", "view = " + view);
		if (columnIndex == SPOTID_COLUMN)
		{
			final String name = cursor.getString(SPOTID_COLUMN);
			final TextView tv = (TextView) view;
			tv.setText(name);
			return true;

		}
		if (columnIndex == ACTIVE_COLUMN)
		{
			final int isActive = cursor.getInt(ACTIVE_COLUMN);
			final TextView tv = (TextView) view;
			tv.setText(""+isActive);
			return true;
		}
		return true;
	}
}
