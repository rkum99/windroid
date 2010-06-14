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
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.db.DAOFactory;
import de.macsystems.windroid.db.DBException;
import de.macsystems.windroid.db.IContinentDAO;
import de.macsystems.windroid.db.ICountryDAO;
import de.macsystems.windroid.db.IRegionDAO;
import de.macsystems.windroid.db.ISpotDAO;
import de.macsystems.windroid.io.IOUtils;

/**
 * Activity which allows User to select a Spot using Spinners.
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public final class SpotSelectionActivity extends DBChainSubActivity
{

	private final static String LOG_TAG = SpotSelectionActivity.class.getSimpleName();

	private final Handler handler = new Handler();

	private ISpotDAO spotDAO = null;
	private IContinentDAO continentDAO = null;
	private ICountryDAO countryDAO = null;
	private IRegionDAO regionDAO = null;

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
		Background.apply(this);
		handler.post(populateParsingResults());

		spotDAO = DAOFactory.getSpotDAO(SpotSelectionActivity.this);
		continentDAO = DAOFactory.getContinentDAO(SpotSelectionActivity.this);
		countryDAO = DAOFactory.getCountryDAO(SpotSelectionActivity.this);
		regionDAO = DAOFactory.getRegionDAO(SpotSelectionActivity.this);

		daoManager.addDAO(spotDAO);
		daoManager.addDAO(continentDAO);
		daoManager.addDAO(countryDAO);
		daoManager.addDAO(regionDAO);

		final Button selectButton = (Button) findViewById(R.id.stationSelect);
		selectButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(final View v)
			{
				/**
				 * Post selected Station to next Activity.
				 */
				final Intent intent = new Intent(SpotSelectionActivity.this, SpotConfigurationActivity.class);

				final Spinner spotSpinner = (Spinner) findViewById(R.id.stationSpinner);
				/**
				 * Create an Transport Object for current station which will be
				 * configured.
				 */

				Cursor cursor = null;
				try
				{
					cursor = (Cursor) spotSpinner.getSelectedItem();
					final int index = cursor.getColumnIndexOrThrow("spotid");
					final String spotId = cursor.getString(index);

					try
					{
						final SpotConfigurationVO info = spotDAO.fetchBy(spotId);
						intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, info);
						SpotSelectionActivity.this.startActivityForResult(intent,
								MainActivity.CONFIGURATION_REQUEST_CODE);
					}
					catch (final DBException e)
					{
						Log.e(LOG_TAG, "failed to fetch Configuration.", e);
					}

				}
				finally
				{
					IOUtils.close(cursor);
				}
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
					final SharedPreferences pref = Util.getSharedPreferences(SpotSelectionActivity.this);
					final String continentID = Util.getSelectedContinentID(pref);

					try
					{
						final int selectionIndex = continentDAO.getIndexByID(continentID);
						final Cursor c = continentDAO.fetchAll();
						startManagingCursor(c);
						final String[] from = new String[]
						{ "id", "name" };
						final int[] to = new int[]
						{ android.R.id.text1, android.R.id.text2 };
						final SimpleCursorAdapter shows = new SimpleCursorAdapter(SpotSelectionActivity.this,
								android.R.layout.simple_spinner_item, c, from, to);

						final Spinner continentSpinner = (Spinner) findViewById(R.id.continentSpinner);
						shows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						continentSpinner.setAdapter(shows);
						continentSpinner.setSelection(selectionIndex);
						continentSpinner.setOnItemSelectedListener(createContinentListener());
					}
					catch (final DBException e)
					{
						Log.e(LOG_TAG, "failed to get index for :" + continentID, e);
					}

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

				final String id = getID(R.id.continentSpinner);
				// final ICountryDAO dao =
				// DAOFactory.getCountryDAO(SpotSelectionActivity.this);

				final Cursor c = countryDAO.fetchByContinentID(id);
				startManagingCursor(c);
				final String[] from = new String[]
				{ "name", "id" };
				final int[] to = new int[]
				{ android.R.id.text1, android.R.id.text2 };
				final SimpleCursorAdapter shows = new SimpleCursorAdapter(SpotSelectionActivity.this,
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
				// final IRegionDAO dao =
				// DAOFactory.getRegionDAO(SpotSelectionActivity.this);
				final String id = getID(R.id.countrySpinner);

				final Cursor c = regionDAO.fetchByCountryID(id);
				startManagingCursor(c);
				final String[] from = new String[]
				{ "name", "id" };
				final int[] to = new int[]
				{ android.R.id.text1, android.R.id.text2 };
				final SimpleCursorAdapter shows = new SimpleCursorAdapter(SpotSelectionActivity.this,
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
				// final ISpotDAO dao =
				// DAOFactory.getSpotDAO(SpotSelectionActivity.this);
				final Spinner spotSpinner = (Spinner) findViewById(R.id.stationSpinner);
				//
				final String countryID = getID(R.id.countrySpinner);
				final String regionID = getID(R.id.regionSpinner);
				final String continentID = getID(R.id.continentSpinner);

				final Cursor c = spotDAO.fetchBy(continentID, countryID, regionID);
				startManagingCursor(c);
				final String[] from = new String[]
				{ "name", "spotid" };
				final int[] to = new int[]
				{ android.R.id.text1, android.R.id.text2 };
				final SimpleCursorAdapter shows = new SimpleCursorAdapter(SpotSelectionActivity.this,
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
		// Util.printCursorColumnNames(cursor);
		final int index = cursor.getColumnIndexOrThrow("id");
		return cursor.getString(index);
	}

}