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
package de.macsystems.windroid.alarm;

import android.app.AlarmManager;

/**
 * @author mac
 * @version $Id$
 */
final class ReleaseStrategie extends BaseStrategie
{
	ReleaseStrategie() throws IllegalArgumentException
	{
		super(7L * AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_FIFTEEN_MINUTES,
				AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15);
	}
}
