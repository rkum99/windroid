package de.macsystems.windroid.progress;

/**
 * 
 * @author mac
 * @version $Id$
 */
public interface IProgress
{
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
