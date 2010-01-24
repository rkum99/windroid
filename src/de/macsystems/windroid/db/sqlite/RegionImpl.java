package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import android.graphics.Region;
import de.macsystems.windroid.db.IRegionDAO;
import de.macsystems.windroid.progress.IProgress;

/**
 * DAO for table 'region'
 * 
 * @author mac
 * @version $Id$
 */
public class RegionImpl extends BaseImpl implements IRegionDAO
{

	private static final String REGION = "region";

	/**
	 * @param database
	 * @param progress
	 */
	public RegionImpl(final Database database, final IProgress progress)
	{
		super(database, REGION, progress);
	}

	/**
	 * @param database
	 */
	public RegionImpl(final Database database)
	{
		super(database, REGION);
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

	@Override
	public Cursor fetchByCountryID(final String _id)
	{
		return fetchBy("countryid", _id);
	}

}
