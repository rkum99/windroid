package de.macsystems.windroid.progress;

import android.app.ProgressDialog;

/**
 * Adapter Class to decouple classes which want to update a progress more
 * flexible.
 * 
 * @author mac
 * @version $Id$
 */
public class ProgressDialogAdapter implements IProgress
{
	private final ProgressDialog delegate;

	/**
	 * 
	 * @param _dialog
	 */
	public ProgressDialogAdapter(final ProgressDialog _dialog)
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
	public void incrementBy(final int value)
	{
		delegate.incrementProgressBy(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.progress.IProgress#setMax(int)
	 */
	@Override
	public void setMax(final int _maximum)
	{
		delegate.setMax(_maximum);

	}

}
