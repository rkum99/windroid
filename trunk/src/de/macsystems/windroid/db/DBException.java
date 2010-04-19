package de.macsystems.windroid.db;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class DBException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public DBException(final String detailMessage, final Throwable throwable)
	{
		super(detailMessage, throwable);
	}

	/**
	 * @param detailMessage
	 */
	public DBException(final String detailMessage)
	{
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param throwable
	 */
	public DBException(final Throwable throwable)
	{
		super(throwable);
	}

}
