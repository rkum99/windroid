package de.macsystems.windroid.identifyable;

/**
 * @author mac
 * @version $Id: org.eclipse.jdt.ui.prefs 44 2009-10-02 15:22:27Z jens.hohl $
 */
public final class WindSpeedPresenter implements IPresenter
{

	private final Measure current;
	
	public WindSpeedPresenter()
	{
		current = Measure.KNOTS;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.identifyable.IPresenter#getMeasure()
	 */
	@Override
	public Measure getMeasure()
	{
		return current;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.macsystems.windroid.identifyable.IPresenter#getValue()
	 */
	@Override
	public float getValue()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.macsystems.windroid.identifyable.IPresenter#showAs(de.macsystems.windroid
	 * .identifyable.Measure)
	 */
	@Override
	public void showAs(Measure measure) throws IllegalArgumentException
	{
		// TODO Auto-generated method stub

	}

}
