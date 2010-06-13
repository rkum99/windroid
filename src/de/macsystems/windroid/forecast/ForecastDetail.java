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

import de.macsystems.windroid.identifyable.CardinalDirection;
import de.macsystems.windroid.identifyable.Cavok;
import de.macsystems.windroid.identifyable.IConvertable;
import de.macsystems.windroid.identifyable.IMeasureValue;
import de.macsystems.windroid.identifyable.TempConverter;
import de.macsystems.windroid.identifyable.WindSpeedConverter;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 *          { "date": "20090722", "time": "140000",
 *          "air_temperature":{"unit":"celsius" ,"value":"20.60"},
 *          "water_temperature":{"unit":"celsius", "value":null}
 *          ,"wind_direction":"SW", "wind_speed":{"unit":"kts","value" :"14"},
 *          "wind_gusts":{"unit":"kts", "value": null},
 *          "weather":null," clouds":"BKN","precipitation": { "unit":
 *          "mm","value": 0.05 }, "wave_height": {
 *          "unit":"m","value":1.6},"wave_direction":"SW","wave_period": {
 *          "unit":"s","value":5},"air_pressure": {
 *          "unit":"hpa","value":1006.20}}
 * 
 */
public final class ForecastDetail
{

	private final long date;

	private final long time;

	private final IConvertable airTemperature;

	private final IConvertable waterTemperature;

	private final CardinalDirection winddirection;

	private final IConvertable windSpeed;

	private final IConvertable windGusts;

	private final Cavok clouds;

	private final IMeasureValue precipitation;

	private final IMeasureValue waveHeight;

	private final IMeasureValue wavePeriod;

	private final CardinalDirection waveDirection;

	private final IMeasureValue airPressure;

	/**
	 * 
	 * @param _builder
	 * @throws NullPointerException
	 *             if any parameter is <code>null</code>.
	 */
	private ForecastDetail(final Builder _builder) throws NullPointerException
	{
		this.date = _builder.date;
		this.time = _builder.time;
		this.airTemperature = new TempConverter(_builder.airTemperature);
		this.waterTemperature = new TempConverter(_builder.waterTemperature);
		this.winddirection = _builder.winddirection;
		this.windSpeed = new WindSpeedConverter(_builder.windSpeed);
		this.windGusts = new WindSpeedConverter(_builder.windGusts);
		this.clouds = _builder.clouds;
		this.precipitation = _builder.precipitation;
		this.waveHeight = _builder.waveHeight;
		this.wavePeriod = _builder.wavePeriod;
		this.waveDirection = _builder.waveDirection;
		this.airPressure = _builder.airPressure;

		nullCheck(date, "date");
		nullCheck(time, "time");
		nullCheck(airTemperature, "airTemperature");
		nullCheck(waterTemperature, "waterTemperature");
		nullCheck(winddirection, "winddirection");
		nullCheck(windSpeed, "windSpeed");
		nullCheck(windGusts, "windGusts");
		nullCheck(clouds, "clouds");
		nullCheck(precipitation, "precipitation");
		nullCheck(waveHeight, "waveheight");
		nullCheck(wavePeriod, "waveperiod");
		nullCheck(waveDirection, "wavedirection");
		nullCheck(airPressure, "airpressure");
	}

	/**
	 * @return the air pressure
	 */
	public IMeasureValue getAirPressure()
	{
		return airPressure;
	}

	/**
	 * Date of this forecast as milliseconds since 1970
	 * 
	 * @return the date
	 * @see #getTime()
	 */
	public long getDate()
	{
		return date;
	}

	/**
	 * Timestamp of this forecast
	 * 
	 * @return the time
	 * @see #getDate()
	 */
	public long getTime()
	{
		return time;
	}

	/**
	 * @return the airTemperature
	 */
	public IMeasureValue getAirTemperature()
	{
		return airTemperature;
	}

	/**
	 * @return the waterTemperature
	 */
	public IMeasureValue getWaterTemperature()
	{
		return waterTemperature;
	}

	/**
	 * @return the winddirection
	 */
	public CardinalDirection getWinddirection()
	{
		return winddirection;
	}

	/**
	 * @return the windSpeed
	 */
	public IMeasureValue getWindSpeed()
	{
		return windSpeed;
	}

	/**
	 * @return the windGusts
	 */
	public IMeasureValue getWindGusts()
	{
		return windGusts;
	}

	/**
	 * @return the clouds
	 */
	public Cavok getClouds()
	{
		return clouds;
	}

	/**
	 * @return the precipitation
	 */
	public IMeasureValue getPrecipitation()
	{
		return precipitation;
	}

	/**
	 * @return the waveHeight
	 */
	public IMeasureValue getWaveHeight()
	{
		return waveHeight;
	}

	/**
	 * @return the wavePeriod
	 */
	public IMeasureValue getWavePeriod()
	{
		return wavePeriod;
	}

	/**
	 * @return the waveDirection
	 */
	public CardinalDirection getWaveDirection()
	{
		return waveDirection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ForecastDetail [airPressure=" + airPressure + ", airTemperature=" + airTemperature + ", clouds="
				+ clouds + ", date=" + date + ", precipitation=" + precipitation + ", time=" + time
				+ ", waterTemperature=" + waterTemperature + ", waveDirection=" + waveDirection + ", waveHeight="
				+ waveHeight + ", wavePeriod=" + wavePeriod + ", windGusts=" + windGusts + ", windSpeed=" + windSpeed
				+ ", winddirection=" + winddirection + "]";
	}

	public static class Builder
	{

		private long date;

		private long time;

		private IMeasureValue airTemperature;

		private IMeasureValue waterTemperature;

		private CardinalDirection winddirection;

		private IMeasureValue windSpeed;

		private IMeasureValue windGusts;

		private Cavok clouds;

		private IMeasureValue precipitation;

		private IMeasureValue waveHeight;

		private IMeasureValue wavePeriod;

		private CardinalDirection waveDirection;

		private IMeasureValue airPressure;

		public Builder()
		{
		}

		/**
		 * @param _date
		 *            the date to set
		 */
		public Builder setDate(final long _date)
		{
			this.date = _date;
			return this;
		}

		/**
		 * @param time
		 *            the time to set
		 */
		public Builder setTime(final long time)
		{
			this.time = time;
			return this;
		}

		/**
		 * @param _airTemperature
		 *            the airTemperature to set
		 */
		public Builder setAirTemperature(final IMeasureValue _airTemperature)
		{
			this.airTemperature = _airTemperature;
			return this;
		}

		/**
		 * @param _waterTemperature
		 *            the waterTemperature to set
		 */
		public Builder setWaterTemperature(final IMeasureValue _waterTemperature)
		{
			this.waterTemperature = _waterTemperature;
			return this;
		}

		/**
		 * @param _winddirection
		 *            the winddirection to set
		 */
		public Builder setWinddirection(final CardinalDirection _winddirection)
		{
			this.winddirection = _winddirection;
			return this;
		}

		/**
		 * @param _windSpeed
		 *            the windSpeed to set
		 */
		public Builder setWindSpeed(final IMeasureValue _windSpeed)
		{
			this.windSpeed = _windSpeed;
			return this;
		}

		/**
		 * @param _windGusts
		 *            the windGusts to set
		 */
		public Builder setWindGusts(final IMeasureValue _windGusts)
		{

			this.windGusts = _windGusts;
			return this;
		}

		/**
		 * @param _clouds
		 *            the clouds to set
		 */
		public Builder setClouds(final Cavok _clouds)
		{
			this.clouds = _clouds;
			return this;
		}

		/**
		 * @param _precipitation
		 *            the precipitation to set
		 */
		public Builder setPrecipitation(final IMeasureValue _precipitation)
		{
			this.precipitation = _precipitation;
			return this;
		}

		/**
		 * @param _waveHeight
		 *            the waveHeight to set
		 */
		public Builder setWaveHeight(final IMeasureValue _waveHeight)
		{
			this.waveHeight = _waveHeight;
			return this;
		}

		/**
		 * @param _wavePeriod
		 *            the wavePeriod to set
		 */
		public Builder setWavePeriod(final IMeasureValue _wavePeriod)
		{
			this.wavePeriod = _wavePeriod;
			return this;
		}

		/**
		 * @param _waveDirection
		 *            the waveDirection to set
		 */
		public Builder setWaveDirection(final CardinalDirection _waveDirection)
		{
			this.waveDirection = _waveDirection;
			return this;
		}

		/**
		 * @param _airPressure
		 *            the airPressure to set
		 */
		public Builder setAirPressure(final IMeasureValue _airPressure)
		{
			this.airPressure = _airPressure;
			return this;
		}

		/**
		 * 
		 * @return
		 * @throws NullPointerException
		 */
		public ForecastDetail build() throws NullPointerException
		{
			return new ForecastDetail(this);
		}

	}

	/**
	 * 
	 * @param _object
	 * @param _name
	 * @throws NullPointerException
	 */
	public final static void nullCheck(final Object _object, final String _name) throws NullPointerException
	{
		if (null == _object)
		{
			throw new NullPointerException(_name);
		}
	}

}
