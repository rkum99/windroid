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

import android.util.Log;

/**
 * Compares Priority's
 * 
 * @author mac
 * @version $Id$
 */
public final class PrioComparator implements Comparator<ITaskPriority>
{

	private final static String LOG_TAG = PrioComparator.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final ITaskPriority p1, final ITaskPriority p2)
	{
		Log.d(LOG_TAG, "compare");
		return p1.getPriority().compareTo(p2.getPriority());
	}

	@Override
	public boolean equals(final Object obj)
	{
		return false;
	}
}