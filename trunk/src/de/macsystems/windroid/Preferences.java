package de.macsystems.windroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class Preferences extends PreferenceActivity
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferencesdescription);

	}
}
