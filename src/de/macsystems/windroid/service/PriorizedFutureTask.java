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
package de.macsystems.windroid.service;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import android.util.Log;

/**
 * @author mac
 * @version $Id$
 */
final class PriorizedFutureTask extends FutureTask<Void>
{

	@Override
	protected void set(final Void v)
	{
		super.set(v);

		Log.d("PriorizedFutureTask", "PriorizedFutureTask finished");
	}

	final PRIORITY prio;

	/**
	 * 
	 * @param _prio
	 * @param _task
	 * @throws NullPointerException
	 * 
	 */
	PriorizedFutureTask(final PRIORITY _prio, final Callable<Void> _task) throws NullPointerException
	{
		super(_task);
		if (_prio == null)
		{
			throw new NullPointerException("prio");
		}
		prio = _prio;
	}
}
