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
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.util.Log;
import de.macsystems.windroid.Logging;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.RetryLaterException;
import de.macsystems.windroid.progress.IProgress;

/**
 * Abstract base class for io tasks using http client. <br>
 * <br>
 * Parameter V is the return value.<br>
 * Parameter I mean the parameter which allows to work with that data.
 *
 * @author Jens Hohl
 * @version $Id$
 * @TODO use IProgress
 */
public abstract class IOTask<V, I> implements Task<V, I>
{
	private final static String LOG_TAG = IOTask.class.getSimpleName();

	private final static HttpClient client = new DefaultHttpClient();
	private final static HttpParams HTTP_PARAMS = client.getParams();
	/**
	 * HTTP User Agent
	 */
	public static final String MOZILLA_5_0 = "Mozilla/5.0";

	private final URI uri;

	private final IProgress progress;
	/**
	 * One Minute Timeout.
	 */
	private final int HTTP_TIMEOUT = 1000 * 60;

	/**
	 *
	 * @param _uri
	 *            points to an io resource
	 * @param _progress
	 * @throws NullPointerException
	 *             if any parameter is null
	 */
	public IOTask(final URI _uri, final IProgress _progress) throws NullPointerException
	{
		if (_uri == null)
		{
			throw new NullPointerException("uri");
		}
		if (_progress == null)
		{
			throw new NullPointerException("progress");
		}
		uri = _uri;
		progress = _progress;
		//
		HttpConnectionParams.setConnectionTimeout(HTTP_PARAMS, HTTP_TIMEOUT);
		HttpConnectionParams.setSoTimeout(HTTP_PARAMS, HTTP_TIMEOUT);
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
	public V execute(final Context _context) throws RetryLaterException, IOException, InterruptedException
	{
		if (!IOUtils.isNetworkReachable(_context))
		{
			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "Network not reachable.");
			}
			throw new RetryLaterException("Cannot execute, Network not reachable.");
		}
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "Using Network :" + IOUtils.getNetworkName(_context));
			Log.d(LOG_TAG, "Connecting to :" + uri.toString());
		}

		final HttpGet httpGet = new HttpGet(uri);
		httpGet.addHeader("User-Agent", MOZILLA_5_0);
		httpGet.setParams(HTTP_PARAMS);

		final HttpResponse response = getHTTPClient().execute(httpGet);
		if (Logging.isEnabled)
		{
			final StatusLine status = response.getStatusLine();
			Log.d(LOG_TAG, "Response Code was : " + status.getStatusCode() + " - " + status.getReasonPhrase());
		}

		if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode())
		{
			final StatusLine status = response.getStatusLine();
			final StringBuilder builder = new StringBuilder(256);
			builder.append("Failed to connect to:").append(uri.toString()).append("\n");
			builder.append("HTTP reponse was : ").append(status.getStatusCode()).append(" - ").append(
					status.getReasonPhrase());
			throw new RetryLaterException(builder.toString());
		}
		/**
		 * Every Exception behind is catched using 'Exception Firewall' and
		 * translated into a RetryLaterException.
		 */
		CountInputStream instream = null;
		try
		{
			instream = new CountInputStream(response.getEntity().getContent());
			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "HTTP Content Lenght:" + response.getEntity().getContentLength());
			}
			return process(_context, instream);
		}
		catch (final Exception e)
		{
			throw new RetryLaterException(e);
		}
		finally
		{
			IOUtils.close(instream);
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

	protected IProgress getProgress()
	{
		return progress;
	}

}
