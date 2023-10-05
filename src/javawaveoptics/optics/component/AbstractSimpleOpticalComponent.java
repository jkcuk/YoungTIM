package javawaveoptics.optics.component;

import java.io.Serializable;
import java.util.ArrayList;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.optics.ComponentInput;
import javawaveoptics.optics.ComponentOutput;
import javawaveoptics.utility.RotatableInterface;

/**
 * Abstract class to define a simple optical component.
 * 
 * Simple optical components take only one light beam as input, and produce only one light beam as output,
 * in contrast to other optical components which may require any number of inputs and outputs. This class
 * removes the need for ArrayList objects to be passed in or out, as we only require one input and one
 * output.
 * 
 * @author Sean
 */
public abstract class AbstractSimpleOpticalComponent extends AbstractOpticalComponent implements Serializable
{
	private static final long serialVersionUID = 788674580927283409L;
		
	/**
	 * Constructor. This creates a simple optical component. It requires only a name, as a simple optical
	 * component only has 1 input and 1 output.
	 * 
	 * @param name			User-friendly name
	 */
	public AbstractSimpleOpticalComponent(String name)
	{
		super(name, 1, 1);
	}
	
	/**
	 * Simulate method. Simple optical components only take one input and return one output, so this method
	 * outputs an array list with one output in it. That output is calculated from the first (and only)
	 * input specified as being attached to this optical component.
	 */
	@Override
	public ArrayList<BeamCrossSection> simulate(ArrayList<BeamCrossSection> inputs)
	throws Exception
	{
		if(!componentEnabled)
		{
			return inputs;
		}
		
		ArrayList<BeamCrossSection> output = new ArrayList<BeamCrossSection>();
		
		BeamCrossSection input = inputs.get(0);
		BeamCrossSection thisOutput;
		
		// Check whether or not the implemented optical component implements the 'rotatable'
		// interface, and if so, rotate the beam accordingly.
		if(this instanceof RotatableInterface)
		{
			RotatableInterface rotatableObject = (RotatableInterface) this;
			
			double angle = rotatableObject.getRotateAngle();
			
			if((angle % 360.0) != 0.0)
			{
				// The component has a non-zero angle w.r.t the normal
				
				// Make a note of the initial width and height so as to ensure the beam
				// is returned width these dimensions after rotation (as rotation will
				// increase the beam size if necessary)
				int initialWidth = input.getWidth();
				int initialHeight = input.getHeight();
				
				// Rotate negatively as we're technically rotating the beam and not the
				// component
				input.rotateAndZoom(-angle, 1, true);
				
				// Have the component do its business as normal
				thisOutput = fromInputBeamCalculateOutputBeam(input);
				
				// Rotate the beam back to the zero angle state
				thisOutput.rotateAndZoom(angle, 1, true);
				
				// Shrink the beam back down to size (if necessary)
				thisOutput.changeDimensions(initialWidth, initialHeight);
			}
			else
			{
				thisOutput = fromInputBeamCalculateOutputBeam(input);
			}
		}
		else
		{
			thisOutput = fromInputBeamCalculateOutputBeam(input);
		}
		
		// Calculate the (only) output
		output.add(thisOutput);
		
		return output;
	}
	
	/**
	 * Abstract class that takes an input beam and provides the corresponding output beam. For example,
	 * a lens optical component subclass would pass the input beam through a virtual lens in order to
	 * provide the output beam.
	 * 
	 * @param inputBeam		The input beam
	 * @return				The output beam
	 */
	public abstract BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam) throws Exception;
	
	/**
	 * Returns the beam inbound to this object. As this is a simple optical component, we just return
	 * the first (and only) object in the input ArrayList.
	 * 
	 * @return		BeamOutput object
	 */
	public ComponentOutput getInput()
	{
		return getComponentInputs()[0];
	}
	
	/**
	 * Returns the beam outbound from this object. As this is a simple optical component, we just return
	 * the first (and only) object in the output ArrayList.
	 * 
	 * @return		BeamOutput object
	 */
	public ComponentInput getOutput()
	{
		return getComponentOutputs()[0];
	}
}