package de.macsystems.windroid.io.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.RetryLaterException;

/**
 * Abstract base class for io tasks using http client.
 * 
 * @author mac
 * @version $Id$
 */
public abstract class IOTask<V> implements Task<V>
{
	private final static String LOG_TAG = IOTask.class.getSimpleName();

	private final static HttpClient client = new DefaultHttpClient();

	private URI uri;

	public IOTask(final URI _uri)
	{
		if (_uri == null)
		{
			throw new NullPointerException();
		}
		uri = _uri;
	}

	protected URI getURI()
	{
		return uri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.io.task.Task#execute(android.content.Context)
	 */
	public V execute(final Context _context) throws RetryLaterException, IOException
	{
		if (!IOUtils.isNetworkReachable(_context))
		{
			Log.d(LOG_TAG, "Network not reachable.");
			throw new RetryLaterException("Cannot execute, Network not reachable.");
		}

		Log.d(LOG_TAG, "Connecting to :" + uri.toString());

		final HttpGet httpGet = new HttpGet(uri);
		httpGet.addHeader("User-Agent", IOUtils.MOZILLA_5_0);

		final HttpResponse response = client.execute(httpGet);
		if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode())
		{
			final StatusLine status = response.getStatusLine();
			throw new RetryLaterException("Server reponse was : " + status.getStatusCode() + " - "
					+ status.getReasonPhrase());
		}
		/**
		 * Every Exception behind is catched using 'Exception Firewall' and
		 * translated into a RetryLaterException
		 */
		try
		{
			return process(_context, response.getEntity().getContent());
		}
		catch (final Exception e)
		{
			throw new RetryLaterException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.io.task.Task#process(android.content.Context,
	 * java.io.InputStream)
	 */
	public abstract V process(final Context _context, final InputStream _instream) throws IOException, Exception;

	protected HttpClient getHTTPClient()
	{
		return client;
	}

}
