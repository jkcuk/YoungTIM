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


import java.awt.*;	// for the Dimension class
import java.io.*;

import library.list.*;
import library.maths.*;


/* implementation of the interface XList
   this class effectively converts the amplitude distribution passed as the argument to the
   constructor into the corresponding intensity distribution
*/
public class IntensityList2D implements DoubleList2D, Serializable
{
	private static final long serialVersionUID = -1696223327300688422L;
	
	public ComplexList2D amplitudeList;
	
	public IntensityList2D(ComplexList2D amplitudeList)
	{
		this.amplitudeList = amplitudeList;
	}
	
	public Dimension getSize()
	{
		return amplitudeList.getSize();
	}
	
	public double getElement(int i, int j)
	{
		Complex a = amplitudeList.getElement(i, j);
		return a.re*a.re + a.im*a.im;
	}
}