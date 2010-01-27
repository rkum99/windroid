package de.macsystems.windroid.identifyable;

import java.io.Serializable;

/**
 * Scheduling Plan for a Station
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class Schedule implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Repeat repeat;
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
		repeat = _repeat;
		time = _time;
		activ = _activ;
	}

	/**
	 * @return the repeat
	 */
	public Repeat getRepeat()
	{
		return repeat;
	}

	/**
	 * @param repeat
	 *            the repeat to set
	 * @throws NullPointerException
	 */
	public void setRepeat(final Repeat repeat) throws NullPointerException
	{
		if (repeat == null)
		{
			throw new NullPointerException("repeat");
		}
		this.repeat = repeat;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Schedule [activ=" + activ + ", repeat=" + repeat + ", time=" + time + "]";
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
		result = prime * result + ((repeat == null) ? 0 : repeat.hashCode());
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
		if (repeat == null)
		{
			if (other.repeat != null)
			{
				return false;
			}
		}
		else if (!repeat.equals(other.repeat))
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
