package de.macsystems.windroid.db;

import android.database.Cursor;

/**
 * @author mac
 * @version $Id$
 */
public interface IDAO
{
	/**
	 * Returns size of table
	 * 
	 * @return
	 */
	public int getSize();

	/**
	 * Returns all content of table
	 * 
	 * @return
	 */
	public Cursor fetchAll();
}
