// LinearRegression.java
//
// references:
//   [1] Numerical Recipes in C


package library.maths;


import library.util.*;

public class LinearRegression
{
	// fitted straight line:
	public double
		offset,	// offset...
		sigmaOffset,	// and its standard deviation
		slope,	// slope...
		sigmaSlope;	// and its standard deviation
	
	// constructor
	// Fit a straight line through the data points (x[1], y[1]), (x[2], y[2]), ...
	// The offset and slope of the fitted straight line are returned in the variables a and b.
	public LinearRegression(double x[], double y[])
	{
		if(x.length != y.length)
			throw(new SizeMismatchError(
				"The arrays holding the x and y coordinates are of unequal length"));
		
		// nomenclature as in chapter 15.2 in ref. [1]
		double
			S = x.length,
			Sx = 0, Sy = 0, Sxx = 0, Sxy = 0, Delta;
		
		for(int i=0; i<S; i++)
		{
			Sx += x[i];	// Sx = Sum_i x_i
			Sy += y[i]; // Sy = Sum_i y_i
			Sxx += x[i] * x[i];	// Sxx = Sum_i x_i^2
			Sxy += x[i] * y[i];	// Sxy = Sum_i x_i * y_i
		}
		
		// calculate the offset and slope of the fitted straight line
		// equations (15.2.6) in [1]
		Delta = S * Sxx - Sx * Sx;
		offset = (Sxx * Sy  - Sx * Sxy) / Delta;
		slope  = (S   * Sxy - Sx * Sy ) / Delta;
		
		// calculate standard deviations of offset and slope according to
		// equations (15.2.8) in [1];
		// these estimates are quite useless!
		sigmaOffset = Math.sqrt(Sxx / Delta);
		sigmaSlope  = Math.sqrt(S   / Delta);
	}
	
	public String toString()
	{
		return
			"offset = " + offset + " +/- " + sigmaOffset +
			", slope = " + slope + " +/- " + sigmaSlope;
	}
}
