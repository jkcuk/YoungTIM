/* */


package library.list;


import java.io.*;

import library.maths.*;
import library.util.*;


///////////////////////
// DoubleList1DClass //
///////////////////////

// abstract implementation of the DoubleList1D interface that
// makes a number of useful (static and non-static) methods available

public abstract class DoubleList1DClass
implements DoubleList1D
{
	//
	// DoubleList1D methods
	//
	
	public abstract int getSize();
	public abstract double getElement(int i);
	
	
	//
	// additional access to elements
	//
	
	// returns a DoubleArray1D with the elements corresponding to the range
	// of indices i1, i1+1, ..., i2-1, i2
	public DoubleArray1D getElementRange(int i1, int i2)
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
		
		// create a new DoubleArray1D, ...
		DoubleArray1D a = new DoubleArray1D(iMax - iMin + 1);
		
		// ... fill it with the desired elements, ...
		for(int i=0; i<iMax-iMin+1; i++) a.setElement(i, getElement(iMin+i));
		
		// ... and return it
		return a;
	}

	// return the smallest element
	public double getMin()
	{
		double min = getElement(0);
		
		for(int i=1; i<getSize(); i++)
			if(getElement(i) < min) min = getElement(i);
		
		return min;
	}
	
	// return the largest element
	public double getMax()
	{
		double max = getElement(0);
		
		for(int i=1; i<getSize(); i++)
			if(getElement(i) > max) max = getElement(i);
		
		return max;
	}

	
	//
	// element-by-element arithmetic operations with two DoubleList1Ds
	//

	// returns the element-by-element sum of two DoubleList1Ds
	public static DoubleArray1D sum(DoubleList1D a, DoubleList1D b)
	throws SizeMismatchError
	{
		DoubleArray1D p;
		
		if(a.getSize() == b.getSize())
		{
			p = new DoubleArray1D(a.getSize());
			
			for(int i=0; i<p.getSize(); i++)
				p.setElement(i, a.getElement(i) + b.getElement(i));
		}
		else
			throw(new SizeMismatchError("DoubleList1D length mismatch."));
		
		return p;
	}

	// returns the element-by-element product of two DoubleList1Ds
	public static DoubleArray1D product(DoubleList1D a, DoubleList1D b)
	throws SizeMismatchError
	{
		DoubleArray1D p;
		
		if(a.getSize() == b.getSize())
		{
			p = new DoubleArray1D(a.getSize());
			
			for(int i=0; i<p.getSize(); i++)
				p.setElement(i, a.getElement(i) * b.getElement(i));
		}
		else
			throw(new SizeMismatchError("DoubleList1D length mismatch."));
		
		return p;
	}


	//
	// other
	//
	
	// returns an array with elements exp(i*<element of argument <e>>)
	public ComplexArray1D expI()
	{
		ComplexArray1D a;
		
		a = new ComplexArray1D(getSize());
			
		for(int i=0; i<getSize(); i++)
			a.setElement(i, Complex.expI(getElement(i)));

		return a;
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
		String filename, DoubleList1D d,
		int writeFromIndex, int writeToIndex )
	throws IOException
	{
		FileOutputStream fos = new FileOutputStream(filename);
		PrintWriter pw = new PrintWriter(fos);
		for(int i=writeFromIndex; i<=writeToIndex; i++)
			pw.println(d.getElement(i));
		pw.close();
		fos.close(); // necessary?
	}
	
	public static void writeToFile(String filename, DoubleList1D d)
	throws IOException
	{
		writeToFile(filename, d, 0, d.getSize()-1);
	}
	
	
	//
	// toString method
	//
	
	public String toString()
	{
		String s = "{";
		
		for(int i=0; i<getSize(); i++)
			s += getElement(i) + (i+1<getSize()?", ":"");
		
		return s + "}";
	}
}