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

import de.macsystems.windroid.db.IConfigDAO;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public final class ConfigImpl extends BaseImpl implements IConfigDAO
{

	public final String DB_STATUS = "db_status";

	/**
	 * 
	 * @param _database
	 */
	public ConfigImpl(final Database _database)
	{
		this(_database, NullProgressAdapter.INSTANCE);
	}

	/**
	 * 
	 * @param _database
	 * @param _progress
	 */
	public ConfigImpl(final Database _database, final IProgress _progress)
	{
		super(_database, "config", _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IConfigDAO#getDatabaseStatus()
	 */
	public String getStatus()
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IConfigDAO#setStatus(java.lang.String)
	 */
	public boolean setStatus(final String _status)
	{
		throw new UnsupportedOperationException();
	}
}