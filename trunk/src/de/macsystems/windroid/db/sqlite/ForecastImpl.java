package de.macsystems.windroid.db.sqlite;

import java.util.Iterator;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.db.IForecast;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.forecast.ForecastDetail;
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

	private final static String LOG_TAG = ForecastImpl.class.getSimpleName();

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
			final Iterator<ForecastDetail> iter = forecast.iterator();
			while (iter.hasNext())
			{
				final ForecastDetail detail = iter.next();
				if (Logging.isLoggingEnabled())
				{
					Log.d(LOG_TAG, "Insert a forecast into Database " + detail.toString());
				}

			}
		}
		finally
		{
			IOUtils.close(db);
		}

	}
}
