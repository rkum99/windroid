package de.macsystems.windroid.identifyable;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class NewContinent implements IdentifyAble, Node<Country>
{

	private final String id;
	private final String name;

	private final Set<Country> countrys = new TreeSet<Country>(new Country.CountryComparator());

	/**
	 * 
	 * @param _id
	 * @param _name
	 */
	public NewContinent(final String _id, final String _name)
	{
		if (_id == null)
		{
			throw new NullPointerException("_id");
		}
		if (_name == null)
		{
			throw new NullPointerException("_name");
		}

		id = _id;
		name = _name;
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
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * 
	 * @param _country
	 */
	public void addCountry(final Country _country) throws NullPointerException
	{
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

	public int getNrOfCountrys()
	{
		return countrys.size();
	}

	@Override
	public String toString()
	{
		return id;
	}

	@Override
	public void add(Country _country)
	{
		if (_country == null)
		{
			throw new NullPointerException("country");
		}

		countrys.add(_country);
	}

	@Override
	public int getSize()
	{
		return countrys.size();
	}

	@Override
	public boolean isLeaf()
	{
		return countrys.isEmpty();
	}

}
