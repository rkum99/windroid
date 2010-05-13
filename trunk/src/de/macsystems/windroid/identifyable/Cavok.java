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
package de.macsystems.windroid.identifyable;

import android.content.res.Resources;
import de.macsystems.windroid.R;

/**
 * 
 * Cavok ist eine Wetterbeschreibung in der Luftfahrt und steht für ceiling and
 * visibility OK oder clouds and visibility OK (dtsch. Wolken und Sicht OK). Die
 * Abkürzung wird zum Beispiel in ATIS-Meldungen anstatt einer expliziten Angabe
 * von Wolkenuntergrenze und Sichtweite verwendet. Eine Wetterklassifizierung
 * als Cavok besagt im wesentlichen, dass gute Sichtflugbedingungen ohne
 * wesentliche Einschränkung vorherrschen.
 * 
 * Folgende Bedingungen sind nötig, damit die Wetterlage als Cavok klassifiziert
 * wird:
 * 
 * Bodensicht (Horizontalsicht) 10 km oder mehr Keine Wolken unter 5000 ft oder
 * der höchsten minimum sector altitude, MSA, falls diese höher ist. Keine
 * signifikanten Wettererscheinungen (Niederschlag, sichtbehindernde
 * meteorologische Erscheinungen wie Dunst, Rauch oder Staub etc.) Keine
 * Gewitterwolken (Cumulonimbus), unabhängig in welcher Höhe diese auftreten
 * 
 * Unter ceiling versteht man die Hauptwolkenuntergrenze. Dies ist die
 * Wolkenschicht, bei der mehr als die Hälfte des Himmels unterhalb 20.000 ft
 * AGL (6096 Meter über Grund) bedeckt ist (mindestens 5/8 = BKN).
 * 
 * Ansonsten klassifiziert man den Bedeckungsgrad des Himmels in Achteln:
 * 
 * 0/8 SKC - sky clear 1/8 bis 2/8 FEW - few 3/8 bis 4/8 SCT - scattered 5/8 bis
 * 7/8 BKN - broken 8/8 OVC - overcast 9/8 OBSC - obscured (Himmel nicht
 * sichtbar)
 * 
 * Source: http://de.wikipedia.org/wiki/Cavok
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public enum Cavok implements IdentifyAble
{

	SKC("SKC", R.string.CAVOK_SKC, R.drawable.weather_sky_clear, R.drawable.weather_clear_night), //
	FEW("FEW", R.string.CAVOK_FEW, R.drawable.weather_few, R.drawable.weather_few_clouds_night),
	SCT("SCT", R.string.CAVOK_SCT, R.drawable.weather_scattered, R.drawable.weather_night_scattered),
	BKN("BKN", R.string.CAVOK_BKN, R.drawable.weather_broken, R.drawable.weather_night_broken),
	OVC("OVC", R.string.CAVOK_OVC, R.drawable.weather_overcast, R.drawable.weather_night_overcast),
	OBS("OBS", R.string.CAVOK_OBS, R.drawable.weather_obscured, R.drawable.weather_obscured);

	private final String id;

	private final int daytimeResId;

	private final int nightimeResId;

	private final int description;
	/**
	 * daytime
	 */
	private final static long DAY = 1000L * 60L * 60 * 6L;
	/**
	 * nighttime
	 */
	private final static long NIGHT = 1000L * 60L * 60 * 22L;

	/**
	 * 
	 * @param _id
	 * @param _descriptionResID
	 */
	private Cavok(final String _id, final int _descriptionResID, final int _daytimeResID, final int _nightimeResID)
	{
		id = _id;
		description = _descriptionResID;
		daytimeResId = _daytimeResID;
		nightimeResId = _nightimeResID;
	}

	/**
	 * Returns an human readable description of this Cavok.
	 * 
	 * @return the description
	 */
	public String getDescription()
	{
		return Resources.getSystem().getString(description);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.identifyable.IdentifyAble#getId()
	 */
	@Override
	public String getId()
	{
		return id;
	}

	/**
	 * Returns Daytime resource id.
	 * 
	 * @return resid
	 */
	public int getDaytimeResId()
	{
		return daytimeResId;
	}

	/**
	 * Returns Icon nightime resource id
	 * 
	 * @return resid
	 */
	public int getNightimeResId()
	{
		return nightimeResId;
	}

	/**
	 * Returns Ressource ID by daytime
	 * 
	 * @param _daytimeMills
	 * @return
	 */
	public int getResIDByTime(final long _daytimeMills)
	{
		return (_daytimeMills >= DAY && _daytimeMills < NIGHT) ? getDaytimeResId() : getNightimeResId();
	}

}