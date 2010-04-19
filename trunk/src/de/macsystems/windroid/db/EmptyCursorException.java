package de.macsystems.windroid.db;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class EmptyCursorException extends DBException
{

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public EmptyCursorException(String detailMessage, Throwable throwable)
	{
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param detailMessage
	 */
	public EmptyCursorException(String detailMessage)
	{
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param throwable
	 */
	public EmptyCursorException(Throwable throwable)
	{
		super(throwable);
		// TODO Auto-generated constructor stub
	}

}
