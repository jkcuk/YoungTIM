package javawaveoptics.optics.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;

/**
 * A Dove prism.
 * 
 * @author johannes
 */
public class DovePrism extends AbstractSimpleOpticalComponent implements Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = 8100735374446414693L;

	/*
	 * Fields
	 */
	
	// Rotation angle
	protected double rotationAngle; // in degrees
	
	/*
	 * GUI edit controls
	 */
	
	private transient JFormattedTextField
		rotationAngleTextField;

	
	/**
	 * @param name
	 * @param rotationAngle	in degrees
	 */
	public DovePrism(String name, double rotationAngle)
	{
		super(name);
		
		this.rotationAngle = rotationAngle;
	}
	
	public DovePrism()
	{
		this("Dove prism", 0);
	}

	@Override
	public String getComponentTypeName()
	{
		return "Dove prism";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			int initialWidth = inputBeam.getWidth();
			int initialHeight = inputBeam.getHeight();
			
			inputBeam.rotateAndZoom(-rotationAngle, 1, true);
			inputBeam.passThroughDovePrism();
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
		
		editPanel.add(UIBitsAndBobs.makeRow("Rotation angle", rotationAngleTextField, "&deg;", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		rotationAngleTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		rotationAngleTextField.setValue(Double.valueOf(rotationAngle));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

        if(rotationAngleTextField != null) rotationAngle = ((Number)rotationAngleTextField.getValue()).doubleValue();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == rotationAngleTextField)
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

	public double getRotationAngle()
	{
		return rotationAngle;
	}

	public void setRotationAngle(double rotationAngle)
	{
		this.rotationAngle = rotationAngle;
	}
}