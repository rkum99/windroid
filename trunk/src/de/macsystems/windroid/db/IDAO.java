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
	 * Primary key {@value #COLUMN_ID}
	 */
	public final static String COLUMN_ID = "_id";
	/**
	 * Constant for MAX (_id). {@value #COLUMN_MAX_ID}
	 */
	public final static String COLUMN_MAX_ID = "MAX(" + COLUMN_ID + ")";

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
