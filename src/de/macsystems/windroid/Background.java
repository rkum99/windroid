package de.macsystems.windroid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

/**
 * Helper class which allows to set a background for a view/layout.
 * 
 * @author mac
 * @version $Id$
 */
public final class Background
{
	private final static List<TileModePair> bgs = new ArrayList<TileModePair>();
	static
	{
		bgs.add(new TileModePair(R.drawable.hibiscus_64x64, TileMode.MIRROR, TileMode.MIRROR));
		bgs.add(new TileModePair(R.drawable.hibiscus_blue_128x128, TileMode.REPEAT, TileMode.REPEAT));
	};
	private static Random random = new Random(bgs.size());

	/**
	 * Applys a Background to an activiy. The Activity already must have a
	 * layout which contains a ID with name: <b>R.id.background_pattern</b>
	 * 
	 * @param activity
	 */
	public static void apply(final Activity activity)
	{
		if (activity == null)
		{
			throw new NullPointerException("Activity");
		}

		final View view = activity.findViewById(R.id.background_pattern);
		final TileModePair pair = getRandom(bgs);
		final BitmapDrawable bgImage = (BitmapDrawable) activity.getResources().getDrawable(pair.resID);
		bgImage.setTileModeXY(pair.modeX, pair.modeY);
		view.setBackgroundDrawable(bgImage);
	}

	private static TileModePair getRandom(final List<? extends TileModePair> _list)
	{
		return _list.get(random.nextInt(_list.size()));
	}

	private static final class TileModePair
	{
		private final TileMode modeX;
		private final TileMode modeY;
		private final int resID;

		private TileModePair(final int _resID, final TileMode _x, final TileMode _y)
		{
			resID = _resID;
			modeX = _x;
			modeY = _y;
		}

	}
}
