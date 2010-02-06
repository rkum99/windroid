package de.macsystems.windroid;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import de.macsystems.windroid.identifyable.Repeat;
import de.macsystems.windroid.identifyable.Schedule;

/**
 * Activity which allows user to configure update schedule
 * 
 * @author mac
 * @version $Id$
 */
public class ScheduleActivity extends ChainSubActivity
{

	private static final String LOG_TAG = ScheduleActivity.class.getSimpleName();

	private final Map<Integer, Integer> checkboxesMap = new HashMap<Integer, Integer>();
	private final Map<Integer, Integer> timeButtonsMap = new HashMap<Integer, Integer>();
	private final Map<Integer, Integer> timeTextViewsMap = new HashMap<Integer, Integer>();

	private final Map<Integer, Long> dayToDaytimeMap = new HashMap<Integer, Long>();

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

		checkboxesMap.put(Calendar.MONDAY, R.id.schedule_checkbox_weekday_monday);
		checkboxesMap.put(Calendar.TUESDAY, R.id.schedule_checkbox_weekday_tuesday);
		checkboxesMap.put(Calendar.WEDNESDAY, R.id.schedule_checkbox_weekday_wednesday);
		checkboxesMap.put(Calendar.THURSDAY, R.id.schedule_checkbox_weekday_thursday);
		checkboxesMap.put(Calendar.FRIDAY, R.id.schedule_checkbox_weekday_friday);
		checkboxesMap.put(Calendar.SATURDAY, R.id.schedule_checkbox_weekday_saturday);
		checkboxesMap.put(Calendar.SUNDAY, R.id.schedule_checkbox_weekday_sunday);
		//
		timeButtonsMap.put(Calendar.MONDAY, R.id.schedule_button_weekday_monday);
		timeButtonsMap.put(Calendar.TUESDAY, R.id.schedule_button_weekday_tuesday);
		timeButtonsMap.put(Calendar.WEDNESDAY, R.id.schedule_button_weekday_wednesday);
		timeButtonsMap.put(Calendar.THURSDAY, R.id.schedule_button_weekday_thursday);
		timeButtonsMap.put(Calendar.FRIDAY, R.id.schedule_button_weekday_friday);
		timeButtonsMap.put(Calendar.SATURDAY, R.id.schedule_button_weekday_saturday);
		timeButtonsMap.put(Calendar.SUNDAY, R.id.schedule_button_weekday_sunday);
		//
		timeTextViewsMap.put(Calendar.MONDAY, R.id.schedule_label_weekday_monday);
		timeTextViewsMap.put(Calendar.TUESDAY, R.id.schedule_label_weekday_tuesday);
		timeTextViewsMap.put(Calendar.WEDNESDAY, R.id.schedule_label_weekday_wednesday);
		timeTextViewsMap.put(Calendar.THURSDAY, R.id.schedule_label_weekday_thursday);
		timeTextViewsMap.put(Calendar.FRIDAY, R.id.schedule_label_weekday_friday);
		timeTextViewsMap.put(Calendar.SATURDAY, R.id.schedule_label_weekday_saturday);
		timeTextViewsMap.put(Calendar.SUNDAY, R.id.schedule_label_weekday_sunday);
		//
		dayToDaytimeMap.put(Calendar.MONDAY, -1L);
		dayToDaytimeMap.put(Calendar.TUESDAY, -1L);
		dayToDaytimeMap.put(Calendar.WEDNESDAY, -1L);
		dayToDaytimeMap.put(Calendar.THURSDAY, -1L);
		dayToDaytimeMap.put(Calendar.FRIDAY, -1L);
		dayToDaytimeMap.put(Calendar.SATURDAY, -1L);
		dayToDaytimeMap.put(Calendar.SUNDAY, -1L);

		installListenerOnCheckBoxes(checkboxesMap, timeButtonsMap);
		installListenerOnTimeButtons(timeButtonsMap, timeTextViewsMap);
		enableTimeButtons(timeButtonsMap, false);
		selectCheckBoxes(checkboxesMap, false);

		final Button okButton = (Button) findViewById(R.id.schedule_button_ok);
		okButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public final void onClick(final View v)
			{
				final Intent intent = new Intent(ScheduleActivity.this, SpotSummary.class);
				intent.putExtra(IntentConstants.SPOT_TO_CONFIGURE, spotInfo);

				final Iterator<Integer> iter = checkboxesMap.keySet().iterator();
				while (iter.hasNext())
				{
					final int resID = checkboxesMap.get(iter.next());
					final CheckBox box = (CheckBox) findViewById(resID);
					final boolean checked = box.isChecked();
					final Schedule schedule = spotInfo.getSchedule();
					schedule.addRepeat(resID, new Repeat(resID, 12L * 60L * 60L * 1000L, checked));
				}

				startActivityForResult(intent, Main.CONFIGURATION_REQUEST_CODE);
			}
		});
	}

	private void installListenerOnCheckBoxes(final Map<Integer, Integer> _checkBoxes,
			final Map<Integer, Integer> _timeButtons)
	{

		final Iterator<Integer> iter = _checkBoxes.keySet().iterator();
		Log.d(LOG_TAG, _checkBoxes.toString());
		while (iter.hasNext())
		{
			final int resID = iter.next();
			Log.d(LOG_TAG, "index " + resID);
			final CheckBox box = (CheckBox) findViewById(_checkBoxes.get(resID));
			box.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public final void onClick(final View _v)
				{
					final CheckBox clickedBox = (CheckBox) _v;
					final Button button = (Button) findViewById(_timeButtons.get(resID));
					button.setEnabled(clickedBox.isChecked());
				}
			});
		}
	}

	private void installListenerOnTimeButtons(final Map<Integer, Integer> _list,
			final Map<Integer, Integer> _timeTextViews)
	{
		final Iterator<Integer> iter = _list.keySet().iterator();
		while (iter.hasNext())
		{
			final int day = iter.next();
			final int resID = _list.get(day);
			final Button view = (Button) findViewById(resID);
			view.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public final void onClick(final View _v)
				{
					final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener()
					{
						public final void onTimeSet(final TimePicker _view, final int _hourOfDay, final int _minute)
						{

							final long dayTime = calcDayTime(_hourOfDay, _minute);
							dayToDaytimeMap.put(day, dayTime);
							Log.d(LOG_TAG, "Day :"+day+" daytime :" + dayTime);

							final TextView timeView = (TextView) findViewById(_timeTextViews.get(day));
							final StringBuilder builder = new StringBuilder();
							// builder.append("Day ").append(day).append(" ");
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

	private void enableTimeButtons(final Map<Integer, Integer> _list, final boolean _enabled)
	{
		final Iterator<Integer> iter = _list.keySet().iterator();
		while (iter.hasNext())
		{
			final int resID = _list.get(iter.next());
			final Button view = (Button) findViewById(resID);
			view.setEnabled(_enabled);
		}
	}

	private void selectCheckBoxes(final Map<Integer, Integer> _list, final boolean _selected)
	{
		final Iterator<Integer> iter = _list.keySet().iterator();
		while (iter.hasNext())
		{
			final int resID = _list.get(iter.next());
			final CheckBox view = (CheckBox) findViewById(resID);
			view.setSelected(_selected);
		}
	}

	/**
	 * @param _hourOfDay
	 * @param _minute
	 * @return
	 */
	private static long calcDayTime(final int _hourOfDay, final int _minute)
	{
		long dayTime = (_hourOfDay * 60L * 60L * 1000L) + (_minute * 60 * 1000L);
		return dayTime;
	}

}