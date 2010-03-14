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
	public final static String COLUMN_ACTIV = "activ";

	public final static String COLUMN_SPOTID = "spotid";

	public final static String COLUMN_NAME = "name";

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
	 */
	public Collection<SpotConfigurationVO> getActivSpots();

}
