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
import de.macsystems.windroid.R;
import de.macsystems.windroid.io.IOUtils;

/**
 * A Task which provides functions to play audio.<br>
 * The default status is <b>false</b> which means on a failure/error there's no
 * need of extra handling in code needed just include the play method in your
 * finally block!
 * 
 * <br>
 * Idiom to use this is:<br>
 * <br>
 * 
 * <pre>
 * try
 * {
 * 	doSomething();
 * 	setAsSuccessfull();
 * }
 * finally
 * {
 * 	play();
 * }
 * </pre>
 * 
 * @author mac
 * @version $Id$
 */
public abstract class AudioFeedbackTask extends AbstractNotificationTask<Void> implements OnCompletionListener
{
	/**
	 * ResID which is used to playback no sound.
	 */
	public final static int NO_SOUND_RES_ID = 137;

	private final String LOG_TAG = AudioFeedbackTask.class.getSimpleName();

	private final MediaPlayer player;

	private final int failedResID;

	private final int successResID;

	private volatile boolean isTaskSuccessfull = false;
	/**
	 * Volume used to play feedback
	 */
	private final float VOLUME = 1.0f;

	/**
	 * Creates the task with its default feedback values.
	 * 
	 * @param _context
	 * @throws NullPointerException
	 * @see {@link #NO_SOUND_RES_ID}
	 */
	public AudioFeedbackTask(final Context _context) throws NullPointerException
	{
		this(_context, R.raw.update_sucessfull, R.raw.update_failed);
	}

	/**
	 * 
	 * @param _context
	 * @param _successAudioResID
	 *            Resource ID which will be played if task succeed
	 * @param _failedAudioResID
	 *            Resource ID which will be played if task failed
	 * @throws NullPointerException
	 */
	public AudioFeedbackTask(final Context _context, final int _successAudioResID, final int _failedAudioResID)
			throws NullPointerException
	{
		super(_context);
		successResID = _successAudioResID;
		failedResID = _failedAudioResID;
		player = new MediaPlayer();
		player.setLooping(false);
		player.setOnCompletionListener(this);
	}

	/**
	 * Set this task as succeed
	 */
	public final void setAsSuccessfull()
	{
		isTaskSuccessfull = true;
	}

	/**
	 * Play audio feedback depending on status.
	 * 
	 * @see #setAsSuccessfull()
	 */
	public void play()
	{
		play(isTaskSuccessfull ? successResID : failedResID);
	}

	/**
	 * Plays a Sound
	 * 
	 * @param _resourceID
	 */
	private void play(final int _resourceID)
	{
		if (player != null && _resourceID != NO_SOUND_RES_ID)
		{

			AssetFileDescriptor afd = null;
			try
			{
				afd = getContext().getResources().openRawResourceFd(_resourceID);
				player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				player.setVolume(VOLUME, VOLUME);
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
		_mp.release();
		if (Logging.isLoggingEnabled())
		{
			Log.d(LOG_TAG, "Media playback end reached.");
		}
	}

}
