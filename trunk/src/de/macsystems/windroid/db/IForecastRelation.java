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
package de.macsystems.windroid.db;

import java.util.Set;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IForecastRelation extends IDAO
{
	/**
	 * {@value #COLUMN_FORECAST_ID}
	 */
	public final static String COLUMN_FORECAST_ID = "forecastid";
	/**
	 * @value {@link #COLUMN_SELECTED_ID}
	 */
	public final static String COLUMN_SELECTED_ID = "selectedid";

	/**
	 * Returns integer set of all columns to delete as their became invalid due
	 * to update. Returned Integers represent the 'forecastID'.
	 * 
	 * @param _db
	 * @param _selectedID
	 * @return a set with forecast IDs
	 * @see IForecastRelation#COLUMN_FORECAST_ID
	 */
	Set<Integer> getRowsToDelete(final SQLiteDatabase _db, final int _selectedID);

}
