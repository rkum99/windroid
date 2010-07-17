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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public class HomeScreenWidget extends AppWidgetProvider
{

	private final static String LOG_TAG = HomeScreenWidget.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onDeleted(android.content.Context,
	 * int[])
	 */
	@Override
	public void onDeleted(final Context context, final int[] appWidgetIds)
	{
		super.onDeleted(context, appWidgetIds);
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "public void onDeleted(Context context, int[] appWidgetIds)");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onDisabled(android.content.Context)
	 */
	@Override
	public void onDisabled(final Context context)
	{
		super.onDisabled(context);
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "public void onDisabled(Context context)");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onEnabled(android.content.Context)
	 */
	@Override
	public void onEnabled(final Context context)
	{
		super.onEnabled(context);
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "public void onEnabled(Context context)");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		super.onReceive(context, intent);
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "public void onReceive(Context context, Intent intent)");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onUpdate(android.content.Context,
	 * android.appwidget.AppWidgetManager, int[])
	 */
	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds)
	{
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "public void onUpdate(final Context context, "
					+ "final AppWidgetManager appWidgetManager, final int[] appWidgetIds)");
		}
		// super.onUpdate(context, appWidgetManager, appWidgetIds);
		for (int index = 0; index < appWidgetIds.length; index++)
		{
			final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.homescreen);

			appWidgetManager.updateAppWidget(appWidgetIds[index], views);

		}
	}

}
