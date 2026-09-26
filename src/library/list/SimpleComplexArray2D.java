/* */


package library.list;


import java.awt.*;
import java.io.*;

import library.maths.*;


public class SimpleComplexArray2D extends ComplexList2DClass
implements Serializable
{
	private static final long serialVersionUID = -6687602386901939263L;
	
	private int width, height;
	private Complex data[][];
	
	// this constructor simply reserves space for <width> * <height> complex numbers, which
	// can be given values using the method setElement(int i, int j, Complex c)
	public SimpleComplexArray2D(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		// allocate memory
		data = new Complex[width][height];
	}
	
	public SimpleComplexArray2D(Dimension size)
	{
		width = size.width;
		height = size.height;
		
		// allocate memory
		data = new Complex[width][height];
	}
	
	// this constructor COPIES the ComplexList1D d into its
	// private array
	public SimpleComplexArray2D(ComplexList2D d)
	{
		width = d.getSize().width;
		height = d.getSize().height;
		data = new Complex[width][height];
		
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++)
				data[i][j] = new Complex(d.getElement(i, j));
	}
	
	public Dimension getSize()
	{
		return new Dimension(width, height);
	}
	
	public Complex getElement(int i, int j)
	{
		return data[i][j];
	}
	
	public double getElementRe(int i, int j)
	{
		return data[i][j].re;
	}
	
	public double getElementIm(int i, int j)
	{
		return data[i][j].im;
	}
	
	public void setElement(int i, int j, Complex value)
	{
		data[i][j] = value;
	}
	
	
	//
	// additional data access
	//
	
	public void setRow(int j, Complex row[])
	{
		// the array rowValues has to be of length width
		for(int i=0; i<width; i++)
			setElement(i, j, row[i]);
	}
	
	public void setRow(int j, ComplexList1D row)
	{
		// the array rowValues has to be of length width
		for(int i=0; i<width; i++)
			setElement(i, j, row.getElement(i));
	}
	
	public void setColumn(int i, Complex column[])
	{
		// the array columnValues has to be of length height
		for(int j=0; j<height; j++)
			setElement(i, j, column[j]);
	}

	public void setColumn(int i, ComplexList1D column)
	{
		// the array columnValues has to be of length height
		for(int j=0; j<height; j++)
			setElement(i, j, column.getElement(j));
	}
}