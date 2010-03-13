package de.macsystems.windroid.forecast;

import java.util.Date;

import de.macsystems.windroid.identifyable.Cavok;
import de.macsystems.windroid.identifyable.MeasureValue;
import de.macsystems.windroid.identifyable.Precipitation;
import de.macsystems.windroid.identifyable.Pressure;
import de.macsystems.windroid.identifyable.Temperature;
import de.macsystems.windroid.identifyable.WaveHeight;
import de.macsystems.windroid.identifyable.WavePeriod;
import de.macsystems.windroid.identifyable.WindDirection;
import de.macsystems.windroid.identifyable.WindSpeed;

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
public class ForecastDetail
{

	private final String spotName;

	private final String spotID;

	private final Date date;

	private final int time;

	private final Temperature airTemperature;

	private final Temperature waterTemperature;

	private final WindDirection winddirection;

	private final WindSpeed windSpeed;

	private final WindSpeed windGusts;

	private final Cavok clouds;

	private final Precipitation precipitation;

	private final WaveHeight waveHeight;

	private final WavePeriod wavePeriod;

	private final MeasureValue waveDirection;

	private final Pressure airPressure;

	/**
	 * 
	 * @param _builder
	 * @throws NullPointerException
	 *             if any parameter is <code>null</code>.
	 */
	private ForecastDetail(final Builder _builder) throws NullPointerException
	{
		this.spotName = _builder.spotName;
		this.spotID = _builder.spotID;
		this.date = _builder.date;
		this.time = _builder.time;
		this.airTemperature = _builder.airTemperature;
		this.waterTemperature = _builder.waterTemperature;
		this.winddirection = _builder.winddirection;
		this.windSpeed = _builder.windSpeed;
		this.windGusts = _builder.windGusts;
		this.clouds = _builder.clouds;
		this.precipitation = _builder.precipitation;
		this.waveHeight = _builder.waveHeight;
		this.wavePeriod = _builder.wavePeriod;
		this.waveDirection = _builder.waveDirection;
		this.airPressure = _builder.airPressure;

		nullCheck(spotName);
		nullCheck(spotID);
		nullCheck(date);
		nullCheck(time);
		nullCheck(airTemperature);
		nullCheck(waterTemperature);
		nullCheck(winddirection);
		nullCheck(windSpeed);
		nullCheck(windGusts);
		nullCheck(windGusts);
		nullCheck(clouds);
		nullCheck(precipitation);
		nullCheck(waveHeight);
		nullCheck(wavePeriod);
		nullCheck(waveDirection);
		nullCheck(airPressure);
	}

	/**
	 * @return the air pressure
	 */
	public Pressure getAirPressure()
	{
		return airPressure;
	}

	/**
	 * @return the spotName
	 */
	public String getSpotName()
	{
		return spotName;
	}

	/**
	 * @return the spotID
	 */
	public String getSpotID()
	{
		return spotID;
	}

	/**
	 * @return the date
	 */
	public Date getDate()
	{
		return date;
	}

	/**
	 * @return the time
	 */
	public int getTime()
	{
		return time;
	}

	/**
	 * @return the airTemperature
	 */
	public Temperature getAirTemperature()
	{
		return airTemperature;
	}

	/**
	 * @return the waterTemperature
	 */
	public Temperature getWaterTemperature()
	{
		return waterTemperature;
	}

	/**
	 * @return the winddirection
	 */
	public WindDirection getWinddirection()
	{
		return winddirection;
	}

	/**
	 * @return the windSpeed
	 */
	public WindSpeed getWindSpeed()
	{
		return windSpeed;
	}

	/**
	 * @return the windGusts
	 */
	public WindSpeed getWindGusts()
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
	public Precipitation getPrecipitation()
	{
		return precipitation;
	}

	/**
	 * @return the waveHeight
	 */
	public WaveHeight getWaveHeight()
	{
		return waveHeight;
	}

	/**
	 * @return the wavePeriod
	 */
	public WavePeriod getWavePeriod()
	{
		return wavePeriod;
	}

	/**
	 * @return the waveDirection
	 */
	public MeasureValue getWaveDirection()
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
				+ clouds + ", date=" + date + ", precipitation=" + precipitation + ", spotID=" + spotID + ", spotName="
				+ spotName + ", time=" + time + ", waterTemperature=" + waterTemperature + ", waveDirection="
				+ waveDirection + ", waveHeight=" + waveHeight + ", wavePeriod=" + wavePeriod + ", windGusts="
				+ windGusts + ", windSpeed=" + windSpeed + ", winddirection=" + winddirection + "]";
	}

	public static class Builder
	{
		private String spotName;
		private final String spotID;

		private Date date;
		private int time;

		private Temperature airTemperature;

		private Temperature waterTemperature;

		private WindDirection winddirection;

		private WindSpeed windSpeed;

		private WindSpeed windGusts;

		private Cavok clouds;

		private Precipitation precipitation;

		private WaveHeight waveHeight;

		private WavePeriod wavePeriod;

		private MeasureValue waveDirection;

		private Pressure airPressure;

		public Builder(final String _id)
		{
			spotID = _id;
		}

		/**
		 * @param _date
		 *            the date to set
		 */
		public Builder setDate(final Date _date)
		{
			this.date = _date;
			return this;
		}

		/**
		 * @param time
		 *            the time to set
		 */
		public Builder setTime(final int time)
		{
			this.time = time;
			return this;
		}

		/**
		 * @param _airTemperature
		 *            the airTemperature to set
		 */
		public Builder setAirTemperature(final Temperature _airTemperature)
		{
			this.airTemperature = _airTemperature;
			return this;
		}

		/**
		 * @param _waterTemperature
		 *            the waterTemperature to set
		 */
		public Builder setWaterTemperature(final Temperature _waterTemperature)
		{
			this.waterTemperature = _waterTemperature;
			return this;
		}

		/**
		 * @param _winddirection
		 *            the winddirection to set
		 */
		public Builder setWinddirection(final WindDirection _winddirection)
		{
			this.winddirection = _winddirection;
			return this;
		}

		/**
		 * @param _windSpeed
		 *            the windSpeed to set
		 */
		public Builder setWindSpeed(final WindSpeed _windSpeed)
		{
			this.windSpeed = _windSpeed;
			return this;
		}

		/**
		 * @param _windGusts
		 *            the windGusts to set
		 */
		public Builder setWindGusts(final WindSpeed _windGusts)
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
		public Builder setPrecipitation(final Precipitation _precipitation)
		{
			nullCheck(_precipitation);
			this.precipitation = _precipitation;
			return this;
		}

		/**
		 * @param _waveHeight
		 *            the waveHeight to set
		 */
		public Builder setWaveHeight(final WaveHeight _waveHeight)
		{
			this.waveHeight = _waveHeight;
			return this;
		}

		/**
		 * @param _wavePeriod
		 *            the wavePeriod to set
		 */
		public Builder setWavePeriod(final WavePeriod _wavePeriod)
		{
			this.wavePeriod = _wavePeriod;
			return this;
		}

		/**
		 * @param _waveDirection
		 *            the waveDirection to set
		 */
		public Builder setWaveDirection(final MeasureValue _waveDirection)
		{
			this.waveDirection = _waveDirection;
			return this;
		}

		/**
		 * @param _airPressure
		 *            the airPressure to set
		 */
		public Builder setAirPressure(final Pressure _airPressure)
		{
			this.airPressure = _airPressure;
			return this;
		}

		/**
		 * 
		 * @return
		 */
		public ForecastDetail build()
		{
			return new ForecastDetail(this);
		}

	}

	/**
	 * 
	 * @param _object
	 */
	public final static void nullCheck(final Object _object)
	{
		if (null == _object)
		{
			throw new NullPointerException();
		}
	}

}
