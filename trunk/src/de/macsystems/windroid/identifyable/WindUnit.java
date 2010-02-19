package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public enum WindUnit implements IdentifyAble
{

	BEAUFORT("bft", "Beaufort", 12), //
	KNOTS("kts", "Knots", 63),
	MPS("mps", "Meter per Second (m/s)", 37),
	MPH("mph", "Miles per Hour", 83),
	KMH("kmh", "Kilometer per Hour", 133);

	private final String description;

	private final int maximum;

	private final String id;

	/**
	 * 
	 * @param _id
	 * @param _description
	 * @param _max
	 */
	private WindUnit(final String _id, final String _description, final int _max)
	{
		id = _id;
		description = _description;
		maximum = _max;
	}

	/**
	 * Returns <code>WindUnit</code> by its id.
	 * 
	 * @param _id
	 * @return
	 * @throws IllegalArgumentException
	 */
	public final static WindUnit getById(final String _id) throws IllegalArgumentException
	{
		if (_id == null)
		{
			throw new IllegalArgumentException("ID is  null.");
		}
		for (final WindUnit unit : WindUnit.values())
		{
			if (_id.equalsIgnoreCase(unit.id))
			{
				return unit;
			}
		}
		throw new IllegalArgumentException("unknown id \"" + _id + "\".");
	}

	/**
	 * Returns maximum of this Unit.
	 * 
	 * @return
	 */
	public int getMaximum()
	{
		return maximum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return description;
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
}