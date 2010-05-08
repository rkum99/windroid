package de.macsystems.windroid.io.task;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.service.AbstractNotificationTask;

/**
 * A Task which provides functions to play audio.
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public abstract class AudioFeedbackTask extends AbstractNotificationTask implements OnCompletionListener
{

	private final String LOG_TAG = AudioFeedbackTask.class.getSimpleName();

	private MediaPlayer player;

	/**
	 * @param context
	 * @throws NullPointerException
	 */
	public AudioFeedbackTask(Context context) throws NullPointerException
	{
		super(context);
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
			catch (Exception e)
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