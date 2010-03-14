/**
 This file is part of Windroid.

 Windroid is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Windroid is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Windroid.  If not, see <http://www.gnu.org/licenses/>.

 */
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
