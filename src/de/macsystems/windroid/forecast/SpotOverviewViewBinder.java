package de.macsystems.windroid.forecast;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.macsystems.windroid.R;
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

	private static final int SPOT_ACTIV = 3;

	/**
	 * Converts integer representing a boolean (0,1) to enabled or disabled icon
	 * resource id.
	 * 
	 * @param value
	 * @return
	 */
	private final static int convertIntToResourceID(final int value)
	{
		return value == 0 ? R.drawable.activ_off : R.drawable.activ_on;
	}

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
		if (columnIndex == SPOT_ACTIV)
		{
			final ImageView iv = (ImageView) view;
			final int resID = convertIntToResourceID(cursor.getInt(columnIndex));
			iv.setBackgroundResource(resID);
		}
		else if (columnIndex == SPOT_NAME)
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
