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

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.alarm.AlarmManagerFactory;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.ISelectedDAO;

/**
 * Installs on each configured Spot an Alarm.
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 * @see BaseStrategie#createAlarmForSpot(int, Context, boolean)
 */
public class SpotAlarmTask implements Callable<Void>
{

	private final static String LOG_TAG = SpotAlarmTask.class.getSimpleName();

	private final Context context;

	/**
	 * 
	 * @param _context
	 */
	public SpotAlarmTask(final Context _context) throws NullPointerException
	{
		if (_context == null)
		{
			throw new NullPointerException("context");
		}
		context = _context;
	}

	@Override
	public Void call()
	{
		final ISelectedDAO dao = DAOFactory.getSelectedDAO(context);

		if (!dao.isSpotActiv())
		{
			if (Logging.isEnabled)
			{
				Log.i(LOG_TAG, "No active spot configured.");
			}
			return null;
		}
		try
		{
			final Collection<SpotConfigurationVO> spots = dao.getActivSpots();
			if (Logging.isEnabled)
			{
				Log.i(LOG_TAG, "Found " + spots.size() + " spot(s) to install alarm trigger(s).");
			}

			final Iterator<SpotConfigurationVO> iter = spots.iterator();
			while (iter.hasNext())
			{
				final SpotConfigurationVO spot = iter.next();
				AlarmManagerFactory.getAlarmManager().createAlarmForSpot(spot, context, false);
			}

		}
		catch (final DBException e)
		{
			Log.e(LOG_TAG, "Failed to get activ spots", e);
		}
		return null;
	}
}