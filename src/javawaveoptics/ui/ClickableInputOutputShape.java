package javawaveoptics.ui;

import java.awt.geom.RectangularShape;

/**
 * Class that defines a shape representing a clickable input or output, for example on
 * the beam splitter image.
 * 
 * @author Sean
 */
public class ClickableInputOutputShape
{
	private RectangularShape shape;
	private int inputOutputNumber;
	
	public ClickableInputOutputShape(RectangularShape shape, int inputOutputNumber)
	{
		this.shape = shape;
		this.inputOutputNumber = inputOutputNumber;
	}
	
	public RectangularShape getShape()
	{
		return shape;
	}

	public void setShape(RectangularShape shape)
	{
		this.shape = shape;
	}

	public int getInputOutputNumber()
	{
		return inputOutputNumber;
	}

	public void setInputOutputNumber(int inputOutputNumber)
	{
		this.inputOutputNumber = inputOutputNumber;
	}
}