package de.macsystems.windroid.forecast;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.macsystems.windroid.R;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.WindDirection;
import de.macsystems.windroid.identifyable.WindUnit;

/**
 * Renders the listview of selected Spots.
 * 
 * @author Jens Hohl
 * @version $Id: SpotOverviewCursorAdapter.java 73 2009-10-25 16:01:54Z
 *          jens.hohl $
 */
public class SpotOverviewViewBinder implements SimpleCursorAdapter.ViewBinder
{

	private static final int INDEX_SPOT_NAME = 1;
	private static final int INDEX_WINDMEASURE = 2;
	private static final int INDEX_WIND_STARTING = 3;
	private static final int INDEX_WIND_TILL = 4;
	private static final int INDEX_SPOT_ACTIV = 5;

	/**
	 * Converts integer representing a boolean (0,1) of enabled or disabled icon
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
		if (columnIndex == INDEX_SPOT_ACTIV)
		{
			final ImageView iv = (ImageView) view;
			final int resID = convertIntToResourceID(cursor.getInt(columnIndex));
			iv.setBackgroundResource(resID);
		}
		else if (columnIndex == INDEX_SPOT_NAME)
		{
			final String name = cursor.getString(columnIndex);
			final TextView tv = (TextView) view;
			tv.setText(name);
		}
		else if (columnIndex == INDEX_WINDMEASURE)
		{
			final TextView tv = (TextView) view;
			final String id = cursor.getString(columnIndex);
			final WindUnit unit = WindUnit.getById(id);
			tv.setText(unit.name());
		}
		else if (columnIndex == INDEX_WIND_STARTING)
		{
			final ImageView tv = (ImageView) view;
			final String id = cursor.getString(columnIndex);
			final int index = IdentityUtil.indexOf(id, WindDirection.values());
			final int resID = WindDirection.values()[index].getImage();
			tv.setImageResource(resID);
		}
		else if (columnIndex == INDEX_WIND_TILL)
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
