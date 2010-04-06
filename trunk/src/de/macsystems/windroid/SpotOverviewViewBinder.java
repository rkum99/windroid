/**
 This file is part of Windroid.

 Windroid is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Windroid is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Windroid.  If not, see <http://www.gnu.org/licenses/>.

 */
package de.macsystems.windroid;

import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import de.macsystems.windroid.identifyable.CardinalDirection;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.WindUnit;

/**
 * Renders the listview of selected Spots.
 * 
 * @author Jens Hohl
 * @version $Id: SpotOverviewCursorAdapter.java 73 2009-10-25 16:01:54Z
 *          jens.hohl $
 */
public final class SpotOverviewViewBinder implements SimpleCursorAdapter.ViewBinder
{
	/**
	 * Index is depending on SQL column order.
	 */
	private static final int INDEX_SPOT_NAME = 1;
	// private static final int INDEX_KEYWORD = 2;
	private static final int INDEX_WIND_START = 3;
	private static final int INDEX_WIND_END = 4;
	private static final int INDEX_WINDUNIT_ID = 5;
	private static final int INDEX_WIND_DIRECTION_START = 6;
	private static final int INDEX_WIND_DIRECTION_END = 7;
	private static final int INDEX_SPOT_ACTIV = 8;

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
		else if (columnIndex == INDEX_WIND_START)
		{
			final String name = cursor.getString(columnIndex);
			final TextView tv = (TextView) view;
			tv.setText(name);
		}
		else if (columnIndex == INDEX_WIND_END)
		{
			final String name = cursor.getString(columnIndex);
			final TextView tv = (TextView) view;
			tv.setText(name);
		}

		else if (columnIndex == INDEX_WINDUNIT_ID)
		{
			final TextView tv = (TextView) view;
			final String id = cursor.getString(columnIndex);
			final WindUnit unit = WindUnit.getById(id);
			String name = (String) tv.getText();
			name = name.replace("$measure", unit.name());
			tv.setText(name);
		}
		else if (columnIndex == INDEX_WIND_DIRECTION_START)
		{
			final ImageView tv = (ImageView) view;
			final String id = cursor.getString(columnIndex);
			final int index = IdentityUtil.indexOf(id, CardinalDirection.values());
			final int resID = CardinalDirection.values()[index].getImage();
			tv.setImageResource(resID);
		}
		else if (columnIndex == INDEX_WIND_DIRECTION_END)
		{
			final ImageView tv = (ImageView) view;
			final String id = cursor.getString(columnIndex);
			final int index = IdentityUtil.indexOf(id, CardinalDirection.values());
			final int resID = CardinalDirection.values()[index].getImage();
			tv.setImageResource(resID);
		}

		return true;
	}
}
