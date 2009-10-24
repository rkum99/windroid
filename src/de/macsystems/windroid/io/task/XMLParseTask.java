package de.macsystems.windroid.io.task;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.io.RetryLaterException;
import de.macsystems.windroid.parser.StationHandler;
import de.macsystems.windroid.progress.IProgress;

/**
 * XML Parse Task
 * 
 * 
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public class XMLParseTask extends IOTask<Integer, InputStream>
{

	/**
	 * 
	 * @param _uri
	 * @param _progress
	 */
	public XMLParseTask(final URI _uri, final IProgress _progress)
	{
		super(_uri, _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.io.task.IOTask#execute(android.content.Context)
	 */
	@Override
	public Integer execute(final Context _context) throws RetryLaterException, IOException
	{
		final InputStream inStream = _context.openFileInput(getURI().toString());
		/**
		 * Every Exception behind is catched using 'Exception Firewall' and
		 * translated into a RetryLaterException
		 */
		try
		{
			return process(_context, inStream);
		}
		catch (final Exception e)
		{
			throw new RetryLaterException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.io.task.IOTask#process(android.content.Context,
	 * java.io.InputStream)
	 */
	@Override
	public Integer process(final Context _context, final InputStream _instream) throws IOException, Exception
	{
		final BufferedInputStream buffInStream = new BufferedInputStream(_instream);
		try
		{
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			final SAXParser parser = factory.newSAXParser();
			final StationHandler stationHandler = new StationHandler();
			parser.parse(buffInStream, stationHandler);

			return stationHandler.getNrOfStations();
		}
		finally
		{
			IOUtils.close(buffInStream);
		}
	}
}
