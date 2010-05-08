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
package de.macsystems.windroid.custom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.shapes.ArcShape;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.identifyable.CardinalDirection;

/**
 * View that renders the Compass and the chosen direction of Wind.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class CompassView extends ImageView
{
	private final static String LOG_TAG = CompassView.class.getSimpleName();
	/**
	 * Create an Color with an alpha channel.
	 */
	private final int OVERLAY_COLOR = Color.argb(200, 0, 0, 150);
	/**
	 * Dimension of overlay.
	 */
	private final float CIRCLE_DIMENSION = 165f;
	/**
	 * Start direction of Wind
	 */
	private CardinalDirection fromDirection;
	/**
	 * End direction of Wind
	 */
	private CardinalDirection toDirection;

	/**
	 * 
	 * @param context
	 */
	public CompassView(final Context context)
	{
		super(context);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public CompassView(final Context context, final AttributeSet attrs)
	{
		super(context, attrs);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CompassView(final Context context, final AttributeSet attrs, final int defStyle)
	{
		super(context, attrs, defStyle);
	}

	/**
	 * 
	 * @param _from
	 */
	public void setFromDirection(final CardinalDirection _from)
	{
		if (_from == null)
		{
			throw new NullPointerException("from");
		}

		fromDirection = _from;

		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "setFromDirection :" + fromDirection.name());
		}
	}

	/**
	 * 
	 * @param _to
	 */
	public void setToDirection(final CardinalDirection _to)
	{
		if (_to == null)
		{
			throw new NullPointerException("to");
		}

		toDirection = _to;

		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "setToDirection :" + toDirection.name());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(final Canvas canvas)
	{
		super.onDraw(canvas);

		if (fromDirection != null && toDirection != null)
		{
			final Paint paint = new Paint();

			paint.setAntiAlias(true);
			paint.setColor(OVERLAY_COLOR);

			paint.setShader(new RadialGradient(100, 100, 100, Color.WHITE, Color.BLUE, TileMode.REPEAT));

			final boolean isFromGreater = fromDirection.getDegree() > toDirection.getDegree();
			// Toast.makeText(getContext(), "isFromGreater Smaller :" +
			// isFromGreater, Toast.LENGTH_LONG).show();
			//
			final ArcShape shape;
			if (!isFromGreater)
			{
				final float startPoint = Math.min(fromDirection.getDegree(), toDirection.getDegree());
				final float maxAngle = Math.max(fromDirection.getDegree(), toDirection.getDegree());
				final float angle = maxAngle - startPoint;
				shape = new ArcShape(startPoint - 90, angle);
			}
			else
			{
				final float startPoint = Math.max(fromDirection.getDegree(), toDirection.getDegree());
				final float range = Math.min(fromDirection.getDegree(), toDirection.getDegree());
				shape = new ArcShape(startPoint - 90, 360 - range);
			}

			/**
			 * translate the canvas to the upper left were Arc begins. Seems
			 * that Android does not draw shapes from its midpoint as Swing
			 * does.
			 */
			canvas.translate((getWidth() - CIRCLE_DIMENSION) / 2, (getHeight() - CIRCLE_DIMENSION) / 2);
			// canvas.rotate(-90);

			shape.resize(CIRCLE_DIMENSION, CIRCLE_DIMENSION);

			shape.draw(canvas, paint);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
