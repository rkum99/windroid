package de.macsystems.windroid.progress;

import android.widget.ProgressBar;

/**
 * Adapter for ProgressBar
 * 
 * @author mac
 * @version $Id$
 */
public class ProgressBarAdapter implements IProgress
{

	private final ProgressBar delegate;

	/**
	 * @param _bar
	 */
	public ProgressBarAdapter(final ProgressBar _bar)
	{
		if (_bar == null)
		{
			throw new NullPointerException();
		}
		delegate = _bar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.progress.IProgress#increment()
	 */
	@Override
	public void increment()
	{
		delegate.incrementProgressBy(1);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.progress.IProgress#incrementBy(int)
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
