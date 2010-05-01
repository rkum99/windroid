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

import de.macsystems.windroid.identifyable.Schedule;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IScheduleDAO extends IDAO
{

	public final static String COLUMN_REPEAT_ID = "repeatid";

	public final static String COLUMN_SELECTED_ID = "selectedid";

	public final static String COLUMN_ACTIV = "activ";

	public boolean isActiv(final int _id);

	public Schedule getSchedule(final int _id);

	public Schedule getScheduleByScheduleID(final int _selectedID) throws DBException;

	public long getTime(final int _id);

	public void insert(final Schedule _schedule, final int _selectedID);
}
