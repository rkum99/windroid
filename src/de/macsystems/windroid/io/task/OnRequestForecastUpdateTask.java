package de.macsystems.windroid.io.task;

import android.content.Context;
import de.macsystems.windroid.service.PRIORITY;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class OnRequestForecastUpdateTask extends UpdateSpotForecastTask
{
	/**
	 * 
	 * @param selectedID
	 * @param context
	 * @throws NullPointerException
	 */
	public OnRequestForecastUpdateTask(int selectedID, Context context) throws NullPointerException
	{
		super(selectedID, context, PRIORITY.USER_REQUEST);
	}

}
