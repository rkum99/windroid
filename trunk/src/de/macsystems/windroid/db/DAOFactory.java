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

import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;
import de.macsystems.windroid.db.sqlite.ContinentImpl;
import de.macsystems.windroid.db.sqlite.CountryImpl;
import de.macsystems.windroid.db.sqlite.Database;
import de.macsystems.windroid.db.sqlite.ForecastImpl;
import de.macsystems.windroid.db.sqlite.PreferenceImpl;
import de.macsystems.windroid.db.sqlite.RegionImpl;
import de.macsystems.windroid.db.sqlite.SelectedImpl;
import de.macsystems.windroid.db.sqlite.SpotImpl;
import de.macsystems.windroid.progress.IProgress;
import de.macsystems.windroid.progress.NullProgressAdapter;

/**
 * @author Jens Hohl
 * @version $Id$
 */
public final class DAOFactory
{

	private final static AtomicReference<Database> dbReference = new AtomicReference<Database>();

	private DAOFactory()
	{
	}

	private static synchronized Database createDatabase(final Context _context)
	{
		Database database = dbReference.get();
		if (database == null)
		{
			database = new Database(_context);
			dbReference.set(database);
		}
		return database;
	}

	public final static IForecastDAO getForecast(final Context _context)
	{
		return new ForecastImpl(createDatabase(_context), NullProgressAdapter.INSTANCE);
	}

	public final static ISpotDAO getSpotDAO(final Context _context)
	{
		return new SpotImpl(createDatabase(_context), NullProgressAdapter.INSTANCE);
		// return new SpotImpl(new Database(_context),
		// NullProgressAdapter.INSTANCE);
	}

	public final static ISpotDAO getSpotDAO(final Context _context, final IProgress _progress)
	{
		return new SpotImpl(createDatabase(_context), _progress);
		// return new SpotImpl(new Database(_context), _progress);
	}

	public final static IContinentDAO getContinentDAO(final Context _context)
	{
		return new ContinentImpl(createDatabase(_context), NullProgressAdapter.INSTANCE);
		// return new ContinentImpl(new Database(_context),
		// NullProgressAdapter.INSTANCE);
	}

	public final static IContinentDAO getContinentDAO(final Context _context, final IProgress _progress)
	{
		return new ContinentImpl(createDatabase(_context), _progress);
		// return new ContinentImpl(new Database(_context), _progress);
	}

	public final static ISelectedDAO getSelectedDAO(final Context _context)
	{

		return new SelectedImpl(createDatabase(_context));
		// return new SelectedImpl(new Database(_context));
	}

	public final static ISelectedDAO getSelectedDAO(final Context _context, final IProgress _progress)
	{
		return new SelectedImpl(createDatabase(_context), _progress);
		// return new SelectedImpl(new Database(_context), _progress);
	}

	public static ICountryDAO getCountryDAO(final Context _context)
	{
		return new CountryImpl(createDatabase(_context));
		// return new CountryImpl(new Database(_context));
	}

	public final static IRegionDAO getRegionDAO(final Context _context, final IProgress _progress)
	{
		return new RegionImpl(createDatabase(_context), _progress);
		// return new RegionImpl(new Database(_context), _progress);
	}

	public final static IRegionDAO getRegionDAO(final Context _context)
	{
		return new RegionImpl(createDatabase(_context));
		// return new RegionImpl(new Database(_context));
	}

	public final static IPreferencesDAO getPreferencesDAO(final Context _context)
	{
		return new PreferenceImpl(createDatabase(_context));
		// return new PreferenceImpl(new Database(_context));
	}

}
