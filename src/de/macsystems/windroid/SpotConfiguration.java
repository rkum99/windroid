package de.macsystems.windroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.WindDirection;
import de.macsystems.windroid.identifyable.WindUnit;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class SpotConfiguration extends ChainSubActivity
{
	private final String LOG_TAG = SpotConfiguration.class.getSimpleName();

	private WindUnit currentSelectUnit = WindUnit.BEAUFORT;

	private Vibrator vibrator = null;
	/**
	 * Time which Vibrate should active
	 */
	private final int VIBRATE_DURATION = 50;

	private volatile int currentMinimum = 0;
	private volatile int currentMaximum = 999;

	private SpotConfigurationVO stationInfo = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spotconfiguration);
		/**
		 * Insert Station Name
		 */

		if (WindUtils.isSpotConfigured(getIntent()))
		{
			stationInfo = WindUtils.getConfigurationFromIntent(getIntent());
			currentSelectUnit = stationInfo.getPreferredWindUnit();

			final TextView stationView = (TextView) findViewById(R.id.title_spot_configuration);

			final String text = stationView.getText().toString().replace("$1", stationInfo.getStation().getName());
			stationView.setText(text);

		}
		else
		{
			new IllegalArgumentException("Spot Configuration missing.");
		}
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		/**
		 * Fill Unit Spinner and pre select default value from preferences
		 */
		final Spinner unitsSpinner = (Spinner) findViewById(R.id.units_spinner);
		final ArrayAdapter<WindUnit> continentAdapter = new ArrayAdapter<WindUnit>(this,
				android.R.layout.simple_spinner_item, WindUnit.values());
		continentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// unitsSpinner.setAdapter(continentAdapter);
		final int index = IdentityUtil.indexOf(currentSelectUnit.getId(), WindUnit.values());
		unitsSpinner.setOnItemSelectedListener(getUnitsListener());
		unitsSpinner.setSelection(index);

		// --
		final Spinner directionsFromSpinner = (Spinner) findViewById(R.id.units_windirection_from_spinner);
		final ArrayAdapter<WindDirection> directionsFrom = new ArrayAdapter<WindDirection>(this,
				android.R.layout.simple_spinner_item, WindDirection.getValues());
		directionsFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		directionsFromSpinner.setAdapter(directionsFrom);
		// --
		final Spinner directionsToSpinner = (Spinner) findViewById(R.id.units_windirection_to_spinner);
		final ArrayAdapter<WindDirection> directionsTo = new ArrayAdapter<WindDirection>(this,
				android.R.layout.simple_spinner_item, WindDirection.getValues());
		directionsTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		directionsToSpinner.setAdapter(directionsTo);
		// --- pre select checkboxes ---
		if (stationInfo.isUseWindirection())
		{
			final WindDirection toDirection = stationInfo.getToDirection();
			final int selectionToIndex = IdentityUtil.indexOf(toDirection.getId(), WindDirection.getValues());
			directionsToSpinner.setSelection(selectionToIndex);
			final WindDirection fromDirection = stationInfo.getFromDirection();
			final int fromSelectionIndex = IdentityUtil.indexOf(fromDirection.getId(), WindDirection.getValues());
			directionsFromSpinner.setSelection(fromSelectionIndex);

		}

		// --
		final CheckBox selectWinddirection = (CheckBox) findViewById(R.id.unit_enable_winddirection_select);
		selectWinddirection.setChecked(stationInfo.isUseWindirection());
		selectWinddirection.setOnClickListener(getEnableWindirectionClickListener());

		final SeekBar minimumSeekbar = (SeekBar) findViewById(R.id.unit_minimum_seekbar);
		final SeekBar maximumSeekbar = (SeekBar) findViewById(R.id.unit_maximum_seekbar);

		directionsFromSpinner.setVisibility(selectWinddirection.isChecked() ? View.VISIBLE : View.INVISIBLE);
		directionsToSpinner.setVisibility(selectWinddirection.isChecked() ? View.VISIBLE : View.INVISIBLE);

		currentMaximum = (int) stationInfo.getWindspeedMax();
		currentMinimum = (int) stationInfo.getWindspeedMin();

		minimumSeekbar.setProgress((int) stationInfo.getWindspeedMin());
		maximumSeekbar.setProgress((int) stationInfo.getWindspeedMax());

		minimumSeekbar.setOnSeekBarChangeListener(createMinimumSeekbarListener());
		maximumSeekbar.setOnSeekBarChangeListener(getMaximumSeekbarListener());

		final Button acceptButton = (Button) findViewById(R.id.spot_configuration_accept_button);
		acceptButton.setOnClickListener(getAcceptOnClickListener());

		updateDeltaTextView(stationInfo.getPreferredWindUnit(), (int) stationInfo.getWindspeedMin(), (int) stationInfo
				.getWindspeedMax());

	}

	/**
	 * Creates an OnClickListener which invokes next Activity
	 * 
	 * @return
	 */
	private View.OnClickListener getAcceptOnClickListener()
	{
		final View.OnClickListener listener = new View.OnClickListener()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public final void onClick(final View v)
			{
				final Checkable selectWinddirection = (Checkable) findViewById(R.id.unit_enable_winddirection_select);
				final Spinner directionsFromSpinner = (Spinner) findViewById(R.id.units_windirection_from_spinner);
				final Spinner directionsToSpinner = (Spinner) findViewById(R.id.units_windirection_to_spinner);

				stationInfo.setUseWindirection(selectWinddirection.isChecked());
				if (selectWinddirection.isChecked())
				{
					stationInfo.setFromDirection((WindDirection) directionsFromSpinner.getSelectedItem());
					stationInfo.setToDirection((WindDirection) directionsToSpinner.getSelectedItem());
				}

				stationInfo.setWindspeedMin(currentMinimum);
				stationInfo.setWindspeedMax(currentMaximum);
				stationInfo.setPreferredWindUnit(SpotConfiguration.this.currentSelectUnit);

				final Intent intent = new Intent(SpotConfiguration.this, ScheduleActivity.class);
				intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, stationInfo);

				startActivityForResult(intent, Main.CONFIGURATION_REQUEST_CODE);
			}
		};
		return listener;
	}

	private View.OnClickListener getEnableWindirectionClickListener()
	{
		final View.OnClickListener listener = new View.OnClickListener()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public final void onClick(final View v)
			{
				final Checkable checkable = (Checkable) v;

				final Spinner directionsFromSpinner = (Spinner) findViewById(R.id.units_windirection_from_spinner);
				final Spinner directionsToSpinner = (Spinner) findViewById(R.id.units_windirection_to_spinner);
				directionsFromSpinner.setVisibility(checkable.isChecked() ? View.VISIBLE : View.INVISIBLE);
				directionsToSpinner.setVisibility(checkable.isChecked() ? View.VISIBLE : View.INVISIBLE);
			}
		};
		return listener;
	}

	private SeekBar.OnSeekBarChangeListener createMinimumSeekbarListener()
	{
		final SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged
			 * (android.widget.SeekBar, int, boolean)
			 */
			@Override
			public final void onProgressChanged(final SeekBar seekBar, int progress, final boolean fromUser)
			{

				Log.d(LOG_TAG, "From user:" + fromUser);

				if (progress >= SpotConfiguration.this.currentMaximum)
				{

					progress = SpotConfiguration.this.currentMaximum;
					seekBar.setProgress(progress);
					vibrator.vibrate(VIBRATE_DURATION);
				}
				SpotConfiguration.this.currentMinimum = progress;
				Log.d(LOG_TAG, "current minimum:" + SpotConfiguration.this.currentMinimum);
				updateDeltaTextView(currentSelectUnit, SpotConfiguration.this.currentMinimum,
						SpotConfiguration.this.currentMaximum);

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.SeekBar.OnSeekBarChangeListener#onStartTrackingTouch
			 * (android.widget.SeekBar)
			 */
			@Override
			public final void onStartTrackingTouch(final SeekBar seekBar)
			{
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.SeekBar.OnSeekBarChangeListener#onStopTrackingTouch
			 * (android.widget.SeekBar)
			 */
			@Override
			public final void onStopTrackingTouch(final SeekBar seekBar)
			{
			}

		};
		return listener;
	}

	private SeekBar.OnSeekBarChangeListener getMaximumSeekbarListener()
	{
		final SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged
			 * (android.widget.SeekBar, int, boolean)
			 */
			@Override
			public final void onProgressChanged(final SeekBar seekBar, int progress, final boolean fromUser)
			{
				Log.d(LOG_TAG, "From user:" + fromUser);
				if (SpotConfiguration.this.currentMinimum >= progress)
				{
					progress = SpotConfiguration.this.currentMinimum;
					seekBar.setProgress(SpotConfiguration.this.currentMinimum);
					vibrator.vibrate(VIBRATE_DURATION);
				}
				SpotConfiguration.this.currentMaximum = progress;
				Log.d(LOG_TAG, "current maximum:" + SpotConfiguration.this.currentMaximum);
				updateDeltaTextView(currentSelectUnit, SpotConfiguration.this.currentMinimum,
						SpotConfiguration.this.currentMaximum);

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.SeekBar.OnSeekBarChangeListener#onStartTrackingTouch
			 * (android.widget.SeekBar)
			 */
			@Override
			public final void onStartTrackingTouch(final SeekBar seekBar)
			{
			}

			@Override
			public final void onStopTrackingTouch(final SeekBar seekBar)
			{
			}

		};
		return listener;
	}

	private OnItemSelectedListener getUnitsListener()
	{
		final OnItemSelectedListener selectionHandler = new OnItemSelectedListener()
		{
			@Override
			public final void onItemSelected(final AdapterView<?> parent, final View view, final int position,
					final long id)
			{
				throw new IllegalArgumentException("Unit Lister aufgerufen.");
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.AdapterView.OnItemSelectedListener#onNothingSelected
			 * (android.widget.AdapterView)
			 */
			@Override
			public final void onNothingSelected(final AdapterView<?> arg0)
			{
			}

		};
		return selectionHandler;
	}

	/**
	 * Updates Delta TextView
	 * 
	 * @param unit
	 * @param min
	 * @param max
	 */
	private void updateDeltaTextView(final WindUnit unit, final int min, final int max)
	{
		Log.d(LOG_TAG, "Mein " + min + " Max " + max);
		final TextView deltaView = (TextView) findViewById(R.id.unit_delta_textview);
		deltaView.setText(unit.toString() + " " + Integer.toString(min) + " - " + Integer.toString(max));
	}
}
