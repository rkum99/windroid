package de.macsystems.windroid;

import java.util.ArrayList;
import java.util.List;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Activity which allows user to configure update schedule
 * 
 * @author mac
 * @version $Id$
 */
public class ScheduleActivity extends ChainSubActivity
{

	private static final String LOG_TAG = ScheduleActivity.class.getSimpleName();

	private final List<Integer> checkboxes = new ArrayList<Integer>();
	private final List<Integer> timeButtons = new ArrayList<Integer>();
	private final List<Integer> timeTextViews = new ArrayList<Integer>();

	private SpotConfigurationVO spotInfo = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);

		if (!WindUtils.isSpotConfigured(getIntent()))
		{
			new IllegalArgumentException("Spot Configuration missing.");
		}
		spotInfo = WindUtils.getConfigurationFromIntent(getIntent());

		checkboxes.add(R.id.schedule_checkbox_weekday_monday);
		checkboxes.add(R.id.schedule_checkbox_weekday_tuesday);
		checkboxes.add(R.id.schedule_checkbox_weekday_wednesday);
		checkboxes.add(R.id.schedule_checkbox_weekday_thursday);
		checkboxes.add(R.id.schedule_checkbox_weekday_friday);
		checkboxes.add(R.id.schedule_checkbox_weekday_saturday);
		checkboxes.add(R.id.schedule_checkbox_weekday_sunday);
		//
		timeButtons.add(R.id.schedule_button_weekday_monday);
		timeButtons.add(R.id.schedule_button_weekday_tuesday);
		timeButtons.add(R.id.schedule_button_weekday_wednesday);
		timeButtons.add(R.id.schedule_button_weekday_thursday);
		timeButtons.add(R.id.schedule_button_weekday_friday);
		timeButtons.add(R.id.schedule_button_weekday_saturday);
		timeButtons.add(R.id.schedule_button_weekday_sunday);
		//
		timeTextViews.add(R.id.schedule_label_weekday_monday);
		timeTextViews.add(R.id.schedule_label_weekday_tuesday);
		timeTextViews.add(R.id.schedule_label_weekday_wednesday);
		timeTextViews.add(R.id.schedule_label_weekday_thursday);
		timeTextViews.add(R.id.schedule_label_weekday_friday);
		timeTextViews.add(R.id.schedule_label_weekday_saturday);
		timeTextViews.add(R.id.schedule_label_weekday_sunday);

		installListenerOnCheckBoxes(checkboxes, timeButtons);
		installListenerOnTimeButtons(timeButtons, timeTextViews);
		enableTimeButtons(timeButtons, false);
		selectCheckBoxes(checkboxes, false);

		final Button okButton = (Button) findViewById(R.id.schedule_button_ok);
		okButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public final void onClick(final View v)
			{
				final Intent intent = new Intent(ScheduleActivity.this, SpotSummary.class);
				intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, spotInfo);

				startActivityForResult(intent, Main.CONFIGURATION_REQUEST_CODE);
			}
		});
	}

	private void installListenerOnCheckBoxes(final List<Integer> _checkBoxes, final List<Integer> _timeButtons)
	{
		final int size = _checkBoxes.size();
		for (int i = 0; i < size; i++)
		{
			final CheckBox box = (CheckBox) findViewById(_checkBoxes.get(i));
			final int index = i;
			box.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public final void onClick(final View _v)
				{
					final CheckBox clickedBox = (CheckBox) _v;
					final Button button = (Button) findViewById(_timeButtons.get(index));
					button.setEnabled(clickedBox.isChecked());
				}
			});
		}
	}

	private void installListenerOnTimeButtons(final List<Integer> _list, final List<Integer> _timeTextViews)
	{
		final int size = _list.size();
		for (int i = 0; i < size; i++)
		{
			final int index = i;
			final Button view = (Button) findViewById(_list.get(i));
			view.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public final void onClick(final View _v)
				{
					final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener()
					{
						public final void onTimeSet(final TimePicker _view, final int _hourOfDay, final int _minute)
						{
							final TextView timeView = (TextView) findViewById(_timeTextViews.get(index));
							final StringBuilder builder = new StringBuilder();
							builder.append(_hourOfDay < 10 ? "0" + _hourOfDay : _hourOfDay);
							builder.append(":");
							builder.append(_minute < 10 ? "0" + _minute : _minute);
							timeView.setText(builder);
						}
					};

					final TimePickerDialog dialog = new TimePickerDialog(ScheduleActivity.this, listener, 12, 0, false);
					dialog.show();
				}
			});
		}
	}

	private void enableTimeButtons(final List<Integer> _list, final boolean _enabled)
	{
		final int size = _list.size();
		for (int i = 0; i < size; i++)

		{
			final Button view = (Button) findViewById(_list.get(i));
			view.setEnabled(_enabled);
		}
	}

	private void selectCheckBoxes(final List<Integer> _list, final boolean _selected)
	{
		final int size = _list.size();
		for (int i = 0; i < size; i++)
		{
			final CheckBox view = (CheckBox) findViewById(_list.get(i));
			view.setSelected(_selected);
		}
	}

}