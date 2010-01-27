package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;
import de.macsystems.windroid.db.ICountryDAO;
import de.macsystems.windroid.progress.IProgress;

/**
 * DAO for table 'country'
 * 
 * @author mac
 * @version $Id$
 */
public class CountryImpl extends BaseImpl implements ICountryDAO
{

	private static final String COUNTRY = "country";

	/**
	 * @param database
	 * @param progress
	 */
	public CountryImpl(final Database database, final IProgress progress)
	{
		super(database, COUNTRY, progress);
	}

	/**
	 * @param database
	 */
	public CountryImpl(final Database database)
	{
		super(database, COUNTRY);
	}

	@Override
	public Cursor fetchByContinentID(final String _id)
	{
		return fetchBy("continentid", _id);
	}

}
