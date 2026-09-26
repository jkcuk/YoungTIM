/****

some custom optical elements (those I needed, obviously)

****/


package library.opticalSystem;



import library.list.*;
import library.optics.*;


public class SideBySideLensSuperposition1D extends CustomOpticalElement1D
implements BeamInitialisationElement1D
{
	private static final long serialVersionUID = -8365270351886150647L;
	
	public double
		f, // focal length in mm
		d; // distance of lens centres from optic axis
		
	
	//
	// constructor
	//
	
	public SideBySideLensSuperposition1D()
	{
		name = "new side-by-side lens superposition";
		z = 0;
		f = 10;
		d = 0.5;
	}
	
	public SideBySideLensSuperposition1D(String name, double z, double f, double d)
	{
		this.name = name;
		this.z = 1e3*z; // store in mm
		this.f = 1e3*f; // store in mm
		this.d = 1e3*d; // store in mm
	}
	
	public SideBySideLensSuperposition1D(SideBySideLensSuperposition1D o)
	{
		name = o.name;
		z = o.z;
		f = o.f;
		d = o.d;
	}
	
		
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "side-by-side lens superposition"; }
		
	public String toString()
	{
		return
			"side-by-side lens superposition " + super.toString() +
			", f=" + (float)f + "mm, d=" + (float)d + "mm";
	}

	public String getExplanation()
	{
		//  " <--        maximum string width         --> "
		return 
			"A side-by-side lens superposition, mainly\n" +
			"used for creating side-by-side images.\n" +
			"f is the focal length in mm of each lens,\n" +
			"d is the distance in mm from the optic axis\n" +
			"of the centres of the lenses." +
			super.getExplanation();
	}
        
	
	//
	// CustomOpticalElement1D methods
	//
	
	private transient ComplexArray1D hologram;
	private transient double fHologram, dHologram; // the values for which the hologram was calculated
	
	public BasicOpticalElement1D toBasicOpticalElement1D(LightBeamCrossSection1D b)
	{
		// does a new hologram need to be calculated?
		if(
			(hologram == null) || // no hologram calculated
			(fHologram != f) || // hologram calculated for different f
			(dHologram != d) // hologram calculated for different d
		)
		{
			// calculate new hologram
			hologram =
				ComplexArray1D.sum(
					b.getLensHologram(1e-3*f, 1e-3*d),
					b.getLensHologram(1e-3*f, -1e-3*d));
			
			fHologram = f;
			dHologram = d;
		}
		
		return new Hologram1D("hologram of " + name, z, hologram);
	}
}
