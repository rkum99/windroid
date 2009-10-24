package de.macsystems.windroid.db;

import android.database.Cursor;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface ICountryDAO
{
	/**
	 * 
	 * @return
	 */
	public Cursor fetchAll();

	/**
	 * 
	 * @return
	 */
	public int getSize();

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Cursor fetchByContinentID(final String id);

}
