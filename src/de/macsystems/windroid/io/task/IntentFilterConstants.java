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
package de.macsystems.windroid.io.task;

/**
 * All Constants there a used to receive intents by using a IntentFilter.
 * Intents will be broadcasted when a task completes or causes an failure.
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class IntentFilterConstants
{
	public final static String SPOT_UPDATE_SUCESS = "de.macsystems.windroid.FILTER.SPOT_UPDATE_SUCESS";

	public final static String SPOT_UPDATE_FAILED = "de.macsystems.windroid.FILTER.SPOT_UPDATE_FAILED";

}
