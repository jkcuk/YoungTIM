package library.opticalSystem;


import library.optics.*;


////////////////
// a plane... //
////////////////

public class Plane1D extends BasicOpticalElement1D
implements BeamInitialisationElement1D
{
	private static final long serialVersionUID = 2545421543019266046L;
	
	public boolean
		forwards,
		backwards;
		
	
	//
	// constructor
	//
	
	public Plane1D()
	{
		name = "new plane";
		z = 0;
		forwards = true;
		backwards = true;
	}
	
	public Plane1D(String name, double z, boolean forwards, boolean backwards)
	{
		this.name = name;
		this.z = 1000*z; // store in mm
		this.forwards = forwards;
		this.backwards = backwards;
	}
	
	public Plane1D(Plane1D o)
	{
		name = o.name;
		z = o.z;
		forwards = o.forwards;
		backwards = o.backwards;
	}
	
		
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "plane"; }
		
	public String toString()
	{
		return "plane " + super.toString();
	}

	public String getExplanation()
	{
		return 
			"A plane in an optical system.  The same plane\n" +
			"in an optical resonator counts as two\n" +
			"different planes in its unfolded lens-guide\n" +
			"equivalent.  The variables \"forwards\" and\n" +
			"\"backwards\" control whether a plane\n" +
			"corresponds to a beam moving forwards, or\n" +
			"backwards, or both.  A plane doesn't do very\n" +
			"much, unless a program uses it as marker for\n" +
			"interesting planes.  For example, a beam\n" +
			"propagation program could plot the intensity\n" +
			"cross-section in all planes.\n" +
			super.getExplanation();
	}
        
	
	//
	// BasicOpticalElement1D methods
	//
	
	public void act(LightBeamCrossSection1D b)
	{}
}
