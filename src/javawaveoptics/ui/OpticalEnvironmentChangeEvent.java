package javawaveoptics.ui;

import java.util.EventObject;

import javawaveoptics.optics.component.AbstractOpticalComponent;

/**
 * Defines a change event for an optical environment, such as an addition or removal of an optical
 * component.
 * 
 * @author Sean
 */
public class OpticalEnvironmentChangeEvent extends EventObject
{
	private static final long serialVersionUID = 6181859723226083904L;

	// Event types
	public static int
		TYPE_ADD	=	0,
		TYPE_REMOVE	=	1;
	
	// The event type
	private int type;
	
	public OpticalEnvironmentChangeEvent(AbstractOpticalComponent source, int type)
	{
		super(source);
		
		this.type = type;
	}

	public int getType()
	{
		return type;
	}
}
