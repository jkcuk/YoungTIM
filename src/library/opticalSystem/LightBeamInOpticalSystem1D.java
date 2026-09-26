package library.opticalSystem;


import java.io.Serializable;

import library.optics.*;


//
//
//

public class LightBeamInOpticalSystem1D implements Serializable
{
	private static final long serialVersionUID = 4095859654754769158L;
	
	public LightBeamCrossSection1D b;
	public OpticalSystem1D system;
	public int
		currentElementIndex, // index of element immediately in front of b ("current element")
		roundTripCounter;
	public boolean forwardDirection; // allows bouncing backwards and forwards

	
	//
	// constructor
	//
	
	public LightBeamInOpticalSystem1D()
	{
		b = null;
		system = null;
		currentElementIndex = 0;
		roundTripCounter = 0;
		forwardDirection = true;
	}
	
	public LightBeamInOpticalSystem1D
	(LightBeamCrossSection1D b, OpticalSystem1D system, int currentElementIndex)
	{
		this.b = b;
		this.system = system;
		this.currentElementIndex = currentElementIndex;
		roundTripCounter = 0;
		forwardDirection = true;
	}
	
	
	// z coordinate of 
	public double getZ()
	throws EndOfOpticalSystemError
	{
		return currentElement().getZ();
	}
	
	
	public OpticalElement1D currentElement()
	throws EndOfOpticalSystemError
	{
		if((currentElementIndex < 0) && (currentElementIndex >= system.getSize()))
			throw new EndOfOpticalSystemError();
			
		return system.getElementAt(currentElementIndex);
	}
	
	
	public OpticalElement1D nextElement()
	throws EndOfOpticalSystemError
	{
		if(forwardDirection) currentElementIndex++;
		else currentElementIndex--;
		
		return currentElement();
	}
	

	//
	// let the elements act on the beam
	//
	
	public void performActionOfCurrentElement()
	{
		try
		{
			OpticalElement1D o = currentElement();
			
			if(o instanceof BasicOpticalElement1D)
			{
				// pass through element
				((BasicOpticalElement1D)o).act(b);
			}
			else if(o instanceof CustomOpticalElement1D)
			{
				// pass through corresponding basic optical element
				((CustomOpticalElement1D)o).toBasicOpticalElement1D(b).act(b);
			}
			else if(o instanceof SpecialOpticalElement1D)
			{
				// do your business
				((SpecialOpticalElement1D)o).act(this);			
			}
		}
		catch(EndOfOpticalSystemError e)
		{
			e.printStackTrace();
		}	
	}
	
	// let current element act on beam and propagate to next element
	public void toNextElement()
	{
		try
		{
                    OpticalElement1D e = currentElement();

                    performActionOfCurrentElement();

                    if(
                       !(e instanceof SpecialOpticalElement1D) ||
                       ( (e instanceof SpecialOpticalElement1D) &&
                         // if it's a special optical element, propagate to the next element
                         // only if the propagateToNextElement() method returns true
                         ((SpecialOpticalElement1D)e).propagateToNextElement() )
                       )
                        propagateFromBehindCurrentElementToNextElement();

		}
		catch(EndOfOpticalSystemError error)
		{
			error.printStackTrace();
		}
	}
	
	private void propagateFromBehindCurrentElementToNextElement()
	{
		// store the current element's z coordinate
		double z0 = getZ();
		
		// make next element the current element
		nextElement();
		
		// store the z coordinate of the new current element
		double z1 = getZ();
		
		// if the new element ahead is at a different position...
		if(z1 != z0)
			// propagate the beam there
			b.propagate(Math.abs(z1 - z0));
	}
}
