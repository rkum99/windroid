package de.macsystems.windroid.db.sqlite;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import de.macsystems.windroid.R;
import de.macsystems.windroid.io.IOUtils;

/**
 * Loads SQL Scripts and returns it as an List of Strings which can be executed.<br>
 * Empty or lines beginning with '--' will be ignored.
 * 
 * @author mac
 * @version $Id$
 */
public class ScriptLoader
{

	private ScriptLoader()
	{
	}

	/**
	 * Returns an SQL script line by line. The script does not contain any
	 * comments or empty lines. Returned List is unmodifiable!
	 * 
	 * @param _context
	 * @param _resourceId
	 * @return the Script line by line
	 * @throws Exception
	 */
	private final static List<String> getScript(final Context _context, final int _resourceId) throws Exception
	{
		if (_context == null)
		{
			throw new NullPointerException("context");
		}
		/**
		 * remove non SQL from script
		 */
		final List<String> script = IOUtils.openResource(_context, _resourceId);
		for (int i = script.size() - 1; i >= 0; i--)
		{
			final String sql = script.get(i);
			if (sql.startsWith("--") || "".equals(sql.trim()))
			{
				script.remove(i);
			}
		}
		return Collections.unmodifiableList(script);

	}

	/**
	 * 
	 * @param _context
	 * @return
	 * @throws Exception
	 */
	public final static List<String> getUpdateScript(final Context _context) throws Exception
	{
		return getScript(_context, R.raw.updatedatabase);
	}

	/**
	 * 
	 * @param _context
	 * @return
	 * @throws Exception
	 */
	public final static List<String> getCreateScript(final Context _context) throws Exception
	{
		return getScript(_context, R.raw.createdatabase);
	}

}
