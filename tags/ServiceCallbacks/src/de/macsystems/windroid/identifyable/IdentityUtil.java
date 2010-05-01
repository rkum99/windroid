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
package de.macsystems.windroid.identifyable;

import java.util.Arrays;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class IdentityUtil
{
	/**
	 * Returns index of <code>IdentifyAble</code> in given array.
	 * 
	 * @param id
	 * @param values
	 * @return
	 * @throws IllegalArgumentException
	 *             when not found by id.
	 * @throws NullPointerException
	 *             when id or values is null
	 */
	public static int indexOf(final String id, final IdentifyAble[] values) throws IllegalArgumentException
	{
		if (id == null)
		{
			throw new NullPointerException("Id is null");
		}
		if (values == null)
		{
			throw new NullPointerException("values is null");
		}

		for (int i = 0; i < values.length; i++)
		{
			if (id.equals(values[i].getId()))
			{
				return i;
			}
		}
		throw new IllegalArgumentException("IdentifyAble not found by id \"" + id + "\". Values :"
				+ Arrays.toString(values));
	}
}