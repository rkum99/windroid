package de.macsystems.windroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Activity which shows spot configuration and allows user finally to select or
 * discard.
 * 
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class SpotSummary extends Activity
{

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
		setContentView(R.layout.spotsummary);
		stationInfo = (SpotConfigurationVO) getIntent().getExtras().get(IntentConstants.SPOT_TO_CONFIGURE);

		final CompassView imageView = (CompassView) findViewById(R.id.compass);
		imageView.setFromDirection(stationInfo.getFromDirection());
		imageView.setToDirection(stationInfo.getToDirection());

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
			public void onClick(final View v)
			{
				final Intent intent = WindUtils.createIntent(SpotSummary.this, Main.class, stationInfo);
				// startActivity(intent);
				SpotSummary.this.setResult(ActivityResult.SUCCESS, intent);
				SpotSummary.this.finish();
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
			public void onClick(final View v)
			{
				final DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener()
				{

					/*
					 * Back to main screen als we pass a null as
					 * SpotConfiguration needs discarded
					 */
					@Override
					public void onClick(final DialogInterface dialog, final int which)
					{
						final Intent intent = WindUtils.createIntent(SpotSummary.this, Main.class, null);
						startActivity(intent);
					}
				};

				new AlertDialog.Builder(SpotSummary.this).setTitle(R.string.spot_summary_delete_alert_title)
						.setMessage(R.string.spot_summary_delete_alert_text).setNegativeButton(android.R.string.no,
								null).setPositiveButton(android.R.string.yes, okListener).setIcon(R.drawable.icon)
						.show();

			}
		};
		return listener;
	}

}
