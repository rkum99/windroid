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
import android.graphics.Region;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public interface IRegionDAO extends IDAO
{
	/**
	 * 
	 * @param _id
	 * @return
	 */
	public Region getById(final int _id);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Cursor fetchByCountryID(final String id);

}
