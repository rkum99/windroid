package de.macsystems.windroid.db;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class EmptyCursorException extends DBException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public EmptyCursorException(final String detailMessage, final Throwable throwable)
	{
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param detailMessage
	 */
	public EmptyCursorException(final String detailMessage)
	{
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param throwable
	 */
	public EmptyCursorException(final Throwable throwable)
	{
		super(throwable);
		// TODO Auto-generated constructor stub
	}

}
