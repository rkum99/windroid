package de.macsystems.windroid;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public class HomeScreenConfigure extends Activity
{
	private final static String LOG_TAG = HomeScreenConfigure.class.getSimpleName();

	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "protected void onCreate(Bundle savedInstanceState)");

		// Find the widget id from the intent.
		final Intent intent = getIntent();
		final Bundle extras = intent.getExtras();
		if (extras != null)
		{
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
		{
			finish();
		}

	}
}
