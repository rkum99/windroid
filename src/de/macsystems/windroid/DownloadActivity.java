package de.macsystems.windroid;

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
import de.macsystems.windroid.db.ISpotDAO;
import de.macsystems.windroid.db.SpotDAO;
import de.macsystems.windroid.db.Database;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.task.XMLParseTask;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.ProgressBarAdapter;

/**
 * Activity which downloads station.xml and updates the database. The User can
 * see the progress while waiting and is able to cancel this.
 * 
 * @author mac
 * @version $Id$
 */
public class DownloadActivity extends Activity
{
	private final static String LOG_TAG = DownloadActivity.class.getSimpleName();

	private final Handler handler = new Handler();

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

		final Thread parseThread = new Thread("Parse XML")
		{
			@Override
			public final void run()
			{
				if (Log.isLoggable(LOG_TAG, Log.DEBUG))
				{
					Log.d(LOG_TAG, "Parsing Thread started.");
				}
				try
				{

					downloadProgress.setMax(100);
					if (WindUtils.isStationListUpdateAvailable(DownloadActivity.this))
					{
						WindUtils.updateStationList(DownloadActivity.this, downloadProgress);
					}
					downloadProgress.incrementBy(50);
					final XMLParseTask task = new XMLParseTask(new URI(IOUtils.stationsXMLFilePath), databaseProgress);
					final int stationsFound = task.execute(DownloadActivity.this);
					downloadProgress.incrementBy(50);

					databaseProgress.setMax(stationsFound);

					final Database database = new Database(DownloadActivity.this);
					final ISpotDAO updater = new SpotDAO(database, databaseProgress);
					updater.insertSpots();
					// final ConfigDAO config = new ConfigDAO(database);
					// config.setDatabaseStatus("succsess");
					showInstallSucceed();
				}
				catch (final Exception e)
				{
					Log.e(LOG_TAG, "Failed to parse xml.", e);
					// final Database database = new
					// Database(DownloadActivity.this);
					// final ConfigDAO config = new ConfigDAO(database);
					// config.setDatabaseStatus("falied");
					showInstallationFailed(e);
				}
				finally
				{
					if (Log.isLoggable(LOG_TAG, Log.DEBUG))
					{
						Log.d(LOG_TAG, "Parsing Thread ended.");
					}
				}
			}
		};
		parseThread.start();
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

				new AlertDialog.Builder(DownloadActivity.this).setPositiveButton(ok, getListener(Main.class)).setTitle(
						header).setMessage(message + stackTrace).show();
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

				new AlertDialog.Builder(DownloadActivity.this).setPositiveButton(ok, getListener(SpotSelection.class))
						.setTitle(header).setMessage(message).show();
			}
		});
	}

	private DialogInterface.OnClickListener getListener(final Class<? extends Activity> _nextActivity)
	{
		final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
		{
			@Override
			public final void onClick(final DialogInterface dialog, final int which)
			{
				final Intent nextActivitiyIntent = new Intent(DownloadActivity.this, _nextActivity);
				startActivity(nextActivitiyIntent);
			}
		};
		return listener;
	}

	/**
	 * @return
	 */
	private OnClickListener getCancelListener()
	{
		final OnClickListener listener = new OnClickListener()
		{
			@Override
			public final void onClick(final View v)
			{
				final Intent nextActivitiyIntent = new Intent(DownloadActivity.this, Main.class);
				startActivity(nextActivitiyIntent);
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