package de.macsystems.windroid.db.sqlite;

import android.database.Cursor;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
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
