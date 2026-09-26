//-----------------------------------------------------------------------------//
//
//  Needed for calculating the f(alpha) spectrum of a multifractal using 
//  the method of A. Chhabra and R. V. Jensen [1] (see also
//  MultiFractal.java)
//
// References:
// -----------
//   (1) Ashvin Chhabra, Roderick V. Jensen,
//       Physical Review Letters 62, 1327-1330, 1989.
//
//-----------------------------------------------------------------------------//


package library.maths.multiFractal;

import java.lang.Math;

import library.list.*;


////////////////////////////////////////////////////////////////////////
// classes that "wrap" an IntegratedMeasure around a 1-dimensional or //
// 2-dimensional list of data                                         //
////////////////////////////////////////////////////////////////////////

public class IntegratedMeasure1D
implements IntegratedMeasure
{
	// "The long-time behavior of chaotic, noninear dynamical systems can often
	//  be characterized by fractal or multifractal measures which correspond,
	//  for example, to the invariant probability distribution on a strange
	//  attractor, ..." [1]
	// The DoubleList1D <P1> describes this measure.  More exactly, it describes
	// the probabilities (integrated measure) in the i-th box of size L=1.
	private DoubleList1D P1;
	
	// dimensions of the array of boxes of size L=1
	private int width;
	
	// constructors
	
	// store a reference to the array of integrated measures <P1>, which correspond
	// to an array of boxes of size L=1
	public IntegratedMeasure1D(DoubleList1D P1)
	{
		this.P1 = P1;
		
		// store the dimension of the array of boxes of size L=1
		width = P1.getSize();
	}
	
	
	///////////////////////////////
	// IntegratedMeasure methods //
	///////////////////////////////
	
	// return the maximum sensible box size
	public int getLMax()
	{
		return width;
	}

	// return the number of boxes of size <L> (<L> >= 1)
	public int getNMax(int L)
	{
		return (int)Math.ceil((double)width /(double)L);
	}
	
	// return P_<n>(<L>), i.e. the probability (integrated measure) in the <n>th
	// box of size <L>
	public double getP(int n, int L)
	{
		// smallest of all the indices <i> of the small boxes
		// (of size 1) that are part of the <n>th big box of size <L>
		int iMin = L*n;
		
		int i;
		
		// calculate the integrated measure in big box #<n>
		// by summing over all the small boxes within that big box
		double PnL = 0.0;
		
		for(i=iMin; (i<iMin+L) && (i<width); i++)
			PnL += P1.getElement(i);
		
		return PnL;
	}
}
