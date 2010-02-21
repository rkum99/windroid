package de.macsystems.windroid.db.sqlite;

import android.database.sqlite.SQLiteDatabase;
import de.macsystems.windroid.db.IForecast;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.progress.IProgress;

/**
 * 
 * DAO for 'Forecast' relation
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class ForecastImpl extends BaseImpl implements IForecast
{

	private final static String FORECAST = "forecast";

	/**
	 * @param _database
	 */
	public ForecastImpl(final Database _database, final IProgress _progress)
	{
		super(_database, FORECAST, _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IForecast#getForecast(int)
	 */
	@Override
	public Forecast getForecast(final int _forecastID)
	{
		final SQLiteDatabase db = getReadableDatabase();
		try
		{
			return null;
		}
		finally
		{
			IOUtils.close(db);
		}
	}

	@Override
	public void setForecast(final Forecast forecast)
	{
		final SQLiteDatabase db = getWritableDatabase();
		try
		{

		}
		finally
		{
			IOUtils.close(db);
		}

	}

}
