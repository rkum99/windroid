package de.macsystems.windroid.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.forecast.ForecastDetail.Builder;
import de.macsystems.windroid.identifyable.AirPressure;
import de.macsystems.windroid.identifyable.Cavok;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.Precipitation;
import de.macsystems.windroid.identifyable.Temperature;
import de.macsystems.windroid.identifyable.WaveHeight;
import de.macsystems.windroid.identifyable.WavePeriod;
import de.macsystems.windroid.identifyable.WindDirection;
import de.macsystems.windroid.identifyable.WindSpeed;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public final class ForecastParser
{

	private final static String LOG_TAG = ForecastParser.class.getSimpleName();

	private static final String WIND_DIRECTION = "wind_direction";
	private static final String CLOUDS = "clouds";
	private static final String WEATHER = "weather";
	private static final String UNIT = "unit";
	private static final String VALUE = "value";

	private ForecastParser()
	{
	}

	private static void parseAirPresure(JSONObject airPresureMap, Builder builder) throws JSONException
	{
		final float value = getFloat(airPresureMap, VALUE);
		final String unit = airPresureMap.getString(UNIT);

		final AirPressure airPressure = AirPressure.create(value, unit);
		builder.setAirPressure(airPressure);
	}

	private static void parseWaveHeight(final JSONObject wavePeriodMap, final Builder builder) throws JSONException
	{
		final float value = getFloat(wavePeriodMap, VALUE);
		final String unit = wavePeriodMap.getString(UNIT);

		final WaveHeight waveHeiht = WaveHeight.create(unit, value);
		builder.setWaveHeight(waveHeiht);
	}

	private static void parseClouds(final JSONObject forecastDetailMap, final Builder builder) throws JSONException
	{
		final String directionString = forecastDetailMap.getString(CLOUDS);
		final int index = IdentityUtil.indexOf(directionString, Cavok.values());
		final Cavok cavok = Cavok.values()[index];
		builder.setClouds(cavok);
	}

	private static void parseWeather(final JSONObject forecastDetailMap, final Builder builder) throws JSONException
	{
		final String weatherString = forecastDetailMap.getString(WEATHER);
		// final int index = IdentityUtil.indexOf(directionString,
		// WindDirection.values());
		// final WindDirection direction = WindDirection.values()[index];
		// builder.setWinddirection(direction);
	}

	private static void parseWindSpeedMap(final JSONObject windSpeedMap, final Builder builder) throws JSONException
	{
		Log.d(LOG_TAG, "windSpeedMap :" + windSpeedMap.toString());
		final float value = getFloat(windSpeedMap, VALUE);
		final String unit = windSpeedMap.getString(UNIT);

		final WindSpeed windSpeed = WindSpeed.create(value, unit);
		builder.setWindSpeed(windSpeed);
	}

	private static void parseWindGustsMap(final JSONObject windGustsMap, final Builder builder) throws JSONException
	{
		Log.d(LOG_TAG, "windGustsMap :" + windGustsMap.toString());
		final float value = getFloat(windGustsMap, VALUE);
		final String unit = windGustsMap.getString(UNIT);

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

	private static void parseWaterTemperatureMap(final JSONObject waterTemperatureMap, final Builder builder)
			throws JSONException
	{
		Log.d(LOG_TAG, "waterTemperatureMap :" + waterTemperatureMap.toString());
		final float value = getFloat(waterTemperatureMap, VALUE);
		final String unit = waterTemperatureMap.getString(UNIT);

		final Temperature waterTemp = Temperature.create(value, unit);
		builder.setWaterTemperature(waterTemp);
	}

	private static void parseWindDirection(final JSONObject jSONObject, final Builder builder) throws JSONException
	{
		final String directionString = jSONObject.getString(WIND_DIRECTION);
		final int index = IdentityUtil.indexOf(directionString, WindDirection.values());
		final WindDirection direction = WindDirection.values()[index];
		builder.setWinddirection(direction);

	}

	private static void parseWavePeriod(final JSONObject wavePeriodMap, final Builder builder) throws JSONException
	{
		Log.d(LOG_TAG, "wavePeriodMap :" + wavePeriodMap.toString());

		final float value = (float) getFloat(wavePeriodMap, VALUE);
		final String unit = wavePeriodMap.getString(UNIT);

		final WavePeriod wavePeriod = WavePeriod.create(unit, value);
		builder.setWavePeriod(wavePeriod);
	}

	private static void parsePrecipitationMap(final JSONObject precipitationMap, final Builder builder)
			throws JSONException
	{
		Log.d(LOG_TAG, "precipitationMap :" + precipitationMap.toString());
		final float value = (float) getFloat(precipitationMap, VALUE);
		final String unit = precipitationMap.getString(UNIT);

		final Precipitation precipitation = Precipitation.create(value, unit);
		builder.setPrecipitation(precipitation);
	}

	/**
	 * "timestamp":"20090722_1800"
	 * 
	 * @param _timestamp
	 * @return
	 */
	private static Date parseTimezone(final String _timestamp) throws ParseException
	{
		final String str = _timestamp.replace("_", "");
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
		return formatter.parse(str);
	}

	/**
	 * "date": "20090722"
	 * 
	 * @param _date
	 * @return
	 */
	private static Date parseForecastDate(final String _date) throws ParseException
	{
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");
		return formatter.parse(_date);
	}

	/**
	 * "time": "020000"
	 * 
	 * 
	 * 
	 * @param _time
	 * @return time as hours
	 */
	private static int parseTime(final String _time)
	{
		return Integer.valueOf(_time.substring(2));
	}

	/**
	 * 
	 * @param _forecast
	 * @return
	 * @throws JSONException
	 */
	public static Forecast parse(final StringBuilder _forecast) throws JSONException
	{
		final Forecast forecast = new Forecast("dummy", 2, new Date());
		final JSONTokener tokener = new JSONTokener(_forecast.toString());
		final JSONObject jsonRoot = new JSONObject(tokener);
		Log.d(LOG_TAG, "json timestamp: " + jsonRoot.getString("timestamp"));

		final JSONArray stations = jsonRoot.getJSONArray("stations");
		final JSONObject forecasts = stations.getJSONObject(0);
		final JSONArray forecastArray = forecasts.getJSONArray("forecasts");

		for (int i = 0; i < forecastArray.length(); i++)
		{
			final Builder builder = new ForecastDetail.Builder("dummy id");

			final JSONObject forecastDetailMap = forecastArray.getJSONObject(i);
			final JSONObject precipitationMap = forecastDetailMap.getJSONObject("precipitation");
			parsePrecipitationMap(precipitationMap, builder);
			final JSONObject wavePeriodMap = forecastDetailMap.getJSONObject("wave_period");
			parseWavePeriod(wavePeriodMap, builder);
			final JSONObject waveHeightMap = forecastDetailMap.getJSONObject("wave_height");
			parseWaveHeight(waveHeightMap, builder);
			parseWindDirection(forecastDetailMap, builder);
			final JSONObject waterTemperatureMap = forecastDetailMap.getJSONObject("water_temperature");
			parseWaterTemperatureMap(waterTemperatureMap, builder);
			final JSONObject windGustsMap = forecastDetailMap.getJSONObject("wind_gusts");
			parseWindGustsMap(windGustsMap, builder);
			final JSONObject windSpeedMap = forecastDetailMap.getJSONObject("wind_speed");
			parseWindSpeedMap(windSpeedMap, builder);
			parseWeather(forecastDetailMap, builder);
			parseClouds(forecastDetailMap, builder);
			final JSONObject airePressureMap = forecastDetailMap.getJSONObject("air_pressure");
			parseAirPresure(airePressureMap, builder);

			forecast.add(builder.build());
		}
		return forecast;
	}

}
