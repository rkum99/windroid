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
package de.macsystems.windroid;

import java.io.IOException;
import java.net.URI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import de.macsystems.windroid.custom.activity.ChainSubActivity;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.ISpotDAO;
import de.macsystems.windroid.identifyable.World;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.task.XMLParseTask;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.ProgressBarAdapter;

/**
 * Activity which downloads station.xml and updates the database. The User can
 * see the progress while waiting and is able to cancel this.
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public final class DownloadActivity extends ChainSubActivity
{
	private final static String LOG_TAG = DownloadActivity.class.getSimpleName();

	private final Handler handler = new Handler();

	private Thread downloadThread = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);

		final ProgressBar downloadProgressBar = (ProgressBar) findViewById(R.id.download_progressbar);
		final ProgressBar databaseProgressBar = (ProgressBar) findViewById(R.id.download_database_progressbar);

		final Button cancelButton = (Button) findViewById(R.id.download_cancel_button);
		cancelButton.setOnClickListener(getCancelListener());
		// FIXME: if user rotates device we create another thread!
		startDownload(downloadProgressBar, databaseProgressBar);
	}

	/**
	 * 
	 * @param _downloadProgress
	 * @param _databaseProgress
	 * @throws NullPointerException
	 */
	private void startDownload(final ProgressBar _downloadProgress, final ProgressBar _databaseProgress)
			throws NullPointerException
	{

		if (_downloadProgress == null || _databaseProgress == null)
		{
			throw new NullPointerException();
		}
		final IProgress downloadProgress = new ProgressBarAdapter(_downloadProgress);
		final IProgress databaseProgress = new ProgressBarAdapter(_databaseProgress);

		if (downloadThread == null)
		{
			downloadThread = new Thread("Parse XML")
			{
				@Override
				public final void run()
				{
					if (Logging.isLoggingEnabled())
					{
						Log.d(LOG_TAG, "Parsing Thread started.");
					}
					try
					{
						downloadProgress.setMax(100);
						// 
						if (WindUtils.isStationListUpdateAvailable(DownloadActivity.this))
						{
							WindUtils.updateStationList(DownloadActivity.this, downloadProgress);
						}
						downloadProgress.incrementBy(50);
						final XMLParseTask task = new XMLParseTask(new URI(IOUtils.stationsXMLFilePath),
								databaseProgress);
						if (Thread.currentThread().isInterrupted())
						{
							Log.d(LOG_TAG, "Thread interrupted.");
							return;
						}
						else
						{
							Log.d(LOG_TAG, "Thread " + Thread.currentThread().getName() + " not interrupted.");
						}

						final int stationsFound = task.execute(DownloadActivity.this);
						downloadProgress.incrementBy(50);
						if (Thread.currentThread().isInterrupted())
						{
							Log.d(LOG_TAG, "Thread interrupted after parse.");
							return;
						}
						else
						{
							Log.d(LOG_TAG, "Thread " + Thread.currentThread().getName() + " not interrupted.");
						}

						//
						final World world = task.getWorld();
						databaseProgress.setMax(stationsFound);
						//
						final ISpotDAO updater = DAOFactory.getSpotDAO(DownloadActivity.this, databaseProgress);
						updater.insertSpots(world);
						showInstallSucceed();
					}
					catch (final InterruptedException e)
					{
						Log.e(LOG_TAG, "Thread Interrupted - by user ?", e);
					}
					catch (IOException e)
					{
						Log.e(LOG_TAG, "IO Failure.", e);
						showInstallationFailed(e);
					}
					catch (final Exception e)
					{
						Log.e(LOG_TAG, "Failed to parse xml.", e);
						showInstallationFailed(e);
					}
					finally
					{
						if (Logging.isLoggingEnabled())
						{
							Log.d(LOG_TAG, "Parsing Thread ended.");
						}
					}
				}
			};
		}
		downloadThread.start();
	}

	private void showInstallationFailed(final Throwable _throwable)
	{
		final String stackTrace = Util.getStackTrace(_throwable);
		handler.post(new Runnable()
		{
			@Override
			public final void run()
			{
				final String ok = DownloadActivity.this.getString(android.R.string.ok);
				final String message = DownloadActivity.this.getString(R.string.download_failure_text);
				final String header = DownloadActivity.this.getString(R.string.download_failure_header);

				new AlertDialog.Builder(DownloadActivity.this).setPositiveButton(ok, getOkListener(MainActivity.class))
						.setTitle(header).setMessage(message + stackTrace).show();
			}
		});
	}

	/**
	 * Shows dialog informing user about successfully installation.
	 */
	private void showInstallSucceed()
	{
		handler.post(new Runnable()
		{
			@Override
			public final void run()
			{
				final String ok = DownloadActivity.this.getString(android.R.string.ok);
				final String message = DownloadActivity.this.getString(R.string.download_success_text);
				final String header = DownloadActivity.this.getString(R.string.download_success_header);

				new AlertDialog.Builder(DownloadActivity.this).setPositiveButton(ok,
						getOkListener(SpotSelectionActivity.class)).setTitle(header).setMessage(message).show();
			}
		});
	}

	private DialogInterface.OnClickListener getOkListener(final Class<? extends Activity> _nextActivity)
	{
		if (_nextActivity == null)
		{
			throw new NullPointerException("next activity");
		}
		//
		final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
		{
			@Override
			public final void onClick(final DialogInterface _dialog, final int _which)
			{
				final Intent nextActivitiyIntent = new Intent(DownloadActivity.this, _nextActivity);
				startActivityForResult(nextActivitiyIntent, MainActivity.CONFIGURATION_REQUEST_CODE);
			}
		};
		return listener;
	}

	/**
	 * @return OnClickListener
	 */
	private OnClickListener getCancelListener()
	{
		final OnClickListener listener = new OnClickListener()
		{
			@Override
			public final void onClick(final View _v)
			{
				try
				{
					if (Logging.isLoggingEnabled())
					{
						Log.d(LOG_TAG, "Interrupting thread");
					}
					downloadThread.interrupt();
				}
				catch (final Exception e)
				{
					Log.e(LOG_TAG, "Failed to interrupt thread", e);
				}
				finally
				{
					setResult(Activity.RESULT_CANCELED);
					finish();
				}
			}
		};
		return listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop()
	{
		super.onStop();
	}
}