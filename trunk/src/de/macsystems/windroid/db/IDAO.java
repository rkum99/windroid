package de.macsystems.windroid.db;

import android.database.Cursor;

/**
 * Defines Methods which can be executed with every DAO.
 * 
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
	 * Returns all content of table like a SELECT * FROM table
	 * 
	 * @return
	 */
	public Cursor fetchAll();
}
