package library.opticalSystem;



import library.list.*;
import library.optics.*;


///////////////////
// a hologram... //
///////////////////

public class Hologram1D extends BasicOpticalElement1D
{
	private static final long serialVersionUID = 8504997594216082236L;
	
	//
	// variables
	//

	public ComplexArray1D hologram; // the actual hologram
	
	
	//
	// constructor
	//
	
	public Hologram1D()
	{
		name = "new hologram";
		z = 0;
	}
	
	public Hologram1D(String name, double z, ComplexArray1D hologram)
	{
		this.name = name;
		this.z = z;
		this.hologram = hologram;
	}

	public Hologram1D(Hologram1D o)
	{
		name = o.name;
		z = o.z;
                hologram = o.hologram;
	}
	
	
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "hologram"; }
		
	public String toString()
	{
		return "hologram " + super.toString();
	}
		
	public String getExplanation()
	{
		return 
			"Hologram.\n" +
			super.getExplanation();
	}
        

	//
	// BasicOpticalElement1D methods
	//
	
	public void act(LightBeamCrossSection1D b)
	{
		b.passThroughHologram(hologram);
	}
}