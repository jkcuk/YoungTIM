/* */

package library.maths;

import library.list.*;

public class BoxCounting
{
	public static int boxCount(DoubleList1D list, double threshold, int boxSize)
	{
		int j;
		int count = 0;
		
		for(int i=0; i<list.getSize(); i+=boxSize)
		{
			for(
				j=0; 
				(j<boxSize) && (i+j<list.getSize()) &&
				(list.getElement(i+j)<threshold);
				j++);
			if((j<boxSize) && (i+j<list.getSize())) count++;
		}
		
		return count;
	}
	
	public static DoubleArray1D boxCount(DoubleList1D function, double threshold)
	{
		return null;
	}
}