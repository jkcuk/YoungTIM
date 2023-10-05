/*

   classes:
   
       OpticalElement1D
   
   interfaces:
   
       OpticalElement1DParametersManipulator

*/


package library.opticalSystem;


import library.optics.*;


/********************************************************************************

   definition of the abstract CustomOpticalElement1D class

   an instance of an CustomOpticalElement1D can describe custom optical elements such
   as a 2-lens Cantor-set hologram

********************************************************************************/

public abstract class CustomOpticalElement1D extends OpticalElement1D
{
	private static final long serialVersionUID = -4873317787227611081L;

	public abstract BasicOpticalElement1D toBasicOpticalElement1D(LightBeamCrossSection1D b);
}
