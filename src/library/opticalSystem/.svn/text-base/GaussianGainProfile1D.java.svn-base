/****

some custom optical elements (those I needed, obviously)

****/


package library.opticalSystem;

import library.list.*;
import library.maths.*;
import library.optics.*;


public class GaussianGainProfile1D extends CustomOpticalElement1D
implements BeamInitialisationElement1D
{
	private static final long serialVersionUID = -1872038382405394109L;
	
	public double
		w; // width of Gaussian
		
	
	//
	// constructor
	//
	
	public GaussianGainProfile1D()
	{
		name = "new Gaussian gain profile";
		z = 0;
		w = 1;
	}
	
	public GaussianGainProfile1D(String name, double z, double w)
	{
		this.name = name;
		this.z = 1e3*z; // store in mm
		this.w = 1e3*w; // store in mm
	}
	
	public GaussianGainProfile1D(GaussianGainProfile1D o)
	{
		name = o.name;
		z = o.z;
		w = o.w;
	}
	
		
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "Gaussian gain profile"; }
		
	public String toString()
	{
		return "Gaussian gain profile " + super.toString() + ", w=" + (float)w + "mm";
	}

	public String getExplanation()
	{
		//  " <--        maximum string width         --> "
		return 
			"Gaussian gain profile, i.e. Gaussian soft\n" +
			"aperture.\n" +
			"w is the width in mm of the Gaussian.";
	}
        
	
	//
	// CustomOpticalElement1D methods
	//
	
	private transient ComplexArray1D hologram;
	private transient double wHologram; // the values for which the hologram was calculated
	
	public BasicOpticalElement1D toBasicOpticalElement1D(LightBeamCrossSection1D b)
	{
		// does a new hologram need to be calculated?
		if(
			(hologram == null) || // no hologram calculated
			(wHologram != w) // hologram calculated for different w
		)
		{
			// calculate new hologram

			hologram = new ComplexArray1D(b.getSize());
		
			for(int i=0; i<b.getSize(); i++)
				hologram.setElement(i,
					new Complex(Math.exp(-MyMath.sqr(b.getPhysicalPosition(i)/(1e-3*w))/2), 0));
			
			wHologram = w;
		}
		
		return new Hologram1D("hologram of " + name, z, hologram);
	}
}