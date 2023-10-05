/* */


package library.list;


import library.maths.*;


///////////////////
// ComplexList1D //
///////////////////

// defines objects that can return a complex number corresponding to a
// range of indices

public interface ComplexList1D
{
	// method that returns the size (in elements) of the table
	public int getSize();
	
	// method that returns the value at index i;
	// indices run between 0 and getSize()-1
	public Complex getElement(int i);
	
	// methods that return the real and imaginary parts of the element
	// corresponding to index i
	public double getElementRe(int i);
	public double getElementIm(int i);
}