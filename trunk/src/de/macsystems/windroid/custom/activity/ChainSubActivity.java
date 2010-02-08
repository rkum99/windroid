package de.macsystems.windroid.custom.activity;

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

	private final static String LOG_TAG = ChainSubActivity.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(final int _requestCode, final int _resultCode, final Intent _data)
	{
		super.onActivityResult(_requestCode, _resultCode, _data);
		Log.d(LOG_TAG, "recieved requestCode :" + _requestCode + " recieved resultCode :" + _resultCode);
		setResult(_resultCode, _data);
		finish();
	}
}
