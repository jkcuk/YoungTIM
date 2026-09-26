package javawaveoptics.optics.component;

import java.io.Serializable;
import java.util.ArrayList;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.optics.ComponentInput;

/**
 * Defines an optical component with no inputs and one output, i.e. a source of a beam. Provides
 * a method which returns a BeamInput object representing the single output, removing the need
 * for the user to have to deal with ArrayList<BeamInput> objects.
 * 
 * @author Sean
 */
public abstract class AbstractLightSourceComponent extends AbstractOpticalComponent implements Serializable
{
	private static final long serialVersionUID = 7881224505371431352L;

	public AbstractLightSourceComponent(String name)
	{
		// Define an optical component with no inputs and one output
		super(name, 0, 1);
	}
	
	/**
	 * The light source simulate method implementation.
	 * 
	 * Although this component is given an input, it discards it. Instead it outputs in all instances a new
	 * beam object as specified by the child class.
	 */
	@Override
	protected ArrayList<BeamCrossSection> simulate(ArrayList<BeamCrossSection> inputs)
	{
		if(!componentEnabled)
		{
			return new ArrayList<BeamCrossSection>();
		}

		if(DEBUG_MODE)
		{
			// Check if inputs are defined, and if so, send user a warning
			for(BeamCrossSection beam : inputs)
			{
				if(beam != null)
				{
					System.err.println("Warning: " + name + " has a non-null beam input. This will be discarded.");
				}
			}
		}
		
		ArrayList<BeamCrossSection> output = new ArrayList<BeamCrossSection>();
		
		output.add(getOutputLightBeam());

		return output;
	}
	
	/**
	 * Abstract class that takes an input beam and provides the corresponding output beam.
	 * 
	 * @return				The output beam
	 */
	public abstract BeamCrossSection getOutputLightBeam();
	
	/**
	 * Returns the beam outbound from this object. As this is a light source, we just return
	 * the first (and only) object in the output ArrayList.
	 * 
	 * @return		BeamInput object
	 */
	public ComponentInput getOutput()
	{
		return getComponentOutputs()[0];
	}
}
