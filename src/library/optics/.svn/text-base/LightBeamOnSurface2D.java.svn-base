/* WaveTrace Version 1.1
   initially translated into C from the Mathematica program WaveTrace.nb which simulates
   the propagation of monochromatic light beams, later translated into Java


version history

	1.0	translation of the Mathematica program WaveTrace.nb into C

	1.1	implementation of wave tracing of evanescent waves
	    it is this version that was first translated into Java


classes or interfaces that this class depends on:

	class complex (defined in complex.java)

	class Fourier (defined in Fourier.java)

	interface xyList (defined in DensityPlotFrame.java)


example for using this class:

	LightBeamCrossSection2D b;
	
	// define new cross section of physical size 1mm x 1mm (1e-3m x 1e-3m),
	// represented by a 512 x 512 element matrix, of a light beam of wavelength
	// 633nm (633e-9m) 
	b = new LightBeamCrossSection2D(512, 512, 1e-3, 1e-3, 633e-9);
	
	// initialise with a uniform amplitude cross section...
	for(int x=0; x<b.width; x++) {
		for(int y=0; y<b.height; y++) {
			b.setAmplitude(x,y,new complex(1.0,0));
		}
	}
	// ... and plot it in a 200 x 200 window
	PlotFrame pf = new PlotFrame(b, "z = 0", 200, 200);
	pf.setWindowListener(new WindowDisposingAdapter());
	pf.setVisible(true);
	
	// pass the beam through a circular aperture of radius 0.3mm...
	b.passThroughCircularAperture(0.3e-3);
	// ... and plot what comes through
	pf = new PlotFrame(b, "z = 0, after aperture", 200, 200);
	pf.setWindowListener(new WindowDisposingAdapter());
	pf.setVisible(true);
	
	// propagate the beam's cross section into a plane a distance 1mm behind the aperture...
	b.propagate(1e-3);
	// ... and plot the new beam cross section
	pf = new PlotFrame(b, "z = 1mm", 200, 200);
	pf.setWindowListener(new WindowDisposingAdapter());
	pf.setVisible(true);
*/

package library.optics;


import java.io.*;

import javawaveoptics.geometry.AbstractSurface3D;
import javawaveoptics.geometry.Point3D;
import javawaveoptics.optics.BeamCrossSection;

import library.field.*;
import library.list.ComplexArray2D;
import library.maths.*;


public class LightBeamOnSurface2D extends ComplexField2D
implements Serializable
{
	private static final long serialVersionUID = 3130215985774303421L;

	// inherited from ComplexArray2D:
	// protected int width, height;
	// protected double data[];

	// inherited from ComplexField2D:
	// protected double physicalWidth, physicalHeight;	// in meters

	// wavelength of the light represented (monochromatic!)
	protected double wavelength;	// in meters
	
	protected AbstractSurface3D surface;	// the surface
	

	/////////////////
	// constructor //
	/////////////////
	
	public LightBeamOnSurface2D(
		AbstractSurface3D surface,
		int width, int height, double physicalWidth, double physicalHeight,
		double wavelength )
	{
		super(width, height, physicalWidth, physicalHeight);
				
		// store surface
		this.surface = surface;
		
		// store wavelength
		this.wavelength = wavelength;
	}
	
	public LightBeamOnSurface2D(AbstractSurface3D surface, LightBeamCrossSection2D lightBeamOnPlane)
	{
		super(lightBeamOnPlane);
		
		this.surface = surface;
		this.wavelength = lightBeamOnPlane.getWavelength();
	}
	
	/**
	 * Copy constructor
	 * 
	 * Takes an existing LightBeamOnSurface2D and creates a copy of it in its current state.
	 * 
	 * @param original		LightBeamOnSurface2D object to copy
	 * 
	 * @author Johannes
	 */
	public LightBeamOnSurface2D(LightBeamOnSurface2D original)
	{		
		super(original);
		
		this.surface = original.getSurface();
		this.wavelength = original.getWavelength();
	}
	
	public BeamCrossSection toBeam()
	{		
		// this only really makes sense if the surface is a plane!
		return new BeamCrossSection(this, getWavelength());
	}
	
	////////////////////////////////////
	// methods for accessing the data //
	////////////////////////////////////
	
	public double getWavelength() {
		return wavelength;
	}

	public void setWavelength(double wavelength) {
		this.wavelength = wavelength;
	}
	
	public double getK()
	{
		return 2.0*Math.PI/getWavelength();
	}
	
	public AbstractSurface3D getSurface() {
		return surface;
	}

	public void setSurface(AbstractSurface3D surface) {
		this.surface = surface;
	}

	public void setAmplitude(int i, int j, Complex amplitude)
	{
		setElement(i, j, amplitude);
	}
	
	public Complex getAmplitude(int i, int j)
	{
		return getElement(i, j);
	}
	
	
	public double getIntensity(int i, int j)
	{
		// sum of the squares of the real and imaginary parts of the amplitude
		return MyMath.sqr(data[getIndexRe(i, j)]) + MyMath.sqr(data[getIndexIm(i, j)]);
	}

	
	/**
	 * propagate the beam from the current surface to the new surface
	 * @param newSurface
	 */
	public void propagate(AbstractSurface3D newSurface)
	{
		double k = getK();
		
		// for Gaussian
		double w0 = 0.45*getDeltaX();	// beam waist of Gaussian beam from each pixel; assume that the sampling frequency in the x and y directions are the same
		double w02 = w0 * w0;	// w0, squared
		double zR = k*w02 / 2.;	// Rayleigh range
		
		// make a copy of the original array
		ComplexArray2D originalArray = new ComplexArray2D(this);

		// go through all the points on the new surface...
		for(int j = 0; j < height; j++)
		{
			double y = getY(j);
			
			for(int i = 0; i < width; i++)
			{
				double x = getX(i);
				double z = surface.getZ(x, y);
				
				Complex uij = new Complex(0, 0);	// initialise the new amplitude to 0...
				
				// ... and calculate, and add to the new amplitude, the contributions from all the points on the old surface
				for(int j0 = 0; j0 < height; j0++)
				// int j0 = height/2;
				{
					double y0 = getY(j0);
					
					for(int i0 = 0; i0 < width; i0++)
					// int i0 = width/2;
					{
						double x0 = getX(i0);
						double z0 = newSurface.getZ(x0, y0);

						// add a spherical wave centred on element (i0, j0) of the original array
//						double r = Math.sqrt(MyMath.sqr(x-x0) + MyMath.sqr(y-y0) + MyMath.sqr(z-z0));						
//						uij.add(
//								Complex.product(
//										originalArray.getElement(i0, j0),
//										Complex.expI(k*r)
//								).multiply(1./r)
//						);
						
						// alternatively (and more slowly, but better?),
						// add a Gaussian beam centred on element (i0, j0) of the original array
						// (formula from http://en.wikipedia.org/wiki/Gaussian_beam)
						double Z = z-z0;
						double R2 = MyMath.sqr(x-x0) + MyMath.sqr(y-y0);	// transverse radius, squared
						double t = 1+MyMath.sqr(Z/zR);	// a common term
						double roc = Z*(1+MyMath.sqr(zR/Z));
						uij.add(
								Complex.product(
										originalArray.getElement(i0, j0),
										Complex.expI(
											-(k*(Z+R2/(2*roc)))
											-Math.atan(Z/zR)
										)
								).multiply(Math.exp(-R2/(w02 * t))/Math.sqrt(t))
						);
					}
				}
				
				// set the element on the new surface
				setElement(i, j, uij);
			}
		}
		
		// make the new surface this beam's surface
		surface = newSurface;
	}
	
	
	/**
	 * Simulate transmission through a phase hologram that images <objectPosition> into <imagePosition>.
	 * This is done such that the phase accumulated on the way from <objectPosition> to any point on the surface
	 * and then to <imagePosition> is independent of the position on the surface.
	 * We do this by making that phase 0.
	 * 
	 * @param objectPosition
	 * @param imagePosition
	 */
	public void passThroughImagingHologram(Point3D objectPosition, Point3D imagePosition)
	{
		double k = getK();
		
		// go through all the points on the surface...
		for(int j = 0; j < height; j++)
		{
			double y = getY(j);
			
			for(int i = 0; i < width; i++)
			{
				double x = getX(i);
				double z = surface.getZ(x, y);
				Point3D p = new Point3D(x, y, z);
				
				double
					r1 = objectPosition.getDistanceFrom(p),
					r2 = imagePosition.getDistanceFrom(p);
				
				setElement(i, j, getElement(i, j).multiply(Complex.expI(-k*(r1+r2))));
			}
		}
	}
}