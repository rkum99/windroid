package de.macsystems.windroid.identifyable;

/**
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IPresenter
{
	/**
	 * Allows to
	 * 
	 * @param _measure
	 */
	public void showAs(final Measure _measure) throws IllegalArgumentException;

	/**
	 * Returns current Measure
	 * 
	 * @return
	 */
	public Measure getMeasure();

	/**
	 * Return value which is converted in current measure
	 * 
	 * @return
	 */
	public float getValue();
}
