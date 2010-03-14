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

import android.database.Cursor;

/**
 * Defines Methods which can be executed with every DAO.
 * 
 * @author mac
 * @version $Id$
 */
public interface IDAO
{

	/**
	 * Primary key {@value #COLUMN_ID}
	 */
	public final static String COLUMN_ID = "_id";
	/**
	 * Constant for MAX (_id). {@value #COLUMN_MAX_ID}
	 */
	public final static String COLUMN_MAX_ID = "MAX(" + COLUMN_ID + ")";

	/**
	 * Returns size of table
	 * 
	 * @return
	 */
	public int getSize();

	/**
	 * Returns all content of table like a SELECT * FROM table
	 * 
	 * @return
	 */
	public Cursor fetchAll();
}
