package de.macsystems.windroid.identifyable;

import java.io.Serializable;
import java.util.Calendar;

import de.macsystems.windroid.Util;

/**
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class Repeat implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Day in Week
	 */
	private int dayOfWeek = Calendar.SUNDAY;
	/**
	 * Time 00:00 - 23:59
	 */
	private long dayTime = 12L * 60L * 60L * 1000L;
	/**
	 * is active ?
	 */
	private boolean activ = false;

	// /**
	// * primary key
	// */
	// private final long id;
	// /**
	// * Primary key of selected
	// */
	// private long selectedId;

	/**
	 * @param id
	 */
	public Repeat()
	{
		super();
	}

	/**
	 * @param _dayOfWeek
	 * @param _dayTime
	 * @param _activ
	 */
	public Repeat(final int _dayOfWeek, final long _dayTime, final boolean _activ)
	{
		super();
		if (!Util.isValidDayOfWeek(_dayOfWeek))
		{
			throw new IllegalArgumentException("Invalid day of week:" + _dayOfWeek);
		}
		dayOfWeek = _dayOfWeek;
		dayTime = _dayTime;
		activ = _activ;
	}

	/**
	 * @return the dayOfWeek
	 */
	public int getDayOfWeek()
	{
		return dayOfWeek;
	}

	/**
	 * @param _dayOfWeek
	 *            the dayOfWeek to set
	 */
	public void setDayOfWeek(final int _dayOfWeek)
	{
		this.dayOfWeek = _dayOfWeek;
	}

	/**
	 * @return the dayTime
	 */
	public long getDayTime()
	{
		return dayTime;
	}

	/**
	 * @param _dayTime
	 *            the dayTime to set
	 */
	public void setDayTime(final long _dayTime)
	{
		this.dayTime = _dayTime;
	}

	/**
	 * @return the activ
	 */
	public boolean isActiv()
	{
		return activ;
	}

	/**
	 * @param _activ
	 *            the activ to set
	 */
	public void setActiv(final boolean _activ)
	{
		this.activ = _activ;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Repeat [activ=" + activ + ", dayOfWeek=" + dayOfWeek + ", dayTime=" + dayTime + "]";
	}

}
