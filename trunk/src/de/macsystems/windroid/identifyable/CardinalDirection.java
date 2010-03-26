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

import java.util.ArrayList;
import java.util.List;

import de.macsystems.windroid.R;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public enum CardinalDirection implements IdentifyAble
{

	N(0, "N", "North", R.drawable.wd_n), //
	NNE(22.5f, "NNE", "North North East", R.drawable.wd_nne),
	NE(45f, "NE", "North East", R.drawable.wd_ne),
	ENE(67.5f, "ENE", "East North East", R.drawable.wd_ene),
	E(90f, "E", "East", R.drawable.wd_e),
	ESE(112.5f, "ESE", "East South East", R.drawable.wd_ese),
	SE(135f, "SE", "South East", R.drawable.wd_se),
	SSE(157.5f, "SSE", "South South East", R.drawable.wd_sse),
	S(180f, "S", "South", R.drawable.wd_s),
	SSW(202.5f, "SSW", "South South West", R.drawable.wd_ssw),
	SW(225f, "SW", "South West", R.drawable.wd_sw),
	WSW(247.5f, "WSW", "West South West", R.drawable.wd_wsw),
	W(270f, "W", "West", R.drawable.wd_w),
	WNW(292.5f, "WNW", "West North West", R.drawable.wd_wnw),
	NW(315f, "NW", "North  West", R.drawable.wd_nw),
	NNW(337.5f, "NNW", "North North West", R.drawable.wd_nnw),

	/**
	 * define no wind direction
	 */

	NO_DIRECTION(0, "n/a", "No Direction", R.drawable.wd_dummy);

	private final float degrees;
	private final String longName;
	private final String shortName;

	private final int imageID;

	/**
	 * 
	 * @param _degrees
	 * @param _longName
	 * @param _shortName
	 */
	private CardinalDirection(final float _degrees, final String _shortName, final String _longName, final int _imageID)
	{
		degrees = _degrees;
		shortName = _shortName;
		longName = _longName;
		imageID = _imageID;
	}

	public float getDegree()
	{
		return degrees;
	}

	public String getShortName()
	{
		return shortName;
	}

	/**
	 * Returns drawable image id.
	 * 
	 * @return
	 */
	public int getImage()
	{
		return imageID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return longName + "(" + degrees + ")";
	}

	@Override
	public String getId()
	{
		return shortName;
	}

	/**
	 * Returns <code>WindDirection</code> by its shortname (eg. NNW).
	 * 
	 * @param _shortName
	 * @return
	 * @throws IllegalArgumentException
	 */
	public final static CardinalDirection getByShortName(final String _shortName) throws IllegalArgumentException
	{
		if (_shortName == null)
		{
			throw new IllegalArgumentException("_shortName is null.");
		}
		for (final CardinalDirection unit : CardinalDirection.values())
		{
			if (_shortName.equals(unit.shortName))
			{
				return unit;
			}
		}
		throw new IllegalArgumentException("unkown shortname \"" + _shortName + "\".");
	}

	/**
	 * Returns <code>WindDirection</code> by its degree (eg. 22.5f results in
	 * North North East).
	 * 
	 * @param _degree
	 * @return
	 * @throws IllegalArgumentException
	 */
	public final static CardinalDirection getByDegree(final float _degree) throws IllegalArgumentException
	{
		for (final CardinalDirection unit : CardinalDirection.values())
		{
			final float THRESHOLD = 0.01f;
			if (Math.abs(_degree - unit.degrees) < THRESHOLD)
			{
				return unit;
			}
		}
		throw new IllegalArgumentException("unkown _degree \"" + _degree + "\".");
	}

	/**
	 * Returns an array of selectable values not including 'no selection'.
	 * 
	 * @return
	 */
	public static CardinalDirection[] getValues()
	{
		final List<CardinalDirection> temp = new ArrayList<CardinalDirection>();
		final CardinalDirection[] values = values();
		for (int i = 0; i < values.length; i++)
		{
			temp.add(values[i]);
		}
		temp.remove(NO_DIRECTION);
		return temp.toArray(new CardinalDirection[temp.size()]);
	}

}