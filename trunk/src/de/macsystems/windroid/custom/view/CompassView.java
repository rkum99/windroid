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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.shapes.ArcShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.widget.ImageView;
import de.macsystems.windroid.R;
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
	/**
	 * Start direction of Wind
	 */
	private volatile CardinalDirection fromDirection = CardinalDirection.N;
	/**
	 * End direction of Wind
	 */
	private volatile CardinalDirection toDirection = CardinalDirection.S;

	private int overlayInnerColor = 0;

	private int overlayOuterColor = 0;

	private int overlayPivotX = 0;

	private int overlayPivotY = 0;

	private int overlayRadius = 0;

	private Paint overlayPaint;

	private Shape shape;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(final Canvas canvas)
	{
		if (overlayPaint == null)
		{
			readResourceValues();
			overlayPaint = new Paint();
			overlayPaint.setShader(new RadialGradient(overlayPivotX, overlayPivotY, overlayRadius, overlayInnerColor,
					overlayOuterColor, TileMode.REPEAT));
			overlayPaint.setAntiAlias(true);
			final boolean isFromGreater = fromDirection.getDegree() > toDirection.getDegree();
			if (!isFromGreater)
			{
				final float startPoint = Math.min(fromDirection.getDegree(), toDirection.getDegree());
				final float maxAngle = Math.max(fromDirection.getDegree(), toDirection.getDegree());
				final float angle = maxAngle - startPoint;
				shape = new ArcShape(startPoint - 90, angle);
			}
			else
			{
				final float startPoint = fromDirection.getDegree();
				// Nullpunkt ist bei 90 Grad
				final float angle = 360 - startPoint + toDirection.getDegree();
				shape = new ArcShape(startPoint - 90, angle);
			}

		}
		/**
		 * translate the canvas to the upper left were Arc begins. Seems that
		 * Android does not draw shapes from its midpoint as Swing does.
		 */
		//
		canvas.translate((getWidth() - overlayRadius) / 2.0f, (getHeight() - overlayRadius) / 2.0f);
		shape.resize(overlayRadius, overlayRadius);
		shape.draw(canvas, overlayPaint);
	}

	/**
	 * Reads resource values to render compass like pivot and colors.
	 */
	private void readResourceValues()
	{
		final Resources resources = getResources();
		overlayInnerColor = resources.getColor(R.color.compass_inner_color);
		overlayOuterColor = resources.getColor(R.color.compass_outer_color);
		overlayPivotX = resources.getDimensionPixelOffset(R.dimen.compass_overlay_pivot_x);
		overlayPivotY = resources.getDimensionPixelOffset(R.dimen.compass_overlay_pivot_y);
		overlayRadius = resources.getDimensionPixelOffset(R.dimen.compass_overlay_radius);
	}

	public void setFromDirection(final CardinalDirection _fromDirection) throws NullPointerException
	{
		if (_fromDirection == null)
		{
			throw new NullPointerException("_fromDirection");
		}

		fromDirection = _fromDirection;
	}

	public void setToDirection(final CardinalDirection _toDirection) throws NullPointerException
	{
		if (_toDirection == null)
		{
			throw new NullPointerException("_toDirection");
		}
		toDirection = _toDirection;
	}
}