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
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

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

	/**
	 * HTTP User Agent
	 */
	public static final String MOZILLA_5_0 = "Mozilla/5.0";

	private final URI uri;

	private final IProgress progress;
	/**
	 * One Minute Timeout.
	 */
	private final static int HTTP_TIMEOUT = 1000 * 60;
	/**
	 * We use only 1 connection as request are queued
	 */
	private final static int MAX_CONNECTIONS = 3;

	private final static HttpClient client;

	static
	{
		final HttpParams parameters = new BasicHttpParams();
		HttpProtocolParams.setVersion(parameters, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(parameters, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(parameters, false);
		ConnManagerParams.setMaxTotalConnections(parameters, MAX_CONNECTIONS);
		HttpConnectionParams.setConnectionTimeout(parameters, HTTP_TIMEOUT);
		HttpConnectionParams.setSoTimeout(parameters, HTTP_TIMEOUT);
		//
		final SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		//
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(parameters, schReg), parameters);
	}

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
		final HttpGet httpGet = new HttpGet(uri);
		httpGet.addHeader("User-Agent", MOZILLA_5_0);
		httpGet.addHeader("Accept-Encoding", "gzip,deflate");

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
		InputStream instream = null;
		try
		{
			// instream = new CountInputStream((response.getEntity()));

			// instream = new GZIPInputStream(new
			// CountInputStream(response.getEntity().getContent()));

			instream = handleContent(response.getEntity());

			if (Logging.isEnabled)
			{
				Log.d(LOG_TAG, "HTTP Content Lenght:" + response.getEntity().getContentLength());
				Log.d(LOG_TAG, "HTTP Content Encoding:" + response.getEntity().getContentEncoding());
				Log.d(LOG_TAG, "HTTP Content Content Type:" + response.getEntity().getContentType());
				Log.d(LOG_TAG, "HTTP Content isChunked:" + response.getEntity().isChunked());
				Log.d(LOG_TAG, "HTTP Content isRepeatable:" + response.getEntity().isRepeatable());
				Log.d(LOG_TAG, "HTTP Content isStreaming:" + response.getEntity().isStreaming());
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

	private InputStream handleContent(final HttpEntity _entity) throws IllegalStateException, IOException
	{
		if (_entity.getContentEncoding() == null)
		{
			return new CountInputStream(_entity.getContent());
		}

		final String encoding = _entity.getContentEncoding().getValue();

		if ("gzip".equalsIgnoreCase(encoding))
		{
			return new GZIPInputStream(new CountInputStream(_entity.getContent()));
		}

		throw new IllegalArgumentException("unknown encoding :" + encoding);

	}

}
