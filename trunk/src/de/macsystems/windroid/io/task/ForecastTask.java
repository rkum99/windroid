package de.macsystems.windroid.io.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import android.content.Context;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.parser.ForecastParser;
import de.macsystems.windroid.progress.IProgress;

/**
 * Parses the JSON Forecast for a station
 * 
 * @author mac
 * @version $Id$
 */
public class ForecastTask extends IOTask<Forecast, InputStream>
{

	/**
	 * 
	 * @param _uri
	 *            the json url
	 * @param _progress
	 * @throws NullPointerException
	 */
	public ForecastTask(final URI _uri, final IProgress _progress) throws NullPointerException
	{
		super(_uri, _progress);
	}

	@Override
	public Forecast process(final Context _context, final InputStream _instream) throws IOException, Exception
	{
		final StringBuilder builder = IOUtils.asString(_instream);
		return ForecastParser.parse(builder);
	}

}
