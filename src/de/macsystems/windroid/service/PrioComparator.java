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
package de.macsystems.windroid.service;

import java.util.Comparator;

/**
 * Compares Priority's
 * 
 * @author mac
 * @version $Id$
 */
public final class PrioComparator implements Comparator<PRIORITY>
{

	@Override
	public int compare(final PRIORITY p1, final PRIORITY p2)
	{
		if (p1.prio == p2.prio)
		{
			return 0;
		}
		else if (p1.prio > p2.prio)
		{
			return -1;
		}
		return +1;

	}

}
