package de.macsystems.windroid.identifyable;

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
	SKC("SKC", "sky clear"),
	FEW("FEW", "few"),
	SCT("SCT", "scattered"),
	BKN("BKN", "broken"),
	OVC("OVC", "overcast"),
	OBS("OBS", "obscured");

	private final String id;

	private final String description;

	/**
	 * 
	 * @param _id
	 * @param _desciption
	 */
	private Cavok(final String _id, final String _desciption)
	{
		id = _id;
		description = _desciption;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	@Override
	public String getId()
	{
		return id;
	}

}
