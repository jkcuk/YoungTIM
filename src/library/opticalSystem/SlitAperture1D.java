package library.opticalSystem;



import library.optics.*;


/////////////////////
// an aperture ... //
/////////////////////

public class SlitAperture1D extends BasicOpticalElement1D
{
	private static final long serialVersionUID = -3460043219882978670L;
	
	//
	// variables
	//
	
	public double
		fwhm, // width
		s; // softness, i.e. width over which the edges smoothly change between 0 and 1
	
	
	//
	// constructor(s)
	//
	
	public SlitAperture1D()
	{
		name = "new slit aperture";
		z = 0;
		fwhm = 1;
		s = 0;
	}
	
	public SlitAperture1D(String name, double z, double fwhm, double s)
	{
		this.name = name;
		this.z = 1000*z;
		this.fwhm = 1000*fwhm; // store data in mm
		this.s = 1000*s;
	}
	
	public SlitAperture1D(SlitAperture1D o)
	{
		name = o.name;
		z = o.z;
		fwhm = o.fwhm;
		s = o.s;
	}
	
	
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "slit aperture"; }
	
	public String toString()
	{
		return
			"slit aperture " + super.toString() + ", fwhm=" +
			(float)fwhm + "mm, s=" + (float)s + "mm";
	}

	public String getExplanation()
	{
		return 
			"Slit aperture, width (fwhm) = <fwhm> mm,\n" +
			"\"softness\" = <s> mm.  The softness is the\n" +
			"width of the cosine-shaped transition of the\n" +
			"transmission function between 0 and 1 at the\n" +
			"edges of the aperture.\n" +
			super.getExplanation();
	}
        

	//
	// BasicOpticalElement1D methods
	//
	
	public void act(LightBeamCrossSection1D b)
	{
		if(s != 0.0)
			b.passThroughSoftSlitAperture(1e-3 * fwhm, 1e-3 * s);
		else
			b.passThroughSlitAperture(1e-3 * fwhm);
	}
}
