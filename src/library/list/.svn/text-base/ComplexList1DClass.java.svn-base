/* */


package library.list;


import java.io.*;

import library.maths.*;
import library.util.*;


////////////////////////
// ComplexList1DClass //
////////////////////////

// abstract implementation of the DoubleList1D interface that
// makes a number of useful (static and non-static) methods available

public abstract class ComplexList1DClass
implements ComplexList1D
{
	//
	// ComplexList1D methods
	//
	
	public abstract int getSize();
	public abstract Complex getElement(int i);
	public abstract double getElementRe(int i);
	public abstract double getElementIm(int i);
	
	
	//
	// additional access to elements
	//
	
	// returns a ComplexArray1D with the elements corresponding to the range
	// of indices i1, i1+1, ..., i2-1, i2
	public ComplexArray1D getElementRange(int i1, int i2)
	{
		int iMin, iMax;
		
		if(i1 <= i2)
		{
			iMin = i1;
			iMax = i2;
		}
		else
		{
			iMin = i2;
			iMax = i1;
		}
		
		if(iMin < 0)
			throw(new OutOfBoundsError("index " + iMin + " out of bounds."));
		if(iMax > getSize()-1)
			throw(new OutOfBoundsError("index " + iMax + " out of bounds."));
		
		// create a new ComplexArray1D, ...
		ComplexArray1D a = new ComplexArray1D(iMax - iMin + 1);
		
		// ... fill it with the desired elements, ...
		for(int i=0; i<iMax-iMin+1; i++) a.setElement(i, getElement(iMin+i));
		
		// ... and return it
		return a;
	}
	
	
	//
	// complex-specific data access
	//
	
	// return a DoubleArray1D that contains the real parts
	public DoubleArray1D getRe()
	{
		DoubleArray1D a = new DoubleArray1D(getSize());
		
		for(int i=0; i<getSize(); i++) a.setElement(i, getElementRe(i));
		
		return a;
	}

	// return a DoubleArray1D that contains the imaginary parts
	public DoubleArray1D getIm()
	{
		DoubleArray1D a = new DoubleArray1D(getSize());
		
		for(int i=0; i<getSize(); i++) a.setElement(i, getElementIm(i));
		
		return a;
	}

	// return a DoubleArray1D that contains the absolute values
	public DoubleArray1D getAbs()
	{
		DoubleArray1D a = new DoubleArray1D(getSize());
		
		for(int i=0; i<getSize(); i++)
			a.setElement(i, getElement(i).getAbs());
		
		return a;
	}

	// return a DoubleArray1D that contains the absolute values squared
	public DoubleArray1D getAbsSqr()
	{
		DoubleArray1D a = new DoubleArray1D(getSize());
		
		for(int i=0; i<getSize(); i++)
			a.setElement(i, getElement(i).getAbsSqr());
		
		return a;
	}

	// return a DoubleArray1D that contains the arguments (phase angles)
	public DoubleArray1D getArg()
	{
		DoubleArray1D a = new DoubleArray1D(getSize());
		
		for(int i=0; i<getSize(); i++)
			a.setElement(i, getElement(i).getArg());
		
		return a;
	}

	// return a ComplexArray1D that contains the conjugate values
	public ComplexArray1D getConjugate()
	{
		ComplexArray1D a = new ComplexArray1D(getSize());
		
		for(int i=0; i<getSize(); i++)
			a.setElement(i, getElement(i).getConjugate());
		
		return a;
	}


	//
	// static functions
	//
	
	// return a DoubleArray1D that contains the real parts
	public static DoubleArray1D re(ComplexArray1D c)
	{
		DoubleArray1D a = new DoubleArray1D(c.getSize());
		
		for(int i=0; i<c.getSize(); i++) a.setElement(i, c.getElementRe(i));
		
		return a;
	}

	// return a DoubleArray1D that contains the imaginary parts
	public static DoubleArray1D im(ComplexArray1D c)
	{
		DoubleArray1D a = new DoubleArray1D(c.getSize());
		
		for(int i=0; i<c.getSize(); i++) a.setElement(i, c.getElementIm(i));
		
		return a;
	}

	// return a DoubleArray1D that contains the absolute values
	public static DoubleArray1D abs(ComplexArray1D c)
	{
		DoubleArray1D a = new DoubleArray1D(c.getSize());
		
		for(int i=0; i<c.getSize(); i++)
			a.setElement(i, c.getElement(i).getAbs());
		
		return a;
	}

	// return a DoubleArray1D that contains the absolute values squared
	public static DoubleArray1D absSqr(ComplexArray1D c)
	{
		DoubleArray1D a = new DoubleArray1D(c.getSize());
		
		for(int i=0; i<c.getSize(); i++)
			a.setElement(i, c.getElement(i).getAbsSqr());
		
		return a;
	}

	// return a DoubleArray1D that contains the arguments (phase angles)
	public static DoubleArray1D arg(ComplexArray1D c)
	{
		DoubleArray1D a = new DoubleArray1D(c.getSize());
		
		for(int i=0; i<c.getSize(); i++)
			a.setElement(i, c.getElement(i).getArg());
		
		return a;
	}

	// return a ComplexArray1D that contains the conjugate values
	public static ComplexArray1D conjugate(ComplexArray1D c)
	{
		ComplexArray1D a = new ComplexArray1D(c.getSize());
		
		for(int i=0; i<c.getSize(); i++)
			a.setElement(i, c.getElement(i).getConjugate());
		
		return a;
	}


	//
	// Fourier transformation
	//
	
	// returns the Fourier Transform of the ComplexList1D t, leaving t unchanged
	public static ComplexArray1D FT(ComplexList1D t, int isign)
	{
		// make a copy of t ...
		ComplexArray1D ft = new ComplexArray1D(t);
		
		// ... and Fourier Transform it:
		ft.FT(isign);
		
		return ft;
	}
	

	//
	// element-by-element arithmetic operations with two DoubleList1Ds
	//

	// returns the element-by-element sum of two ComplexList1Ds
	public static ComplexArray1D sum(ComplexList1D a, ComplexList1D b)
	throws SizeMismatchError
	{
		ComplexArray1D p;
		
		if(a.getSize() != b.getSize())
			throw(new SizeMismatchError("ComplexList1D size mismatch"));

		p = new ComplexArray1D(a.getSize());
		
		for(int i=0; i<p.getSize(); i++)
			p.setElement(i, Complex.sum(a.getElement(i), b.getElement(i)));

		return p;
	}

	// returns the element-by-element product of two ComplexList1Ds
	public static ComplexArray1D product(ComplexList1D a, ComplexList1D b)
	throws SizeMismatchError
	{
		ComplexArray1D p;
		
		if(a.getSize() != b.getSize())
			throw(new SizeMismatchError("ComplexList1D size mismatch"));

		p = new ComplexArray1D(a.getSize());
		
		for(int i=0; i<p.getSize(); i++)
			p.setElement(i, Complex.product(a.getElement(i), b.getElement(i)));

		return p;
	}


	//
	// write data to file
	//

	public void writeToFile(String filename, int writeFromIndex, int writeToIndex)
	throws IOException
	{
	 	writeToFile(filename, this, writeFromIndex, writeToIndex);
	}
	
	public void writeToFile(String filename)
	throws IOException
	{
		writeToFile(filename, this);
	}

	public static void writeToFile(
		String filename, ComplexList1D d,
		int writeFromIndex, int writeToIndex )
	throws IOException
	{
		FileOutputStream fos = new FileOutputStream(filename);
		PrintWriter pw = new PrintWriter(fos);
		
		pw.println("// ComplexList1D");
		pw.println("// format:");
		pw.println("// <size>");
		pw.println("// <element[0].re>\t<element[0].im>");
		pw.println("// <element[1].re>\t<element[1].im>");
		pw.println("// ...");
		pw.println("// <element[size-1].re>\t<element[size-1].im>");
		
		// write length of array...
		pw.println("" + (writeToIndex-writeFromIndex+1));
		
		// ... and the data
		for(int i=writeFromIndex; i<=writeToIndex; i++)
			pw.println("" + d.getElementRe(i) + "\t" + d.getElementIm(i));
			
		pw.close();
	}
	
	public static void writeToFile(String filename, ComplexList1D d)
	throws IOException
	{
		writeToFile(filename, d, 0, d.getSize()-1);
	}

	public static void writeToCSVFile(
			String filename, ComplexList1D d,
			int writeFromIndex, int writeToIndex )
		throws IOException
		{
			FileOutputStream fos = new FileOutputStream(filename);
			PrintWriter pw = new PrintWriter(fos);
			
			// ... and the data
			for(int i=writeFromIndex; i<=writeToIndex; i++)
				pw.println("" + d.getElementRe(i) + ", " + d.getElementIm(i));
				
			pw.close();
		}

	public static void writeToCSVFile(String filename, ComplexList1D d)
	throws IOException
	{
		writeToCSVFile(filename, d, 0, d.getSize()-1);
	}

}