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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.custom.activity.ChainSubActivity;
import de.macsystems.windroid.identifyable.CardinalDirection;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.Measure;

/**
 * @author Jens Hohl
 * @version $Id: SpotConfigurationActivity.java 195 2010-02-08 02:02:06Z
 *          jens.hohl $
 * 
 */
public final class SpotConfigurationActivity extends ChainSubActivity
{
	private final String LOG_TAG = SpotConfigurationActivity.class.getSimpleName();

	private Measure currentSelectUnit = Measure.BEAUFORT;

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
	protected void onCreate(final Bundle savedInstanceState) throws IllegalArgumentException
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spotconfiguration);
		Background.apply(this);
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
			throw new IllegalArgumentException("Spot Configuration missing.");
		}
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		final ArrayAdapter<Measure> continentAdapter = new ArrayAdapter<Measure>(this,
				android.R.layout.simple_spinner_item, Measure.values());
		continentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// unitsSpinner.setAdapter(continentAdapter);

		// --
		final Spinner directionsFromSpinner = (Spinner) findViewById(R.id.units_windirection_from_spinner);
		final ArrayAdapter<CardinalDirection> directionsFrom = new ArrayAdapter<CardinalDirection>(this,
				android.R.layout.simple_spinner_item, CardinalDirection.getValues());
		directionsFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		directionsFromSpinner.setAdapter(directionsFrom);
		// --
		final Spinner directionsToSpinner = (Spinner) findViewById(R.id.units_windirection_to_spinner);
		final ArrayAdapter<CardinalDirection> directionsTo = new ArrayAdapter<CardinalDirection>(this,
				android.R.layout.simple_spinner_item, CardinalDirection.getValues());
		directionsTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		directionsToSpinner.setAdapter(directionsTo);
		// --- pre select checkboxes ---
		if (stationInfo.isUseWindirection())
		{
			final CardinalDirection toDirection = stationInfo.getToDirection();
			final int selectionToIndex = IdentityUtil.indexOf(toDirection.getId(), CardinalDirection.getValues());
			directionsToSpinner.setSelection(selectionToIndex);
			final CardinalDirection fromDirection = stationInfo.getFromDirection();
			final int fromSelectionIndex = IdentityUtil.indexOf(fromDirection.getId(), CardinalDirection.getValues());
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
					stationInfo.setFromDirection((CardinalDirection) directionsFromSpinner.getSelectedItem());
					stationInfo.setToDirection((CardinalDirection) directionsToSpinner.getSelectedItem());
				}

				stationInfo.setWindspeedMin(currentMinimum);
				stationInfo.setWindspeedMax(currentMaximum);
				stationInfo.setPreferredWindUnit(SpotConfigurationActivity.this.currentSelectUnit);

				final Intent intent = new Intent(SpotConfigurationActivity.this, ScheduleActivity.class);
				intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, stationInfo);

				startActivityForResult(intent, MainActivity.CONFIGURATION_REQUEST_CODE);
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
				if (Logging.isEnabled)
				{
					Log.d(LOG_TAG, "From user:" + fromUser);
				}

				if (progress >= SpotConfigurationActivity.this.currentMaximum)
				{

					progress = SpotConfigurationActivity.this.currentMaximum;
					seekBar.setProgress(progress);
					vibrator.vibrate(VIBRATE_DURATION);
				}
				SpotConfigurationActivity.this.currentMinimum = progress;
				if (Logging.isEnabled)
				{
					Log.d(LOG_TAG, "current minimum:" + SpotConfigurationActivity.this.currentMinimum);
				}
				updateDeltaTextView(currentSelectUnit, SpotConfigurationActivity.this.currentMinimum,
						SpotConfigurationActivity.this.currentMaximum);
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
			public final void onProgressChanged(final SeekBar _seekBar, int _progress, final boolean _fromUser)
			{

				if (Logging.isEnabled)
				{
					Log.d(LOG_TAG, "From user:" + _fromUser);
				}
				if (SpotConfigurationActivity.this.currentMinimum >= _progress)
				{
					_progress = SpotConfigurationActivity.this.currentMinimum;
					_seekBar.setProgress(SpotConfigurationActivity.this.currentMinimum);
					vibrator.vibrate(VIBRATE_DURATION);
				}
				SpotConfigurationActivity.this.currentMaximum = _progress;
				if (Logging.isEnabled)
				{
					Log.d(LOG_TAG, "current maximum:" + SpotConfigurationActivity.this.currentMaximum);
				}
				updateDeltaTextView(currentSelectUnit, SpotConfigurationActivity.this.currentMinimum,
						SpotConfigurationActivity.this.currentMaximum);

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

	/**
	 * Updates Delta TextView
	 * 
	 * @param unit
	 * @param min
	 * @param max
	 */
	private void updateDeltaTextView(final Measure unit, final int min, final int max)
	{
		final TextView deltaView = (TextView) findViewById(R.id.unit_delta_textview);
		deltaView.setText(unit.toString() + " " + Integer.toString(min) + " - " + Integer.toString(max));
	}
}
