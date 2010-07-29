/**
 This file is part of Windroid.

 Windroid is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Windroid is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Windroid.  If not, see <http://www.gnu.org/licenses/>.

 */
package de.macsystems.windroid.db;

import java.util.Collection;

import android.database.Cursor;
import de.macsystems.windroid.common.SpotConfigurationVO;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public interface ISelectedDAO extends IDAO
{
	/**
	 * {@value #COLUMN_ACTIV}
	 */
	public final static String COLUMN_ACTIV = "activ";
	/**
	 * {@value #COLUMN_UPDATEFAILED}
	 */
	public final static String COLUMN_UPDATEFAILED = "updatefailed";
	/**
	 * {@value #COLUMN_LASTUPATE}
	 */
	public final static String COLUMN_LASTUPATE = "lastupdate";
	/**
	 * {@value #COLUMN_SPOTID}
	 */
	public final static String COLUMN_SPOTID = "spotid";
	/**
	 * {@value #COLUMN_NAME}
	 */
	public final static String COLUMN_NAME = "name";
	/**
	 * {@value #COLUMN_USEDIRECTION}
	 */
	public final static String COLUMN_USEDIRECTION = "usedirection";
	/**
	 * {@value #COLUMN_STARTING}
	 */
	public final static String COLUMN_STARTING = "starting";
	/**
	 * {@value #COLUMN_TILL}
	 */
	public final static String COLUMN_TILL = "till";
	/**
	 * {@value #COLUMN_WINDMEASURE}
	 */
	public final static String COLUMN_WINDMEASURE = "windmeasure";
	/**
	 * {@value #COLUMN_MINWIND}
	 */
	public final static String COLUMN_MINWIND = "minwind";
	/**
	 * {@value #COLUMN_MAXWIND}
	 */
	public final static String COLUMN_MAXWIND = "maxwind";

	/**
	 * Adds a Spot
	 *
	 * @param _vo
	 */
	public void insertSpot(SpotConfigurationVO _vo);

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
	 * @throws DBException
	 */
	public boolean isActiv(final long _id) throws DBException;

	/**
	 * Returns an Configuration Object
	 *
	 * @param _id
	 *            primary key
	 * @return
	 * @throws DBException
	 */
	public SpotConfigurationVO getSpotConfiguration(final long _id) throws DBException;

	/**
	 * Updates an Spot by using given {@link SpotConfigurationVO}
	 *
	 * @param _vo
	 */
	public void update(final SpotConfigurationVO _vo);

	/**
	 * Deletes a spot from selected table using its primary key.
	 *
	 * @param _id
	 */
	public void delete(final int _id);

	/**
	 * Enables / Disables all Spots
	 *
	 * @param active
	 */
	public void setAllActiv(final boolean active);

	/**
	 * Returns a cursor with all spots configured.
	 *
	 * @return
	 */
	public Cursor getConfiguredSpots();

	/**
	 * Returns true when at least one spot is configured and activ.
	 *
	 * @return
	 */
	public boolean isSpotActiv();

	/**
	 * Returns a Collection of Spots which are active.
	 *
	 * @return
	 * @throws DBException
	 */
	public Collection<? extends SpotConfigurationVO> getActivSpots() throws DBException;

	/**
	 * Returns selectedid array of active spots
	 *
	 * @return
	 * @throws DBException
	 */
	public int[] getActivSpotIDs() throws DBException;

}
