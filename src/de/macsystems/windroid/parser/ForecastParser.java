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
package de.macsystems.windroid.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.forecast.ForecastDetail.Builder;
import de.macsystems.windroid.identifyable.CardinalDirection;
import de.macsystems.windroid.identifyable.Cavok;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.Precipitation;
import de.macsystems.windroid.identifyable.Pressure;
import de.macsystems.windroid.identifyable.Temperature;
import de.macsystems.windroid.identifyable.WaveHeight;
import de.macsystems.windroid.identifyable.WavePeriod;
import de.macsystems.windroid.identifyable.WindSpeed;

/**
 * Parses the JSON forecast response and returns an {@link Forecast}
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public final class ForecastParser
{

	private final static String LOG_TAG = ForecastParser.class.getSimpleName();

	private final static DateFormat jsonDateFormat = new SimpleDateFormat("yyyyMMdd");

	// private final static SimpleDateFormat yyyyMMddHHFormat = new
	// SimpleDateFormat("yyyyMMddHH", Locale.getDefault());
	// private final static SimpleDateFormat yyyyMMddHHmmFormat = new
	// SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());

	private static final String WIND_DIRECTION = "wind_direction";
	private static final String WAVE_DIRECTION = "wave_direction";
	private static final String CLOUDS = "clouds";
	private static final String UNIT = "unit";
	private static final String VALUE = "value";
	private static final String TIME = "time";
	private static final String DATE = "date";

	private ForecastParser()
	{
	}

	/**
	 * 
	 * @param airPresureMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseAirPresure(final JSONObject airPresureMap, final Builder builder) throws JSONException
	{
		final float value = getFloat(airPresureMap, VALUE);
		final String unit = airPresureMap.getString(UNIT);

		final Pressure airPressure = Pressure.create(value, unit);
		builder.setAirPressure(airPressure);
	}

	/**
	 * 
	 * @param airTempMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseAirTemperature(final JSONObject airTempMap, final Builder builder) throws JSONException
	{
		final float value = getFloat(airTempMap, VALUE);
		final String unit = airTempMap.getString(UNIT);

		final Temperature temp = Temperature.create(value, unit);
		builder.setAirTemperature(temp);
	}

	/**
	 * 
	 * @param wavePeriodMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseWaveHeight(final JSONObject wavePeriodMap, final Builder builder) throws JSONException
	{
		final float value = getFloat(wavePeriodMap, VALUE);
		final String unit = wavePeriodMap.getString(UNIT);

		final WaveHeight waveHeiht = WaveHeight.create(value, unit);
		builder.setWaveHeight(waveHeiht);
	}

	/**
	 * 
	 * @param forecastDetailMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseClouds(final JSONObject forecastDetailMap, final Builder builder) throws JSONException
	{
		final String directionString = forecastDetailMap.getString(CLOUDS);
		final int index = IdentityUtil.indexOf(directionString, Cavok.values());
		final Cavok cavok = Cavok.values()[index];
		builder.setClouds(cavok);
	}

	/**
	 * 
	 * @param windSpeedMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseWindSpeedMap(final JSONObject windSpeedMap, final Builder builder) throws JSONException
	{
		final float value = getFloat(windSpeedMap, VALUE);
		final String unit = windSpeedMap.getString(UNIT);

		final WindSpeed windSpeed = WindSpeed.create(value, unit);
		builder.setWindSpeed(windSpeed);
	}

	/**
	 * 
	 * @param windGustsMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseWindGustsMap(final JSONObject windGustsMap, final Builder builder) throws JSONException
	{
		final float value = getFloat(windGustsMap, VALUE);
		final String unit = windGustsMap.getString(UNIT);
		//
		final WindSpeed windGusts = WindSpeed.create(value, unit);
		builder.setWindGusts(windGusts);
	}

	/**
	 * Fail safe returns an float. null is interpreted as '0f'
	 * 
	 * @param _jsonObject
	 * @param _key
	 * @return
	 * @throws JSONException
	 */
	private static float getFloat(final JSONObject _jsonObject, final String _key) throws JSONException
	{
		if (_jsonObject.isNull(_key))
		{
			return 0;
		}

		final float value = (float) _jsonObject.getDouble(_key);
		return value;
	}

	/**
	 * 
	 * @param waterTemperatureMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseWaterTemperatureMap(final JSONObject waterTemperatureMap, final Builder builder)
			throws JSONException
	{
		final float value = getFloat(waterTemperatureMap, VALUE);
		final String unit = waterTemperatureMap.getString(UNIT);

		final Temperature waterTemp = Temperature.create(value, unit);
		builder.setWaterTemperature(waterTemp);
	}

	/**
	 * 
	 * @param jSONObject
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseWindDirection(final JSONObject jSONObject, final Builder builder) throws JSONException
	{
		final String directionString = jSONObject.getString(WIND_DIRECTION);
		final int index = IdentityUtil.indexOf(directionString, CardinalDirection.values());
		final CardinalDirection direction = CardinalDirection.values()[index];
		builder.setWinddirection(direction);

	}

	/**
	 * 
	 * @param wavePeriodMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseWavePeriod(final JSONObject wavePeriodMap, final Builder builder) throws JSONException
	{
		final float value = getFloat(wavePeriodMap, VALUE);
		final String unit = wavePeriodMap.getString(UNIT);

		final WavePeriod wavePeriod = WavePeriod.create(value, unit);
		builder.setWavePeriod(wavePeriod);
	}

	/**
	 * 
	 * @param precipitationMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parsePrecipitationMap(final JSONObject precipitationMap, final Builder builder)
			throws JSONException
	{
		final float value = getFloat(precipitationMap, VALUE);
		final String unit = precipitationMap.getString(UNIT);

		final Precipitation precipitation = Precipitation.create(value, unit);
		builder.setPrecipitation(precipitation);
	}

	// /**
	// * "timestamp":"20090722_1800"
	// *
	// * @param _timestamp
	// * @return
	// */
	// private static Date parseTimezone(final String _timestamp) throws
	// ParseException
	// {
	// final String str = _timestamp.replace("_", "");
	// return yyyyMMddHHmmFormat.parse(str);
	// }

	// /**
	// * "date": "20090722"
	// *
	// * @param _date
	// * @return
	// */
	// private static Date parseForecastDate(final String _date) throws
	// ParseException
	// {
	// return yyyyMMddHHFormat.parse(_date);
	// }

	/**
	 * 
	 * @param _forecast
	 * @return
	 * @throws JSONException
	 */
	public static Forecast parse(final StringBuilder _forecast) throws JSONException
	{
		final Forecast forecast = new Forecast("dummy", 2, 10009988L);
		final JSONTokener tokener = new JSONTokener(_forecast.toString());
		final JSONObject jsonRoot = new JSONObject(tokener);

		final JSONArray stations = jsonRoot.getJSONArray("stations");
		final JSONObject forecasts = stations.getJSONObject(0);
		final JSONArray forecastArray = forecasts.getJSONArray("forecasts");

		for (int i = 0; i < forecastArray.length(); i++)
		{
			final Builder builder = new ForecastDetail.Builder("dummy id");

			final JSONObject forecastDetailMap = forecastArray.getJSONObject(i);
			//
			final JSONObject precipitationMap = forecastDetailMap.getJSONObject("precipitation");
			parsePrecipitationMap(precipitationMap, builder);
			final JSONObject wavePeriodMap = forecastDetailMap.getJSONObject("wave_period");
			parseWavePeriod(wavePeriodMap, builder);
			final JSONObject waveHeightMap = forecastDetailMap.getJSONObject("wave_height");
			parseWaveHeight(waveHeightMap, builder);

			// final JSONObject waveDirectionMap =
			// forecastDetailMap.getJSONObject("wave_direction");
			parseWaveDirection(forecastDetailMap, builder);
			parseWindDirection(forecastDetailMap, builder);
			parseTime(forecastDetailMap, builder);
			parseDate(forecastDetailMap, builder);
			final JSONObject waterTemperatureMap = forecastDetailMap.getJSONObject("water_temperature");
			parseWaterTemperatureMap(waterTemperatureMap, builder);
			final JSONObject windGustsMap = forecastDetailMap.getJSONObject("wind_gusts");
			parseWindGustsMap(windGustsMap, builder);
			final JSONObject windSpeedMap = forecastDetailMap.getJSONObject("wind_speed");
			parseWindSpeedMap(windSpeedMap, builder);
			// parseWeather(forecastDetailMap, builder);
			parseClouds(forecastDetailMap, builder);
			final JSONObject airePressureMap = forecastDetailMap.getJSONObject("air_pressure");
			parseAirPresure(airePressureMap, builder);
			final JSONObject airTempMap = forecastDetailMap.getJSONObject("air_temperature");
			parseAirTemperature(airTempMap, builder);
			//
			forecast.add(builder.build());
		}
		return forecast;
	}

	/**
	 * 
	 * @param waveDirectionMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseWaveDirection(final JSONObject waveDirectionMap, final Builder builder)
			throws JSONException
	{
		final String directionString = waveDirectionMap.getString(WAVE_DIRECTION);
		final int index = IdentityUtil.indexOf(directionString, CardinalDirection.values());
		final CardinalDirection direction = CardinalDirection.values()[index];
		builder.setWaveDirection(direction);

	}

	/**
	 * "date":"20100207"
	 * 
	 * "date":"2010 02 07"
	 * 
	 * @param forecastDetailMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseDate(final JSONObject forecastDetailMap, final Builder builder) throws JSONException
	{
		final String dateString = forecastDetailMap.getString(DATE);

		if (dateString.length() != 8)
		{
			throw new IllegalArgumentException("wrong format, expected length of 8.");
		}

		try
		{
			final long newTime = jsonDateFormat.parse(dateString).getTime();
			builder.setDate(newTime);
		}
		catch (final ParseException e)
		{
			Log.e(LOG_TAG, "Failed parsing json date", e);
		}
	}

	/**
	 * parses the time of a forecast "time":"02 00 00"
	 * 
	 * TODO: Keep care of Timezone (GMT) when calculating.
	 * 
	 * @param forecastDetailMap
	 * @param builder
	 * @throws JSONException
	 */
	private static void parseTime(final JSONObject forecastDetailMap, final Builder builder) throws JSONException
	{
		final String timeString = forecastDetailMap.getString(TIME);

		if (timeString.length() != 6)
		{
			throw new IllegalArgumentException("wrong format, expected length of 6.");
		}

		final long hrs = Long.parseLong(timeString.substring(0, 2));
		final long min = Long.parseLong(timeString.substring(2, 4));
		final long sec = Long.parseLong(timeString.substring(4, 6));
		final long time = (hrs * 60L * 60L * 1000L) + (min * 60L * 1000L) + (sec * 1000L);

		builder.setTime(time);

	}

}
