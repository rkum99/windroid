package de.macsystems.windroid.identifyable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class TempConverter implements IConvertable
{
	public final static Set<Measure> supportedMeasure = Collections.unmodifiableSet(EnumSet.of(Measure.CELSIUS,
			Measure.FAHRENHEIT));

	private static Measure preferredMeasure = Measure.CELSIUS;

	private final IMeasureValue orginalValue;

	private IMeasureValue convert;

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
	 * Creates an TempConverter which acts as a decorator between the original
	 * value and the converted. The original value will never be changed!
	 * 
	 * @param _value
	 * @throws NullPointerException
	 */
	public TempConverter(final IMeasureValue _value) throws NullPointerException
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
	 * @see
	 * de.macsystems.windroid.identifyable.IConvertable#convertTo(de.macsystems
	 * .windroid.identifyable.Measure)
	 */
	@Override
	public void convertTo(final Measure _measure) throws IllegalArgumentException
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

		switch (_measure)
		{
			case CELSIUS:
				convert = orginalValue;
				break;
			case FAHRENHEIT:
				convert = Temperature.asFahrenheit(orginalValue.getValue() * 1.8f + 32);
				break;
			default:
				throw new IllegalArgumentException("unknown Measure" + _measure.getDescription());

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.identifyable.IConvertable#getSupportedMeasure()
	 */
	@Override
	public Set<Measure> getSupportedMeasure()
	{
		return supportedMeasure;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.identifyable.IMeasureValue#getValue()
	 */
	@Override
	public float getValue()
	{
		convertTo(preferredMeasure);
		return convert.getValue();
	}

}
