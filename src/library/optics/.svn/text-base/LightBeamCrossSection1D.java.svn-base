/* WaveTrace1D Version 1.0
   adapted from WaveTrace


version history:

	1.0	adapted for 1-dimensional beam cross sections from WaveTrace.java version 1.1


public classes:

  public class DiscretePhaseList1D implements DoubleList1D

  public class DiscreteIntensityList implements DoubleList1D

  public class LightBeamCrossSection1D implements ComplexList1D


example:

*/

package library.optics;

import java.awt.*;
import java.io.*;

import library.list.*;
import library.maths.*;
import library.util.*;


public class LightBeamCrossSection1D
implements ComplexList1D, Serializable
{
	private static final long serialVersionUID = 8019999151786542634L;
	
	//////////
	// data //
	//////////

	/* amplitude array, stored in a form so that it can be passed to Fourier:
	   a real array of length twice the product of width and height, in which the data are 
	   stored such that real and imaginary parts of each element
	   are in consecutive locations
	*/
	private double amplitudes[];
	
	// size of the amplitude array ...
	private int width;
	
	// ... and the physical size it represents
	private double physicalWidth;	// in meters
	
	// wavelength of the light represented (monochromatic!)
	private double wavelength;	// in meters

	// program will output a warning message when it detects that some of the
	// elements in the amplitude matrix represent evanescent components of the beam
	private transient boolean evanescentComponentsWarningShown = false;
	

	/////////////////
	// constructor //
	/////////////////
	
	public LightBeamCrossSection1D(
		int width, double physicalWidth, double wavelength )
	{
		// store length of the amplitude array
		this.width = width;
				
		// store physicalWidth
		this.physicalWidth = physicalWidth;
		
		// store wavelength
		this.wavelength = wavelength;
		
		// allocate memory for data
		amplitudes = new double[width<<1];
	}
	
	// make a copy of the LightBeamCrossSection1D l
	public LightBeamCrossSection1D(LightBeamCrossSection1D l)
	{
		width = l.width;
		physicalWidth = l.physicalWidth;
		wavelength = l.wavelength;
		amplitudes = new double[width<<1];
		for(int x=0; x<width; x++)
			setAmplitude(x, l.getAmplitude(x));
	}
	
	
	/////////////////////////////////////////////////////
	// methods defined by the ComplexList1D interface  //
	/////////////////////////////////////////////////////

	public int getSize()
	{
		return width;
	}

	public Complex getElement(int i)
	{
		return getAmplitude(i);
	}
	
	public double getElementRe(int i)
	{
		return amplitudes[ i<<1   ];
	}
	
	public double getElementIm(int i)
	{
		return amplitudes[(i<<1)+1];
	}


	////////////////////////////////////
	// methods for accessing the data //
	////////////////////////////////////
	
	public void setAmplitude(int i, Complex amplitude)
	{
		amplitudes[getIndexRe(i)] = amplitude.re;
		amplitudes[getIndexIm(i)] = amplitude.im;
	}
	
	public void setAmplitude(int i, double amplitude)
	{
		amplitudes[getIndexRe(i)] = amplitude;
		amplitudes[getIndexIm(i)] = 0;
	}
	
	public Complex getAmplitude(int i)
	{
		return new Complex(amplitudes[getIndexRe(i)], amplitudes[getIndexIm(i)]);
	}
	
	public double getPhase(int i)
	{
		return Complex.arg(getAmplitude(i));
	}
	
	public double getIntensity(int i)
	{
		// sum of the squares of the real and imaginary parts of the amplitude
		return MyMath.sqr(amplitudes[getIndexRe(i)]) +
			   MyMath.sqr(amplitudes[getIndexIm(i)]);
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public double getPhysicalWidth()
	{
		return physicalWidth;
	}
	
	public double getWavelength()
	{
		return wavelength;
	}
	
	// calculate the physical coordinate corresponding to index i in the amplitude matrix
	public double getPhysicalPosition(int i)
	{
		// return (physicalWidth / (width-1) * (i - (width-1)/2.0));
		return (physicalWidth / (width-1) * (i - (width>>1) + 0.5));
	}
	
	// calculate the index corresponding to the physical position x
	public double getIndex(double x)
	{
		return (x/physicalWidth * (width-1) + (width>>1) - 0.5);
	}
	
	
	//
	// exporting to disk
	//
	
	public void saveDataInTextFormat()
	{
		Frame f = new Frame();
		FileDialog fd = new FileDialog(f, "", FileDialog.SAVE);
			
		// save
		try
		{
			fd.setVisible(true);
			
			if(fd.getFile() != null)
			{
				ComplexList1DClass.writeToFile(fd.getDirectory() + fd.getFile(), this);
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception during saving: " + e);
			// System.exit(0);
		}
		finally
		{
			// free system resources associated with frame and dialog
			f.dispose();
			fd.dispose();
		}
	}
	
	public void saveDataInCSVFormat()
	{
		Frame f = new Frame();
		FileDialog fd = new FileDialog(f, "Save as CSV...", FileDialog.SAVE);
			
		// save
		try
		{
			fd.setVisible(true);
			
			if(fd.getFile() != null)
			{
				ComplexList1DClass.writeToCSVFile(fd.getDirectory() + fd.getFile(), this);
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception during saving: " + e);
			// System.exit(0);
		}
		finally
		{
			// free system resources associated with frame and dialog
			f.dispose();
			fd.dispose();
		}
	}
	
	
	//////////////////////////////////////////////////
	// method that simulates free-space propagation //
	//////////////////////////////////////////////////
	
	/* this function models propagation of an amplitude matrix from one plane
	   to another, parallel plane;
	   it replaces amplitudes by the amplitude array corresponding to a plane
	   a distance deltaZ (in meters) behind the original plane
	*/
	public void propagate(double deltaZ)
	{
		double
			kZ, kzdeltaz, coskzdeltaz, sinkzdeltaz, expikzdeltaz,
			re, im, powerFactor;
  
		// 1. Fourier transform the amplitude matrix
		FT(+1);	// +1 means do a Fourier transform, not an inverse FT
  
		// 2. multiply each element with exp(i kz deltaZ)
		// this is also a good time to divide each element by
		// width * height, such that power is conserved!
  
		powerFactor = 1.0/(width);
  
		for(int i = 0; i < width; i++)
		{
			kZ = getWaveVectorZ(i);
			if(kZ >= 0)
			{
				// Fourier component i represents a 'normal' wave

				kzdeltaz = kZ * deltaZ;
				coskzdeltaz = Math.cos(kzdeltaz);
				sinkzdeltaz = Math.sin(kzdeltaz);
      
				re = amplitudes[getIndexRe(i)];
				im = amplitudes[getIndexIm(i)];
	      
				// real part of (re + i im) exp(i kz*deltaZ)
				amplitudes[getIndexRe(i)] =
					powerFactor * (re * coskzdeltaz - im * sinkzdeltaz);

				// imaginary part of (re + i im) exp(i kz*deltaZ)
				amplitudes[getIndexIm(i)] =
					powerFactor * (re * sinkzdeltaz + im * coskzdeltaz);
			}
			else
			{
				// Fourier component i represents evanescent wave;
				// this was indicated by waveVectorZ returning a
				// negative number, which is the negative value of
				// the imaginary part of the wave number in the z
				// direction
				expikzdeltaz = Math.exp(-Math.abs(kZ) * deltaZ);
				// for purely imaginary values of kz, the argument
				// in the exponential function in the factor
				// exp(i kz * deltaZ) becomes real and the factor
				// becomes just an exponential function
				amplitudes[getIndexRe(i)] *= expikzdeltaz * powerFactor;
				amplitudes[getIndexIm(i)] *= expikzdeltaz * powerFactor;
			}
		}
  
		// 3. inverse Fourier transform the whole lot
		FT(-1);	// 1 means do an INVERSE Fourier transform
	}
	

	public void FT(int isign)
	{
		// number of matrix elements in the two dimensions; for use by Fourier.transform
		int nn[] = {width};

		// Fourier transform the amplitude matrix
		Fourier.transform(isign,	// +1 means do a Fourier transform, -1 an inverse FT
			1,	// number of dimensions
			nn,	// number of elements in those dimensions
			amplitudes );
	}


	////////////////////////////////////////////////////
	// method that models passage through a thin lens //
	////////////////////////////////////////////////////
	
	// lens of focal length f
	public void passThroughLens(double f)
	{
		double phaseShift, cosPhaseShift, sinPhaseShift, re, im;
  
		for(int i = 0; i < width; i++)
		{
			// locally shift the phase of the amplitude cross-section
			phaseShift = getLensPhase(f, getPhysicalPosition(i));
			cosPhaseShift = Math.cos(phaseShift);
			sinPhaseShift = Math.sin(phaseShift);
			re = amplitudes[getIndexRe(i)];
			im = amplitudes[getIndexIm(i)];
	  	
	  		// real part of (re + i im) exp(i phaseShift)
			amplitudes[getIndexRe(i)] = re * cosPhaseShift - im * sinPhaseShift;
			// imaginary part of (re + i im) exp(i phaseShift)
			amplitudes[getIndexIm(i)] = re * sinPhaseShift + im * cosPhaseShift;
		}
	}

	// needed by passThroughLens:
	// phase introduced by a thin lens of focal length f, a distance
	// r = x from the optic axis
	private double getLensPhase(double f, double x)
	{
		return -(2*Math.PI/wavelength) * x*x / (2*f);
	}
	
	// returns an instance of an implementation of XList that describes the phase
	// distribution of a lens with centre at centreX
	public ComplexArray1D getLensHologram(double f, double centreX)
	{
		// XArray is an implementation of XList
		ComplexArray1D hologram = new ComplexArray1D(width);
		
		for(int i=0; i<width; i++)
		{
			hologram.setElement(i, Complex.expI(getLensPhase(f, getPhysicalPosition(i)-centreX)));
		}
		
		return hologram;
	}
	
	public ComplexArray1D getLensHologram(double f)
	{
		return getLensHologram(f, 0.0);
	}


	///////////////////////////////////////////////////
	// method that models passage through a hologram //
	////////////////////////////////////////////////////
	
	public void passThroughHologram(ComplexList1D h)
	throws SizeMismatchError
	{
		if(h.getSize() == width)
		{
			for(int i=0; i<width; i++)
				setAmplitude(i, Complex.product(getElement(i), h.getElement(i)));
		}
		else
		{
			throw(new SizeMismatchError("passThroughHologram: hologram size does not match beam size."));
		}
	}
	
	
	////////////////////////////////////////////////////////
	// method that models passage through a slit aperture //
	////////////////////////////////////////////////////////

        // a slit opening between x1 and x2
        public void passThroughSlitAperture(double x1, double x2)
        {
            double xMin, xMax, x;
            
            if(x1 <= x2)
            {
                xMin = x1;
                xMax = x2;
            }
            else
            {
                xMin = x2;
                xMax = x1;
            }

            for(int i = 0; i < width; i++)
            {
                x = getPhysicalPosition(i);
                if((xMin > x) || (x > xMax))
                {
                    amplitudes[getIndexRe(i)] = 0.0;
                    amplitudes[getIndexIm(i)] = 0.0;
                }
            }
        }

        // slit aperture of width w
	public void passThroughSlitAperture(double w)
	{
            passThroughSlitAperture(-w/2, w/2);
            
/*		for(int i = 0; i < width; i++)
		{
			if(Math.abs(getPhysicalPosition(i)) > w/2)
			{
				amplitudes[getIndexRe(i)] = 0.0;
				amplitudes[getIndexIm(i)] = 0.0;
			}
		}
*/
	}

	public ComplexArray1D getSlitApertureHologram(double w)
	{
		// XArray is an implementation of XList
		ComplexArray1D hologram = new ComplexArray1D(width);
		
		for(int i=0; i<width; i++)
			if(Math.abs(getPhysicalPosition(i)) <= w/2) hologram.setElement(i, new Complex(1.0, 0.0));
			else hologram.setElement(i, new Complex(0.0, 0.0));
		
		return hologram;
	}
	
	// a slit opening between x1 and x2
	public ComplexArray1D getSlitApertureHologram(double x1, double x2)
	{
		double xMin, xMax;
		if(x1 <= x2)
		{
			xMin = x1;
			xMax = x2;
		}
		else
		{
			xMin = x2;
			xMax = x1;
		}
		
		ComplexArray1D hologram = new ComplexArray1D(width);
		
		for(int i=0; i<width; i++)
			if((xMin <= getPhysicalPosition(i)) && (getPhysicalPosition(i) <= xMax))
				hologram.setElement(i, new Complex(1.0, 0.0));
			else
				hologram.setElement(i, new Complex(0.0, 0.0));
		
		return hologram;
	}

	// Whereas a slit aperture hologram takes the value 1 for |x| < w/2 and 0 everywhere else,
	// in a "soft slit" aperture of "softness" s the step at x = +/- w/2 is "softened" by
	// a cosine function.  The steps on either side of the slit do not happen between two
	// pixels, but in a zone of width s

	// slit aperture of width w
	public void passThroughSoftSlitAperture(double w, double s)
	{
		double xAbs, f;
		
		for(int i = 0; i < width; i++)
		{
			xAbs = Math.abs(getPhysicalPosition(i));
			if( xAbs <= (w-s)/2 ) f = 1;	// inner slit region
			else if( xAbs >= (w+s)/2 ) f = 0;	// outer region
			else f = (1+Math.cos( (xAbs-(w-s)/2)/s*Math.PI ))/2;

			amplitudes[getIndexRe(i)] *= f;
			amplitudes[getIndexIm(i)] *= f;
		}
	}

	public ComplexArray1D getSoftSlitApertureHologram(double w, double s)
	{
		double xAbs;
		
		// XArray is an implementation of XList
		ComplexArray1D hologram = new ComplexArray1D(width);
		
		for(int i=0; i<width; i++)
		{
			xAbs = Math.abs(getPhysicalPosition(i));
			if( xAbs <= (w-s)/2 )
				hologram.setElement(i, new Complex(1.0, 0.0));	// inner slit region
			else if( xAbs >= (w+s)/2 )
				hologram.setElement(i, new Complex(0.0, 0.0));	// outer region
			else
				hologram.setElement(i, new Complex(
					(1+Math.cos( (xAbs-(w-s)/2)/s*Math.PI ))/2,
					0.0
				));
		}
		
		return hologram;
	}
	

	/////////////////////////////////////////////////////////////////////
	// method that calculates the power in the amplitude cross section //
	/////////////////////////////////////////////////////////////////////
	
	// relative units
	public double getPowerInBeam()
	{
		double powerDensity = 0.0;
  
		for(int i = 0; i < width; i++)
		{
			powerDensity += 
				MyMath.sqr(amplitudes[getIndexRe(i)]) +
				MyMath.sqr(amplitudes[getIndexIm(i)]);
		}
  
		return powerDensity; /* * deltaX */
	}
	
	public void normalisePowerInBeam()
	{
		// calculate the factor by which all the amplitudes have to be multiplied
		// in order to make the total power in the beam one
		double f = 1.0 / Math.sqrt(getPowerInBeam());
		
		for(int i=0; i<2*width; i++) amplitudes[i] *= f;
	}


	//////////////////////////////////////
	// the methods that do all the work //
	//////////////////////////////////////

	// get the index where the real part of the amplitude with array index i
	// is stored in the array amplitudes
	protected int getIndexRe(int i)
	{
		return  i<<1 ;
	}
	
	// get the index where the imaginary part of the amplitude with coordinate i
	// is stored in the array amplitudes
	protected int getIndexIm(int i)
	{
		return (i<<1) + 1;
	}
	
	// physical separation in the x direction between the points represented by
	// neighbouring values in the amplitude matrix
	private double getDeltaX()
	{
		return (physicalWidth / (width - 1));
	}

	// wave vector
	// functions that calculate the components of wave vectors corresponding to
	// matrix elements in the Fourier transform

	// x component of the wave vector corresponding to element (i,j) in the
	// Fourier transform of the amplitude matrix
	private double getWaveVectorX(int i)
	{
		return
			( 2*Math.PI*(((i+(width-1)/2.0) % width) - (width-1)/2.0) ) /
			(getDeltaX()*width);
	}

	// z component of the wave vector corresponding to element (i,j) in the
	// Fourier transform of the amplitude matrix
	private double getWaveVectorZ(int i)
	{
		double sqrkX, sqrk;

		sqrk  = MyMath.sqr(2*Math.PI/wavelength);
		sqrkX = MyMath.sqr(getWaveVectorX(i));
  
  		if(sqrkX <= sqrk)
    	{
    		return Math.sqrt(sqrk - sqrkX) ;
    	}
		else
		{
			// Fourier components in a monochromatic beam of wave number k for which
			//   k_x^2 > k^2
			// represent evanescent components
			if(!evanescentComponentsWarningShown)
			{
				System.out.println(
					"\nYour array size is so large that the Nyquist wave number, i.e.\n" +
		 			"the greatest wave number represented in the Fourier representation\n" +
		 			"of the data array, is greater than the wave number of the light.\n" +
		 			"Fourier components which represent wave numbers greater than that\n" +
		 			"of the light represent evanescent waves.\n" +
		 			"WARNING: The propagation of an evanescent component over a negative\n" +
		 			"distance leads to exponential growth!\n"
		 		);
				evanescentComponentsWarningShown = true;
			}
			// the function returns the imaginary part of the
			// z component of the wave vector.  To indicate that
			// it should be treated like an imaginary number, the
			// negative value is returned
			return -Math.sqrt(sqrkX - sqrk);
		}
	}
}
