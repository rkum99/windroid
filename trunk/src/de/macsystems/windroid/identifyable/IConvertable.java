package de.macsystems.windroid.identifyable;

/**
 * @author Jens
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IConvertable
{
	/**
	 * Converts a MeasureValue
	 * @param _measure
	 * @return
	 */
	public MeasureValue convertTo(final Measure _measure);
}
