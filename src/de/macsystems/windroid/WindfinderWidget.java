package de.macsystems.windroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class WindfinderWidget
{

	public WindfinderWidget()
	{
		try
		{
			final URL url = WindUtils.getJSONForcastURL("nl48");
			System.out.println("" + url);

			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			//
			final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line;
			final StringBuilder builder = new StringBuilder(1024);
			while ((line = reader.readLine()) != null)
			{
				System.out.println(line);
				builder.append(line);
			}

			final JSONTokener tokener = new JSONTokener(builder.toString());
			final JSONObject jsonRoot = new JSONObject(tokener);
			System.out.println("" + jsonRoot.getString("timestamp"));

			final JSONArray stations = jsonRoot.getJSONArray("stations");
			System.out.println("Stations = " + stations.toString());

			final JSONObject generalStationInfos = stations.getJSONObject(0);

			final String stationName = generalStationInfos.getString("name");
			final String stationID = generalStationInfos.getString("id");
			final String stationTimezone = generalStationInfos.getString("timezone");
			// final StationBean stationBean = new StationBean(stationName,
			// stationID, stationTimezone);
			// System.out.println("" + stationBean.toString());

		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		catch (final JSONException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		new WindfinderWidget();

	}

}
