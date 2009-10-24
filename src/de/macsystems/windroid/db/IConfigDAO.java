package de.macsystems.windroid.db;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public interface IConfigDAO
{

	public abstract String getStatus();

	public abstract boolean setStatus(final String _status);

}