package de.macsystems.windroid.identifyable;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.macsystems.windroid.Logging;

import android.util.Log;

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
		if(Logging.isLoggingEnabled())
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
