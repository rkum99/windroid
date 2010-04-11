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

import android.app.ListActivity;
import android.util.Log;
import de.macsystems.windroid.db.IDAO;
import de.macsystems.windroid.db.ISelectedDAO;

/**
 * @author mac
 * @version $Id$
 */
public class DBListActivity extends ListActivity
{

	private final static String LOG_TAG = DBListActivity.class.getSimpleName();

	private IDAO dao = null;

	/**
	 * 
	 * @param _dao
	 */
	public void onCreateDAO(final ISelectedDAO _dao)
	{
		if (_dao == null)
		{
			throw new NullPointerException("dao");
		}
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "DAO setted.");
		}
		dao = _dao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop()
	{
		super.onStop();
		if (dao == null)
		{
			throw new IllegalStateException("dao is null, do you called onCreateDAO ?");
		}
		dao.onStop();
	}
}