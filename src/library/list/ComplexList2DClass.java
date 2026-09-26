/* */


package library.list;


import java.awt.*;

import library.maths.*;
import library.util.*;


////////////////////////
// ComplexList2DClass //
////////////////////////

// abstract implementation of the ComplexList2D interface that
// makes a number of useful (static and non-static) methods available

public abstract class ComplexList2DClass
implements ComplexList2D
{
	//
	// ComplexList2D methods
	//
	
	public abstract Dimension getSize();
	public abstract Complex getElement(int i, int j);
	public abstract double getElementRe(int i, int j);
	public abstract double getElementIm(int i, int j);
	
	
	//
	// complex-specific data access
	//
	
	// return a DoubleArray2D that contains the real parts
	public DoubleArray2D getRe()
	{
		int 
			width = getSize().width,
			height = getSize().height;
		
		DoubleArray2D a = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, getElementRe(i, j));
		
		return a;
	}

	// return a DoubleArray2D that contains the imaginary parts
	public DoubleArray2D getIm()
	{
		int 
			width = getSize().width,
			height = getSize().height;
		
		DoubleArray2D a = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, getElementIm(i, j));
		
		return a;
	}

	// return a DoubleArray2D that contains the absolute values
	public DoubleArray2D getAbs()
	{
		int 
			width = getSize().width,
			height = getSize().height;
		
		DoubleArray2D a = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, getElement(i, j).getAbs());
		
		return a;
	}

	// return a DoubleArray2D that contains the absolute values squared
	public DoubleArray2D getAbsSqr()
	{
		int 
			width = getSize().width,
			height = getSize().height;
		
		DoubleArray2D a = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, getElement(i, j).getAbsSqr());
		
		return a;
	}

	// return a DoubleArray2D that contains the arguments (phase angles)
	public DoubleArray2D getArg()
	{
		int 
			width = getSize().width,
			height = getSize().height;
		
		DoubleArray2D a = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, getElement(i, j).getArg());
		
		return a;
	}

	// return a ComplexArray2D that contains the conjugate values
	public ComplexArray2D getConjugate()
	{
		int 
			width = getSize().width,
			height = getSize().height;
		
		ComplexArray2D a = new ComplexArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, getElement(i, j).getConjugate());
		
		return a;
	}


	//
	// static functions
	//
	
	// return a DoubleArray2D that contains the real parts
	public static DoubleArray2D re(ComplexArray2D c)
	{
		int 
			width = c.getSize().width,
			height = c.getSize().height;
		
		DoubleArray2D a = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, c.getElementRe(i, j));
		
		return a;
	}

	// return a DoubleArray2D that contains the imaginary parts
	public DoubleArray2D im(ComplexArray2D c)
	{
		int 
			width = c.getSize().width,
			height = c.getSize().height;
		
		DoubleArray2D a = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, c.getElementIm(i, j));
		
		return a;
	}

	// return a DoubleArray2D that contains the absolute values
	public DoubleArray2D abs(ComplexArray2D c)
	{
		int 
			width = c.getSize().width,
			height = c.getSize().height;
		
		DoubleArray2D a = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, c.getElement(i, j).getAbs());
		
		return a;
	}

	// return a DoubleArray2D that contains the absolute values squared
	public DoubleArray2D absSqr(ComplexArray2D c)
	{
		int 
			width = c.getSize().width,
			height = c.getSize().height;
		
		DoubleArray2D a = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, c.getElement(i, j).getAbsSqr());
		
		return a;
	}

	// return a DoubleArray2D that contains the arguments (phase angles)
	public DoubleArray2D arg(ComplexArray2D c)
	{
		int 
			width = c.getSize().width,
			height = c.getSize().height;
		
		DoubleArray2D a = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, c.getElement(i, j).getArg());
		
		return a;
	}

	// return a ComplexArray2D that contains the conjugate values
	public ComplexArray2D conjugate(ComplexArray2D c)
	{
		int 
			width = c.getSize().width,
			height = c.getSize().height;
		
		ComplexArray2D a = new ComplexArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				a.setElement(i, j, c.getElement(i, j).getConjugate());
		
		return a;
	}


	//
	// Fourier transformation
	//
	
	// returns the Fourier Transform of the ComplexList2D t, leaving t unchanged
	public static ComplexArray2D FT(ComplexList2D t, int isign)
	{
		// make a copy of t ...
		ComplexArray2D ft = new ComplexArray2D(t);
		
		// ... and Fourier Transform it:
		ft.FT(isign);
		
		return ft;
	}
	

	//
	// element-by-element arithmetic operations with two ComplexList2Ds
	//

	// returns the element-by-element sum of two ComplexList2Ds
	public static ComplexArray2D sum(ComplexList2D a, ComplexList2D b)
	throws SizeMismatchError
	{
		if(a.getSize() != b.getSize())
			throw(new SizeMismatchError("ComplexList2D size mismatch"));

		int 
			width = a.getSize().width,
			height = a.getSize().height;
		
		ComplexArray2D c = new ComplexArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				c.setElement(i, j, Complex.sum(a.getElement(i, j), b.getElement(i, j)));
		
		return c;
	}

	// returns the element-by-element difference of two ComplexList2Ds
	public static ComplexArray2D difference(ComplexList2D a, ComplexList2D b)
	throws SizeMismatchError
	{
		if(a.getSize() != b.getSize())
			throw(new SizeMismatchError("ComplexList2D size mismatch"));

		int 
			width = a.getSize().width,
			height = a.getSize().height;
		
		ComplexArray2D c = new ComplexArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				c.setElement(i, j, Complex.difference(a.getElement(i, j), b.getElement(i, j)));
		
		return c;
	}

	// returns the element-by-element product of two ComplexList2Ds
	public static ComplexArray2D product(ComplexList2D a, ComplexList2D b)
	throws SizeMismatchError
	{
		if(a.getSize() != b.getSize())
			throw(new SizeMismatchError("ComplexList2D size mismatch"));

		int 
			width = a.getSize().width,
			height = a.getSize().height;
		
		ComplexArray2D c = new ComplexArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				c.setElement(i, j, Complex.product(a.getElement(i, j), b.getElement(i, j)));
		
		return c;
	}
}