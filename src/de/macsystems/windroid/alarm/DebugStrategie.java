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


/**
 * A Strategie which allows better debugging control.
 * 
 * @author mac
 * @version $Id$
 */
final class DebugStrategie extends BaseStrategie
{
	DebugStrategie() throws IllegalArgumentException
	{
		// super(AlarmManager.INTERVAL_FIFTEEN_MINUTES * 2,
		// AlarmManager.INTERVAL_FIFTEEN_MINUTES / 3, 10L * 1000L);
		super(10 * 1000L, 60 * 1000L, 10L * 1000L);
	}
}
