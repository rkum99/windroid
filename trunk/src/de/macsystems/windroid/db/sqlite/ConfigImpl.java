package de.macsystems.windroid.db.sqlite;

import de.macsystems.windroid.db.IConfigDAO;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public final class ConfigImpl extends BaseImpl implements IConfigDAO
{

	public final String DB_STATUS = "db_status";

	/**
	 * 
	 * @param _database
	 */
	public ConfigImpl(final Database _database)
	{
		this(_database, NullProgressAdapter.INSTANCE);
	}

	/**
	 * 
	 * @param _database
	 * @param _progress
	 */
	public ConfigImpl(final Database _database, final IProgress _progress)
	{
		super(_database, "config", _progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IConfigDAO#getDatabaseStatus()
	 */
	public String getStatus()
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.db.IConfigDAO#setStatus(java.lang.String)
	 */
	public boolean setStatus(final String _status)
	{
		throw new UnsupportedOperationException();
	}
}