package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import de.macsystems.windroid.db.ICountryDAO;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * DAO for table 'country'
 * 
 * @author mac
 * @version $Id$
 */
public final class CountryImpl extends BaseImpl implements ICountryDAO
{

	/**
	 * @param database
	 * @param progress
	 */
	public CountryImpl(final Database database, final IProgress progress)
	{
		super(database, "country", progress);
	}

	/**
	 * @param database
	 */
	public CountryImpl(final Database database)
	{
		this(database, NullProgressAdapter.INSTANCE);
	}

	@Override
	public Cursor fetchByContinentID(final String _id)
	{
		return fetchBy("continentid", _id);
	}

}
