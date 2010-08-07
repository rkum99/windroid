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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.NotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import de.macsystems.windroid.alarm.AlarmManagerFactory;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.ISelectedDAO;
import de.macsystems.windroid.db.ISpotDAO;
import de.macsystems.windroid.proxy.SpotServiceConnection;
import de.macsystems.windroid.receiver.EnableViewConnectionBroadcastReciever;
import de.macsystems.windroid.service.SpotService;

/**
 * This is the Entry Point when the Application will be launched.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class MainActivity extends DBActivity
{

	private final static int OPTION_ABOUT_MENU_ID = 777;
	/**
	 * Configuration SubActivities will be launched by this request code
	 */
	public final static int CONFIGURATION_REQUEST_CODE = 0x100;

	private final static String LOG_TAG = MainActivity.class.getSimpleName();

	private EnableViewConnectionBroadcastReciever broadcastReceiver;

	private ISpotDAO spotDAO = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CONFIGURATION_REQUEST_CODE)
		{
			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "Recieved requestcode CONFIGURATION_REQUEST_CODE");
			}
			if (resultCode == Activity.RESULT_OK)
			{
				if (Logging.isEnabled)
				{
					Log.d(LOG_TAG, "Recieved resultcode RESULT_OK");
				}
				// updateSpot(data);
				if (WindUtils.isSpotConfigured(data))
				{
					final SpotConfigurationVO spot = WindUtils.getConfigurationFromIntent(data);

					final ISelectedDAO dao = DAOFactory.getSelectedDAO(this);
					dao.insertSpot(spot);
					AlarmManagerFactory.getAlarmManager().createAlarmForSpot(spot, this, false);

					// if (Logging.isLoggingEnabled())s
					// {
					// final StringBuilder builder = new
					// StringBuilder(256).append("\n");
					// builder.append("Folgender Spot wurde Angelegt:\n").append("\n\n");
					// builder.append("Name: ").append(spot.getStation().getName()).append("\n");
					// builder.append("ID: ").append(spot.getStation().getId()).append("\n");
					// builder.append("Keyword: ").append(spot.getStation().getKeyword()).append("\n");
					// builder.append("hasStatistic: ").append(spot.getStation().hasStatistic()).append("\n");
					// builder.append("hasSuperForcast: ").append(spot.getStation().hasSuperforecast()).append("\n");
					// builder.append("PreferedWindUnit: ").append(spot.getPreferredWindUnit()).append("\n");
					// builder.append("Wind From: ").append(spot.getFromDirection()).append("\n");
					// builder.append("Wind To: ").append(spot.getToDirection()).append("\n");
					// builder.append("Take care of Windirection: ").append(spot.isUseWindirection()).append("\n");
					// Toast.makeText(MainActivity.this, builder.toString(),
					// Toast.LENGTH_LONG).show();
					// }
				}

			}
			else if (resultCode == Activity.RESULT_CANCELED)
			{
				if (Logging.isEnabled)
				{
					Log.d(LOG_TAG, "Recieved resultCode RESULT_CANCELED");
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("Wrong request recieved. Expected CONFIGURATION_REQUEST_CODE");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		final View buttonSelectStation = findViewById(R.id.button_show_station_selection);
		final View buttonHelp = findViewById(R.id.button_show_station_help);
		final List<View> viewsToDisableOnConnectionLost = new ArrayList<View>();
		viewsToDisableOnConnectionLost.add(buttonHelp);
		viewsToDisableOnConnectionLost.add(buttonSelectStation);

		broadcastReceiver = new EnableViewConnectionBroadcastReciever(viewsToDisableOnConnectionLost, this);
		registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		//
		// Show actual database size.
		//
		final TextView footer = (TextView) findViewById(R.id.main_footer_database_size);

		spotDAO = DAOFactory.getSpotDAO(this);
		daoManager.addDAO(spotDAO);
		final DecimalFormat df = new DecimalFormat(",##0");
		final String nrSpot = df.format(spotDAO.getSize());

		final String orgString = getString(R.string.welcome_database_size);
		final String dbSizeString = orgString.replace("$1", nrSpot);
		footer.setText(dbSizeString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop()
	{
		unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onDestroy()
	{
		/**
		 * Destroy the Service. As we not using it any more.
		 * 
		 * 
		 * TODO: Is this correct there ? As the Service is running we may
		 * interrupt it there.
		 */
		final Intent stopIntent = new Intent(this, SpotService.class);
		stopService(stopIntent);
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle _savedInstanceState)
	{
		super.onCreate(_savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Util.logDisplayMetrics(this);

		showGPL();

		// startSpotService();
		setContentView(R.layout.main);

		// final View view = findViewById(R.id.background_image_main);
		// System.out.println("View "+view);

		Background.apply(this);

		final Button selectSpotButton = (Button) findViewById(R.id.button_show_station_selection);
		selectSpotButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public final void onClick(final View v)
			{
				MainActivity.this.launchSetupOrSpotSelectionActivity();
			}
		});
		final Button selectPreferencesButton = (Button) findViewById(R.id.button_show_station_preferences);
		selectPreferencesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public final void onClick(final View v)
			{
				final Intent intent = new Intent(MainActivity.this, Preferences.class);
				MainActivity.this.startActivity(intent);
			}
		});

		final Button showSpotOverviewButton = (Button) findViewById(R.id.button_show_spot_overview);
		showSpotOverviewButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public final void onClick(final View v)
			{
				MainActivity.this.showSpotOverview();
			}
		});

		final Button selectHelpPreferencesButton = (Button) findViewById(R.id.button_show_station_help);
		selectHelpPreferencesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public final void onClick(final View v)
			{
				showHelpPage();
			}
		});

		setupCancelAlarmTest();
	}

	final private void showGPL()
	{
		if (Util.isLicenceAccepted(this))
		{
			return;
		}

		final android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public final void onClick(final DialogInterface _dialog, final int _which)
			{
				if (_which == DialogInterface.BUTTON_NEGATIVE)
				{
					if (Logging.isEnabled)
					{
						Log.i(LOG_TAG, "GPL not accepted.");
						Log.i(LOG_TAG, "Closing App.");
					}
					Util.setLicenceAccepted(MainActivity.this, false);
					finish();
				}
				else
				{
					if (Logging.isEnabled)
					{
						Log.i(LOG_TAG, "GPL accepted.");
						Log.i(LOG_TAG, "Starting App.");
					}
					Util.setLicenceAccepted(MainActivity.this, true);
				}
			}
		};

		showAlertDialogWithLinks(R.string.licence_title, R.string.licence, listener);

	}

	/**
	 * @param listener
	 * @throws NotFoundException
	 */
	private void showAlertDialogWithLinks(final int _titleResID, final int _textResID,
			final android.content.DialogInterface.OnClickListener listener) throws NotFoundException
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.launcher);
		builder.setCancelable(false);
		builder.setNegativeButton(android.R.string.no, listener);
		builder.setPositiveButton(android.R.string.yes, listener);
		builder.setTitle(_titleResID);

		final TextView gplTextView = new TextView(this);
		final ScrollView scrollView = new ScrollView(this);
		scrollView.setPadding(10, 10, 10, 10);

		scrollView.addView(gplTextView);
		final SpannableString spanned = new SpannableString(Html.fromHtml(getResources().getString(_textResID)));
		Linkify.addLinks(spanned, Linkify.WEB_URLS);
		gplTextView.setMovementMethod(LinkMovementMethod.getInstance());
		gplTextView.setText(spanned);
		builder.setView(scrollView);
		final AlertDialog dialog = builder.show();
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}

	/**
	 * Installs a Listener for events on Service On/Off toggle button which
	 * called AIDL interface.
	 * 
	 * @param _connection
	 * @return
	 */
	private OnCheckedChangeListener createServiceToogleListener(final SpotServiceConnection _connection)
	{
		if (_connection == null)
		{
			throw new NullPointerException();
		}

		final OnCheckedChangeListener listener = new OnCheckedChangeListener()
		{
			@Override
			public final void onCheckedChanged(final CompoundButton _buttonView, final boolean _isChecked)
			{
				try
				{
					if (_isChecked)
					{
						_connection.initAlarms();
					}
					else
					{
						// TODO: Implement Stop on all enqueued alerts
						// _connection.stop();
					}
				}
				catch (final RemoteException e)
				{
					Log.e(LOG_TAG, "Failed to change Service status", e);
				}
			}
		};
		return listener;
	}

	/**
	 * Test Code
	 */
	@Deprecated
	private final void setupCancelAlarmTest()
	{

		final Button button = (Button) findViewById(R.id.button_cancel_all_alerts);

		final OnClickListener listener = new View.OnClickListener()
		{

			@Override
			public final void onClick(final View v)
			{
				AlarmManagerFactory.getAlarmManager().cancelAllAlerts(MainActivity.this);
			}
		};
		button.setOnClickListener(listener);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		final MenuItem about = menu.add(Menu.NONE, OPTION_ABOUT_MENU_ID, Menu.NONE, R.string.about_button_text);
		about.setIcon(R.drawable.info);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		super.onOptionsItemSelected(item);
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "selected icon id " + item.getItemId());
		}
		boolean result = false;
		if (item.getItemId() == OPTION_ABOUT_MENU_ID)
		{
			showAboutDialog();
			result = true;
		}
		return result;
	}

	private void showAboutDialog()
	{
		final LayoutInflater inflater = LayoutInflater.from(this);
		final View view = inflater.inflate(R.layout.about, null);

		final AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
		aboutBuilder.setIcon(R.drawable.launcher);
		aboutBuilder.setTitle(R.string.about_title);
		aboutBuilder.setView(view);
		aboutBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			public final void onClick(final DialogInterface dialog, final int which)
			{
				dialog.dismiss();
			}
		});

		final AlertDialog dialog = aboutBuilder.show();
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}

	/**
	 * Shows Spot Overview
	 */
	private void showSpotOverview()
	{
		final Intent intent = new Intent(this, SpotOverviewActivity.class);
		startActivity(intent);
	}

	/**
	 * Opens Browser by using @ {@link ACTION_VIEW}
	 */
	private void showHelpPage()
	{
		final String url = getString(R.string.main_help_url);
		final Uri uri = Uri.parse(url);
		final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	/**
	 * Launches {@link DownloadActivity} Activity or the
	 * {@link SpotSelectionActivity} Activity which depends on database
	 * 
	 */
	private void launchSetupOrSpotSelectionActivity()
	{

		@Deprecated
		final ISpotDAO dao = DAOFactory.getSpotDAO(MainActivity.this);
		final boolean spotsfound = dao.hasSpots();

		final SpotConfigurationVO spotConfigurationVO = new SpotConfigurationVO();

		final Intent intent;
		if (spotsfound)
		{
			/**
			 * If user wants to configure a Spot we create an Transport Object
			 * to collect all properties
			 */
			intent = new Intent(MainActivity.this, SpotSelectionActivity.class);
			intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, spotConfigurationVO);
		}
		else
		{
			/**
			 * Start download of data first, then user can configure a spot
			 * using Transport Object to collect all properties.
			 */
			intent = new Intent(MainActivity.this, DownloadActivity.class);
			intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, spotConfigurationVO);
		}
		// We expect a result in method onActivityResult(....)
		startActivityForResult(intent, CONFIGURATION_REQUEST_CODE);

	}
}