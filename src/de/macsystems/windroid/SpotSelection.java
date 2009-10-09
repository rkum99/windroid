package de.macsystems.windroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import de.macsystems.windroid.identifyable.Continent;
import de.macsystems.windroid.identifyable.Country;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.Region;
import de.macsystems.windroid.identifyable.Station;

/**
 * Activity which allows User to select a Spot using Spinners.
 * 
 * @author mac
 * @version $Id$
 */
public class SpotSelection extends Activity
{

	private final static String LOG_TAG = SpotSelection.class.getPackage().getName();

	private final Handler handler = new Handler();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.spotselection);
		/**
		 * Test if Parsing already done.
		 */
		// if (!Continent.isParsed())
		// {
		// parseXML();
		//
		// }
		// else
		// {
		handler.post(populateParsingResults(Continent.values(), Continent.AFRICA.getCoutrys(), Continent.AFRICA
				.getCoutrys()[0].getRegions()));
		// }

		final Button selectButton = (Button) findViewById(R.id.stationSelect);
		selectButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(final View v)
			{
				/**
				 * Post selected Station to next Activity.
				 */
				final Intent intent = new Intent(SpotSelection.this, SpotConfiguration.class);

				final Spinner spotSpinner = (Spinner) findViewById(R.id.stationSpinner);
				/**
				 * Create an Transport Object for current station which will be
				 * configured.
				 */
				final SpotConfigurationVO info = new SpotConfigurationVO();
				info.setStation((Station) spotSpinner.getSelectedItem());
				intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, info);
				SpotSelection.this.startActivity(intent);
			}
		});
	}

//	private void parseXML()
//	{
//		final String networkName = IOUtils.getNetworkName(this);
//
//		// final ProgressDialog progress = ProgressDialog.show(this,
//
//		final ProgressDialog dialog = new ProgressDialog(this);
//		dialog.setTitle("Bitte Warten");
//		dialog
//				.setMessage("Lese Daten über "
//						+ networkName
//						+ ".\nDanach wir die Datenbank aktualisiert.\nJe nach Verbindung kann dies etwas Zeit in anspruch nehmen. ");
//		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		dialog.show();
//		dialog.setMax(1);
//
//		final IProgress progress = new ProgressDialogAdapter(dialog);
//
//		final Thread parseThread = new Thread("Parse XML")
//		{
//			@Override
//			public final void run()
//			{
//				boolean isFailure = false;
//				Throwable exception = null;
//				if (Log.isLoggable(LOG_TAG, Log.DEBUG))
//				{
//					Log.d(LOG_TAG, "Parsing Thread started.");
//				}
//				try
//				{
//					if (WindUtils.isStationListUpdateAvailable(SpotSelection.this))
//					{
//						WindUtils.updateStationList(SpotSelection.this);
//					}
//
//					final XMLParseTask task = new XMLParseTask(new URI(IOUtils.stationsXMLFilePath));
//					task.execute(SpotSelection.this);
//
//					dialog.setMax(task.getNrOfStations());
//
//					final Database database = new Database(SpotSelection.this);
//					final SpotDAO updater = new SpotDAO(database, progress);
//					updater.update();
//
//					handler.post(populateParsingResults(Continent.values(), Continent.AFRICA.getCoutrys(),
//							Continent.AFRICA.getCoutrys()[0].getRegions()));
//
//				}
//				catch (final Exception e)
//				{
//					isFailure = true;
//					exception = e;
//					Log.e(LOG_TAG, "Failed to parse xml.", e);
//
//				}
//				finally
//				{
//					if (Log.isLoggable(LOG_TAG, Log.DEBUG))
//					{
//						Log.d(LOG_TAG, "Parsing Thread ended.");
//					}
//					dialog.dismiss();
//
//					// if (isFailure)
//					// {
//					//
//					// new
//					// AlertDialog.Builder(SpotSelection.this).setMessage(Util.getStackTrace(exception)).setTitle(
//					// "Fatal Error.").setCancelable(false).show();
//					//
//					// }
//
//				}
//			}
//		};
//		parseThread.start();
//	}

	private Runnable populateParsingResults(final Continent[] continents, final Country[] countrys,
			final Region[] regions)
	{
		final Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				final Spinner continentSpinner = (Spinner) findViewById(R.id.continentSpinner);
				final ArrayAdapter<Continent> continentAdapter = new ArrayAdapter<Continent>(SpotSelection.this,
						android.R.layout.simple_spinner_item, continents);
				continentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				continentSpinner.setAdapter(continentAdapter);
				continentSpinner.setOnItemSelectedListener(createContinentListener());

				final SharedPreferences pref = Util.getSharedPreferences(SpotSelection.this);
				final String continentID = Util.getSelectedContinentID(pref);
				final int index = IdentityUtil.indexOf(continentID, continents);
				continentSpinner.setSelection(index);

				final Spinner coutrySpinner = (Spinner) findViewById(R.id.countrySpinner);
				final ArrayAdapter<Country> coutryAdapter = new ArrayAdapter<Country>(SpotSelection.this,
						android.R.layout.simple_spinner_item, countrys);
				coutryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				coutrySpinner.setAdapter(coutryAdapter);
				coutrySpinner.setOnItemSelectedListener(createCountryListener());

				final Spinner regionSpinner = (Spinner) findViewById(R.id.regionSpinner);
				final ArrayAdapter<Region> regionAdapter = new ArrayAdapter<Region>(SpotSelection.this,
						android.R.layout.simple_spinner_item, regions);
				regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				regionSpinner.setAdapter(regionAdapter);
				regionSpinner.setOnItemSelectedListener(createRegionListener());
			}
		};
		return runnable;
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
	}

	private OnItemSelectedListener createContinentListener()
	{
		final OnItemSelectedListener selectionHandler = new OnItemSelectedListener()
		{
			@Override
			public final void onItemSelected(final AdapterView<?> parent, final View view, final int position,
					final long id)
			{
				final Spinner spinner = (Spinner) findViewById(R.id.countrySpinner);
				final Continent selectedContinent = (Continent) parent.getSelectedItem();
				final ArrayAdapter<Country> coutryAdapter = new ArrayAdapter<Country>(SpotSelection.this,
						android.R.layout.simple_spinner_item, selectedContinent.getCoutrys());
				updateSpinner(spinner, coutryAdapter);
			}

			@Override
			public final void onNothingSelected(final AdapterView<?> arg0)
			{
			}

		};
		return selectionHandler;
	}

	private OnItemSelectedListener createCountryListener()
	{
		final OnItemSelectedListener selectionHandler = new OnItemSelectedListener()
		{
			@Override
			public final void onItemSelected(final AdapterView<?> parent, final View view, final int position,
					final long id)
			{
				final Spinner spinner = (Spinner) findViewById(R.id.regionSpinner);
				if (Log.isLoggable(LOG_TAG, Log.DEBUG))
				{
					Log.d("", "Selected Coutry:" + parent.getSelectedItem().getClass());
				}
				final Country selectedCountry = (Country) parent.getSelectedItem();
				final ArrayAdapter<Region> coutryAdapter = new ArrayAdapter<Region>(SpotSelection.this,
						android.R.layout.simple_spinner_item, selectedCountry.getRegions());
				updateSpinner(spinner, coutryAdapter);
			}

			@Override
			public final void onNothingSelected(final AdapterView<?> arg0)
			{
			}

		};
		return selectionHandler;
	}

	private OnItemSelectedListener createRegionListener()
	{
		final OnItemSelectedListener selectionHandler = new OnItemSelectedListener()
		{
			@Override
			public final void onItemSelected(final AdapterView<?> parent, final View view, final int position,
					final long id)
			{
				final Spinner spinner = (Spinner) findViewById(R.id.stationSpinner);
				Log.d("", "Selected Region:" + parent.getSelectedItem().getClass());
				final Region selectedRegion = (Region) parent.getSelectedItem();
				final ArrayAdapter<Station> regionAdapter = new ArrayAdapter<Station>(SpotSelection.this,
						android.R.layout.simple_spinner_item, selectedRegion.getStations());
				updateSpinner(spinner, regionAdapter);
			}

			@Override
			public final void onNothingSelected(final AdapterView<?> arg0)
			{
			}
		};
		return selectionHandler;
	}

	/**
	 * 
	 * @param spinner
	 * @param _adapter
	 */
	private static void updateSpinner(final Spinner spinner, final ArrayAdapter<?> _adapter)
	{
		_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(_adapter);

	}

}