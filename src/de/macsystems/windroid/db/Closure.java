/**
 This file is part of Windroid.

 Windroid is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Windroid is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Windroid.  If not, see <http://www.gnu.org/licenses/>.

 */
package de.macsystems.windroid.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.macsystems.windroid.io.IOUtils;

/**
 * A the mo. unused class, is more a remainder to me.
 * 
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class Closure
{
	private final SQLiteDatabase db;
	private Cursor cursor;

	public Closure(final SQLiteDatabase _db, final Cursor _cursor)
	{
		db = _db;
		cursor = _cursor;
	}

	public void updateCursor(final Cursor _cursor)
	{
		IOUtils.close(cursor);
		cursor = cursor;
	}

	public void close()
	{
		IOUtils.close(cursor);
		IOUtils.close(db);
	}
}
