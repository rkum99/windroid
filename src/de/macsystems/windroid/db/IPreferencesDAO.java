package de.macsystems.windroid.db;

import java.util.Map;

/**
 * Dao Interface for general application preferences.
 * 
 * @author mac
 * @version $Id$
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

	/**
	 * 
	 * persists all entry's into database.
	 * 
	 * @param
	 */
	public void update(final Map<?, ?> sharedPreferences);
}
