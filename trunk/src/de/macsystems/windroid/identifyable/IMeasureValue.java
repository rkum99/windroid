package de.macsystems.windroid.identifyable;

/**
 * Interface of a MeasureValue
 * 
 * @author mac
 * @version $Id$
 * @see MeasureValue
 */
public interface IMeasureValue
{

	/**
	 * 
	 * @return
	 */
	public abstract float getValue();

	/**
	 * 
	 * @return
	 */
	public abstract Measure getMeasure();

}