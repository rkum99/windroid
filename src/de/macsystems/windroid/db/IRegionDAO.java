package de.macsystems.windroid.db;

import android.database.Cursor;
import android.graphics.Region;

/**
 * @author Jens Hohl
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IRegionDAO
{
	/**
	 * Returns Cursor over all
	 * 
	 * @return
	 */
	public Cursor fetchAll();

	/**
	 * 
	 * @param _id
	 * @return
	 */
	public Region getById(final int _id);

	/**
	 * get table size
	 * 
	 * @return
	 */
	public int getSize();
}
