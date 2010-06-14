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
package de.macsystems.windroid;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import de.macsystems.windroid.db.IDAO;

/**
 * Manages <code>IDAO</code> references to easily discard them if an
 * <code>Activity</code> is stopping.
 * 
 * @author mac
 * @version $Id$
 */
public final class DAOManger
{

	private final static String LOG_TAG = DAOManger.class.getSimpleName();

	private final List<IDAO> daos = new ArrayList<IDAO>(3);

	/**
	 * Add a <code>IDAO</code> which can be later stopped easily. Usual you have
	 * to call this every time your Activity comes in front.
	 * 
	 * 
	 * @param _dao
	 * @throws NullPointerException
	 * @see #onStop()
	 */
	protected void addDAO(final IDAO _dao)
	{
		if (_dao == null)
		{
			throw new NullPointerException("dao");
		}
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "DAO setted.");
		}
		daos.add(_dao);
	}

	/**
	 * Call this method if your activity is going to stop.<br>
	 * It stops each <code>IDAO</code> and removes all references.
	 */
	protected void onStop()
	{
		for (final IDAO dao : daos)
		{
			dao.onStop();
		}
		daos.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "DAOManger [daos=" + daos + "]";
	}
}