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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
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

	private final static HttpClient client;

	/**
	 * One Minute Timeout.
	 */
	private final static int HTTP_TIMEOUT = 1000 * 60;

	private final static List<String> agents = new ArrayList<String>();

	private final static String USER_AGENT = "User-Agent";

	private final static Random random = new Random();

	static
	{
		agents
				.add("Mozilla/5.0 (X11; U; Linux x86_64; de; rv:1.9.2.6) Gecko/20100628 Ubuntu/10.04 (lucid) Firefox/3.6.6");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.1.7) Gecko/20091221 Firefox/3.5.7");
		agents.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8) Gecko/20051111 Firefox/1.5 BAVM/1.0.0");
		agents
				.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1a2pre) Gecko/2008073000 Shredder/3.0a2pre ThunderBrowse/3.2.1.8");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.7.10) Gecko/20050716 Thunderbird/1.0.6");
		agents.add("Mozilla/5.0 (X11; U; Linux armv61; en-US; rv:1.9.1b2pre) Gecko/20081015 Fennec/1.0a1");
		agents
				.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.2.2pre) Gecko/20100225 Ubuntu/9.10 (karmic) Namoroka/3.6.2pre");
		agents.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.6) Gecko/20100127 Gentoo Shiretoko/3.5.6");
		agents.add("Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.3a1pre) Gecko/20100128 Minefield/3.7a1pre");
		agents
				.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.2) Gecko/20090803 Ubuntu/9.04 (jaunty) Shiretoko/3.5.2");
		agents.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:2.0a1pre) Gecko/2008060602 Minefield/4.0a1p");
		agents.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.2a1pre) Gecko/20090403 Minefield/3.6a1pre");
		agents.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1b1pre) Gecko/20080916020338 Minefield/3.1b1pre");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1b2) Gecko/20081201 Firefox/3.1b2");
		agents.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1a2) Gecko/20080829071937 Shiretoko/3.1a2");
		agents
				.add("Mozilla/5.0 (Windows; U; Windows NT 6.0 x64; en-US; rv:1.9pre) Gecko/2008072421 Minefield/3.0.2pre");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9a7 Gecko/2007080210 GranParadiso/3.0a7");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9b2pre) Gecko/2007120505 Minefield/3.0b2pre");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9a9pre) Gecko/2007100205 Minefield/3.0a9pre");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9a4pre Gecko/20070402 Minefield/3.0a4pre");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9a1) Gecko/20061204 GranParadiso/3.0a1");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.2 x64; en-US; rv:1.9a1) Gecko/20061007 Minefield/3.0a1");
		agents.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1) Gecko/20061102 BonEcho/2.0");
		agents.add("Mozilla/5.0 (X11; U; Linux x86_64; de; rv:1.8.1.1) Gecko/20061223 BonEcho/2.0.0.");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8) Gecko/20060319 Firefox/2.0a1");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.2 x64; en-US; rv:1.9a1) Gecko/20060214 Firefox/1.6a1");
		agents.add("Mozilla/5.0 (BeOS; U; BeOS BePC; en-US; rv:1.9a1) Gecko/20051002 Firefox/1.6a1");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.9a1) Gecko/20051102 Firefox/1.6a1");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8) Gecko/20051111 Firefox/1.5");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.5) Gecko/20041103 Firefox/1.0RC2");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; he; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; it; rv:1.9.2) Gecko/20100115 Firefox/3.6");
		agents
				.add("Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.7) Gecko/20091221 Firefox/3.5.7 (.NET CLR 3.5.30729)");
		agents
				.add("Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.1.3) Gecko/20091020 Linux Mint/8 (Helena) Firefox/3.5.3");
		agents.add("Mozilla/5.0 (X11; U; OpenBSD i386; en-US; rv:1.9.1) Gecko/20090702 Firefox/3.5");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6 GTB5");
		agents.add("Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.1.5) Gecko/20091107 Firefox/3.5.5");
		agents
				.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)");
		agents
				.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.1.5) Gecko/20091102 (BT-nasa) Firefox/3.5.5 (.NET CLR 3.5.30729)");
		agents
				.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.1.3) Gecko/20090911 Jolicloud/alpha (robby) Firefox/3.5.3");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3 GTB5");
		agents
				.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3 GTB5 (.NET CLR 3.5.30729) Scribd Toolbar 1.0");
		agents
				.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.1) Gecko/20090624 Firefox/3.5 (.NET CLR 3.5.30729)");
		agents.add("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; en-US; rv:1.9.1) Gecko/20090624 Firefox/3.5");
		agents.add("Mozilla/5.0 (X11; U; OpenBSD i386; en-US; rv:1.9.1) Gecko/20090702 Firefox/3.5");
		agents.add("Mozilla/5.0 (X11; U; SunOS i86pc; en-US; rv:1.9.1b3) Gecko/20090429 Firefox/3.1b3");
		agents
				.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.14) Gecko/2009082707 Firefox/3.0.14 (.NET CLR 3.5.30729)");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.13) Gecko/2009073022 Firefox/3.0.13");
		agents
				.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.11) Gecko/2009060309 Ubuntu/9.10 (karmic) Firefox/3.0.11");
		agents
				.add("Mozilla/5.0 (Windows; U; Windows NT 6.0; en-GB; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729) ");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10");
		agents
				.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 Creative ZENcast v1.02.12");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.6) Gecko/2009011913 Firefox/3.0.6");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.5) Gecko/2008120122 Firefox/3.0.5");
		agents
				.add("Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.0.4) Gecko/2008111319 Ubuntu/8.10 (intrepid) Firefox/3.0.4");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
		agents.add("Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10.5; en-US; rv:1.9.0.3) Gecko/2008092414 Firefox/3.0.3 ");
		agents.add("Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10.5; en-US; rv:1.9.0.3) Gecko/2008092414 Firefox/3.0.3 ");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.2) Gecko/2008091620 Firefox/3.0.2");
		agents
				.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072403 Mandriva/3.0.1-1mdv2008.1 (2008.1) Firefox/3.0.1 GTB5 Ubiquity/0.1.5");
		agents
				.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072403 Mandriva/3.0.1-1mdv2008.1 (2008.1) Firefox/3.0.1");
		agents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9) Gecko/2008052906 Firefox/3.0");
		agents.add("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5");

		//
		random.setSeed(agents.size() - 1);

		//
		final HttpParams httpParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		ConnManagerParams.setMaxTotalConnections(httpParams, 3);

		HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, HTTP_TIMEOUT);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		final ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(httpParams,
				schemeRegistry);
		client = new DefaultHttpClient(clientConnectionManager, httpParams);
	}

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
		if (Logging.isEnabled)
		{
			Log.d(LOG_TAG, "Using Network :" + IOUtils.getNetworkName(_context));
			Log.d(LOG_TAG, "Connecting to :" + uri.toString());
		}

		final HttpGet httpGet = new HttpGet(uri);

		httpGet.addHeader(USER_AGENT, getAgent());

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

	/**
	 * Returns an random http user agent from the agent list.
	 * 
	 * @return
	 */
	private static final String getAgent()
	{
		final int agentIndex = (int) ((double) random.nextDouble() * agents.size());
		return agents.get(agentIndex);
	}
}