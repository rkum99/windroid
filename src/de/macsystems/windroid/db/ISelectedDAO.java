package de.macsystems.windroid.db;

import android.database.Cursor;
import de.macsystems.windroid.SpotConfigurationVO;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public interface ISelectedDAO extends IDAO
{
	public final static String COLUMN_ACTIV = "activ";

	public final static String COLUMN_SPOTID = "spotid";

	public final static String COLUMN_NAME = "name";

	public final static String COLUMN_ID = "_id";

	public final static String COLUMN_USEDIRECTION = "usedirection";

	public final static String COLUMN_STARTING = "starting";

	public final static String COLUMN_TILL = "till";

	public final static String COLUMN_WINDMEASURE = "windmeasure";

	public final static String COLUMN_MINWIND = "minwind";

	public final static String COLUMN_MAXWIND = "maxwind";

	/**
	 * Adds a Spot
	 * 
	 * @param _vo
	 * @returns id of spot or -1 if something went wrong.
	 */
	public long insertSpot(SpotConfigurationVO _vo);

	/**
	 * Enables or Disables a Spot
	 * 
	 * @param _id
	 * @param isActiv
	 */
	public void setActiv(final long _id, final boolean isActiv);

	/**
	 * Returns <code>true<code> if spot is activ.
	 * 
	 * @param _id
	 * @return
	 */
	public boolean isActiv(final long _id);

	/**
	 * Returns an Configuration Object
	 * 
	 * @param _id
	 *            primary key
	 * @return
	 */
	public SpotConfigurationVO getSpotConfiguration(final long _id);

	/**
	 * Updates an Spot by using given {@link SpotConfigurationVO}
	 * 
	 * @param _vo
	 */
	public void update(final SpotConfigurationVO _vo);

	/**
	 * Returns a cursor with all spots configured.
	 * 
	 * @return
	 */
	public Cursor getSpots();

}
