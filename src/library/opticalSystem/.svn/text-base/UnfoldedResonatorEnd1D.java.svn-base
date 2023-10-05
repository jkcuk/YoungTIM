package library.opticalSystem;


/////////////////////////////////////////
// the end of an unfolded resonator... //
/////////////////////////////////////////

public class UnfoldedResonatorEnd1D extends SpecialOpticalElement1D
{
	private static final long serialVersionUID = -6108412415778553186L;
	
	//
	// constructor
	//

	public UnfoldedResonatorEnd1D()
	{
		name = "new end of unfolded resonator";
		z = 0;
	}
	
	public UnfoldedResonatorEnd1D(String name, double z)
	{
		this.name = name;
		this.z = 1000*z; // store in mm
	}
	
	public UnfoldedResonatorEnd1D(UnfoldedResonatorEnd1D o)
	{
		name = o.name;
		z = o.z;
	}
	
	
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "END OF UNFOLDED RESONATOR"; }
		
	public String toString()
	{
		return "END OF UNFOLDED RESONATOR " + super.toString();
	}
	
	public String getExplanation()
	{
		return
			"End of an unfolded optical resonator.  The light\n" +
			"beam will continue at the beginning of the\n" +
			"resonator.";
	}
        

	//
	// SpecialOpticalElement1D methods
	//
	
	public void act(LightBeamInOpticalSystem1D bs)
	{
		// back to beginning of resonator
		
		// find beginning of resonator
		int i;
		
		for(
			i=0;
			(i<bs.system.getSize())
			&& !(bs.system.getElementAt(i) instanceof ResonatorBeginning1D);
			i++
		);

		// jump to beginning of resonator
		bs.currentElementIndex = i;
	}
	
	public boolean propagateToNextElement()
	{
		return false;
	}
}
