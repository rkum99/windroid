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
package de.macsystems.windroid.io.task;

import java.util.Collection;
import java.util.Iterator;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.R;
import de.macsystems.windroid.alarm.AlarmManagerFactory;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.ISelectedDAO;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class UpdateAlarmTask extends AbstractNotificationTask<Void>

{
	private final static String LOG_TAG = UpdateAlarmTask.class.getSimpleName();

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
	public void execute()
	{
		showStatus(getContext().getString(R.string.ongoing_update_title), getContext().getString(
				R.string.ongoing_update_text));

		try
		{
			final ISelectedDAO dao = DAOFactory.getSelectedDAO(getContext());
			if (!dao.isSpotActiv())
			{
				Log.i(LOG_TAG, "No active spot configured, skipping!");
				return;
			}
			try
			{
				final Collection<SpotConfigurationVO> spots = dao.getActivSpots();
				if (Logging.isEnabled())
				{
					Log.i(LOG_TAG, "Found " + spots.size() + " Spots to update.");
				}

				final Iterator<SpotConfigurationVO> iter = spots.iterator();
				while (iter.hasNext())
				{
					final SpotConfigurationVO spot = iter.next();
					AlarmManagerFactory.getAlarmManager().createAlarmForSpot(spot, getContext(), false);
				}
			}
			catch (final DBException e)
			{
				Log.e(LOG_TAG, "Failure while fetch active spots.", e);
			}
		}
		finally
		{
			clearNotification();
		}
	}
}