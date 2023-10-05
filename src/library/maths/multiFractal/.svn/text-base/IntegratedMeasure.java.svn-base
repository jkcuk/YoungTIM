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



// "The long-time behavior of chaotic, noninear dynamical systems can often
//  be characterized by fractal or multifractal measures which correspond,
//  for example, to the invariant probability distribution on a strange
//  attractor [...]  If we cover the support of the measure with boxes of
//  size L and define P_i(L) as the probability (integrated measure) in the
//  ith box [then P_i(L) provides sufficient information to characterise the
//  measure usefully]" [1]
// Using the interface <IntegratedMeasure>, the values P_<n>(<L>)
// [<i> is called <n> here!] for any measure can be passed around.
//
// See also:
//   IntegratedMeasure2D
public interface IntegratedMeasure
{
	// return the maximum sensible box size
	public int getLMax();
	
	// return the number of boxes of size <L> (<L> >= 1)
	public int getNMax(int L);
	
	// return P_<n>(<L>), i.e. the probability (integrated measure) in the <n>th
	// box of size <L>
	public double getP(int n, int L);
}
