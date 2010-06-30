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
package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import de.macsystems.windroid.db.ICountryDAO;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * DAO for table 'country'
 * 
 * @author mac
 * @version $Id$
 */
public final class CountryImpl extends BaseImpl implements ICountryDAO
{

	/**
	 * @param database
	 * @param progress
	 */
	public CountryImpl(final Database database, final IProgress progress)
	{
		super(database, "country", progress);
	}

	/**
	 * @param database
	 */
	public CountryImpl(final Database database)
	{
		this(database, NullProgressAdapter.INSTANCE);
	}

	@Override
	public Cursor fetchByContinentID(final String _id)
	{
		return fetchBy("continentid", _id);
	}

}
