/*

public classes:
example:

*/


package library.plot;


import library.maths.*;


public class ScaleTicks
{
	final int maxTickSeparationInPixels = 100;
	
	// parameters that describe the scale
	private int scaleSizeInPixels;
	private double min, max;	// the represented range
	
	// parameter calculated by calculateTickSeparation
	private double tickSeparation, firstTick;
	private int numberOfTicks;
	
	
	//
	// constructor
	//
	
	public ScaleTicks(double min, double max, int scaleSizeInPixels)
	{
		setScaleParameters(min, max, scaleSizeInPixels);
	}
	
	
	//
	// set information about the scale
	//
	
	public void setScaleParameters(double min, double max, int scaleSizeInPixels)
	{
		// copy parameters
		this.min = min;
		this.max = max;
		this.scaleSizeInPixels = scaleSizeInPixels;
		
		// calculate tick separation
		tickSeparation = calculateTickSeparation();
		firstTick = tickSeparation * Math.ceil(min / tickSeparation);
		numberOfTicks =
			(int)(Math.floor(max / tickSeparation) - Math.ceil(min / tickSeparation)) + 1;
	}
	
	
	//
	// get the calculated tick information
	//
	
	public double getTickSeparation()
	{
		return tickSeparation;
	}
	
	public int getNumberOfTicks()
	{
		return numberOfTicks;
	}
	
	public double getFirstTick()
	{
		return firstTick;
	}


	//
	// calculate tick separation
	//

	// calculates the tick separation
	public double calculateTickSeparation()
	{
		double maxTickSeparation =
			(max - min) * maxTickSeparationInPixels / scaleSizeInPixels;
		
		// start off with a tick separation of the smallest power of 10 that is
		// larger than (xMax - xMin)
		tickSeparation = Math.pow(10, Math.ceil(MyMath.log10(max - min)));
		
		// try successively smaller xTickSeparations and stop as soon as the resulting number of scale ticks is larger than minNoOfScaleTicks
		for(int n = 0; n < 10; n++)
		{
			// try ticks at values 1e...
		 	if( tickSeparation    < maxTickSeparation )
		 		return( tickSeparation    );
		 	// try ticks at values 0.5e...
		 	if( tickSeparation/2  < maxTickSeparation )
		 		return( tickSeparation/2  );
		 	// try ticks at values 0.2e...
		 	if( tickSeparation/5  < maxTickSeparation )
		 		return( tickSeparation/5  );
		 	
		 	// try next smaller order of magnitude
		 	tickSeparation /= 10;
		}
		
		// if no solution can be found just return the distance between the borders
		if(max != min) return( max - min );
		else return(min);
	}
}