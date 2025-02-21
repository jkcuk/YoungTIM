package javawaveoptics.optics.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;
import javawaveoptics.utility.MathsUtilities;

public class DovePrismArray extends AbstractSimpleOpticalComponent implements Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = 1821764373919687332L;

	/*
	 * Fields
	 */

	// Dove prism width
	protected int prismWidth;
	
	// Rotation angle
	protected double rotationAngle;
	
	/*
	 * GUI edit controls
	 */
	
	private transient JFormattedTextField
		prismWidthTextField,
		rotationAngleTextField;

	
	public DovePrismArray(String name, int numberOfPrisms, double rotationAngle)
	{
		super(name);
		
		this.prismWidth = numberOfPrisms;
		this.rotationAngle = rotationAngle;
	}
	
	public DovePrismArray()
	{
		this("Dove-prism array", 8, 45);
	}

	@Override
	public String getComponentTypeName()
	{
		return "Dove-prism array";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			int initialWidth = inputBeam.getWidth();
			int initialHeight = inputBeam.getHeight();
			
			inputBeam.rotateAndZoom(-rotationAngle, 1, true);
			inputBeam.passThroughDovePrismArray(prismWidth);
			inputBeam.rotateAndZoom(rotationAngle, 1, true);
			inputBeam.changeDimensions(initialWidth, initialHeight);
		}
		
		return inputBeam;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Edit number of prisms control
		 */
		
		editPanel.add(UIBitsAndBobs.makeRow("Prism width", prismWidthTextField, "elements", true));
		editPanel.add(UIBitsAndBobs.makeRow("Rotation angle", rotationAngleTextField, "&deg;", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		prismWidthTextField = UIBitsAndBobs.makeIntFormattedTextField(this);
		prismWidthTextField.setValue(Integer.valueOf(prismWidth));
		
		rotationAngleTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		rotationAngleTextField.setValue(Double.valueOf(rotationAngle));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

        if(prismWidthTextField != null) prismWidth = ((Number)prismWidthTextField.getValue()).intValue();
        if(rotationAngleTextField != null) rotationAngle = ((Number)rotationAngleTextField.getValue()).doubleValue();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == prismWidthTextField)
	    {
	        prismWidth = ((Number)prismWidthTextField.getValue()).intValue();
	    }
	    else if (source == rotationAngleTextField)
	    {
	        rotationAngle = ((Number)rotationAngleTextField.getValue()).doubleValue();
	    }
		    
		// Fire an edit panel event
		editListener.editMade();
	}

	
	@Override
	public String getFormattedName()
	{
		return getName() + " (@ " + rotationAngle + "&deg;)";
	}

	public double getPrismWidth()
	{
		return prismWidth;
	}

	public void setPrismWidth(int prismWidth)
	{
		// Prism width must be a power of 2, so as to enforce an integer number of prisms in the
		// array (which is itself a power of two in width)
		if(MathsUtilities.isPowerOfTwo(prismWidth))
		{
			this.prismWidth = prismWidth;
		}
		else
		{
			throw new NumberFormatException("Prism width must be a power of 2");
		}
	}

	public double getRotationAngle()
	{
		return rotationAngle;
	}

	public void setRotationAngle(double rotationAngle)
	{
		this.rotationAngle = rotationAngle;
	}
}