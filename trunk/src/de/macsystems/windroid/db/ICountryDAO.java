package de.macsystems.windroid.db;

import android.database.Cursor;

/**
 * @author mac
 * @version $Id$
 */
public interface ICountryDAO extends IDAO
{
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Cursor fetchByContinentID(final String id);

}
