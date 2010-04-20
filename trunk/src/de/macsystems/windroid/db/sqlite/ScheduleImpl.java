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

import java.util.Iterator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.IRepeatDAO;
import de.macsystems.windroid.db.IScheduleDAO;
import de.macsystems.windroid.identifyable.Repeat;
import de.macsystems.windroid.identifyable.Schedule;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class ScheduleImpl extends BaseImpl implements IScheduleDAO
{
	private final static String LOG_TAG = ScheduleImpl.class.getSimpleName();

	/**
	 * @param database
	 * @param progress
	 */
	public ScheduleImpl(final Database database, final IProgress progress)
	{
		super(database, "schedule_repeat_relation", progress);
	}

	/**
	 * @param database
	 */
	public ScheduleImpl(final Database database)
	{
		this(database, NullProgressAdapter.INSTANCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IScheduleDAO#getSchedule(long)
	 */
	@Override
	public Schedule getSchedule(final int id)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IScheduleDAO#getScheduleByScheduleID(int)
	 */
	@Override
	public Schedule getScheduleByScheduleID(final int _selectedID)
	throws DBException
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "getScheduleByScheduleID::selectedID :" + _selectedID);
		}
		final SQLiteDatabase db = getReadableDatabase();
		Cursor c = null;
		final Schedule result;
		try
		{
			c = db.rawQuery("SELECT * FROM " + tableName + " WHERE selectedid=?", new String[]
			{ Integer.toString(_selectedID) });

			moveToFirstOrThrow(c);
			//
			result = new Schedule();
			//
			final IRepeatDAO dao = new RepeatImpl(getDatabase());
			//
			do
			{
				final int repeatID = getInt(c, COLUMN_REPEAT_ID);
				final Repeat repeat = dao.getRepeat(repeatID);
				result.addRepeat(repeat);
			}
			while (c.moveToNext());

		}
		finally
		{
			IOUtils.close(c);
			IOUtils.close(db);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IScheduleDAO#getTime(long)
	 */
	@Override
	public long getTime(final int id)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IScheduleDAO#isActiv(long)
	 */
	@Override
	public boolean isActiv(final int id)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void insert(final Schedule _schedule, final int _selectedID)
	{
		if (_schedule == null)
		{
			throw new NullPointerException("schedule");
		}
		final SQLiteDatabase db = getReadableDatabase();
		final Cursor c = null;
		try
		{
			/**
			 * For each repeat create an relation on table.
			 */
			final Iterator<Integer> iter = _schedule.getRepeatIterator();
			while (iter.hasNext())
			{
				final Repeat repeat = _schedule.getRepeat(iter.next());
				final ContentValues repeatValue = new ContentValues();
				repeatValue.put(COLUMN_SELECTED_ID, _selectedID);
				repeatValue.put(COLUMN_REPEAT_ID, repeat.getId());
				final int primaryKey = (int) db.insert(tableName, null, repeatValue);
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, "Updated table " + tableName + " by a schedule, primaryKey is:" + primaryKey);
				}
			}
		}
		finally
		{
			IOUtils.close(c);
			IOUtils.close(db);
		}
	}

}
