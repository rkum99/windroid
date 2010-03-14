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

import android.database.Cursor;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.identifyable.World;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public interface ISpotDAO extends IDAO
{
	// TODO : Add report COLUMN
	public final static String COLUMN_KEYWORD = "keyword";
	public final static String COLUMN_SPOTID = "spotid";
	public final static String COLUMN_CONTINENTID = "continentid";
	public final static String COLUMN_COUNTRYID = "countryid";
	public final static String COLUMN_REGIONID = "regionid";
	public final static String COLUMN_SUPERFORECAST = "superforecast";
	public final static String COLUMN_FORECAST = "forecast";
	public final static String COLUMN_REPORT = "report";
	public final static String COLUMN_WAVEREPORT = "wavereport";
	public final static String COLUMN_WAVEFORECAST = "waveforecast";
	public final static String COLUMN_NAME = "name";
	public final static String COLUMN_STATISTIC = "statistic";

	/**
	 * @param world
	 * 
	 */
	public void insertSpots(World world);

	/**
	 * Returns true if spots available
	 * 
	 * @return
	 */
	public boolean hasSpots();

	public Cursor fetchBy(final String continentid, final String countryid, final String regionid);

	/**
	 * Returns a {@link SpotConfigurationVO} by its id.
	 * 
	 * @param stationid
	 * @return
	 */
	public SpotConfigurationVO fetchBy(final String stationid);
}
