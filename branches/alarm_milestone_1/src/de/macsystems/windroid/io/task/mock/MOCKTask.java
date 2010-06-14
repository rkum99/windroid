package de.macsystems.windroid.io.task.mock;

import android.content.Context;
import de.macsystems.windroid.io.task.AbstractNotificationTask;

/**
 * Testing task which only sleeps and shows some notifications.
 * 
 * @author mac
 * @version $Id$
 */
public final class MOCKTask extends AbstractNotificationTask<Void>
{

	/**
	 * @param context
	 * @throws NullPointerException
	 */
	public MOCKTask(final Context context) throws NullPointerException
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