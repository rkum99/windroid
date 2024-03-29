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
 * Define Priority of a Task
 * 
 * @author mac
 * @version $Id$
 */
public enum PRIORITY implements Comparator<PRIORITY>
{
	NORMAL(100), USER_REQUEST(1000);

	private final int prio;

	private PRIORITY(final int _prio)
	{
		prio = _prio;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
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
