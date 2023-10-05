package javawaveoptics.optics.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.optics.ComponentInput;
import javawaveoptics.optics.ComponentOutput;
import javawaveoptics.ui.UIBitsAndBobs;
import javawaveoptics.utility.ImageableLightSourceInterface;

/**
 * Defines a clone of a component that is already in the system
 * 
 * @author Johannes
 */
public class CloneOfComponent extends AbstractSimpleOpticalComponent
implements Serializable, ActionListener, ImageableLightSourceInterface // PropertyChangeListener
{
	private static final long serialVersionUID = 7491052663422570197L;
	
	/*
	 * Fields
	 */

	// List of suitable components in the system
	private ArrayList<AbstractSimpleOpticalComponent> components;
	
	// The selected plane to image
	private AbstractSimpleOpticalComponent selectedComponent;
	
	// where to start looking for planes; usually "this", but can also be another component
	private AbstractOpticalComponent searchTreeStartComponent;
	
	/*
	 * GUI edit controls
	 */
	
	private transient JComboBox<AbstractSimpleOpticalComponent> componentComboBox;
	

	// constructors

	public CloneOfComponent(String name, AbstractSimpleOpticalComponent selectedComponent)
	{
		super(name);
		
		this.selectedComponent = selectedComponent;
		searchTreeStartComponent = this;
	}
	
	public CloneOfComponent(AbstractSimpleOpticalComponent selectedComponent)
	{
		this("("+selectedComponent.getName()+")'", selectedComponent);
	}

	public CloneOfComponent(String name)
	{
		this(name, null);
	}
	
	public CloneOfComponent()
	{
		this("Clone");
	}

	@Override
	public String getComponentTypeName()
	{
		return "Clone of component";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam) throws Exception
	{
		if(inputBeam != null)
		{
			if(selectedComponent != null)
			{
				return selectedComponent.fromInputBeamCalculateOutputBeam(inputBeam);
			}
		}
		
		return inputBeam;
	}

	/**
	 * Overrides the default behaviour of the parent class so that the list of planes
	 * to image is updated before the edit panel is viewed.
	 */
	@Override
	public JComponent getEditPanel()
	{
		// First check whether or not the edit panel has been instantiated. If it has
		// been, then the plane combo box might be out of date and must therefore
		// be refreshed.
		if(editPanel != null)
		{			
			// first update the combo box to list all...
			initialiseComponentComboBox();
		}
		
		// Return the edit panel, complete with up-to-date combo box.
		return super.getEditPanel();
	}
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Beam type drop down box
		 */
		editPanel.add(UIBitsAndBobs.makeRow("Clone of", componentComboBox, true));
				
		editPanel.add(Box.createVerticalGlue());
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		componentComboBox = new JComboBox<AbstractSimpleOpticalComponent>();
		initialiseComponentComboBox();
		componentComboBox.addActionListener(this);
		componentComboBox.setActionCommand("Component selected");

		// imageCounterTextField = UIBitsAndBobs.makeIntFormattedTextField(this);
		// imageCounterTextField.setValue(new Integer(0));
		
		// already initialised above
		// roundTripCounter = new Counter(0, false, false, true);
	}

	@Override
	public void readWidgets()
	{
		super.readWidgets();

		// Set the new selected component output
		if(componentComboBox != null) selectedComponent = (AbstractSimpleOpticalComponent)componentComboBox.getSelectedItem();
		
		selectedComponent.readWidgets();
	}
		
	/**
	 * Adds suitable components to the combo box recursively. This class is able to be run as a thread.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialiseComponentComboBox()
	{
		// Disable the combo box while we update it
		componentComboBox.setEnabled(false);
				
		// (Re)create the image components list
		components = new ArrayList<AbstractSimpleOpticalComponent>();
		
		// add a component that represents "None"
		components.add(Plane.NO_PLANE);
							
		// Add output(s) of the output attached to this component to the outputBeams list, recursively
		addComponentToListRecursively(searchTreeStartComponent, null);
				
		// Set the combo box model from list of components
		componentComboBox.setModel(new DefaultComboBoxModel(components.toArray()));
				
		// Set the selected item
		//
		// We set the selected item only if it is present in the list. Otherwise we
		// have nothing selected. This prevents the default behaviour of Java
		// selecting the next item in the list, which can give undesired results.
		//
		// Note: we don't use indices here as the list may have items added before the index in question.
		// Instead we use a reference to the selected BeamInput object itself, which there should only be
		// one of.
		if(components.contains(selectedComponent))
		{
			componentComboBox.setSelectedItem(selectedComponent);
		}
		else
		{
			componentComboBox.setSelectedItem(Plane.NO_PLANE);	// setSelectedIndex(-1);
		}
		
		// Re-enable the combo box now that we've updated it
		componentComboBox.setEnabled(true);
	}
	
	private void addComponentToListRecursively(AbstractOpticalComponent component, AbstractOpticalComponent parent)
	{
		if(component != null)
		{
			// if this component is an AbstractSimpleOpticalComponent, add it to the list
			if((component instanceof AbstractSimpleOpticalComponent) && !(component instanceof CloneOfComponent))
			{
				// Add this component to the list
				components.add((AbstractSimpleOpticalComponent)component);
			}
			
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
							addComponentToListRecursively(nextComponent, component);
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
							addComponentToListRecursively(nextComponent, component);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		
		if(command.equals("Component selected"))
		{
			// Set the new selected component output
			selectedComponent = (AbstractSimpleOpticalComponent)componentComboBox.getSelectedItem();
			
			// Fire an edit panel event
			if(editListener != null) editListener.editMade();
		}
	}

	@Override
	public String getFormattedName()
	{
		if(selectedComponent != null)
		{
			return "(" + ((AbstractSimpleOpticalComponent)selectedComponent).getName() + ")'";
		}
		else
		{
			return "Clone (no original component selected)";
		}
	}
	
	public AbstractSimpleOpticalComponent getSelectedComponent() {
		return selectedComponent;
	}

	public void setSelectedComponent(AbstractSimpleOpticalComponent selectedComponent)
	{
		this.selectedComponent = selectedComponent;
	}

	public AbstractOpticalComponent getSearchTreeStartComponent() {
		return searchTreeStartComponent;
	}

	public void setSearchTreeStartComponent(
			AbstractOpticalComponent searchTreeStartComponent) {
		this.searchTreeStartComponent = searchTreeStartComponent;
	}
	
//	@Override
//	public BufferedImage getComponentImage()
//	{
//		if(selectedComponent != null)
//		{
//			return selectedComponent.getComponentImage();
//		}
//		
//		return super.getComponentImage();
//	}

}