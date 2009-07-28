package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * @see MeasureValue
 * 
 */
public enum Measure implements IdentifyAble
{

	METER("m"), //
	MILLIMETER("mm"),
	SECONDS("s"),
	FAHRENHEIT("fahrenheit"),
	CELSIUS("celsius");

	private final String id;

	/**
	 * 
	 * @param _id
	 */
	private Measure(final String _id)
	{
		id = _id;
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
}
