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
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.R;
import de.macsystems.windroid.alarm.AlarmUtil;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.ISelectedDAO;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class UpdateAlarmTask extends AbstractNotificationTask implements Callable<Void>

{
	private final static String LOG_TAG = UpdateAlarmTask.class.getSimpleName();

	/**
	 * Thread save integer which can be used to count alarm id.
	 */
	private final static AtomicInteger notificationCounter = new AtomicInteger(1);

	/**
	 * 
	 * @param _context
	 * @throws NullPointerException
	 */
	public UpdateAlarmTask(final Context _context) throws NullPointerException
	{
		super(_context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Void call()
	{
		showStatus(getContext().getString(R.string.ongoing_update_title), getContext().getString(
				R.string.ongoing_update_text));

		try
		{
			final ISelectedDAO dao = DAOFactory.getSelectedDAO(getContext());
			if (!dao.isSpotActiv())
			{
				Log.i(LOG_TAG, "No active spot configured.");
				return null;
			}
			try
			{
				final Collection<SpotConfigurationVO> spots = dao.getActivSpots();
				if (Logging.isLoggingEnabled())
				{
					Log.i(LOG_TAG, "Found " + spots.size() + " Spots to update.");
				}

				final Iterator<SpotConfigurationVO> iter = spots.iterator();
				while (iter.hasNext())
				{
					final SpotConfigurationVO spot = iter.next();
					AlarmUtil.createAlarmForSpot(spot.getPrimaryKey(), getContext());
				}
			}
			catch (final DBException e)
			{
				Log.e(LOG_TAG, "", e);
			}
		}
		finally
		{
			clearNotification();
		}
		return null;
	}
}