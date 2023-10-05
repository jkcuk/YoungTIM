/*

   classes:
   
       OpticalElement1D
   
   interfaces:
   
       OpticalElement1DParametersManipulator

*/


package library.opticalSystem;


/********************************************************************************

   definition of the abstract SpecialOpticalElement1D class

   an instance of an SpecialOpticalElement1D describes elements that do not simply
   act on the light beam
   
   example: the beginning and end of a resonator could be described by instances
            of the SpecialOpticalElement1D class

********************************************************************************/


public abstract class SpecialOpticalElement1D extends OpticalElement1D
{
	private static final long serialVersionUID = 1288102204127849448L;

	public abstract void act(LightBeamInOpticalSystem1D bs);
	
	// after the element has "acted", does the beam need to be
	// propagated to the next element?
	public abstract boolean propagateToNextElement();
}