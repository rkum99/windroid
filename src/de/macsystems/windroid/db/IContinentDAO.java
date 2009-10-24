package de.macsystems.windroid.db;

import android.database.Cursor;
import de.macsystems.windroid.identifyable.Continent;

/**
 * DAO for table 'continent'
 * 
 * @author Jens Hohl
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IContinentDAO
{
	/**
	 * Returns Cursor over all continents
	 * 
	 * @return
	 */
	public Cursor fetchAll();

	/**
	 * Returns Continent by id
	 * 
	 * @param id
	 * @return
	 */
	public Continent get(final int id);
}
