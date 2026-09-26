package library.opticalSystem;


import library.optics.*;


///////////////
// a lens... //
///////////////

public class Lens1D extends BasicOpticalElement1D
{
	private static final long serialVersionUID = -804738936960156477L;
	
	//
	// variables
	//
	
	public double f; // focal length
	
	
	//
	// constructor(s)
	//
	
	public Lens1D()
	{
		name = "new lens";
		z = 0;
		f = 100;
	}
	
	public Lens1D(String name, double z, double f)
	{
		this.name = name;
		this.z = 1000*z;
		this.f = 1000*f; // store f in mm
	}
	
	public Lens1D(Lens1D o)
	{
		name = o.name;
		z = o.z;
		f = o.f;
	}
	
        
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "lens"; }
		
	public String toString()
	{
		return "lens " + super.toString() + ", f=" + (float)f + "mm";
	}
		
	public String getExplanation()
	{
		return
			"Lens, focal length = <f> mm.\n" +
			super.getExplanation();
	}
        

	//
	// BasicOpticalElement1D methods
	//
	
	public void act(LightBeamCrossSection1D b)
	{
		b.passThroughLens(1e-3 * f);
	}
}
