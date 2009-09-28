package de.macsystems.windroid.forecast;

import java.util.Date;

import de.macsystems.windroid.identifyable.AirPressure;
import de.macsystems.windroid.identifyable.Cavok;
import de.macsystems.windroid.identifyable.MeasureValue;
import de.macsystems.windroid.identifyable.Precipitation;
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

	private final AirPressure airPressure;

	private ForecastDetail(final Builder _builder)
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ForecastDetail [spotName=+" + spotName + ", spotID=" + spotID + "]";
	}

	/**
	 * @return the airPressure
	 */
	public AirPressure getAirPressure()
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

		private AirPressure airPressure;

		public Builder(final String _id)
		{
			spotID = _id;
		}

		/**
		 * @param spotName
		 *            the spotName to set
		 */
		public Builder setSpotName(final String spotName)
		{
			this.spotName = spotName;
			return this;
		}

		/**
		 * @param date
		 *            the date to set
		 */
		public Builder setDate(final Date date)
		{
			this.date = date;
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
		 * @param airTemperature
		 *            the airTemperature to set
		 */
		public Builder setAirTemperature(final Temperature airTemperature)
		{
			this.airTemperature = airTemperature;
			return this;
		}

		/**
		 * @param waterTemperature
		 *            the waterTemperature to set
		 */
		public Builder setWaterTemperature(final Temperature waterTemperature)
		{
			this.waterTemperature = waterTemperature;
			return this;
		}

		/**
		 * @param winddirection
		 *            the winddirection to set
		 */
		public Builder setWinddirection(final WindDirection winddirection)
		{
			this.winddirection = winddirection;
			return this;
		}

		/**
		 * @param windSpeed
		 *            the windSpeed to set
		 */
		public Builder setWindSpeed(final WindSpeed windSpeed)
		{
			this.windSpeed = windSpeed;
			return this;
		}

		/**
		 * @param windGusts
		 *            the windGusts to set
		 */
		public Builder setWindGusts(final WindSpeed windGusts)
		{
			this.windGusts = windGusts;
			return this;
		}

		/**
		 * @param clouds
		 *            the clouds to set
		 */
		public Builder setClouds(final Cavok clouds)
		{
			this.clouds = clouds;
			return this;
		}

		/**
		 * @param precipitation
		 *            the precipitation to set
		 */
		public Builder setPrecipitation(final Precipitation precipitation)
		{
			this.precipitation = precipitation;
			return this;
		}

		/**
		 * @param waveHeight
		 *            the waveHeight to set
		 */
		public Builder setWaveHeight(final WaveHeight waveHeight)
		{
			this.waveHeight = waveHeight;
			return this;
		}

		/**
		 * @param wavePeriod
		 *            the wavePeriod to set
		 */
		public Builder setWavePeriod(final WavePeriod wavePeriod)
		{
			this.wavePeriod = wavePeriod;
			return this;
		}

		/**
		 * @param waveDirection
		 *            the waveDirection to set
		 */
		public Builder setWaveDirection(final MeasureValue waveDirection)
		{
			this.waveDirection = waveDirection;
			return this;
		}

		/**
		 * @param airPressure
		 *            the airPressure to set
		 */
		public Builder setAirPressure(final AirPressure airPressure)
		{
			this.airPressure = airPressure;
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

}
