package de.macsystems.windroid.identifyable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Scheduling Plan for a Station
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class Schedule implements Serializable
{
	private static final long serialVersionUID = 1L;
	// private final List<Repeat> repeats = new ArrayList<Repeat>();

	private final Map<Integer, Repeat> repeats = new HashMap<Integer, Repeat>();

	/**
	 * 
	 */
	public Schedule()
	{
		super();
	}

	/**
	 * 
	 * @param _repeat
	 * @param _time
	 * @param _activ
	 * @throws NullPointerException
	 */
	public Schedule(final Repeat _repeat) throws NullPointerException
	{
		super();
		if (_repeat == null)
		{
			throw new NullPointerException("repeat");
		}
	}

	/**
	 * @return the repeat of days
	 */
	public Repeat getRepeat(final int _day)
	{
		return repeats.get(_day);
	}

	/**
	 * @param _day
	 * @param repeat
	 *            the repeat to add
	 * @throws NullPointerException
	 */
	public void addRepeat(final int _day, final Repeat _repeat) throws NullPointerException
	{
		if (_repeat == null)
		{
			throw new NullPointerException("repeat");
		}
		repeats.put(_day, _repeat);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((repeats == null) ? 0 : repeats.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Schedule other = (Schedule) obj;
		if (repeats == null)
		{
			if (other.repeats != null)
			{
				return false;
			}
		}
		else if (!repeats.equals(other.repeats))
		{
			return false;
		}
		return true;
	}

}
