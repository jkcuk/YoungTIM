package library.opticalSystem;


/////////////////////////////////////
// the beginning of a resonator... //
/////////////////////////////////////

public class ResonatorBeginning1D extends SpecialOpticalElement1D
{
	private static final long serialVersionUID = -5454643329943999230L;
	
	//
	// constructor
	//

	public ResonatorBeginning1D()
	{
		name = "new beginning of resonator";
		z = 0;
	}
	
	public ResonatorBeginning1D(String name, double z)
	{
		this.name = name;
		this.z = 1000*z; // store in mm
	}
	
	public ResonatorBeginning1D(ResonatorBeginning1D o)
	{
		name = o.name;
		z = o.z;
	}
	
	
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "BEGINNING OF RESONATOR"; }
		
	public String toString()
	{
		return "BEGINNING OF RESONATOR " + super.toString();
	}
	
	public String getExplanation()
	{
		return "Beginning of an optical resonator.";
	}
        

	//
	// SpecialOpticalElement1D methods
	//
	
	public void act(LightBeamInOpticalSystem1D bs)
	{
		// if encountered by a backward-travelling beam...
		bs.forwardDirection = true;
		
		// increment the round trip counter
		bs.roundTripCounter++;
	}
	
	public boolean propagateToNextElement()
	{
		return true;
	}
}
