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
package de.macsystems.windroid.io.task;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.io.IOUtils;

/**
 * A Task which provides functions to play audio.
 * 
 * @author mac
 * @version $Id$
 */
public abstract class AudioFeedbackTask extends AbstractNotificationTask implements OnCompletionListener
{

	private final String LOG_TAG = AudioFeedbackTask.class.getSimpleName();

	private final MediaPlayer player;

	/**
	 * @param _context
	 * @throws NullPointerException
	 */
	public AudioFeedbackTask(final Context _context) throws NullPointerException
	{
		super(_context);
		player = new MediaPlayer();
		player.setLooping(false);
		player.setOnCompletionListener(this);
	}

	/**
	 * Plays a Sound
	 * 
	 * @param _resourceID
	 */
	protected void play(final int _resourceID)
	{
		if (player != null)
		{

			AssetFileDescriptor afd = null;
			try
			{
				afd = getContext().getResources().openRawResourceFd(_resourceID);
				player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				player.setVolume(1.0f, 1.0f);
				afd.close();
				player.prepare();
				player.start();
			}
			catch (final Exception e)
			{
				Log.e(LOG_TAG, "Failed to play sound with id: " + _resourceID, e);
			}
			finally
			{
				IOUtils.close(afd);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.media.MediaPlayer.OnCompletionListener#onCompletion(android.media
	 * .MediaPlayer)
	 */
	@Override
	public final void onCompletion(final MediaPlayer _mp)
	{
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Media playback end reached.");
		}
		_mp.release();
	}

}
