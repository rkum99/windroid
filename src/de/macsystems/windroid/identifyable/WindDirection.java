package de.macsystems.windroid.identifyable;

import de.macsystems.windroid.R;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public enum WindDirection implements IdentifyAble
{

	N(0, "N", "North", R.drawable.wd_n), //
	NNE(22.5f, "NNE", "North North East", R.drawable.wd_nne),
	NE(45f, "NE", "North East", R.drawable.wd_ne),
	ENE(67.5f, "ENE", "East North East", R.drawable.wd_ene),
	E(90f, "E", "East", R.drawable.wd_e),
	ESE(112.5f, "ESE", "East South East", R.drawable.wd_ese),
	SE(135f, "SE", "South East", R.drawable.wd_se),
	SSE(157.5f, "SSE", "South South East", R.drawable.wd_sse),
	S(180f, "S", "South", R.drawable.wd_s),
	SSW(202.5f, "SSW", "South South West", R.drawable.wd_ssw),
	SW(225f, "SW", "South West", R.drawable.wd_sw),
	WSW(247.5f, "WSW", "West South West", R.drawable.wd_wsw),
	W(270f, "W", "West", R.drawable.wd_w),
	WNW(292.5f, "WNW", "West North West", R.drawable.wd_wnw),
	NW(315f, "NW", "North  West", R.drawable.wd_nw),
	NNW(337.5f, "NNW", "North North West", R.drawable.wd_nnw),

	/**
	 * define no wind direction
	 */

	NO_DIRECTION(0, "n/a", "No Direction", -1);

	private final float degrees;
	private final String longName;
	private final String shortName;

	private final int imageID;

	/**
	 * 
	 * @param _degrees
	 * @param _longName
	 * @param _shortName
	 */
	private WindDirection(final float _degrees, final String _shortName, final String _longName, final int _imageID)
	{
		degrees = _degrees;
		shortName = _shortName;
		longName = _longName;
		imageID = _imageID;
	}

	public float getDegree()
	{
		return degrees;
	}

	public String getShortName()
	{
		return shortName;
	}

	/**
	 * Returns drawable image id.
	 * 
	 * @return
	 */
	public int getImage()
	{
		return imageID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return longName + "(" + degrees + ")";
	}

	@Override
	public String getId()
	{
		return shortName;
	}
}