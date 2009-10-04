package de.macsystems.windroid.io.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.SpotConfigurationVO;
import de.macsystems.windroid.WindUtils;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.RetryLaterException;
import de.macsystems.windroid.parser.ForecastParser;

/**
 * Callable which will update the current forecast of a spot.
 * 
 * @author mac
 * @version $Id$
 * 
 */
public class UpdateSpotsTask implements Callable<Forecast>
{

	private final static String LOG_TAG = UpdateSpotsTask.class.getSimpleName();

	private static final String MOZILLA_5_0 = "Mozilla/5.0";

	final SpotConfigurationVO spot;

	final private Context context;

	/**
	 * 
	 * 
	 * @param _spot
	 * @param _context
	 */
	public UpdateSpotsTask(final SpotConfigurationVO _spot, final Context _context) throws NullPointerException
	{
		if (_spot == null)
		{
			throw new NullPointerException();
		}
		if (_context == null)
		{
			throw new NullPointerException();
		}
		spot = _spot;
		context = _context;

	}

	@Override
	public Forecast call() throws Exception
	{
		if (!IOUtils.isNetworkReachable(context))
		{
			Log.d(LOG_TAG, "Skipping update, Network not reachable.");
			throw new RetryLaterException("Skipping update, Network not reachable.");
		}

		final HttpClient httpclient = new DefaultHttpClient();

		try
		{
			final HttpGet httpGet = new HttpGet(WindUtils.getJSONForcastURL(spot.getStation().getId()).toExternalForm());
			httpGet.addHeader("User-Agent", MOZILLA_5_0);

			final HttpResponse response = httpclient.execute(httpGet);
			Log.d(LOG_TAG, "Server Response Code:" + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
			{
				throw new RetryLaterException("Server reponse was : " + response.getStatusLine().getStatusCode());
			}
			final StringBuilder builder = readResult(response.getEntity().getContent());
			return ForecastParser.parse(builder);
		}
		finally
		{
			httpclient.getConnectionManager().shutdown();
		}

	}

	/**
	 * @param _instream
	 * @return
	 * @throws IOException
	 */
	public static StringBuilder readResult(final InputStream _instream) throws IOException
	{
		final BufferedReader reader = new BufferedReader(new InputStreamReader(_instream), 4000);

		String line;
		final StringBuilder builder = new StringBuilder(1024);
		while ((line = reader.readLine()) != null)
		{
			builder.append(line);
		}
		return builder;
	}
}