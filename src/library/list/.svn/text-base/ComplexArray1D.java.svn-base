/* */


package library.list;


import java.io.*;
import java.util.*;

import library.maths.*;



////////////////////
// ComplexArray1D //
////////////////////

// implementation of ComplexList1D
// extension of ComplexList1DClass


public class ComplexArray1D extends ComplexList1DClass
implements Serializable
{
	private static final long serialVersionUID = -5240959862485955410L;
	
	private int size;
	private double data[]; // more efficient than Complex data[]; also allows Fourier transform
	
	
	//
	// constructors
	//
	
	// this constructor simply reserves space for <size> complex numbers, which
	// can be given values using the method setElement(int i, Complex c)
	public ComplexArray1D(int size)
	{
		this.size = size;
		
		// allocate memory
		data = new double[size << 1];
	}
	
	// this constructor COPIES the ComplexList1D <t> into its
	// private array
	public ComplexArray1D(ComplexList1D t)
	{
		size = t.getSize();
		data = new double[size << 1];
		
		for(int i=0; i<size; i++)
		{
			data[ i<<1     ] = t.getElement(i).re;
			data[(i<<1) + 1] = t.getElement(i).im;
		}
	}
	
	// this constructor COPIES the DoubleList1D <t> into its
	// private array
	public ComplexArray1D(DoubleList1D t)
	{
		size = t.getSize();
		data = new double[size << 1];
		
		for(int i=0; i<size; i++)
		{
			data[ i<<1     ] = t.getElement(i);
			data[(i<<1) + 1] = 0;
		}
	}
	
	// this constructor stores a REFERENCE to the data  and interprets
	// them as alternating real and imaginary parts
	public ComplexArray1D(double data[])
	{
		size = data.length >> 1;
		this.data = data;
	}
	
	// this constructor reads the data in the file with name <filename>	
	public ComplexArray1D(String filename)
	throws IOException, NumberFormatException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		LineNumberReader lnr = new LineNumberReader(br);

		String s;
		
		// read length of array
		while((s = lnr.readLine()).startsWith("//")); // read the next line that is not a comment
		size = (new Integer(s)).intValue();
		
		// allocate memory
		data = new double[size << 1];
		
		try
		{
			// read the data
			for(int i=0; i<size; i++)
			{
				// read the next line that is not a comment
				while((s = lnr.readLine()).startsWith("//"));
				
				StringTokenizer st = new StringTokenizer(s);
				data[(i<<1)  ] = (new Double(st.nextToken())).doubleValue();
				data[(i<<1)+1] = (new Double(st.nextToken())).doubleValue();
			}
		}
		catch(NoSuchElementException e)
		{
			throw(new NumberFormatException("wrong file format"));
		}
			
		br.close();
	}
	

	//////////////////////////////////////////////////////
	// methods required by the ComplexList1D interface //
	//////////////////////////////////////////////////////
	
	public int getSize()
	{
		return size;
	}
	
	public Complex getElement(int i)
	{
		return new Complex(data[i<<1], data[(i<<1)+1]);
	}
	
	public double getElementRe(int i)
	{
		return data[i<<1];
	}
	
	public double getElementIm(int i)
	{
		return data[(i<<1)+1];
	}
	
	
	//
	// data entry
	//
	
	public void setElement(int i, Complex c)
	{
		data[ i<<1   ] = c.re;
		data[(i<<1)+1] = c.im;
	}
	
	public void setElement(int i, double re, double im)
	{
		data[ i<<1   ] = re;
		data[(i<<1)+1] = im;
	}
	
	public void setElementRe(int i, double re)
	{
		data[ i<<1   ] = re;
	}
	
	public void setElementIm(int i, double im)
	{
		data[(i<<1)+1] = im;
	}
	
	
	//
	// complex-specific array transformations
	//
	
	// set all the imaginary parts to zero
	public void re()
	{
		for(int i=(size<<1)+1; i>0; i-=2) // go through all the imaginary parts...
			data[i] = 0; // ... and set them to zero
	}
	
	// set all the real parts to zero
	public void im()
	{
		for(int i=(size<<1); i>=0; i-=2) // go through all the real parts...
			data[i] = 0; // ... and set them to zero
	}
	
	public void abs()
	{
		for(int i=0; i<size; i++) // go through all the elements...
			setElement(i, getElement(i).getAbs(), 0); // ... and replace them by their absolute value
	}
	
	public void absSqr()
	{
		for(int i=0; i<size; i++) // go through all the elements...
			setElement(i, getElement(i).getAbsSqr(), 0); // ... and replace them by the square of their abs
	}
	
	public void arg()
	{
		for(int i=0; i<size; i++) // go through all the elements...
			setElement(i, getElement(i).getArg(), 0); // ... and replace them by their argument
	}
	
	public void conjugate()
	{
		for(int i=0; i<size; i++) // go through all the elements...
			setElement(i, getElement(i).getConjugate()); // ... and replace them by their complex conjugate
	}
			
	
	//
	// Fourier transformation
	//
	
	// isign = +1 : Fourier Transform
	// isign = -1 : inverse Fourier Transform
	
	// Fourier transform the array
	public void FT(int isign)
	{
		// number of matrix elements in the two dimensions; for use by Fourier.transform
		int nn[] = {size};

		// Fourier transform the amplitude matrix
		Fourier.transform(isign,	// +1 means do a Fourier transform, -1 an inverse FT
			1,	// number of dimensions
			nn,	// number of elements in those dimensions
			data );
	}
		

	//
	// element-by-element arithmetic methods
	//
	
	// adds <c> to every element of the array
	public void add(Complex c)
	{
		for(int i=0; i<size; i++) setElement(i, Complex.sum(getElement(i), c));
	}

	// multiplies every element of the array by <d>
	public void multiply(Complex c)
	{
		for(int i=0; i<size; i++) setElement(i, Complex.product(getElement(i), c));
	}
}