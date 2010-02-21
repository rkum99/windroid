package de.macsystems.windroid.db;

import de.macsystems.windroid.forecast.Forecast;

/**
 * DAO Interface for Forecast
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public interface IForecast extends IDAO
{
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
