package de.macsystems.windroid.db;

public interface IConfigDAO
{

	public abstract String getDatabaseStatus();

	public abstract boolean setDatabaseStatus(final String status);

}