/**
 This file is part of Windroid.

 Windroid is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Windroid is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Windroid.  If not, see <http://www.gnu.org/licenses/>.

 */
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
