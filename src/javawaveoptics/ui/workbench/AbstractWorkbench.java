package javawaveoptics.ui.workbench;

import javax.swing.JPanel;

import javawaveoptics.optics.ComponentInput;
import javawaveoptics.optics.ComponentOutput;
import javawaveoptics.optics.component.AbstractOpticalComponent;
import javawaveoptics.optics.environment.AbstractOpticalEnvironment;

public abstract class AbstractWorkbench extends JPanel
{
	private static final long serialVersionUID = -566483615143699989L;
	
	// The optical environment to be used
	protected AbstractOpticalEnvironment opticalEnvironment;
	
	/**
	 * Defines a workbench GUI component.
	 * 
	 * @param opticalEnvironment
	 */
	public AbstractWorkbench(AbstractOpticalEnvironment opticalEnvironment)
	{
		this.opticalEnvironment = opticalEnvironment;
	}

	/**
	 * (Re)draws the workbench in its current state.
	 */
	//public abstract void drawWorkbench();
	
	public abstract AbstractOpticalComponent getStartComponent();
	
	public AbstractOpticalEnvironment getOpticalEnvironment()
	{
		return opticalEnvironment;
	}

	public void setOpticalEnvironment(AbstractOpticalEnvironment opticalEnvironment)
	{
		this.opticalEnvironment = opticalEnvironment;
	}
	
	/**
	 * read the widgets of all components in the optical environment
	 */
	public void readAllWidgets()
	{
		readWidgetsRecursively(getStartComponent(), null);
	}
	
	private void readWidgetsRecursively(AbstractOpticalComponent component, AbstractOpticalComponent parent)
	{
		if(component != null)
		{
			component.readWidgets();
						
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
							readWidgetsRecursively(nextComponent, component);
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
							readWidgetsRecursively(nextComponent, component);
						}
					}
				}
			}
		}
	}

	
	/**
	 * @return	true if the "Luxury buttons" (Load, Save, and Clear data in all planes) should be shown, false otherwise
	 */
	public abstract boolean showLoadAndSaveButtons();
	
	/**
	 * @return	true if the "Research buttons" (Clear data in all planes and text field that enables simulation of several round trips)
	 * should be shown, false otherwise
	 */
	public abstract boolean showResearchButtons();
}
