package de.macsystems.windroid.progress;

/**
 * NULL Value which can be used if no progress need to be monitored.
 * 
 * @author mac
 * @version $Id$
 */
public final class NullProgressAdapter implements IProgress
{
	/**
	 * Instance which can be used.
	 */
	public final static NullProgressAdapter INSTANCE = new NullProgressAdapter();

	private NullProgressAdapter()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.progress.IProgress#increment()
	 */
	@Override
	public void increment()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.progress.IProgress#incrementBy(int)
	 */
	@Override
	public void incrementBy(final int value)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.progress.IProgress#setMax(int)
	 */
	@Override
	public void setMax(final int _maximum)
	{
	}

}
