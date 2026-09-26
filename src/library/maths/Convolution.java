/* Convolution

classes or interfaces that this class depends on: none
*/

package library.maths;

import library.list.*;

public class Convolution
{
	public static DoubleArray1D convolve(DoubleList1D function, DoubleList1D filter)
	{
		int i, k;
		double s;
		
		DoubleArray1D convolution = new DoubleArray1D(function.getSize()-filter.getSize()+1);
		
		for(i=0; i<convolution.getSize(); i++)
		{
			s = 0;
			for(k=0; k<filter.getSize(); k++)
				s += filter.getElement(k) * function.getElement(k+i);
			convolution.setElement(i, s);
		}
		
		return convolution;
	}
}