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
package de.macsystems.windroid.forecast;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.macsystems.windroid.R;
import de.macsystems.windroid.common.SpotConfigurationVO;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class SpotOverviewAdapter extends BaseAdapter
{
	private final List<SpotConfigurationVO> spotList;
	private final int rowResID;
	private final LayoutInflater layoutInflater;

	/**
	 * 
	 * @param _context
	 * @param _rowResID
	 * @param _spotList
	 */
	public SpotOverviewAdapter(final Context _context, final int _rowResID, final List<SpotConfigurationVO> _spotList)
	{
		rowResID = _rowResID;
		spotList = _spotList;
		//
		layoutInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	public int getCount()
	{
		return spotList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	public Object getItem(final int position)
	{
		return spotList.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	public long getItemId(final int position)
	{
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	public View getView(final int position, final View convertView, final ViewGroup parent)
	{
		final SpotConfigurationVO weather = spotList.get(position);

		View row = convertView;
		final ViewHolder wrapper;
		if (row == null)
		{
			row = layoutInflater.inflate(rowResID, null);
			wrapper = new ViewHolder(row);
			row.setTag(wrapper);
		}
		else
		{
			wrapper = (ViewHolder) row.getTag();
		}

		wrapper.getSpotnameTextView().setText(weather.getStation().getName());
		wrapper.getWindFromImageView().setImageResource(weather.getFromDirection().getImage());
		wrapper.getWindToImageView().setImageResource(weather.getToDirection().getImage());

		return row;
	}

	/**
	 * 
	 * @author Jens Hohl
	 * @version $Id: SpotOverviewAdapter.java 15 2009-07-30 03:22:32Z jens.hohl
	 *          $
	 * 
	 */
	private class ViewHolder
	{
		private final View base;
		private TextView spotnameTextView;
		private ImageView windFromImageView;
		private ImageView windToImageView;

		/**
		 * 
		 * @param base
		 */
		ViewHolder(final View base)
		{
			if (base == null)
			{
				throw new NullPointerException("Base is null.");
			}
			this.base = base;
		}

		/**
		 * @return the spotnameTextView
		 */
		public TextView getSpotnameTextView()
		{
			if (spotnameTextView == null)
			{
				spotnameTextView = (TextView) base.findViewById(R.id.custom_spotoverview_name);
			}

			return spotnameTextView;
		}

		/**
		 * @return the windFromImageView
		 */
		public ImageView getWindFromImageView()
		{
			if (windFromImageView == null)
			{
				windFromImageView = (ImageView) base.findViewById(R.id.custom_spotoverview_wind_from);
			}
			return windFromImageView;

		}

		/**
		 * @return the windToImageView
		 */
		public ImageView getWindToImageView()
		{
			if (windToImageView == null)
			{
				windToImageView = (ImageView) base.findViewById(R.id.custom_spotoverview_wind_to);
			}
			return windToImageView;

		}

	}

}
