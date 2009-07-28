package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public enum Pascal implements IdentifyAble
{

	HEKTOPASCAL("hpa");

	private final String id;

	private Pascal(final String _id)
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
