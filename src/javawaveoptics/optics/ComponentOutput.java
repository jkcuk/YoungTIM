package javawaveoptics.optics;

import java.io.Serializable;

import javawaveoptics.optics.component.AbstractOpticalComponent;

/**
 * Represents a light beam output for a component. Some components, e.g. beam splitters, have
 * more than one output; different outputs are distinguished simply by numbering them.
 * This class collects together all the information to identify a component's output, namely
 * the component and the number of the output.
 *  
 * @author Sean
 *
 */
public class ComponentOutput implements Serializable
{
	private static final long serialVersionUID = -393055695248610720L;

	// Reference to the component
	AbstractOpticalComponent component;
	
	// The output number of the component
	int number;
	
	/**
	 * Constructor. Takes an optical component and the output number we're interested in as input.
	 * 
	 * @param component		The optical component in question
	 * @param number		The output number in question
	 */
	public ComponentOutput(AbstractOpticalComponent component, int number)
	{
		this.component = component;
		this.number = number;
	}
	
	/**
	 * Returns the specific beam output we're interested in
	 * 
	 * @return		Beam output
	 */
	public BeamCrossSection calculateOutput()
	{
		return component.calculateOutput(number);
	}

	/*
	 * Getters and Setters
	 */
	
	public AbstractOpticalComponent getComponent() {
		return component;
	}

	public void setComponent(AbstractOpticalComponent component) {
		this.component = component;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}