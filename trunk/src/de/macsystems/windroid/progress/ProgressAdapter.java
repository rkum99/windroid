package de.macsystems.windroid.progress;

import android.app.ProgressDialog;

/**
 * Adapter Class to decouple classes which want to update a progress more
 * flexible.
 * 
 * @author mac
 * @version $Id$
 */
public class ProgressAdapter implements IProgress
{
	private final ProgressDialog delegate;

	/**
	 * 
	 * @param _dialog
	 */
	public ProgressAdapter(ProgressDialog _dialog)
	{
		if (_dialog == null)
		{
			throw new NullPointerException();

		}
		delegate = _dialog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.IProgress#increment()
	 */
	@Override
	public void increment()
	{
		delegate.incrementProgressBy(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.IProgress#incrementBy(int)
	 */
	@Override
	public void incrementBy(int value)
	{
		delegate.incrementProgressBy(value);
	}

}
