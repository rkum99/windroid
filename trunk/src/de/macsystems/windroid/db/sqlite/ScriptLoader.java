package de.macsystems.windroid.db.sqlite;

import java.util.ArrayList;
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
	 * comments or empty lines. Returned List is unmodifiable! Triggers will be
	 * detected and returned as a single line.
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
		final List<String> orgScript = IOUtils.readTextfile(_context, _resourceId);
		final List<String> modifiedScript = new ArrayList<String>();
		final List<String> tempScript = new ArrayList<String>();
		final StringBuilder builder = new StringBuilder(256);
		boolean isInTrigger = false;
		final int size = orgScript.size();
		for (int i = 0; i < size; i++)
		{
			final String sql = orgScript.get(i);
			if (sql.startsWith("--") || "".equals(sql.trim()))
			{
				// dont add
			}
			else if (sql.trim().startsWith("CREATE TRIGGER"))
			{
				isInTrigger = true;
				tempScript.add(sql);
			}
			else if (sql.trim().startsWith("END"))
			{
				tempScript.add(" ");
				tempScript.add(sql.trim());
				isInTrigger = false;
				builder.setLength(0);
				for (int j = 0; j < tempScript.size(); j++)
				{
					builder.append(tempScript.get(j));
				}
				tempScript.clear();
				// write block as one line
				modifiedScript.add(builder.toString());
			}
			else if (isInTrigger)
			{
				tempScript.add(" ");
				tempScript.add(sql.trim());
			}
			else if (!isInTrigger)
			{
				modifiedScript.add(sql);
			}
		}
		return Collections.unmodifiableList(modifiedScript);

	}

	/**
	 * returns database upgrade sql script
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
	 * returns database create sql script
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
