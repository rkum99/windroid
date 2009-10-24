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

	private final URI uri;

	private final IProgress progress;

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
			throw new NullPointerException();
		}
		if (_progress == null)
		{
			throw new NullPointerException();
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

		final HttpResponse response = getHTTPClient().execute(httpGet);
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
		 * translated into a RetryLaterException
		 */
		InputStream instream = null;
		try
		{
			instream = response.getEntity().getContent();
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
