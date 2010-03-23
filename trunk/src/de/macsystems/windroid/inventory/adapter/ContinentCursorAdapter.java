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
package de.macsystems.windroid.inventory.adapter;

import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * 
 * 
 * @author Jens Hohl
 * @version $Id: ContinentCursorAdapter.java 102 2009-11-07 01:39:30Z jens.hohl
 *          $
 */
public class ContinentCursorAdapter implements SimpleCursorAdapter.ViewBinder
{

	private static final int CONTINENT_ID_COLUMN = 1;
	private static final int CONTINENT_NAME_COLUMN = 2;

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
			tv.setText(name);
		}
		return true;
	}
}