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
package de.macsystems.windroid.progress;

import android.app.ProgressDialog;

/**
 * Adapter Class to decouple classes which want to update a progress more
 * flexible.
 * 
 * @author Jens Hohl
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
