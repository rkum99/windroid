package de.macsystems.windroid.identifyable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Scheduling Plan for a Station
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class Schedule implements Serializable
{
	private static final long serialVersionUID = 1L;
	private final List<Repeat> repeats = new ArrayList<Repeat>();
	private long time;
	private boolean activ;

	/**
	 * 
	 * @param _repeat
	 * @param _time
	 * @param _activ
	 * @throws NullPointerException
	 */
	public Schedule(final Repeat _repeat, final long _time, final boolean _activ) throws NullPointerException
	{
		if (_repeat == null)
		{
			throw new NullPointerException("repeat");
		}
		repeats.add(_repeat);
		time = _time;
		activ = _activ;
	}

	/**
	 * @param time
	 * @param activ
	 */
	public Schedule(final long time, final boolean activ)
	{
		super();
		this.time = time;
		this.activ = activ;
	}

	/**
	 * @return the repeat
	 */
	public List<Repeat> getRepeat()
	{
		return new ArrayList<Repeat>(repeats);
	}

	/**
	 * @param repeat
	 *            the repeat to add
	 * @throws NullPointerException
	 */
	public void addRepeat(final Repeat repeat) throws NullPointerException
	{
		if (repeat == null)
		{
			throw new NullPointerException("repeat");
		}
		repeats.add(repeat);
	}

	/**
	 * Removes repeat at index;
	 * 
	 * @param _index
	 * @throws IndexOutOfBoundsException
	 */
	public void removeRepeat(final int _index) throws IndexOutOfBoundsException
	{
		repeats.remove(_index);
	}

	/**
	 * Removes repeat with id.
	 * 
	 * @param _id
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public boolean removeRepeat(final long _id) throws IndexOutOfBoundsException
	{
		for (int i = repeats.size() - 1; i > 0; i--)
		{
			if (repeats.get(i).getId() == _id)
			{
				repeats.remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the time
	 */
	public long getTime()
	{
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(final long time)
	{
		this.time = time;
	}

	/**
	 * @return the activ
	 */
	public boolean isActiv()
	{
		return activ;
	}

	/**
	 * @param activ
	 *            the activ to set
	 */
	public void setActive(final boolean activ)
	{
		this.activ = activ;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Schedule [activ=" + activ + ", repeats=" + repeats + ", time=" + time + "]";
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
		result = prime * result + (activ ? 1231 : 1237);
		result = prime * result + ((repeats == null) ? 0 : repeats.hashCode());
		result = prime * result + (int) (time ^ (time >>> 32));
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
		if (activ != other.activ)
		{
			return false;
		}
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
		if (time != other.time)
		{
			return false;
		}
		return true;
	}

}
