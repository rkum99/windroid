package de.macsystems.windroid.io.task;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.RetryLaterException;
import de.macsystems.windroid.parser.StationHandler;

import android.content.Context;

/**
 * @author mac
 * @version $Id$
 */
public class XMLParseTask extends IOTask<Void>
{

	private int nrOfStationsFound = -1;

	/**
	 * 
	 * @param _uri
	 */
	public XMLParseTask(final URI _uri)
	{
		super(_uri);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.io.task.IOTask#execute(android.content.Context)
	 */
	@Override
	public Void execute(final Context _context) throws RetryLaterException, IOException
	{
		final InputStream inStream = _context.openFileInput(getURI().toString());
		/**
		 * Every Exception behind is catched using 'Exception Firewall' and
		 * translated into a RetryLaterException
		 */
		try
		{
			process(_context, inStream);
		}
		catch (final Exception e)
		{
			throw new RetryLaterException(e);
		}
		return null;
	}

	@Override
	public Void process(final Context _context, final InputStream _instream) throws IOException, Exception
	{
		final BufferedInputStream buffInStream = new BufferedInputStream(_instream);
		try
		{
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			final SAXParser parser = factory.newSAXParser();
			final StationHandler stationHandler = new StationHandler();
			parser.parse(buffInStream, stationHandler);

			nrOfStationsFound = stationHandler.getNrOfStations();
		}
		finally
		{
			IOUtils.close(buffInStream);
			IOUtils.close(_instream);
		}

		return null;
	}

	public int getNrOfStations()
	{
		return nrOfStationsFound;
	}
}
