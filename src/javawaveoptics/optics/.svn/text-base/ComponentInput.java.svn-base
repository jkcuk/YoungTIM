package javawaveoptics.optics;

import java.io.Serializable;

import javawaveoptics.optics.component.AbstractOpticalComponent;

/**
 * Represents a light beam input for a component. Some components, e.g. beam splitters, have
 * more than one input; different inputs are distinguished simply by numbering them.
 * This class collects together all the information to identify a component's input, namely
 * the component and the number of the input.
 *  
 * @author Sean
 *
 */
public class ComponentInput implements Serializable
{
	private static final long serialVersionUID = -1446822655327035748L;

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
	public ComponentInput(AbstractOpticalComponent component, int number)
	{
		this.component = component;
		this.number = number;
	}
	
	/**
	 * Tells the component to deal with the specified input.
	 * 
	 * @param inputBeam
	 */
	public void dealWithInput(BeamCrossSection inputBeam)
	{
		component.dealWithInput(number, inputBeam);
	}
	
	public String toString()
	{
		return "Output " + (number + 1) + " of " + component.getName();
	}

	/*
	 * Getters and Setters
	 */
	
	public AbstractOpticalComponent getComponent()
	{
		return component;
	}

	public void setComponent(AbstractOpticalComponent component)
	{
		this.component = component;
	}

	public int getNumber()
	{
		return number;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}
}