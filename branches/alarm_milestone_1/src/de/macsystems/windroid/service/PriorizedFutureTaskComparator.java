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
 * Compares {@link PRIORITY}
 * 
 * @author mac
 * @version $Id: PriorizedFutureTaskComparator.java 330 2010-04-24 14:23:11Z
 *          jens.hohl $
 * @see PRIORITY#compareTo(PRIORITY)
 */
final class PriorizedFutureTaskComparator<T> implements Comparator<PriorizedFutureTask>
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final PriorizedFutureTask _t1, final PriorizedFutureTask _t2)
	{
		final PRIORITY p1 = _t1.prio;
		final PRIORITY p2 = _t2.prio;
		return p1.compareTo(p2);
	}
}