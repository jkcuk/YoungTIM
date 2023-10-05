/*

	example:

		// collect all possible types of optical element to register with the dialog
		Vector elementTypes = new Vector();
		elementTypes.addElement(new Plane1D());
		elementTypes.addElement(new Lens1D());
		elementTypes.addElement(new Aperture1D());
		elementTypes.addElement(new Hologram1D());
		elementTypes.addElement(new ResonatorBeginning1D());
		elementTypes.addElement(new UnfoldedResonatorEnd1D());
		elementTypes.addElement(new FoldedResonatorEnd1D());
	
		OpticalSystem1D s = new OpticalSystem1D();
		
		// add some random components to the optical system
		s.add(new Plane1D("P0", 0, 10e-3));
		s.add(new Lens1D("L1", 20e-3, 40e-3));
		s.add(new Aperture1D("A1", 21e-3, 10e-3, 1e-3));
		
		// show the control that allows system configuration
		add(new OpticalSystem1DControl(8, s, "some components", elementTypes));

*/

package library.opticalSystem;


import java.io.Serializable;
import java.util.Vector;


///////////////////////////////////////////////////////////
// optical system comprising positioned optical elements //
///////////////////////////////////////////////////////////

public class OpticalSystem1D implements Serializable
{
	private static final long serialVersionUID = -6762493875551116916L;
	
	// all the positioned optical elements are held in this Vector
	private Vector s;

	
	//
	// constructor
	//
	
	public OpticalSystem1D()
	{
		s = new Vector(); // start off with an empty system
	}
	
	
	//
	// data access
	//
	
	public int getSize()
	{
		return s.size();
	}
	
	public OpticalElement1D getElementAt(int index)
	{
		return (OpticalElement1D)s.elementAt(index);
	}
	
	
	//
	// methods for adding / deleting elements
	//
	
	// add a new element, sorted according to its z coordinate
	public void add(OpticalElement1D n)
	{
		int i;
		
		for(i=0; (i<s.size()) && (getElementAt(i).z <= n.z); i++);
		
		s.insertElementAt(n, i);
	}
	
	public void remove(OpticalElement1D d)
	{
		s.removeElement(d);
	}
	
	public void removeAt(int index)
	{
		s.removeElementAt(index);
	}
	
	public void replaceAt(int index, OpticalElement1D newElement)
	{
		OpticalElement1D oldElement = getElementAt(index);
		
		// has z changed?
		if(newElement.z != oldElement.z)
		{
			// yes, z has changed
			
			// remove the old element from the system...
			removeAt(index);
			
			// ... and add the new element at the correct place
			add(newElement);
		}
		else
		{
			// no, z has not changed
			
			// replace the old element
			s.setElementAt(newElement, index);
		}
	}
}