package de.macsystems.windroid.identifyable;

/**
 * @author Jens Hohl
 * @version $Id$
 * 
 */
public class IdentityUtil
{
	/**
	 * Returns index of <code>IdentifyAble</code> in given array.
	 * 
	 * @param id
	 * @param values
	 * @return
	 * @throws IllegalArgumentException
	 *             when not found by id.
	 * @throws NullPointerException
	 *             when id or values is null
	 */
	public static int indexOf(final String id, final IdentifyAble[] values) throws IllegalArgumentException
	{
		if (id == null)
		{
			throw new NullPointerException("Id is null");
		}
		if (values == null)
		{
			throw new NullPointerException("values is null");
		}

		for (int i = 0; i < values.length; i++)
		{
			if (id.equals(values[i].getId()))
			{
				return i;
			}
		}
		throw new IllegalArgumentException("IdentifyAble not found by id \"" + id + "\". Values :" + printIds(values));
	}

	/**
	 * Outputs IDs of given IdentifyAbles
	 * 
	 * @param values
	 * @return comma separated IDs
	 */
	private final static String printIds(final IdentifyAble[] values)
	{
		if (values == null)
		{
			return "[]";
		}
		final StringBuilder builder = new StringBuilder(values.length * 3);
		builder.append("[");
		for (int i = 0; i < values.length; i++)
		{
			builder.append(values[i].getId());
			if (i < values.length - 1)
			{
				builder.append(", ");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}
