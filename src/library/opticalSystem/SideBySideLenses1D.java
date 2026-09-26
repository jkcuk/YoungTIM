/****

some custom optical elements (those I needed, obviously)

****/


package library.opticalSystem;



import library.list.*;
import library.optics.*;


public class SideBySideLenses1D extends CustomOpticalElement1D
implements BeamInitialisationElement1D
{
	private static final long serialVersionUID = 3284653659721893079L;
	
	public double
		f, // focal length in mm
		d; // distance of lens centres from optic axis
		
	
	//
	// constructor
	//
	
	public SideBySideLenses1D()
	{
		name = "new side-by-side lenses";
		z = 0;
		f = 10;
		d = 0.5;
	}
	
	public SideBySideLenses1D(String name, double z, double f, double d)
	{
		this.name = name;
		this.z = 1e3*z; // store in mm
		this.f = 1e3*f; // store in mm
		this.d = 1e3*d; // store in mm
	}
	
	public SideBySideLenses1D(SideBySideLenses1D o)
	{
		name = o.name;
		z = o.z;
		f = o.f;
		d = o.d;
	}
	
		
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "side-by-side lenses"; }
		
	public String toString()
	{
		return
			"side-by-side lenses " + super.toString() +
			", f=" + (float)f + "mm, d=" + (float)d + "mm";
	}

	public String getExplanation()
	{
		//  " <--        maximum string width         --> "
		return 
			"Two side-by-side lenses, joined together at\n" +
			"the optic axis; mainly used for creating\n" +
			"side-by-side images.\n" +
			"f is the focal length in mm of each lens,\n" +
			"d is the distance in mm from the optic axis\n" +
			"of the centres of the lenses.";
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
					ComplexArray1D.product(
						b.getLensHologram(1e-3*f, 1e-3*d), 
						b.getSlitApertureHologram(0.0, b.getPhysicalWidth()/2)
					),
					ComplexArray1D.product(
						b.getLensHologram(1e-3*f, -1e-3*d),
						b.getSlitApertureHologram(-b.getPhysicalWidth()/2, 0.0)
					)
				);
			
			fHologram = f;
			dHologram = d;
		}
		
		return new Hologram1D("hologram of " + name, z, hologram);
	}
}
