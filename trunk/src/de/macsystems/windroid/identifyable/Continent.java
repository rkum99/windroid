package de.macsystems.windroid.identifyable;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public enum Continent implements IdentifyAble
{
	AFRICA("Africa"), EUROPE("Europe"), ASIA("Asia"), AUSTRALIA_OCEANIA("Australia & Oceania"), NORTH_AMERICA(
			"North America"), SOUTH_AMERICA("South America");

	private final Set<Country> countrys = new TreeSet<Country>(new Country.CountryComparator());

	private final String id;

	private static boolean isParsed = false;

	private Continent(final String _id)
	{
		id = _id;
	}

	public static void setParsed()
	{
		isParsed = true;
	}

	public static boolean isParsed()
	{
		return isParsed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.identifyable.IdentifyAble#getId()
	 */
	public String getId()
	{
		return id;
	}

	public int getNrOfCountrys()
	{
		return countrys.size();
	}

	/**
	 * 
	 * @param _continentID
	 * @return
	 * @throws IllegalArgumentException
	 *             if continent not found
	 */
	public static Continent getById(final String _continentID) throws IllegalArgumentException
	{
		for (final Continent continent : values())
		{
			if (continent.id.equals(_continentID))
			{
				return continent;
			}
		}
		throw new IllegalArgumentException("Unkown Continent \"" + _continentID + "\".");
	}

	/**
	 * 
	 * @param _country
	 */
	public void addCountry(final Country _country)
	{
		countrys.add(_country);
	}

	/**
	 * Returns an Iterator over all Country's of this Continent.
	 * 
	 * @return
	 */
	public Iterator<Country> iterator()
	{
		return countrys.iterator();
	}

	public Country[] getCoutrys()
	{
		return countrys.toArray(new Country[countrys.size()]);
	}

	@Override
	public String toString()
	{
		return id;
	}

}
