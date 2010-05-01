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
	/**
	 * {@value #COLUMN_KEYWORD}
	 */
	public final static String COLUMN_KEYWORD = "keyword";
	/**
	 * {@value #COLUMN_SPOTID}
	 */
	public final static String COLUMN_SPOTID = "spotid";
	/**
	 * {@value #COLUMN_CONTINENTID}
	 */
	public final static String COLUMN_CONTINENTID = "continentid";
	/**
	 * {@value #COLUMN_COUNTRYID}
	 */
	public final static String COLUMN_COUNTRYID = "countryid";
	/**
	 * {@value #COLUMN_REGIONID}
	 */
	public final static String COLUMN_REGIONID = "regionid";
	/**
	 * {@value #COLUMN_SUPERFORECAST}
	 */
	public final static String COLUMN_SUPERFORECAST = "superforecast";
	/**
	 * {@value #COLUMN_FORECAST}
	 */
	public final static String COLUMN_FORECAST = "forecast";
	/**
	 * {@value #COLUMN_REPORT}
	 */
	public final static String COLUMN_REPORT = "report";
	/**
	 * {@value #COLUMN_WAVEREPORT}
	 */
	public final static String COLUMN_WAVEREPORT = "wavereport";
	/**
	 * {@value #COLUMN_WAVEFORECAST}
	 */
	public final static String COLUMN_WAVEFORECAST = "waveforecast";
	/**
	 * {@value #COLUMN_NAME}
	 */
	public final static String COLUMN_NAME = "name";
	/**
	 * {@value #COLUMN_STATISTIC}
	 */
	public final static String COLUMN_STATISTIC = "statistic";
	/**
	 * {@value #COLUMN_GMT}
	 */
	public final static String COLUMN_GMT = "gmt";

	/**
	 * @param world
	 */
	public void insertSpots(final World world);

	/**
	 * Returns true if spots available
	 * 
	 * @return
	 */
	public boolean hasSpots();

	/**
	 * Returns spots of given continent->country->region
	 * 
	 * @param continentid
	 * @param countryid
	 * @param regionid
	 * @return
	 */
	public Cursor fetchBy(final String continentid, final String countryid, final String regionid);

	/**
	 * Returns a {@link SpotConfigurationVO} by its id.
	 * 
	 * @param stationid
	 * @return
	 * @throws DBException
	 */
	public SpotConfigurationVO fetchBy(final String stationid) throws DBException;
}
