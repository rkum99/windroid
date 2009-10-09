package de.macsystems.windroid.db;

/**
 * 
 * @author mac
 * @version $Id$
 */
public interface IConfigDAO
{

	public abstract String getDatabaseStatus();

	public abstract boolean setDatabaseStatus(final String status);

}