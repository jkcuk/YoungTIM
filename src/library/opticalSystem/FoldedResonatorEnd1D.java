package library.opticalSystem;




///////////////////////////////////////
// the end of a folded resonator... //
///////////////////////////////////////

public class FoldedResonatorEnd1D extends SpecialOpticalElement1D
{
	private static final long serialVersionUID = -8809159864063236908L;
	
	//
	// constructor
	//

	public FoldedResonatorEnd1D()
	{
		name = "new end of folded resonator";
		z = 0;
	}
	
	public FoldedResonatorEnd1D(String name, double z)
	{
		this.name = name;
		this.z = 1000*z; // store in mm
	}

        public FoldedResonatorEnd1D(FoldedResonatorEnd1D o)
        {
            name = o.name;
            z = o.z;
        }


	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "END OF FOLDED RESONATOR"; }
		
	public String toString()
	{
		return "END OF FOLDED RESONATOR " + super.toString();
	}
	
	public String getExplanation()
	{
		return
			"End of a folded optical resonator.  The direction\n" +
			"of the light beam will is reversed.";
	}
        
	//
	// SpecialOpticalElement1D methods
	//
	
	public void act(LightBeamInOpticalSystem1D bs)
	{
		// if encountered by a forward-travelling beam...
		bs.forwardDirection = false;
	}
	
	public boolean propagateToNextElement()
	{
		return true;
	}
}