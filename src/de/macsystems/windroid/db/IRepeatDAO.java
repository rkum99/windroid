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

import de.macsystems.windroid.identifyable.Repeat;

/**
 * @author mac
 * @version $Id$
 */
public interface IRepeatDAO extends IDAO
{
	public final static String COLUMN_SCHEDULE_ID = "scheduleid";

	public final static String COLUMN_WEEKDAY = "weekday";

	public final static String COLUMN_DAYTIME = "daytime";

	public final static String COLUMN_ACTIV = "activ";

	/**
	 * 
	 * @param _id
	 * @return
	 */
	public Repeat getRepeat(final int _id);

	/**
	 * Updates a repeat
	 * 
	 * @param _repeat
	 */
	public void update(final Repeat _repeat);

	/**
	 * Inserts a newly Repeat
	 * 
	 * @param _repeat
	 */
	public void insert(final Repeat _repeat);
}
