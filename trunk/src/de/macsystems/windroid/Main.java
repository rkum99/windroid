package de.macsystems.windroid;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import de.macsystems.windroid.alarm.AlarmDetail;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.ISpotDAO;
import de.macsystems.windroid.forecast.SpotOverview;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.proxy.SpotServiceConnection;

/**
 * This is the Entry Point when the Application will be launched.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class Main extends Activity
{

	private final static int ABOUT_MENU_ID = 777;
	/**
	 * Configuration SubActivities will be launched by this request code
	 */
	public final static int CONFIGURATION_REQUEST_CODE = 0x100;

	private final static String LOG_TAG = Main.class.getSimpleName();

	private EnableViewConnectionBroadcastReciever broadcastReceiver;

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
		if (requestCode == Activity.RESULT_CANCELED)
		{
			Log.d(LOG_TAG, "SubActivity was canceled.");
			return;
		}
		else if (CONFIGURATION_REQUEST_CODE == requestCode)
		{
			Log.d(LOG_TAG, "CONFIGURATION_REQUEST_CODE Code recieved :" + requestCode);
			/**
			 * TODO: Remove Code later
			 */
			if (WindUtils.isSpotConfigured(getIntent()))
			{
				final SpotConfigurationVO spot = WindUtils.getConfigurationFromIntent(data);

				// TODO Move into DAO package.
				Util.persistSpotConfigurationVO(spot, this);

				final StringBuilder builder = new StringBuilder(256).append("\n");
				builder.append("Folgender Spot wurde Angelegt:\n").append("\n\n");
				builder.append("Name: ").append(spot.getStation().getName()).append("\n");
				builder.append("ID: ").append(spot.getStation().getId()).append("\n");
				builder.append("Keyword: ").append(spot.getStation().getKeyword()).append("\n");
				builder.append("hasStatistic: ").append(spot.getStation().hasStatistic()).append("\n");
				builder.append("hasSuperForcast: ").append(spot.getStation().hasSuperforecast()).append("\n");
				builder.append("PreferedWindUnit: ").append(spot.getPreferredWindUnit()).append("\n");
				builder.append("Wind From: ").append(spot.getFromDirection()).append("\n");
				builder.append("Wind To: ").append(spot.getToDirection()).append("\n");
				builder.append("Take care of Windirection: ").append(spot.isUseWindirection()).append("\n");
				Toast.makeText(Main.this, builder.toString(), Toast.LENGTH_LONG).show();
			}

		}
		else
		{
			Log.e(LOG_TAG, "Illegal Request Code recieved :" + requestCode);
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
		final ISpotDAO dao = DAOFactory.getSpotDAO(this);
		final DecimalFormat df = new DecimalFormat(",##0");
		final String nrSpot = df.format(dao.getSize());
		final String newText = footer.getText().toString().replace("$1", nrSpot);
		footer.setText(newText);
		//
		super.onResume();
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
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle _savedInstanceState)
	{
		super.onCreate(_savedInstanceState);

		startSpotService();
		setContentView(R.layout.main);

		final Button selectSpotButton = (Button) findViewById(R.id.button_show_station_selection);
		selectSpotButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public final void onClick(final View v)
			{
				Main.this.launchSetupOrSpotSelectionActivity();
			}
		});
		final Button selectPreferencesButton = (Button) findViewById(R.id.button_show_station_preferences);
		selectPreferencesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public final void onClick(final View v)
			{
				final Intent intent = new Intent(Main.this, Preferences.class);
				Main.this.startActivity(intent);
			}
		});

		final Button showSpotOverviewButton = (Button) findViewById(R.id.button_show_spot_overview);
		showSpotOverviewButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public final void onClick(final View v)
			{
				Main.this.showSpotOverview();
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

		final CompoundButton toogleServiceButton = (CompoundButton) findViewById(R.id.button_toogle_service);
		final SpotServiceConnection serviceConnection = new SpotServiceConnection(toogleServiceButton, this);
		toogleServiceButton.setOnCheckedChangeListener(createServiceToogleListener(serviceConnection));

		setupNotificationTest();

	}

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
						_connection.start();
					}
					else
					{
						_connection.stop();
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
	 * Checks if Background SpotService already running. If not it will be
	 * started.
	 */
	private void startSpotService()
	{
		final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

		boolean isServiceFound = false;

		for (int i = 0; i < services.size(); i++)
		{
			Log.d(LOG_TAG, "Service Nr. " + i + " :" + services.get(i).service);
			Log.d(LOG_TAG, "Service Nr. " + i + " package name : " + services.get(i).service.getPackageName());
			Log.d(LOG_TAG, "Service Nr. " + i + " class name   : " + services.get(i).service.getClassName());

			if ("de.macsystems.windroid".equals(services.get(i).service.getPackageName()))
			{
				if ("de.macsystems.windroid.SpotService".equals(services.get(i).service.getClassName()))
				{
					isServiceFound = true;
					break;
				}
			}
		}

		if (!isServiceFound)
		{
			Log.d(LOG_TAG, "Starting Service");
			final Intent startServiceIntent = new Intent();
			startServiceIntent.setAction(SpotService.DE_MACSYSTEMS_WINDROID_START_SPOT_SERVICE_ACTION);
			startService(startServiceIntent);
		}
		else
		{
			Log.i(LOG_TAG, "Nothing to do, SpotService already running !!!!!");
		}
	}

	/**
	 * Test Code
	 */
	@Deprecated
	private final void setupNotificationTest()
	{

		final Button button = (Button) findViewById(R.id.button_simulate_wind_alert);

		final OnClickListener listener = new View.OnClickListener()
		{

			@Override
			public final void onClick(final View v)
			{
				final NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				showNotification(Main.this, mManager, 999, "Windalarm", "Alarm für Station XYZ wurde ausgelöst.");
			}
		};
		button.setOnClickListener(listener);
	}

	private static void showNotification(final Context context, final NotificationManager notificationManager,
			final int alarmID, final String alarmTitle, final String alarmDetails)
	{
		final Notification notification = new Notification(R.drawable.icon, "Alarm", System.currentTimeMillis());

		notification.ledARGB = 0xff00ff00;
		notification.ledOnMS = 40;
		notification.ledOffMS = 40;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		notification.sound = IOUtils.getResourceURI(context, R.raw.wind_chime);

		final Intent intent = new Intent(context, AlarmNotificationDetail.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		final AlarmDetail details = new AlarmDetail(alarmID, "station id", "Mauritius");
		intent.putExtra(IntentConstants.ALARM_DETAIL, details);
		intent.putExtra("AlarmID", alarmID);
		final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		notification.setLatestEventInfo(context, alarmTitle, alarmDetails, pendingIntent);
		notificationManager.notify(alarmID, notification);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, ABOUT_MENU_ID, Menu.NONE, R.string.about_button_text);
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

		Log.d(LOG_TAG, "selected icon id " + item.getItemId());
		if (item.getItemId() == ABOUT_MENU_ID)
		{
			showAboutDialog();
			return true;
		}
		return false;
	}

	private void showAboutDialog()
	{
		final AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
		aboutBuilder.setIcon(R.drawable.icon);
		aboutBuilder.setTitle(R.string.app_name);
		aboutBuilder.setMessage(R.string.about_text);
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
		final Intent intent = new Intent(this, SpotOverview.class);
		startActivity(intent);
	}

	/**
	 * Opens Browser by using ACTION_VIEW
	 */
	private void showHelpPage()
	{
		final Uri uri = Uri.parse("http://windfinder.com");
		final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	/**
	 * Launches {@link DownloadActivity} Activity or the {@link SpotSelection}
	 * Activity which depends on database
	 * 
	 */
	private void launchSetupOrSpotSelectionActivity()
	{
		final ISpotDAO dao = DAOFactory.getSpotDAO(Main.this);
		final boolean spotsfound = dao.hasSpots();

		final SpotConfigurationVO spotConfigurationVO = new SpotConfigurationVO();

		final Intent intent;
		if (spotsfound)
		{
			/**
			 * If user wants to configure a Spot we create an Transport Object
			 * to collect all properties
			 */
			intent = new Intent(Main.this, SpotSelection.class);
			intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, spotConfigurationVO);
		}
		else
		{
			/**
			 * Start download of data first, then user can configure a spot
			 * using Transport Object to collect all properties.
			 */
			intent = new Intent(Main.this, DownloadActivity.class);
			intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, spotConfigurationVO);
		}
		// We expect a result in method onActivityResult(....)
		startActivityForResult(intent, CONFIGURATION_REQUEST_CODE);

	}
}