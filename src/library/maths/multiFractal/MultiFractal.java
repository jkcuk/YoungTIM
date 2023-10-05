//
// MultiFractal
// 
// Adapted from the Matpack package "falpha.cc" (written in C++) by Berndt M. Gammel
// 

//-----------------------------------------------------------------------------//
//
//  Method: (this description is copied straight out of the Matpack file "falpha.cc")
//  -------
//  Calculate the f(alpha) spectrum of a multifractal using 
//  the method of A. Chhabra and R. V. Jensen [1].
//
//  Calculate normalized q-measure for P by
//
//                                  q
//                          P_i (L) 
//  (1)   mu_i (q,L)  =  ---------------------
//                                        q
//                       Sum(k)[  P_k (L)   ]
//      
//  Calculate f(q,L) by
//
//                  Sum(i)[ mu_i (q,L) ln( mu_i (q,L) ) ]
//  (2)   f(q,L) = ---------------------------------------
//                               ln( L )
//
//  Calculate alpha(q,L) by
//
//                      Sum(i)[ mu_i (q,L) ln( P_i(q,L) ) ]
//  (3)   alpha(q,L) = -------------------------------------
//                                   ln( L )
//
//
//
// References:
// -----------
//   (1) Ashvin Chhabra, Roderick V. Jensen,
//       Physical Review Letters 62, 1327-1330, 1989.
//
//-----------------------------------------------------------------------------//


package library.maths.multiFractal;

import java.lang.Math;

import library.maths.*;


public class MultiFractal
{
	// these arrays hold the coordinates of points on the multifractal spectrum
	// curve f(alpha) and the errors associated with these coordinates 
	public double
		alpha[],
		sigmaAlpha[],
		f[],
		sigmaF[];
	
	private IntegratedMeasure im;
	
	
	/////////////////
	// constructor //
	/////////////////
	
	// "The long-time behavior of chaotic, noninear dynamical systems can often
	//  be characterized by fractal or multifractal measures which correspond,
	//  for example, to the invariant probability distribution on a strange
	//  attractor [...]  If we cover the support of the measure with boxes of
	//  size L and define P_i(L) as the probability (integrated measure) in the
	//  ith box, ..." [1]
	// <im> is an implementation of the IntegratedMeasure interface; as such it
	// can return P_<i>(<L>) for all sensible values of <i> and <L>.
	public MultiFractal(IntegratedMeasure im)
	{
		// store a reference to <im>
		this.im = im;
		
		// calculate the multifractal spectrum f(alpha)
		double
			qMin = -30,
			qMax = 30,
			qDelta = 0.5;
		
		System.out.println(
			"(* output format: { " +
			"{q, alpha, sigma_alpha, f, sigma_f}_1, " +
			"{q, alpha, sigma_alpha, f, sigma_f}_2, ... } *)"
		);
		System.out.println("{");
		for(double q=qMin; q<=qMax; q+=qDelta)
		{
			MeanSigma
				alpha = getAlpha(q),
				f = getF(q);
			
			System.out.print(
				"{" + (float)q + ", " + (float)alpha.mean + ", " + (float)alpha.sigma +
				", " + (float)f.mean + ", " + (float)f.sigma + "}");
			
			System.out.println((q+qDelta <= qMax)?",":"");
		}
		System.out.println("}");
	}



	
	// This method returns an array containing the values P_n(<L>) for
	// n=1, ..., im.getNMax(<L>), i.e. the integrated measures for all boxes of size <L>
	private double[] getP_n(int L)
	{
		int nMax = im.getNMax(L);
		
		// allocate memory for the array
		double P[] = new double[nMax];
		
		for(int n=0; n<nMax; n++)
			P[n] = im.getP(n, L);
		
		// for debugging purposes
		// System.out.println("P_n(" + L + ") = " + new DoubleArray1D(P));
		
		return P;
	}
	
	// This method returns an array containing the values mu_n(<q>, <L>) for
	// n=1, ..., im.getNMax(<L>), i.e. the "normalized measures" which,
	// in terms of the integrated measures P_n(L), are defined as
	//   mu_n(q,L) = [P_n(L)]^q / Sum_j [P_j(L)]^q
	// (equation (6) in ref. [1])
	// The argument P[] should be calculated using the method getP_n(L).
	private double[] getMu_n(double q, double P[])
	{
		// how many big boxes are there?
		int nMax = P.length;
		
		// allocate memory for an array to perform the calculations in
		double d[] = new double[nMax];
		
		// raise all the (P_n)s to the power <q>, store them in <d>,
		// and calculate the sum of the resulting numbers
		double sum = 0.0;
		for(int n=0; n<nMax; n++)
		{
			if(P[n] != 0.0) d[n] = Math.pow(P[n], q);
			else d[n] = 0.0;
			// System.out.println("P[" + n + "] = " + P[n] + ", d[" + n + "] = " + d[n]);
			sum += d[n];
		}
		
		// System.out.println("sum = " + sum);
		
		// divide every P_n(L)^q by the sum of all the P_n(L)^q
		if(sum != 0.0)
			for(int n=0; n<nMax; n++)
			{
				d[n] /= sum;
				// System.out.println("d[" + n + "] = " + d[n]);
			}
		
		// for debugging purposes
		// System.out.println("mu_n(" + q + ", P) = " + new DoubleArray1D(d));

		return d;
	}
	
	// calculate the numerator of f(q) (the entropy)
	// (equation (7) in [1])
	// The argument mu[] should be calculated using the method getMu_n(q, P[]).
	private double getEntropy(double mu[])
	{
		double S = 0.0;
		
		for(int n=0; n<mu.length; n++)
			if(mu[n] > 0) S += mu[n] * Math.log(mu[n]);
		
		// for debugging purposes
		// System.out.println("entropy(" + new DoubleArray1D(mu) + ") = " + S);
		
		return S;
	}
	
	private MeanSigma getF(double q)
	{
		// how many different Ls are there?
		int mMax = 0;
		for(int L=1; L<=im.getLMax(); L*=2) mMax++;
		
		// allocate memory for the arrays holding the entropies and log(L) for
		// different Ls
		double entropy[] = new double[mMax];
		double logL[] = new double[mMax];
		
		// calculate the entropies and log(L)s
		int m=0;
		for(int L=1; L<=im.getLMax(); L*=2)
		{
			entropy[m] = getEntropy(getMu_n(q, getP_n(L)));
			logL[m] = Math.log((double)L);
			m++;
		}
		
		// least-squares-fit a straight line to the entropy as a function of log(L)
		LinearRegression lr = new LinearRegression(logL, entropy);
		
		return new MeanSigma(lr.slope, lr.sigmaSlope);
	}

	// calculate the numerator of alpha(q) (the mean singularity strength)
	// (equation (8) in [1])
	// The argument P[] should be calculated using the method getP_n(L),
	// mu[] should be calculated using the method getMu_n(q, P[]).
	private double getSingularity(double P[], double mu[])
	{
		double S = 0.0;
		
		for(int n=0; n<mu.length; n++)
		{
			// System.out.println("n = " + n + ", mu[n] = " + mu[n] + ", P[n] = " + P[n]);
			if(P[n] > 0) S += mu[n] * Math.log(P[n]);
		}
		
		// for debugging purposes
		// System.out.println("singularity(P_n,mu_n) = " + S);
		
		return S;
	}
	
	private MeanSigma getAlpha(double q)
	{
		// how many different Ls are there?
		int mMax = 0;
		for(int L=1; L<=im.getLMax(); L*=2) mMax++;
		
		// allocate memory for the arrays holding the singularity strengths and log(L) for
		// different Ls
		double ss[] = new double[mMax];
		double logL[] = new double[mMax];
		
		// calculate the singularity strengths and log(L)s
		int m=0;
		for(int L=1; L<=im.getLMax(); L*=2)
		{
			double P[] = getP_n(L);
			ss[m] = getSingularity(P, getMu_n(q, P));
			logL[m] = Math.log((double)L);
			// System.out.println("m = " + m + ", ss[m] = " + ss[m] + ", logL[m] = " + logL[m]);
			m++;
		}
		
		// least-squares-fit a straight line to the entropy as a function of log(L)
		LinearRegression lr = new LinearRegression(logL, ss);
		
		return new MeanSigma(lr.slope, lr.sigmaSlope);
	}
}