package de.macsystems.windroid.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;
import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.WindUtils;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
import de.macsystems.windroid.forecast.ForecastDetail.Builder;
import de.macsystems.windroid.identifyable.AirPressure;
import de.macsystems.windroid.identifyable.Cavok;
import de.macsystems.windroid.identifyable.IdentityUtil;
import de.macsystems.windroid.identifyable.Precipitation;
import de.macsystems.windroid.identifyable.Station;
import de.macsystems.windroid.identifyable.Temperature;
import de.macsystems.windroid.identifyable.WaveHeight;
import de.macsystems.windroid.identifyable.WavePeriod;
import de.macsystems.windroid.identifyable.WindDirection;
import de.macsystems.windroid.identifyable.WindSpeed;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public class SpotUpdater
{

	private final static String LOG_TAG = SpotUpdater.class.getSimpleName();

	/**
	 * 
	 * @param _spotConfiguration
	 * @return
	 * @throws RetryLaterException
	 * @throws IOException
	 * @throws NullPointerException
	 */
	public static Forecast getUpdate(final SpotConfigurationVO _spotConfiguration) throws RetryLaterException,
			IOException, NullPointerException
	{

		if (_spotConfiguration == null)
		{
			throw new NullPointerException("SpotConfigurationVO is null.");
		}

		final Station station = _spotConfiguration.getStation();
		final URL url = WindUtils.getJSONForcastURL(station.getId());

		final Forecast forecast = new Forecast("dummy", 2, new Date());
		HttpURLConnection connection = null;

		try
		{

			connection = (HttpURLConnection) url.openConnection();
			final int responseCode = connection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK)
			{
				throw new RetryLaterException("Server response code was :" + responseCode);
			}
			connection.connect();
			//
			final StringBuilder stringBuilder = readResult(connection);
			try
			{
				final JSONTokener tokener = new JSONTokener(stringBuilder.toString());
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
			}
			catch (final JSONException e)
			{
				final IOException ioe = new IOException("Server Answer was: " + stringBuilder.toString());
				ioe.initCause(e);
				throw ioe;
			}
		}
		finally
		{
			IOUtils.close(connection);
		}
		return forecast;
	}

	private static void parseAirPresure(JSONObject airPresureMap, Builder builder) throws JSONException
	{
		final float value = getFloat(airPresureMap, "value");
		final String unit = airPresureMap.getString("unit");

		final AirPressure airPressure = AirPressure.create(value, unit);
		builder.setAirPressure(airPressure);
	}

	private static void parseWaveHeight(final JSONObject wavePeriodMap, final Builder builder) throws JSONException
	{
		final float value = getFloat(wavePeriodMap, "value");
		final String unit = wavePeriodMap.getString("unit");

		final WaveHeight waveHeiht = WaveHeight.create(unit, value);
		builder.setWaveHeight(waveHeiht);
	}

	private static void parseClouds(final JSONObject forecastDetailMap, final Builder builder) throws JSONException
	{
		final String directionString = forecastDetailMap.getString("clouds");
		final int index = IdentityUtil.indexOf(directionString, Cavok.values());
		final Cavok cavok = Cavok.values()[index];
		builder.setClouds(cavok);
	}

	private static void parseWeather(final JSONObject forecastDetailMap, final Builder builder) throws JSONException
	{
		final String directionString = forecastDetailMap.getString("weather");
		// final int index = IdentityUtil.indexOf(directionString,
		// WindDirection.values());
		// final WindDirection direction = WindDirection.values()[index];
		// builder.setWinddirection(direction);
	}

	private static void parseWindSpeedMap(final JSONObject windSpeedMap, final Builder builder) throws JSONException
	{
		Log.d(LOG_TAG, "windSpeedMap :" + windSpeedMap.toString());
		final float value = getFloat(windSpeedMap, "value");
		final String unit = windSpeedMap.getString("unit");

		final WindSpeed windSpeed = WindSpeed.create(value, unit);
		builder.setWindSpeed(windSpeed);
	}

	private static void parseWindGustsMap(final JSONObject windGustsMap, final Builder builder) throws JSONException
	{
		Log.d(LOG_TAG, "windGustsMap :" + windGustsMap.toString());
		final float value = getFloat(windGustsMap, "value");
		final String unit = windGustsMap.getString("unit");

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
		final float value = getFloat(waterTemperatureMap, "value");
		final String unit = waterTemperatureMap.getString("unit");

		final Temperature waterTemp = Temperature.create(value, unit);
		builder.setWaterTemperature(waterTemp);
	}

	private static void parseWindDirection(final JSONObject jSONObject, final Builder builder) throws JSONException
	{
		final String directionString = jSONObject.getString("wind_direction");
		final int index = IdentityUtil.indexOf(directionString, WindDirection.values());
		final WindDirection direction = WindDirection.values()[index];
		builder.setWinddirection(direction);

	}

	private static void parseWavePeriod(final JSONObject wavePeriodMap, final Builder builder) throws JSONException
	{
		Log.d(LOG_TAG, "wavePeriodMap :" + wavePeriodMap.toString());

		final float value = (float) getFloat(wavePeriodMap, "value");
		final String unit = wavePeriodMap.getString("unit");

		final WavePeriod wavePeriod = WavePeriod.create(unit, value);
		builder.setWavePeriod(wavePeriod);
	}

	private static void parsePrecipitationMap(final JSONObject precipitationMap, final Builder builder)
			throws JSONException
	{
		Log.d(LOG_TAG, "precipitationMap :" + precipitationMap.toString());
		final float value = (float) getFloat(precipitationMap, "value");
		final String unit = precipitationMap.getString("unit");

		final Precipitation precipitation = Precipitation.create(value, unit);
		builder.setPrecipitation(precipitation);
	}

	/**
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	static StringBuilder readResult(final HttpURLConnection connection) throws IOException
	{
		final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String line;
		final StringBuilder builder = new StringBuilder(1024);
		while ((line = reader.readLine()) != null)
		{
			builder.append(line);
		}
		return builder;
	}

	/**
	 * "timestamp":"20090722_1800"
	 * 
	 * @param _timestamp
	 * @return
	 */
	public static Date parseTimezone(final String _timestamp) throws ParseException
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
	public static Date parseForecastDate(final String _date) throws ParseException
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
	public static int parseTime(final String _time)
	{
		return Integer.valueOf(_time.substring(2));
	}

}
