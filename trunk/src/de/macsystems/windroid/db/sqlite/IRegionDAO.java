package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;

/**
 * @author mac
 * @version $Id$
 */
public interface IRegionDAO
{
	public Cursor fetchAll();

	/**
	 * Returns belonging Regions by countryid
	 * 
	 * @param _countryID
	 * @return
	 */
	public Cursor fetchByCountryID(final String _countryID);

	public int getSize();
}
