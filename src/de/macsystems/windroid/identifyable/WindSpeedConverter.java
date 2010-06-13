package de.macsystems.windroid.identifyable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Decorates a WindSpeed MeasureValue to allow converting into different
 * measures.
 * 
 * @author mac
 * @version $Id$
 * @see IConvertable
 */
public final class WindSpeedConverter implements IConvertable
{

	public final static Set<Measure> supportedMeasure = Collections.unmodifiableSet(EnumSet.of(Measure.BEAUFORT,
			Measure.KNOTS, Measure.FPS, Measure.FTM, Measure.KMH, Measure.MMI, Measure.MPH, Measure.MPS));

	private final IMeasureValue orginalValue;

	private volatile IMeasureValue convert;

	private static Measure preferredMeasure = Measure.KNOTS;

	/**
	 * Set preferred measure of wind speed.
	 * 
	 * @param _preferredMeasure
	 * @throws IllegalArgumentException
	 *             if measure not supported
	 * @see #getMeasure()
	 * @see #getSupportedMeasure()
	 */
	public static void setPreferredMeasure(final Measure _preferredMeasure) throws IllegalArgumentException
	{
		if (_preferredMeasure == null)
		{
			throw new NullPointerException("Preferred Measure");
		}

		if (!supportedMeasure.contains(_preferredMeasure))
		{
			throw new IllegalArgumentException("cant set preferred measure to " + _preferredMeasure.getDescription()
					+ " expected: " + supportedMeasure.toString());
		}
		preferredMeasure = _preferredMeasure;
	}

	/**
	 * Creates an WindSpeedConverter which acts as a decorator between the
	 * original value and the converted. The original value will never be
	 * changed!
	 * 
	 * @param _value
	 * @throws NullPointerException
	 */
	public WindSpeedConverter(final IMeasureValue _value) throws NullPointerException
	{
		if (_value == null)
		{
			throw new NullPointerException("value");
		}
		orginalValue = _value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.identifyable.IMeasureValue#getMeasure()
	 */
	@Override
	public Measure getMeasure()
	{
		convertTo(preferredMeasure);
		return convert.getMeasure();
	}

	@Override
	public float getValue()
	{
		convertTo(preferredMeasure);
		return convert.getValue();
	}

	@Override
	public void convertTo(final Measure _measure)
	{

		if (_measure == null)
		{
			throw new NullPointerException("Measure");
		}
		if (orginalValue.getMeasure() == _measure)
		{
			convert = orginalValue;
			return;
		}

		if (convert != null && convert.getMeasure() == _measure)
		{
			return;
		}

		if (!supportedMeasure.contains(_measure))
		{
			throw new IllegalArgumentException("cant convert " + orginalValue.getMeasure().getDescription() + " into "
					+ _measure.getShortDisplayName());
		}
		// System.out.println("Converting into: "+_measure.getDescription());
		// TODO: Move calculation into switch block for better performance
		//
		float v = orginalValue.getValue(); // always Knots!!!

		final float ms = v * 0.514444f; // m s-1

		final float kmh = v * 1.852f; // km/h

		final float knot = v; // knots

		final float mph = v * 1.150779f; // mi h-1

		final float fts = (v * 1.687810f); // ft s-1

		final float mmi = (ms * 60.f); // m min-1

		final float ftm = (fts * 60.f); // ft min-1

		final int beaufort = knotToBeaufort(knot);

		switch (_measure)
		{
			case MPS:
				convert = WindSpeed.create(ms, Measure.MPS);
				break;
			case KMH:
				convert = WindSpeed.create(kmh, Measure.KMH);
				break;
			case KNOTS:
				convert = WindSpeed.create(knot, Measure.KNOTS);
				break;
			case MPH:
				convert = WindSpeed.create(mph, Measure.MPH);
				break;
			case FPS:
				convert = WindSpeed.create(fts, Measure.FPS);
				break;
			case MMI:
				convert = WindSpeed.create(mmi, Measure.MMI);
				break;
			case FTM:
				convert = WindSpeed.create(ftm, Measure.FTM);
				break;
			case BEAUFORT:
				convert = WindSpeed.create(beaufort, Measure.BEAUFORT);
				break;
			default:
				throw new IllegalArgumentException("unknown Measure" + _measure.getDescription());

		}
	}

	@Override
	public Set<Measure> getSupportedMeasure()
	{
		return supportedMeasure;
	}

	private static int knotToBeaufort(final float _knots)
	{

		if (_knots <= 1)
		{
			return 0;
		}

		if (_knots > 1 && _knots < 3.5)
		{
			return 1;
		}

		if (_knots >= 3.5 && _knots < 6.5)
		{
			return 2;
		}

		if (_knots >= 6.5 && _knots < 10.5)
		{
			return 3;
		}

		if (_knots >= 10.5 && _knots < 16.5)
		{
			return 4;
		}

		if (_knots >= 16.5 && _knots < 21.5)
		{
			return 5;
		}

		if (_knots >= 21.5 && _knots < 27.5)
		{
			return 6;
		}

		if (_knots >= 27.5 && _knots < 33.5)
		{
			return 7;
		}

		if (_knots >= 33.5 && _knots < 40.5)
		{
			return 8;
		}

		if (_knots >= 40.5 && _knots < 47.5)
		{
			return 9;
		}

		if (_knots >= 47.5 && _knots < 55.5)
		{
			return 10;
		}

		if (_knots >= 55.5 && _knots < 63.5)
		{
			return 11;
		}

		if (_knots >= 63.5 && _knots < 74.5)
		{
			return 12;
		}

		if (_knots >= 74.5 && _knots < 80.5)
		{
			return 13;
		}

		if (_knots >= 80.5 && _knots < 89.5)
		{
			return 14;
		}
		else if (_knots >= 89.5)
		{
			return 15;
		}

		return -1;
	}

}