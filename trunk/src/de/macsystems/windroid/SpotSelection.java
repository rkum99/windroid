package de.macsystems.windroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.IContinentDAO;
import de.macsystems.windroid.db.ICountryDAO;
import de.macsystems.windroid.db.IRegionDAO;
import de.macsystems.windroid.db.ISpotDAO;

/**
 * Activity which allows User to select a Spot using Spinners.
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public class SpotSelection extends ChainSubActivity
{

	private final static String LOG_TAG = SpotSelection.class.getSimpleName();

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
		handler.post(populateParsingResults());

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
				final Cursor cursor = (Cursor) spotSpinner.getSelectedItem();
				final int index = cursor.getColumnIndexOrThrow("spotid");
				final String spotId = cursor.getString(index);

				final ISpotDAO dao = DAOFactory.getSpotDAO(SpotSelection.this);
				final SpotConfigurationVO info = dao.fetchBy(spotId);

				intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, info);
				// SpotSelection.this.startActivity(intent);
				SpotSelection.this.startActivityForResult(intent, Main.CONFIGURATION_REQUEST_CODE);
			}
		});
	}

	private Runnable populateParsingResults()
	{
		final Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				{
					final SharedPreferences pref = Util.getSharedPreferences(SpotSelection.this);
					final String continentID = Util.getSelectedContinentID(pref);

					final IContinentDAO continentDAO = DAOFactory.getContinentDAO(SpotSelection.this);
					final int selectionIndex = continentDAO.getIndexByID(continentID);

					Log.d(LOG_TAG, "preferred continentID is     :" + continentID);
					Log.d(LOG_TAG, "preferred Index is :" + selectionIndex);

					final Cursor c = continentDAO.fetchAll();
					startManagingCursor(c);
					final String[] from = new String[]
					{ "id", "name" };
					final int[] to = new int[]
					{ android.R.id.text1, android.R.id.text2 };
					final SimpleCursorAdapter shows = new SimpleCursorAdapter(SpotSelection.this,
							android.R.layout.simple_spinner_item, c, from, to);

					final Spinner continentSpinner = (Spinner) findViewById(R.id.continentSpinner);
					shows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					continentSpinner.setAdapter(shows);
					// TODO selection is not working yet.
					continentSpinner.setSelection(selectionIndex);
					continentSpinner.setOnItemSelectedListener(createContinentListener());
				}
				// Country
				{

					final Spinner countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
					countrySpinner.setOnItemSelectedListener(createCountryListener());
				}
				// Region
				{

					final Spinner regionSpinner = (Spinner) findViewById(R.id.regionSpinner);
					regionSpinner.setOnItemSelectedListener(createRegionListener());

				}

				// Spot
				{
					final Spinner spotSpinner = (Spinner) findViewById(R.id.stationSpinner);
					spotSpinner.setOnItemSelectedListener(createSpotListener());
				}

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
					final long _id)
			{
				final ICountryDAO dao = DAOFactory.getCountryDAO(SpotSelection.this);
				final String id = getID(R.id.continentSpinner);

				final Cursor c = dao.fetchByContinentID(id);
				startManagingCursor(c);
				final String[] from = new String[]
				{ "name", "id" };
				final int[] to = new int[]
				{ android.R.id.text1, android.R.id.text2 };
				final SimpleCursorAdapter shows = new SimpleCursorAdapter(SpotSelection.this,
						android.R.layout.simple_spinner_item, c, from, to);

				final Spinner countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
				countrySpinner.setAdapter(shows);
				shows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
					final long _id)
			{
				final IRegionDAO dao = DAOFactory.getRegionDAO(SpotSelection.this);
				final String id = getID(R.id.countrySpinner);

				final Cursor c = dao.fetchByCountryID(id);
				startManagingCursor(c);
				final String[] from = new String[]
				{ "name", "id" };
				final int[] to = new int[]
				{ android.R.id.text1, android.R.id.text2 };
				final SimpleCursorAdapter shows = new SimpleCursorAdapter(SpotSelection.this,
						android.R.layout.simple_spinner_item, c, from, to);

				final Spinner regionSpinner = (Spinner) findViewById(R.id.regionSpinner);
				regionSpinner.setAdapter(shows);
				shows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
					final long _id)
			{
				final ISpotDAO dao = DAOFactory.getSpotDAO(SpotSelection.this);
				final Spinner spotSpinner = (Spinner) findViewById(R.id.stationSpinner);
				//
				final String countryID = getID(R.id.countrySpinner);
				final String regionID = getID(R.id.regionSpinner);
				final String continentID = getID(R.id.continentSpinner);

				final Cursor c = dao.fetchBy(continentID, countryID, regionID);
				startManagingCursor(c);
				final String[] from = new String[]
				{ "name", "spotid" };
				final int[] to = new int[]
				{ android.R.id.text1, android.R.id.text2 };
				final SimpleCursorAdapter shows = new SimpleCursorAdapter(SpotSelection.this,
						android.R.layout.simple_spinner_item, c, from, to);

				spotSpinner.setAdapter(shows);
				shows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			}

			@Override
			public final void onNothingSelected(final AdapterView<?> arg0)
			{
			}
		};
		return selectionHandler;
	}

	
	
	private OnItemSelectedListener createSpotListener()
	{
		final OnItemSelectedListener selectionHandler = new OnItemSelectedListener()
		{
			@Override
			public final void onItemSelected(final AdapterView<?> parent, final View view, final int position,
					final long _id)
			{
				// nothing to do.
			}

			@Override
			public final void onNothingSelected(final AdapterView<?> arg0)
			{
			}
		};
		return selectionHandler;
	}

	

	/**
	 * @param _resID
	 * @return
	 * @throws IllegalArgumentException
	 */
	private final String getID(final int _resID) throws IllegalArgumentException
	{
		final Spinner spinner = (Spinner) findViewById(_resID);
		final Cursor cursor = (Cursor) spinner.getSelectedItem();
//		Util.printCursorColumnNames(cursor);
		final int index = cursor.getColumnIndexOrThrow("id");
		return cursor.getString(index);
	}

}