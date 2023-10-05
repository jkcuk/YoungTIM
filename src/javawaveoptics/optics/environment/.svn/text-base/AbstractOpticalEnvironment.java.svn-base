package javawaveoptics.optics.environment;

import java.io.Serializable;
import java.util.BitSet;

import javawaveoptics.optics.ComponentInput;
import javawaveoptics.optics.ComponentOutput;
import javawaveoptics.optics.component.AbstractOpticalComponent;
import javawaveoptics.optics.component.OpticalComponentFactory;
import javawaveoptics.ui.OpticalEnvironmentChangeEvent;
import javawaveoptics.utility.ImageableInterface;
import javawaveoptics.utility.ImageableLightSourceInterface;

/**
 * Defines an optical environment. This class contains all the information required
 * to build an optical environment.
 * 
 * The program uses the optical environment in the saving and loading process. The
 * start component is linked to the rest of the components in the optical environment
 * and so simply saving (i.e. serialising) this class will also save the whole
 * environment as defined by the user. The same applies to loading.
 * 
 * This class is abstract, and so must be subclassed with the relevant optical setup.
 * 
 * @author Sean
 */
public abstract class AbstractOpticalEnvironment implements Serializable
{
	private static final long serialVersionUID = 7440893404380382160L;
	
	// The initial optical component
	protected AbstractOpticalComponent startComponent;
	
	// Whether or not the first component has been added
	private boolean firstComponentAdded = false;
	
	// Workbench type to use
	protected int workbenchType;
	
	// The bitset representing available optical components and light sources
	private BitSet availableLightSourceComponentsBitField;
	private BitSet availableNonLightSourceComponentsBitField;
	
	// The change listener, if any, to use, to notify an object about changes to this environment
	// protected transient OpticalEnvironmentChangeListener changeListener;
	// TODO re-create this, somehow!
	
	/**
	 * Constructor without a specified workbench (for use with e.g. command line)
	 */
	public AbstractOpticalEnvironment()
	{
		availableLightSourceComponentsBitField = new BitSet(OpticalComponentFactory.getNumberOfLightSources());
		availableNonLightSourceComponentsBitField = new BitSet(OpticalComponentFactory.getNumberOfNonLightSources());
	}
	
	/**
	 * Constructor allowing a workbench type to be set up.
	 * 
	 * @param workbenchType
	 */
	public AbstractOpticalEnvironment(int workbenchType)
	{
		this();
		
		this.workbenchType = workbenchType;
	}
	
	/**
	 * Disables the specified light source from being added to the environment
	 * 
	 * @param lightSource
	 */
	public void disableLightSource(String lightSource)
	{
		String[] lightSources = OpticalComponentFactory.getLightSources();
		
		int i = 0;
		
		for(String thisLightSource : lightSources)
		{
			if(thisLightSource.equals(lightSource))
			{
				availableLightSourceComponentsBitField.set(i);
				
				System.out.println("Disabling " + lightSource);
			}
			
			i++;
		}
	}
	
	/**
	 * Disables the specified non light source from being added to the environment
	 * 
	 * @param nonLightSource
	 */
	public void disableNonLightSource(String nonLightSource)
	{
		String[] nonLightSources = OpticalComponentFactory.getNonLightSources();
		
		int i = 0;
		
		for(String thisNonLightSource : nonLightSources)
		{
			if(thisNonLightSource.equals(nonLightSource))
			{
				availableNonLightSourceComponentsBitField.set(i);
				
				System.out.println("Disabling " + nonLightSource);
			}
			
			i++;
		}
	}
	
	/**
	 * Enables the specified light source in the environment. This might be used in
	 * conjunction with disableAllLightSources() to disable all light sources and
	 * selectively enable certain ones.
	 * 
	 * @param lightSource
	 */
	public void enableLightSource(String lightSource)
	{
		String[] lightSources = OpticalComponentFactory.getLightSources();
		
		int i = 0;
		
		for(String thisLightSource : lightSources)
		{
			if(thisLightSource.equals(lightSource))
			{
				availableLightSourceComponentsBitField.set(i, false);
				
				System.out.println("Enabling " + lightSource);
			}
			
			i++;
		}
	}
	
	/**
	 * Enables the specified non light source in the environment. This might be used in
	 * conjunction with disableAllNonLightSources() to disable all non light sources and
	 * selectively enable certain ones.
	 * 
	 * @param nonLightSource
	 */
	public void enableNonLightSource(String nonLightSource)
	{
		String[] nonLightSources = OpticalComponentFactory.getNonLightSources();
		
		int i = 0;
		
		for(String thisNonLightSource : nonLightSources)
		{
			if(thisNonLightSource.equals(nonLightSource))
			{
				availableNonLightSourceComponentsBitField.set(i, false);
				
				System.out.println("Enabling " + nonLightSource);
			}
			
			i++;
		}
	}
	
	/**
	 * Disables all light sources
	 */
	public void disableAllLightSources()
	{
		// Set all light sources to true, i.e. disabled
		// Note: you cannot use availableLightSourceComponentsBitField.length() to get the upper boundary,
		// due to the way Java handles bitfields (they are 'shrunken' to their lowest non-0 value)
		availableLightSourceComponentsBitField.set(0, OpticalComponentFactory.getNumberOfLightSources(), true);
		
		System.out.println("Disabling all light sources");
	}
	
	/**
	 * Disables all non light sources
	 */
	public void disableAllNonLightSources()
	{
		// Set all non light sources to true, i.e. disabled
		// Note: you cannot use availableNonLightSourceComponentsBitField.length() to get the upper boundary,
		// due to the way Java handles bitfields (they are 'shrunken' to their lowest non-0 value)
		availableNonLightSourceComponentsBitField.set(0, OpticalComponentFactory.getNumberOfNonLightSources(), true);
		
		System.out.println("Disabling all non light sources");
	}
	
	public void addFirstComponent(AbstractOpticalComponent component)
	{
		startComponent = component;
		
		// give only planes (or other components that implement ImageableInterface) a unique name
		if(component instanceof ImageableInterface)
		{
			component.setName(getUniqueName(component.getName()));
		}
		
		firstComponentAdded = true;
		
		// Fire a change event
		fireChangeEvent(new OpticalEnvironmentChangeEvent(component, OpticalEnvironmentChangeEvent.TYPE_ADD));
	}
	
	public void addAfter(AbstractOpticalComponent component, int outputIndex, AbstractOpticalComponent componentToAdd, int componentToAddInputIndex)
	{
		if(!firstComponentAdded)
		{
			System.err.println("Critical error: first component not added. Specify first component to be added using addFirstComponent()");
			
			System.exit(1);
		}
		else
		{
			// give only planes (or other components that implement ImageableInterface) a unique name
			if(componentToAdd instanceof ImageableInterface)
			{
				componentToAdd.setName(getUniqueName(componentToAdd.getName()));
			}

			ComponentInput outputBeam = component.getComponentOutputs()[outputIndex];
			
			if(outputBeam != null)
			{
				// There is currently something connected to this output already.
				// Add it on to the output of the new component we're adding.
				
				outputBeam.getComponent().setComponentInput(outputBeam.getNumber(), new ComponentOutput(componentToAdd, 0));
				componentToAdd.setComponentOutput(0, outputBeam);
			}
			
			component.setComponentOutput(outputIndex, new ComponentInput(componentToAdd, componentToAddInputIndex));
			
			// Set the visible output to be the newly added output
			component.setOpticalTrainOutputIndex(outputIndex);
			
			componentToAdd.setComponentInput(componentToAddInputIndex, new ComponentOutput(component, outputIndex));
						
			// Fire a change event
			fireChangeEvent(new OpticalEnvironmentChangeEvent(componentToAdd, OpticalEnvironmentChangeEvent.TYPE_ADD));
		}
	}
	
	public void addBefore(AbstractOpticalComponent component, int inputIndex, AbstractOpticalComponent componentToAdd, int componentToAddOutputIndex)
	{
		if(!firstComponentAdded)
		{
			System.err.println("Critical error: first component not added. Specify first component to be added using addFirstComponent()");
			
			System.exit(1);
		}
		else
		{
			if(componentToAdd instanceof ImageableInterface)
			{
				componentToAdd.setName(getUniqueName(componentToAdd.getName()));
			}
			
			ComponentOutput inputBeam = component.getComponentInputs()[inputIndex];
			
			if(inputBeam != null)
			{
				// There is currently something connected to this input already.
				// Add it on to the input of the new component we're adding.
				
				inputBeam.getComponent().setComponentOutput(inputBeam.getNumber(), new ComponentInput(componentToAdd, 0));
				componentToAdd.setComponentInput(0, inputBeam);
			}
			else
			{
				// The component is being added to the start of the train - set it to be the start
				// component.
				//
				// Sean comment: this has been disabled. I don't know why I put this
				// functionality in here originally, but by having it enabled it then
				// causes issues with beam splitters as by default, inputs to newly drawn
				// beam splitters (such as from DovePrismInterferometerOpticalEnvironment)
				// are shown as being connected to the first (top) input, whether this is
				// in fact true or not.
				// 
				//
				// startComponent = componentToAdd;
			}
			
			component.setComponentInput(inputIndex, new ComponentOutput(componentToAdd, componentToAddOutputIndex));
			
			// Set the visible input to be the newly added input
			component.setOpticalTrainInputIndex(inputIndex);
			
			componentToAdd.setComponentOutput(componentToAddOutputIndex, new ComponentInput(component, inputIndex));
			
			// Fire a change event
			fireChangeEvent(new OpticalEnvironmentChangeEvent(componentToAdd, OpticalEnvironmentChangeEvent.TYPE_ADD));
		}
	}
	
	public void remove(AbstractOpticalComponent component)
	{
		AbstractOpticalComponent previous;
		AbstractOpticalComponent next;
		
		ComponentOutput beamInput = component.getVisibleInputComponent();
		
		if(beamInput != null)
		{
			previous = beamInput.getComponent();
		}
		else
		{
			previous = null;
		}
		
		ComponentInput beamOutput = component.getVisibleOutputComponent();
		
		if(beamOutput != null)
		{
			next = beamOutput.getComponent();
		}
		else
		{
			next = null;
		}
		
		if(previous != null && next != null)
		{
			// The component we're removing is in the middle of two others. We need to piece these two
			// others together.
			
			previous.setVisibleComponentOutput(new ComponentInput(next, next.getOpticalTrainInputIndex()));
			next.setVisibleComponentInput(new ComponentOutput(previous, previous.getOpticalTrainOutputIndex()));
		}
		else if(previous != null)
		{
			// The component we're removing is on the end of the train. Set the previous component's
			// output to null.
			
			previous.setVisibleComponentOutput(null);
		}
		else if(next != null)
		{
			// The component we're removing is at the start of the train. Set the next component's input
			// to null
			next.setVisibleComponentInput(null);
			
			// Set the next component to be the new start component
			startComponent = next;
		}
		else
		{
			// The component we're removing has no neighbours
			
			// Set the start component in the optical environment to null
			// Required for serialization functionality to work properly
			startComponent = null;
		}
		
		// Fire a change event
		fireChangeEvent(new OpticalEnvironmentChangeEvent(component, OpticalEnvironmentChangeEvent.TYPE_REMOVE));
	}
	
	/**
	 * Searches for identical names in the optical environment and returns a unique name.
	 * 
	 * This function is not threaded as the name must be set before the GUI does anything with that
	 * name. Threading this might result in different names being assigned to the same component, one
	 * unique and one not unique.
	 * 
	 * @param name
	 * @param opticalComponent
	 * @return
	 */
	public String getUniqueName(String name)
	{
		String uniqueName = name;
		int number = 0;
		
		boolean hasMatches = checkForNameMatchRecursively(uniqueName, startComponent, null);
		
		while(hasMatches)
		{
			// There are matched names - attempt to create a new unique name
			
			number++;
			uniqueName = name + " " + number;
				
			hasMatches = checkForNameMatchRecursively(uniqueName, startComponent, null);
		}
		
		return uniqueName;
	}
	
	// TODO: Make some sort of generic recursive 'search' method which traverses the tree and runs some
	// sort of callback method on each component. This would be useful because tree searching is 
	// implemented in multiple parts of the program. Maybe this would involve using a 'searchable'
	// interface of some sort?
	
	private boolean checkForNameMatchRecursively(String name, AbstractOpticalComponent component, AbstractOpticalComponent parent)
	{
		if(component != null)
		{			
			// check if component's name matches
			if(component.getName().equals(name))
			{
				return true;
			}
			else
			{
				// go through all the component's inputs and outputs and follow them up
				
				if(component.getComponentOutputs() != null)
				{
					for(ComponentInput output : component.getComponentOutputs())
					{
						if(output != null)
						{
							AbstractOpticalComponent nextComponent = output.getComponent();
							
							if(nextComponent != parent)
							{
								if(checkForNameMatchRecursively(name, nextComponent, component))
								{
									return true;
								}
							}
						}
					}
				}
				
				if(component.getComponentInputs() != null)
				{
					for(ComponentOutput input : component.getComponentInputs())
					{
						if(input != null)
						{
							AbstractOpticalComponent nextComponent = input.getComponent();
							
							if(!nextComponent.equals(parent))
							{
								if(checkForNameMatchRecursively(name, nextComponent, component))
								{
									return true;
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean isImageableLightSourcePresent()
	{
		return checkForImageableLightSourceRecursively(startComponent, null);
	}
	
	private boolean checkForImageableLightSourceRecursively(AbstractOpticalComponent component, AbstractOpticalComponent parent)
	{
		if(component != null)
		{			
			// check if component is an imageable light source
			if(component instanceof ImageableLightSourceInterface)
			{
				return true;
			}
			else
			{
				// now go through all the component's inputs and outputs and follow them up
				if(component.getComponentOutputs() != null)
				{
					for(ComponentInput output : component.getComponentOutputs())
					{
						if(output != null)
						{
							AbstractOpticalComponent nextComponent = output.getComponent();
							
							if(nextComponent != parent)
							{
								if(checkForImageableLightSourceRecursively(nextComponent, component))
								{
									return true;
								}
							}
						}
					}
				}
				
				if(component.getComponentInputs() != null)
				{
					for(ComponentOutput input : component.getComponentInputs())
					{
						if(input != null)
						{
							AbstractOpticalComponent nextComponent = input.getComponent();
							
							if(!nextComponent.equals(parent))
							{
								if(checkForImageableLightSourceRecursively(nextComponent, component))
								{
									return true;
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Fires a change event to the change listener object.
	 */
	private void fireChangeEvent(OpticalEnvironmentChangeEvent event)
	{
//		if(changeListener != null)
//		{
//			changeListener.changeMade(event);
//		}
	}
	
	public AbstractOpticalComponent getStartComponent()
	{
		return startComponent;
	}

	public void setStartComponent(AbstractOpticalComponent startComponent)
	{
		this.startComponent = startComponent;
	}

	public int getWorkbenchType()
	{
		return workbenchType;
	}

	public void setWorkbenchType(int workbenchType)
	{
		this.workbenchType = workbenchType;
	}

	public BitSet getAvailableLightSourceComponentsBitField()
	{
		return availableLightSourceComponentsBitField;
	}

	public void setAvailableLightSourceComponentsBitField(
			BitSet availableLightSourceComponentsBitField)
	{
		this.availableLightSourceComponentsBitField = availableLightSourceComponentsBitField;
	}

	public BitSet getAvailableNonLightSourceComponentsBitField()
	{
		return availableNonLightSourceComponentsBitField;
	}

	public void setAvailableNonLightSourceComponentsBitField(
			BitSet availableNonLightSourceComponentsBitField)
	{
		this.availableNonLightSourceComponentsBitField = availableNonLightSourceComponentsBitField;
	}

//	public OpticalEnvironmentChangeListener getChangeListener()
//	{
//		return changeListener;
//	}
//
//	public void set1ChangeListener(OpticalEnvironmentChangeListener changeListener)
//	{
//		this.changeListener = changeListener;
//	}
}
