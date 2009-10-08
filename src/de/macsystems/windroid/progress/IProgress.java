package de.macsystems.windroid.progress;

/**
 * Interface to report progress
 * 
 * @author mac
 * @version $Id$
 */
public interface IProgress
{
	/**
	 * Sets Maximum
	 */
	public void setMax(final int _maximum);

	/**
	 * Increments progress by 1
	 */
	public void increment();

	/**
	 * Increments progress value by given value
	 * 
	 * @param value
	 */
	public void incrementBy(int value);
}
