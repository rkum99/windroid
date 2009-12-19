package de.macsystems.windroid;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

/**
 * A base implementation of an activity which acts as a sub activity by passing
 * recieved result back to caller. A chain is no endpoint in a sequence of
 * Activitys
 * 
 * @author mac
 * @version $Id$
 */
public abstract class ChainSubActivity extends Activity
{

	final static String LOG_TAG = ChainSubActivity.class.getName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
//		super.onActivityResult(requestCode, resultCode, data);
		Log.d(LOG_TAG, "recieved requestCode :" + requestCode + " recieved resultCode :" + resultCode);
		setResult(resultCode, data);
		finish();
	}
}
