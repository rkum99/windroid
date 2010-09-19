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
 * Simple Factory which creates a concrete alarm handling strategy.
 * 
 * @author mac
 * @version $Id$
 */
public final class AlarmManagerFactory
{
	private final static IAlarmManager strategie = new ReleaseStrategie();

	private AlarmManagerFactory()
	{
	}

	/**
	 * Allows a flexible testing facility.
	 * 
	 * @return
	 */
	public final static IAlarmManager getAlarmManager()
	{
		return strategie;
	}
}
