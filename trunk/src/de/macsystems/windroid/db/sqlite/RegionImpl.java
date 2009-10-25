package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Region;
import de.macsystems.windroid.db.IRegionDAO;
import de.macsystems.windroid.progress.IProgress;

/**
 * DAO for table 'region'
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class RegionImpl extends BaseImpl implements IRegionDAO
{

	/**
	 * @param database
	 * @param progress
	 */
	public RegionImpl(final Database database, final IProgress progress)
	{
		super(database, progress);
	}

	/**
	 * @param database
	 */
	public RegionImpl(final Database database)
	{
		super(database);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IRegionDAO#fetchAll()
	 */
	@Override
	public Cursor fetchAll()
	{
		final SQLiteDatabase db = getReadableDatabase();
		return db.rawQuery("select * from region;", null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IRegionDAO#getById(int)
	 */
	@Override
	public Region getById(final int id)
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IRegionDAO#getSize()
	 */
	@Override
	public int getSize()
	{
		return getCount("region");
	}

}