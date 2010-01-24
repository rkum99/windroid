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
	public CountryImpl(Database database, IProgress progress)
	{
		super(database, COUNTRY, progress);
	}

	/**
	 * @param database
	 */
	public CountryImpl(Database database)
	{
		super(database, COUNTRY);
	}

	@Override
	public Cursor fetchByContinentID(String _id)
	{
		return fetchBy("continentid", _id);
	}

}
