package de.macsystems.windroid.db;

/**
 * Dao Interface for general application preferences.
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IPreferencesDAO extends IDAO
{
	/**
	 * Updates a preference
	 * 
	 * @param _key
	 * @param _value
	 */
	public void update(final String _key, String _value);

	/**
	 * Returns a preference by given key, if not found <code>null</code> will be
	 * returned.
	 * 
	 * @param _key
	 * @return
	 */
	public String fetchBy(final String _key);
}
