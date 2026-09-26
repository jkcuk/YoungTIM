/****

some custom optical elements (those I needed, obviously)

****/


package library.opticalSystem;


import library.list.*;
import library.optics.*;


public class ImperfectSideBySideLenses1D extends CustomOpticalElement1D
implements BeamInitialisationElement1D
{
	private static final long serialVersionUID = -1277725541879284668L;
	
	public double
		f1, // focal length of lens1 in mm
		f2, // focal length of lens2
		c1, // x coordinate of the centre of lens1 (optic axis = 0)
		c2, // x coordinate of the centre of lens2
		s; // x coordinate of the seam, i.e. the line along which the lenses are cemented together
		
	
	//
	// constructor
	//
	
	public ImperfectSideBySideLenses1D()
	{
		name = "new imperfect side-by-side lenses";
		z = 0;
		f1 = 10;
		f2 = 10;
		c1 = -0.5;
		c2 = 0.5;
		s = 0;
	}
	
	public ImperfectSideBySideLenses1D(
		String name, double z, double f1, double f2, double c1, double c2, double s)
	{
		this.name = name;
		this.z = 1e3*z; // store in mm
		this.f1 = 1e3*f1; // store in mm
		this.f2 = 1e3*f2; // store in mm
		this.c1 = 1e3*c1; // store in mm
		this.c2 = 1e3*c2; // store in mm
		this.s = 1e3*s; // store in mm
	}
	
	public ImperfectSideBySideLenses1D(ImperfectSideBySideLenses1D o)
	{
		name = o.name;
		z = o.z;
		f1 = o.f1;
		f2 = o.f2;
		c1 = o.c1;
		c2 = o.c2;
		s = o.s;
	}
	
		
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "imperfect side-by-side lenses"; }
		
	public String toString()
	{
		return 
			"imperfect side-by-side lenses " + super.toString() +
			", f1=" + (float)f1 + "mm, f2=" +
			(float)f2 + "mm, c1=" + (float)c1 + "mm, c2=" + (float)c2 + "mm, s=" +
			(float)s + "mm";
	}

	public String getExplanation()
	{
		//  " <--        maximum string width         --> "
		return 
			"Two imperfect side-by-side lenses, joined\n" +
			"together at the optic axis; mainly used for\n" +
			"creating side-by-side images.\n" +
			"f1 and f2 are the focal lengths in mm of\n" +
			"the lenses, c1 and c2 are the x coordinates\n" +
			"in mm of the centres of the lenses, and\n" +
			"s is the x coordinate in mm of the seam along\n" +
			"which the two lenses are cemented together.";
	}
        
	
	//
	// CustomOpticalElement1D methods
	//
	
	private transient ComplexArray1D hologram;
	// the values for which the hologram was calculated
	private transient double f1Hologram, f2Hologram, c1Hologram, c2Hologram, sHologram;
	
	public BasicOpticalElement1D toBasicOpticalElement1D(LightBeamCrossSection1D b)
	{
		// does a new hologram need to be calculated?
		if(
			(hologram == null) || // no hologram calculated
			(f1Hologram != f1) || // hologram calculated for different f1
			(f2Hologram != f2) ||
			(c1Hologram != c1) ||
			(c2Hologram != c2) ||
			(sHologram != s) // hologram calculated for different s
		)
		{
			// calculate new hologram
			hologram =
				ComplexArray1D.sum(
					ComplexArray1D.product(
						b.getLensHologram(1e-3*f1, 1e-3*c1), 
						b.getSlitApertureHologram(-b.getPhysicalWidth()/2, 1e-3*s)
					),
					ComplexArray1D.product(
						b.getLensHologram(1e-3*f2, 1e-3*c2),
						b.getSlitApertureHologram(1e-3*s, b.getPhysicalWidth()/2)
					)
				);
			
			f1Hologram = f1;
			f2Hologram = f2;
			c1Hologram = c1;
			c2Hologram = c2;
			sHologram = s;
		}
		
		return new Hologram1D("hologram of " + name, z, hologram);
	}
}
