package de.macsystems.windroid.io;

/**
 * Exception which indicates that a current called method cannot be executed but
 * may can called later successfully.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class RetryLaterException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param detailMessage
	 */
	public RetryLaterException(final String detailMessage)
	{
		super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public RetryLaterException(final Throwable throwable)
	{
		super(throwable);
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public RetryLaterException(final String detailMessage, final Throwable throwable)
	{
		super(detailMessage, throwable);
	}

}
