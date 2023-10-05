package javawaveoptics.utility;

import java.io.Serializable;

/**
 * Provides useful static maths functions.
 * 
 * @author Sean
 */
public class MathsUtilities implements Serializable
{
	private static final long serialVersionUID = -1289236007720340158L;
	public static final double SQRT2 = Math.sqrt(2.);

	/**
	 * Checks that the number supplied is a power of 2.
	 * 
	 * Credit: http://sabbour.wordpress.com/2008/07/24/interview-question-check-that-an-integer-is-a-power-of-two/
	 * 
	 * @param n		Number to check
	 * 
	 * @return		True or false depending on whether n is a power of 2
	 * 
	 * @author		Sean
	 */
	public static boolean isPowerOfTwo(int n)
	{
		return ((n != 0) && (n & (n - 1)) == 0);
	}
	
	public static double laguerreL(int p, int l, double x)
	{		
		double laguerre = 1;
		
		double laguerre0 = 0;
		double laguerre1 = 0;
		
		for(int i = 1; i <= p; i++)
		{
			laguerre0 = laguerre1;
			laguerre1 = laguerre;
			
			laguerre = ((2 * i - 1 + l - x) * laguerre1 - (i - 1 + l) * laguerre0) / i;
		}
		
		return laguerre;
	}
	
	public static double laguerreNormalisation(int l, int p, double waist)
	{
		return (1 / waist) * (Math.sqrt(2 * factorial(p)) / (Math.PI * factorial(Math.abs(l) + p)));
	}
	
	/**
	 * Hermite polynomial H<sub>n</sub>(x);
	 * see http://en.wikipedia.org/wiki/Hermite_polynomials
	 * @param n
	 * @param x
	 * @return H<sub>n</sub>(x)
	 */
	public static double hermiteH(int n, double x)
	{
		if(n < 0)
		{
			throw new IllegalArgumentException("MathsUtilities::HermiteH: The specified index n must be positive");
		}
		else if(n == 0)
		{
			return 1;
		}
		else if(n == 1)
		{
			return 2*x;
		}
		else
		{
			return 2*x*hermiteH(n-1, x) - 2*(n-1)*hermiteH(n-2, x);
		}
	}
	
	public static int factorial(int n) throws IllegalArgumentException
	{
		if(n < 0)
		{
			throw new IllegalArgumentException("MathsUtilities::factorial: The specified number must be positive");
		}
		else
		{
			if(n == 0)
			{
				return 1;
			}
			else
			{
				return n * factorial(n - 1);
			}
		}
	}
	
	
	/**
	 * A modulo function that never gives a negative result
	 * @param a
	 * @param b
	 * @return a % b (+b, if necessary)
	 */
	public static int groovierMod(int a, int b)
	{
		return (a - (int)Math.floor((double)a / (double)b) * b);

//		int c = a % b;
//		
//		return (c<0)?(c+b):c;
	}
}
