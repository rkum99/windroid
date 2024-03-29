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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import de.macsystems.windroid.common.IntentConstants;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.custom.view.CompassView;

/**
 * Activity which shows spot configuration and allows user finally to select or
 * discard.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public final class SpotSummaryActivity extends Activity
{

	private SpotConfigurationVO spotConfiguration = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spot_summary_new);
		Background.apply(this);
		spotConfiguration = (SpotConfigurationVO) getIntent().getExtras().get(IntentConstants.SPOT_TO_CONFIGURE);

		final CompassView compassView = (CompassView) findViewById(R.id.compass);
		compassView.setFromDirection(spotConfiguration.getFromDirection());
		compassView.setToDirection(spotConfiguration.getToDirection());

		final Button acceptButton = (Button) findViewById(R.id.spotsummary_accept);
		acceptButton.setOnClickListener(getAcceptClickListener());

		final Button cancelButton = (Button) findViewById(R.id.spotsummary_cancel);
		cancelButton.setOnClickListener(getCancelClickListener());
	}

	/**
	 * Creates an OnClickListener which invokes next Activity
	 * 
	 * @return
	 */
	private View.OnClickListener getAcceptClickListener()
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
				returnToStartActivity(RESULT_OK);
			}
		};
		return listener;
	}

	/**
	 * Creates an OnClickListener which invokes next Activity
	 * 
	 * @return
	 */
	private View.OnClickListener getCancelClickListener()
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
				final DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener()
				{
					/*
					 * Back to main screen als we pass a null as
					 * SpotConfiguration needs discarded
					 */
					@Override
					public final void onClick(final DialogInterface dialog, final int which)
					{
						returnToStartActivity(RESULT_CANCELED);
					}
				};

				new AlertDialog.Builder(SpotSummaryActivity.this).setTitle(R.string.spot_summary_delete_alert_title)
						.setMessage(R.string.spot_summary_delete_alert_text).setNegativeButton(android.R.string.no,
								null).setPositiveButton(android.R.string.yes, okListener).setIcon(R.drawable.launcher)
						.show();

			}
		};
		return listener;
	}

	/**
	 * Returns to parent activity.
	 */
	private void returnToStartActivity(final int _resultCode)
	{
		Util.checkResultCode(_resultCode);
		// FIXME: We may come back from the "Edit Spot" instead of "Add Spot"
		final Intent intent = WindUtils.createIntent(SpotSummaryActivity.this, MainActivity.class, spotConfiguration);
		setResult(_resultCode, intent);
		finish();
	}
}