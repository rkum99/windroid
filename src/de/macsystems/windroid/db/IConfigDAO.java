package de.macsystems.windroid.db;

/**
 * 
 * @author Jens Hohl
 * @version $Id$
 */
public interface IConfigDAO extends IDAO
{

	public String getStatus();

	public boolean setStatus(final String _status);

}