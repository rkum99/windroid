package de.macsystems.windroid.db;

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
	private DAOFactory()
	{
	}

	public final static IForecastDAO getForecast(final Context _context)
	{
		return new ForecastImpl(new Database(_context), NullProgressAdapter.INSTANCE);
	}

	public final static ISpotDAO getSpotDAO(final Context _context)
	{
		return new SpotImpl(new Database(_context), NullProgressAdapter.INSTANCE);
	}

	public final static ISpotDAO getSpotDAO(final Context _context, final IProgress _progress)
	{
		return new SpotImpl(new Database(_context), _progress);
	}

	public final static IContinentDAO getContinentDAO(final Context _context)
	{
		return new ContinentImpl(new Database(_context), NullProgressAdapter.INSTANCE);
	}

	public final static IContinentDAO getContinentDAO(final Context _context, final IProgress _progress)
	{
		return new ContinentImpl(new Database(_context), _progress);
	}

	public final static ISelectedDAO getSelectedDAO(final Context _context)
	{
		return new SelectedImpl(new Database(_context));
	}

	public final static ISelectedDAO getSelectedDAO(final Context _context, final IProgress _progress)
	{
		return new SelectedImpl(new Database(_context), _progress);
	}

	public static ICountryDAO getCountryDAO(final Context _context)
	{
		return new CountryImpl(new Database(_context));
	}

	public final static IRegionDAO getRegionDAO(final Context _context, final IProgress _progress)
	{
		return new RegionImpl(new Database(_context), _progress);
	}

	public final static IRegionDAO getRegionDAO(final Context _context)
	{
		return new RegionImpl(new Database(_context));
	}

	public final static IPreferencesDAO getPreferencesDAO(final Context _context)
	{
		return new PreferenceImpl(new Database(_context));
	}

}
