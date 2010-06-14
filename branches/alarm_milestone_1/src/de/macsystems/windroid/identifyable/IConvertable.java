package de.macsystems.windroid.identifyable;

import java.util.Set;

/**
 * a MeasureValue may supports conversion into different measures using this
 * interface.
 * 
 * @author Jens
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IConvertable extends IMeasureValue
{
	/**
	 * Converts a MeasureValue into any Measure it supports.
	 * 
	 * @param _measure
	 * @throws IllegalArgumentException
	 *             see {@link #getSupportedMeasure()}
	 * 
	 */
	public void convertTo(final Measure _measure) throws IllegalArgumentException;

	/**
	 * Returns a set of measures which are supported for conversion.
	 * 
	 * @return
	 */
	public Set<Measure> getSupportedMeasure();
}
