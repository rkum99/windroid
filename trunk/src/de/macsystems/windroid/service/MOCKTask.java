package de.macsystems.windroid.service;

import android.content.Context;

/**
 * Testing task which only sleeps and shows some notifications.
 * 
 * @author mac
 * @version $Id$
 */
public final class MOCKTask extends AbstractNotificationTask
{

	/**
	 * @param context
	 * @throws NullPointerException
	 */
	public MOCKTask(Context context) throws NullPointerException
	{
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.service.AbstractNotificationTask#execute()
	 */
	@Override
	public void execute() throws Exception
	{
		showStatus("MOCKTask", "MOCKTask");
		Thread.sleep(5000L);
		clearNotification();
	}
}