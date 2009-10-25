package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.progress.IProgress;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public class SelectedImpl extends BaseImpl implements ISelectedDAO
{
	/**
	 * 
	 * @param _database
	 */
	public SelectedImpl(final Database _database)
	{
		super(_database, "selected");
	}

	/**
	 * 
	 * @param _database
	 * @param _progress
	 */
	public SelectedImpl(final Database _database, final IProgress _progress)
	{
		super(_database, "selected", _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.ISelectedDAO#fetchAll()
	 */
	@Override
	public Cursor fetchAll()
	{
		final SQLiteDatabase db = getDatabase().getReadableDatabase();
		return db.rawQuery("SELECT * FROM selected", null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IDAO#getSize()
	 */
	@Override
	public int getSize()
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.db.ISelectedDAO#insertSpot(de.macsystems.windroid
	 * .SpotConfigurationVO)
	 */
	@Override
	public void insertSpot(final SpotConfigurationVO _vo)
	{
		throw new UnsupportedOperationException();
	}

}