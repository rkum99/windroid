package de.macsystems.windroid.forecast;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import de.macsystems.windroid.R;
import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.identifyable.Station;
import de.macsystems.windroid.identifyable.WindDirection;
import de.macsystems.windroid.identifyable.WindUnit;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class SpotOverview extends ListActivity
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spotoverview);

		final List<SpotConfigurationVO> spotConfigurations = new ArrayList<SpotConfigurationVO>();

		for (int i = 0; i < 10; i++)
		{
			final SpotConfigurationVO vo = new SpotConfigurationVO();
			vo.setFromDirection(WindDirection.E);
			vo.setToDirection(WindDirection.W);
			vo.setStation(new Station("Test Station", "id", "keyword", true, true,true,true,true,true));
			vo.setPreferredWindUnit(WindUnit.KNOTS);
			spotConfigurations.add(vo);
		}

		final SpotOverviewAdapter adapter = new SpotOverviewAdapter(this, R.layout.custom_listview_spotoverview,
				spotConfigurations);

		setListAdapter(adapter);
	}
}