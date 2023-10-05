/*

   classes:
   
       OpticalElement1D
   
   interfaces:
   
       OpticalElement1DParametersManipulator

*/


package library.opticalSystem;


import java.io.*;

import library.util.*;


/********************************************************************************

   definition of the abstract OpticalElement1D class

   an instance of an OpticalElement1D describes any type of optical element, such
   as a lens of focal length 10 mm or the end of a resonator
   
   subclasses: BasicOpticalElement1D
               SpecialOpticalElement1D
               CustomOpticalElement1D

********************************************************************************/

public abstract class OpticalElement1D
implements SelfExplainingObject, Serializable, Cloneable
{
	private static final long serialVersionUID = 5152055287435777291L;

	public double
		z; // z coordinate of element in mm
	
	public String
		name; // name of element
	
	// returns the type name of the element, such as "lens", "aperture", ...
	public abstract String getTypeName();
	
	// OVERRIDE THIS METHOD
	// returns a description of the element, such as "lens, f=40mm"
	public String toString()
	{
		return "\"" + name + "\", z=" + z + "mm";
	}
	
	public String shortToString()
	{
		return name + ", z=" + z + "mm";
	}

	// OVERRIDE THIS METHOD
	// SelfExplainingObject method
	public String getExplanation()
	{
		//  " <--        maximum string width         --> "
		return 
			"z describes the position in mm along the\n" +
			"optic axis.";		
	}
	
	// returns z IN METERS
	public double getZ()
	{
		return 1e-3*z;
	}

        public OpticalElement1D copy()
        {
            try {
                return (OpticalElement1D)(super.clone());
            }
            catch(CloneNotSupportedException e)
            {
                System.out.println("OpticalSystem1DDialog.actionPerformed(): Internal error: CloneNotSupportedException.  Details:\n" + e);
                return null;
            }
        }
}
