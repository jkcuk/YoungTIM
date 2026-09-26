/* */


package library.list;


import java.awt.*;

import library.util.*;


///////////////////////
// DoubleList2DClass //
///////////////////////

// abstract implementation of the DoubleList2D interface that
// makes a number of useful (static and non-static) methods available

public abstract class DoubleList2DClass
implements DoubleList2D
{
	//
	// DoubleList2D methods
	//
	
	public abstract Dimension getSize();
	public abstract double getElement(int i, int j);
	
	
	//
	// additional access to elements
	//
	
	// return the smallest element
	public double getMin()
	{
		int 
			width = getSize().width,
			height = getSize().height;

		double min = getElement(0, 0);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				if(getElement(i, j) < min) min = getElement(i, j);
		
		return min;
	}
	
	// return the largest element
	public double getMax()
	{
		int 
			width = getSize().width,
			height = getSize().height;

		double max = getElement(0, 0);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				if(getElement(i, j) > max) max = getElement(i, j);
		
		return max;
	}

	
	//
	// element-by-element arithmetic operations with two DoubleList2Ds
	//

	// returns the element-by-element sum of two DoubleList2Ds
	public static DoubleArray2D sum(DoubleList2D a, DoubleList2D b)
	throws SizeMismatchError
	{
		if(a.getSize() != b.getSize())
			throw(new SizeMismatchError("DoubleList2D size mismatch"));

		int 
			width = a.getSize().width,
			height = a.getSize().height;
		
		DoubleArray2D c = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				c.setElement(i, j, a.getElement(i, j) + b.getElement(i, j));
		
		return c;
	}

	// returns the element-by-element product of two DoubleList2Ds
	public static DoubleArray2D product(DoubleList2D a, DoubleList2D b)
	throws SizeMismatchError
	{
		if(a.getSize() != b.getSize())
			throw(new SizeMismatchError("DoubleList2D size mismatch"));

		int 
			width = a.getSize().width,
			height = a.getSize().height;
		
		DoubleArray2D c = new DoubleArray2D(width, height);
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				c.setElement(i, j, a.getElement(i, j) * b.getElement(i, j));
		
		return c;
	}
}