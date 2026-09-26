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


public class IntegratedMeasure2D
implements IntegratedMeasure
{
	// "The long-time behavior of chaotic, noninear dynamical systems can often
	//  be characterized by fractal or multifractal measures which correspond,
	//  for example, to the invariant probability distribution on a strange
	//  attractor, ..." [1]
	// The DoubleList2D <P1> describes this measure.  More exactly, it describes
	// the probabilities (integrated measure) in the (i, j)th box of size L=1.
	private DoubleList2D P1;
	
	// dimensions of the array of boxes of size L=1
	private int width, height;
	
	// constructors
	
	// store a reference to the array of integrated measures <P1>, which correspond
	// to an array of boxes of size L=1
	public IntegratedMeasure2D(DoubleList2D P1)
	{
		this.P1 = P1;
		
		// store the dimensions of the array of boxes of size L=1
		width = P1.getSize().width;
		height = P1.getSize().height;
	}
	
	
	///////////////////////////////
	// IntegratedMeasure methods //
	///////////////////////////////
	
	// return the maximum sensible box size
	public int getLMax()
	{
		return (width>height)?width:height;
	}

	// return the number of boxes of size <L> (<L> >= 1)
	public int getNMax(int L)
	{
		return
			(int)Math.ceil((double)width /(double)L) *
			(int)Math.ceil((double)height/(double)L);
	}
	
	// return P_<n>(<L>), i.e. the probability (integrated measure) in the <n>th
	// box of size <L>
	public double getP(int n, int L)
	{
		int
			bigBoxArrayWidth = (int)Math.ceil((double)width /(double)L),
			// row number of the current big box (of size L) in the array of big boxes
			jBigBox = n/bigBoxArrayWidth,
			// smallest of all the indices (i, j) of the small boxes
			// (of size 1) that are part of the <n>th big box of size <L>
			iMin = L*(n-bigBoxArrayWidth*jBigBox),
			jMin = L*jBigBox;
		
		int i, j;
		
		// calculate the integrated measure in big box #<n>
		// by summing over all the small boxes within that big box
		double PnL = 0.0;
		
		for(i=iMin; (i<iMin+L) && (i<width); i++)
			for(j=jMin; (j<jMin+L) && (j<height); j++)
				PnL += P1.getElement(i, j);
		
		return PnL;
	}
}