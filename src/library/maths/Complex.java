package library.maths;

import java.io.*;

public class Complex
implements Serializable
{
	private static final long serialVersionUID = -5064368565266345551L;

	// the "imaginary" unit, i
	public final static Complex i = new Complex(0.0, 1.0);

	public double re, im;	// real and imaginary parts
	
	// constructor that generates a complex number
	public Complex(double re, double im)
	{
		this.re = re;
		this.im = im;
	}

	// constructor that copies a complex number
	public Complex(Complex c)
	{
		this.re = c.re;
		this.im = c.im;
	}
	
	// constructor that creates a complex number that is real
	public Complex(double re)
	{
		this.re = re;
		this.im = 0;
	}
	
	public Complex getConjugate()
	{
		return new Complex(re, -im);
	}
	
	public static Complex conjugate(Complex c)
	{
		return new Complex(c.re, -c.im);
	}
	
	// returns the absolute value of the complex number
	public double getAbs()
	{
		return Math.sqrt(re*re + im*im);
	}
	
	// return the absolute value of a complex number
	public static double abs(Complex c)
	{
		return Math.sqrt(c.re*c.re + c.im*c.im);
	}
	
	// returns the absolute value squared
	public double getAbsSqr()
	{
		return re*re + im*im;
	}
	
	// return the absolute value squared
	public static double absSqr(Complex c)
	{
		return c.re*c.re + c.im*c.im;
	}
	
	// returns the argument (phase angle) of the complex number
	public double getArg()
	{
		return Math.atan2(im, re);
	}
	
	public static double arg(Complex c)
	{
		return Math.atan2(c.im, c.re);
	}
	
	public static Complex expI(double f)
	{
		return new Complex(Math.cos(f), Math.sin(f));
	}
	
	// add two complex numbers
	public static Complex sum(Complex a, Complex b)
	{
		return new Complex(a.re+b.re, a.im+b.im);
	}
	
	// add to this complex number the complex number c
	public void add(Complex c)
	{
		re += c.re;
		im += c.im;
	}
	
	// difference of two complex numbers
	public static Complex difference(Complex a, Complex b)
	{
		return new Complex(a.re-b.re, a.im-b.im);
	}

	
	// multiply two complex numbers
	public static Complex product(Complex a, Complex b)
	{
		return new Complex(
			a.re*b.re - a.im*b.im,
			a.re*b.im + a.im*b.re
		);
	}
	
	// product of real number with complex number...
	public static Complex product(double a, Complex b)
	{
		return new Complex(a*b.re, a*b.im);
	}
	
	// ... and of complex number with real number
	public static Complex product(Complex a, double b)
	{
		return new Complex(a.re*b, a.im*b);
	}
	
	// multiply this complex number with the complex number c
	public Complex multiply(Complex c)
	{
		double reNew = re*c.re - im*c.im;
		im = re*c.im + im*c.re;
		re = reNew;
		return this;
	}
	
	// multiply this complex number with the real number f
	public Complex multiply(double f)
	{
		im *= f;
		re *= f;
		return this;
	}
	
	public String toString()
	{
		return "(" + re + ((im>0)?"+":"") + im + "*i)";
	}
}