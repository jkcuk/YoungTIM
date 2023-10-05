// LinearRegression.java
//
// references:
//   [1] Numerical Recipes in C


package library.maths;



//////////////////////////////////////
// test linear regression algorithm //
//////////////////////////////////////
		
public class LinearRegressionTest
{
	public LinearRegressionTest()
	{
		int N=1000;
		double
			offset = 1.0,
			slope = 2.0;
		
		System.out.println(
			"*** LinearRegressionTest ***\n" +
			"  creating " + N + " noisy data points centred on a straight line with " +
			"offset " + offset + " and slope " + slope + "...");
		double
			x[] = new double[N],
			y[] = new double[N];
		for(int i=0; i<N; i++)
		{
			x[i] = i + (Math.random()-0.5);
			y[i] = offset + slope*i + (Math.random()-0.5);
		}
		
		System.out.println("  invoking the LinearRegression algorithm...");
		LinearRegression lr = new LinearRegression(x, y);
		
		System.out.println("  result: " + lr);
	}
}