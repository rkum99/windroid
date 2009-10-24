package de.macsystems.windroid.db.sqlite;

import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * @author Jens Hohl
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public class BaseImpl
{
	private final Database database;

	private final IProgress progress;

	/**
	 * 
	 * @param _database
	 * @param _progress
	 */
	protected BaseImpl(final Database _database, final IProgress _progress)
	{
		if (_database == null)
		{
			throw new NullPointerException("Database");
		}
		if (_progress == null)
		{
			throw new NullPointerException("progress");
		}

		database = _database;
		progress = _progress;
	}

	/**
	 * A Base Implementation with a {@link NullProgressAdapter}
	 * 
	 * @param _database
	 * @see NullProgressAdapter
	 */
	protected BaseImpl(final Database _database)
	{
		this(_database, NullProgressAdapter.INSTANCE);
	}

	/**
	 * 
	 * @return
	 */
	protected IProgress getProgress()
	{
		return progress;
	}

	/**
	 * Returns the Database
	 * 
	 * @return
	 */
	protected Database getDatabase()
	{
		return database;
	}

}
