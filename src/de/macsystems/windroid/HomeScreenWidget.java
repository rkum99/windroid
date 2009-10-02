package de.macsystems.windroid;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * 
 * @author mac
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
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		Log.d(LOG_TAG, "public void onDeleted(Context context, int[] appWidgetIds)");
		super.onDeleted(context, appWidgetIds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onDisabled(android.content.Context)
	 */
	@Override
	public void onDisabled(Context context)
	{

		Log.d(LOG_TAG, "public void onDisabled(Context context)");
		super.onDisabled(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onEnabled(android.content.Context)
	 */
	@Override
	public void onEnabled(Context context)
	{
		Log.d(LOG_TAG, "public void onEnabled(Context context)");
		super.onEnabled(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.appwidget.AppWidgetProvider#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d(LOG_TAG, "public void onReceive(Context context, Intent intent)");
		super.onReceive(context, intent);
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
		Log.d(LOG_TAG, "public void onUpdate(final Context context, "
				+ "final AppWidgetManager appWidgetManager, final int[] appWidgetIds)");
		// super.onUpdate(context, appWidgetManager, appWidgetIds);
		for (int index = 0; index < appWidgetIds.length; index++)
		{
			final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.homescreen);
			
			appWidgetManager.updateAppWidget(appWidgetIds[index], views);

		}
	}

}
