package de.macsystems.windroid.db;

import de.macsystems.windroid.forecast.Forecast;

/**
 * DAO Interface for Forecast
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IForecastDAO extends IDAO
{

	public final static String COLUMN_DATE = "date";
	public final static String COLUMN_TIME = "time";
	public final static String COLUMN_WAVE_PERIOD = "wave_period";
	public final static String COLUMN_WAVE_PERIOD_UNIT = "wave_period_unit";
	public final static String COLUMN_WIND_DIRECTION = "wind_direction";
	public final static String COLUMN_WAVE_DIRECTION = "wave_direction";
	public final static String COLUMN_PRECIPITATION = "precipitation";
	public final static String COLUMN_AIR_PRESSURE = "air_pressure";
	public final static String COLUMN_AIR_PRESSURE_UNIT = "air_pressure_unit";
	public final static String COLUMN_WIND_GUSTS = "wind_gusts";
	public final static String COLUMN_WIND_GUST_UNIT = "wind_gusts_unit";
	public final static String COLUMN_WATER_TEMPERATURE = "water_temperature";
	public final static String COLUMN_WATER_TEMPERATURE_UNIT = "water_temperature_unit";
	public final static String COLUMN_AIR_TEMPERATURE = "air_temperature";
	public final static String COLUMN_AIR_TEMPERATURE_UNIT = "air_temperature_unit";
	public final static String COLUMN_WAVE_HEIGHT = "wave_height";
	public final static String COLUMN_WAVE_HEIGHT_UNIT = "wave_height_unit";
	public final static String COLUMN_CLOUDS = "clouds";
	public final static String COLUMN_WIND_SPEED = "wind_speed";
	public final static String COLUMN_WIND_SPEED_UNIT = "wind_speed_unit";

	/**
	 * retrieve latest <code>Forecast</code> for Spot with id from database.
	 * 
	 * @param _forecastID
	 * @return
	 */
	public Forecast getForecast(final int _forecastID);

	/**
	 * Inserts / Updates Forecast of a spot in database.
	 * 
	 * @param _forecast
	 */
	public void setForecast(final Forecast _forecast);
}
