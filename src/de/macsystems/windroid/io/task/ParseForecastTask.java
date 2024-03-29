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

import android.content.Context;
import de.macsystems.windroid.common.SpotConfigurationVO;
import de.macsystems.windroid.forecast.Forecast;
import de.macsystems.windroid.io.IOUtils;
import de.macsystems.windroid.parser.ForecastParser;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * Parses the JSON Forecast for a station
 * 
 * @author Jens Hohl
 * @version $Id$
 * @see ForecastParser
 * @see Forecast
 */
public class ParseForecastTask extends IOTask<Forecast, InputStream>
{

	private final SpotConfigurationVO spotConfig;

	/**
	 * 
	 * @param _uri
	 *            the json url
	 * @param _vo
	 * @throws NullPointerException
	 */
	public ParseForecastTask(final URI _uri, final SpotConfigurationVO _vo) throws NullPointerException
	{
		this(_uri, NullProgressAdapter.INSTANCE, _vo);
	}

	/**
	 * 
	 * @param _uri
	 *            the json url
	 * @param _progress
	 * @param _vo
	 * @throws NullPointerException
	 */
	public ParseForecastTask(final URI _uri, final IProgress _progress, final SpotConfigurationVO _vo)
			throws NullPointerException
	{
		super(_uri, _progress);
		if (_vo == null)
		{
			throw new NullPointerException("vo");
		}
		spotConfig = _vo;

	}

	@Override
	public Forecast process(final Context _context, final InputStream _instream) throws IOException, Exception
	{
		final StringBuilder builder = IOUtils.asString(_instream, IOUtils.EIGHT_KB_BUFFER_SIZE);
		return ForecastParser.parse(builder, spotConfig);
	}
}