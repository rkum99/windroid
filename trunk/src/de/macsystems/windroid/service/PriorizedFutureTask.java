package de.macsystems.windroid.service;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class PriorizedFutureTask extends FutureTask<Void>
{

	private final PRIORITY prio;

	public PriorizedFutureTask(final PRIORITY _prio, final Callable<Void> _task)
	{
		super(_task);
		prio = _prio;
	}
}
