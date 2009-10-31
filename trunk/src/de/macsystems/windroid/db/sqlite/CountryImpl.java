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

	/**
	 * @param database
	 * @param progress
	 */
	public CountryImpl(Database database, IProgress progress)
	{
		super(database, "region", progress);
	}

	/**
	 * @param database
	 */
	public CountryImpl(Database database)
	{
		super(database, "region");
	}

	@Override
	public Cursor fetchByContinentID(String _id)
	{
		return fetchBy("continentid", _id);
	}

}
