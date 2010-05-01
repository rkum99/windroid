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

package de.macsystems.windroid.custom.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import de.macsystems.windroid.Logging;

/**
 * A base implementation of an activity which acts as a sub activity by passing
 * recieved result back to caller. A chain is no endpoint in a sequence of
 * Activitys
 * 
 * @author mac
 * @version $Id$
 */
public abstract class ChainSubActivity extends Activity
{

	private final static String LOG_TAG = ChainSubActivity.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(final int _requestCode, final int _resultCode, final Intent _data)
	{
		super.onActivityResult(_requestCode, _resultCode, _data);
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "recieved requestCode :" + _requestCode + " recieved resultCode :" + _resultCode);
		}
		setResult(_resultCode, _data);
		finish();
	}
}
