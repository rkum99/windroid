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
package de.macsystems.windroid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;

/**
 * Helper class which allows to set a background for a view/layout.
 * 
 * @author mac
 * @version $Id$
 */
public final class Background
{
	private final static String LOG_TAG = Background.class.getSimpleName();

	private final static List<TileModePair> bgs = new ArrayList<TileModePair>();
	static
	{
		bgs.add(new TileModePair(R.drawable.hibiscus_64x64, TileMode.MIRROR, TileMode.MIRROR));
		bgs.add(new TileModePair(R.drawable.hibiscus_blue_128x128, TileMode.REPEAT, TileMode.REPEAT));
		bgs.add(new TileModePair(R.drawable.r02_3, TileMode.REPEAT, TileMode.REPEAT));
	};
	private static Random random = new Random(bgs.size());

	private final static int UNKNOWN = -1;

	private static volatile int choosenBg = UNKNOWN;

	private Background()
	{
	}

	/**
	 * Apply a Background to an activity. The Activity must have a layout which
	 * contains an ID : {@link R.id.background_pattern}. If ID is not found the
	 * method just returns.
	 * 
	 * @param _activity
	 * @throws NullPointerException
	 *             if activiy is <code>null</code>
	 */
	public static void apply(final Activity _activity) throws NullPointerException
	{
		if (_activity == null)
		{
			throw new NullPointerException("Activity");
		}

		final View view = _activity.findViewById(R.id.background_pattern);
		if (view == null)
		{
			Log.w(LOG_TAG, "Cant find id for Background, skipping! Activity is :" + _activity.getClass().getName());
			return;
		}

		final TileModePair pair = getRandom(bgs);
		final BitmapDrawable bgImage = (BitmapDrawable) _activity.getResources().getDrawable(pair.resID);
		bgImage.setTileModeXY(pair.modeX, pair.modeY);
		view.setBackgroundDrawable(bgImage);
	}

	/**
	 * Returns {@link TileModePair}
	 * 
	 * @param _list
	 * @return
	 */
	private static TileModePair getRandom(final List<? extends TileModePair> _list)
	{
		if (choosenBg == UNKNOWN)
		{
			choosenBg = random.nextInt(_list.size());
		}
		return _list.get(choosenBg);
	}

	private static final class TileModePair
	{
		private final TileMode modeX;
		private final TileMode modeY;
		private final int resID;

		/**
		 * 
		 * @param _resID
		 * @param _x
		 * @param _y
		 * @throws NullPointerException
		 */
		private TileModePair(final int _resID, final TileMode _x, final TileMode _y) throws NullPointerException
		{
			if (_x == null || _y == null)
			{
				throw new NullPointerException("tilemode");
			}

			resID = _resID;
			modeX = _x;
			modeY = _y;
		}
	}
}