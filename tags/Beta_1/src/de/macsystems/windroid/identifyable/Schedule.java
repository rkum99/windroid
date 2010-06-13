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
package de.macsystems.windroid.identifyable;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;
import de.macsystems.windroid.Logging;

/**
 * This is some glue Code that a SpotConfiguration did not contain so many
 * fields/methods.
 * 
 * @author mac
 * @version $Id$
 */
public final class Schedule implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final static String LOG_TAG = Schedule.class.getSimpleName();

	/**
	 * Maps weekdays like Calendar#MONDAY to repeat.
	 */
	private final Map<Integer, Repeat> repeats = Collections.synchronizedMap(new HashMap<Integer, Repeat>());

	public Schedule()
	{
		super();
	}

	/**
	 * Returns an Iterator over all <code>Repeat</code> in this schedule.
	 * 
	 * @return
	 * @see #getRepeat(int)
	 */
	public Iterator<Integer> getRepeatIterator()
	{
		return repeats.keySet().iterator();
	}

	/**
	 * @return the repeat by repeatid.
	 */
	public Repeat getRepeat(final int _repeatID)
	{
		return repeats.get(_repeatID);
	}

	/**
	 * @param repeat
	 *            the repeat to add
	 * @throws NullPointerException
	 */
	public void addRepeat(final Repeat _repeat) throws NullPointerException
	{
		if (_repeat == null)
		{
			throw new NullPointerException("repeat");
		}

		final int id = _repeat.getDayOfWeek();
		repeats.put(id, _repeat);
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Added repeat on schedule: " + _repeat.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Schedule [repeats=" + repeats + "]";
	}

}
