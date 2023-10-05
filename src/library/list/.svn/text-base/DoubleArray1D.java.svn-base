/* */


package library.list;


import java.io.*;

import library.util.*;


///////////////////
// DoubleArray1D //
///////////////////

// implementation of DoubleList1D
// extension of DoubleList1DClass

public class DoubleArray1D extends DoubleList1DClass
implements Serializable
{
	private static final long serialVersionUID = -1890514852723724571L;
	
	private int size;
	private double element[];
	
	
	//
	// constructors
	//
	
	// this constructor simply reserves space for <size> real numbers, which
	// can be given values using the method setElement(int i, double c)
	public DoubleArray1D(int size)
	{
		this.size = size;
		
		// allocate memory
		element = new double[size];
	}
	
	// this constructor COPIES the elements of the DoubleList1D d
	public DoubleArray1D(DoubleList1D d)
	{
		size = d.getSize();
		element = new double[size];
		
		for(int i=0; i<size; i++)
			element[i] = d.getElement(i);
	}
	
	// this constructor "wraps" a DoubleArray1D around the array d
	public DoubleArray1D(double d[])
	{
		size = d.length;
		element = d;
	}

	
	//
	// DoubleList1D methods (DoubleList1DClass abstract methods)
	//
	
	public int getSize()
	{
		return size;
	}
	
	public double getElement(int i)
	{
		return element[i];
	}
	

	//
	// data entry
	//
	
	public void setElement(int i, double d)
	{
		element[i] = d;
	}
	

	//
	// element-by-element arithmetic methods
	//
	
	// addition

	// adds <d> to every element
	public void add(double d)
	{
		for(int i=0; i<size; i++) element[i] += d;
	}
	
	// adds (element by element) <d> to data
	public void add(DoubleList1D d)
	throws SizeMismatchError
	{
		if(size == d.getSize())
			for(int i=0; i<size; i++) element[i] += d.getElement(i);
		else
			throw(new SizeMismatchError("DoubleList1D length mismatch."));
	}
	
	// multiplication
	
	// multiplies every element of the array by <d>
	public void multiply(double d)
	{
		for(int i=0; i<size; i++) element[i] *= d;
	}

	// square every member of the array
	public void sqr()
	{
		for(int i=0; i<size; i++) element[i] *= element[i];
	}

	
	//
	// mod and unMod
	//
	
	// this method attempts to reverse the application of the mod operation "% <m>"
	// to every element of the real array
	public void unMod(double m, int i0)
	{
		double previous, delta;
		
		// start with i0 and work your way to the lowest index
		delta = 0.0;
		previous = element[i0];
		for(int i=i0-1; i>=0; i--)
		{

			if( Math.abs(element[i] + m - previous) <
			    Math.abs(element[i]     - previous) ) delta += m;
			else
			if( Math.abs(element[i] - m - previous) <
			    Math.abs(element[i]     - previous) ) delta -= m;

			previous = element[i];
			element[i] += delta;
		}

		// start with i0 and work your way to the highest index
		delta = 0.0;
		previous = element[i0];
		for(int i=i0+1; i<size; i++)
		{

			if( Math.abs(element[i] + m - previous) <
			    Math.abs(element[i]     - previous) ) delta += m;
			else
			if( Math.abs(element[i] - m - previous) <
			    Math.abs(element[i]     - previous) ) delta -= m;

			previous = element[i];
			element[i] += delta;
		}
	}
	
	public void mod(double m)
	{
		for(int i=0; i<size; i++) element[i] %= m;
	}
}