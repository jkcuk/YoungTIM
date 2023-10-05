/* Fourier transform
   originally punched in from Numerical Recipes in C, 2nd Edition,
   later translated into java

classes or interfaces that this class depends on: none
*/

package library.maths;

public class Fourier
{
	/* Replaces data by its ndim-dimensional discrete Fourier transform, if isign is input as 1.
	   If isign is input as -1, data is replaced by its inverse
	   transform times the product of the lengths of all dimensions

	   isign = +/-1 (for Fourier Transform / inverse Fourier Transform)
		
	   ndim = number of dimensions
	   
	   nn[0..ndim-1] is an integer array containing the lengths of each dimension (number of complex
	   values), which MUST all be powers of 2

	   data is a real array of length twice the product of these lengths, in which the data are 
	   stored as in a multidimensional complex array: real and imaginary parts of each element
	   are in consecutive locations, and the rightmost index of the array increases most rapidly
	   as one proceeds along data. For a two-dimensional array, this is equivalent to storing the
	   array by rows.
	   
	   In the original C version the indices in nn[] and data[] ran from 1; here, more naturally,
	   they run from 0
	*/
	public static void transform(int isign, int ndim, int nn[], double data[])
	{
		int idim;
		int i1, i2, i3, i2rev, i3rev, ip1, ip2, ip3, ifp1, ifp2;
		int ibit, k1, k2, n, nprev, nrem, ntot;
		double tempi, tempr;
		double theta, wi, wpi, wpr, wr, wtemp;
		/* Double precision for trigonometric recurrences */
		
		/* Compute total number of complex values */
		for (ntot = 1, idim = 0; idim < ndim; idim++) ntot *= nn[idim];
		nprev = 1;
		for (idim = ndim-1; idim >= 0; idim--) /* Main loop over the dimensions */
		{
			n = nn[idim];
			nrem = ntot / (n * nprev);
			ip1 = nprev << 1;
			ip2 = ip1 * n;
			ip3 = ip2 * nrem;
			i2rev = 1;
			for (i2 = 1; i2 <= ip2; i2 += ip1)
			/* This is the bit-reversal section of the routine */
			{
				if (i2 < i2rev)
				{
					for (i1 = i2; i1 <= i2 + ip1 - 2; i1 += 2)
					{
						for (i3 = i1; i3 <= ip3; i3 += ip2)
						{
							i3rev = i2rev + i3 - i2;
							swap(i3 - 1, i3rev - 1, data);
							swap(i3, i3rev, data);
						}
					}
				}
				ibit = ip2 >> 1;
				while (ibit >= ip1 && i2rev > ibit)
				{
					i2rev -= ibit;
					ibit >>= 1;
				}
				i2rev += ibit;
			}
			/* Here begins the Danielson-Lanczos section of the routine */
			ifp1 = ip1;
			while (ifp1 < ip2)
			{
				ifp2 = ifp1 << 1;
				/* Initialize for the trig. recurrence */
				theta = isign * 6.28318530717959 / (ifp2 / ip1);
				wtemp = Math.sin(0.5 * theta);
				wpr = -2.0 * wtemp * wtemp;
				wpi = Math.sin(theta);
				wr = 1.0;
				wi = 0.0;
				for (i3 = 1; i3 <= ifp1; i3 += ip1)
				{
					for (i1 = i3; i1 <= i3 + ip1 - 2; i1 += 2)
					{
						for (i2 = i1; i2 <= ip3; i2 += ifp2)
						{
							k1 = i2; /* Danielson-Lanczos formula: */
							k2 = k1 + ifp1;
							tempr = wr * data[k2 - 1] - wi * data[k2];
							tempi = wr * data[k2] + wi * data[k2 - 1];
							data[k2 - 1] = data[k1 - 1] - tempr;
							data[k2] = data[k1] - tempi;
							data[k1 - 1] += tempr;
							data[k1] += tempi;
						}
					}
					wr = (wtemp = wr) * wpr - wi * wpi + wr; /* Trigonometric recurrence */
					wi = wi * wpr + wtemp * wpi + wi;
				}
				ifp1 = ifp2;
			}
			nprev *= n;
		}
	}

	// swaps the numbers with indices index1 and index2 in the array data[]
	private static void swap(int index1, int index2, double data[])
	{
		double temp = data[index1];
		data[index1] = data[index2];
		data[index2] = temp;
	}
}