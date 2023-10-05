package javawaveoptics.optics.aperture;

import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;
import library.maths.MyMath;

public class Slit extends AbstractAperture implements Serializable //, PropertyChangeListener
{
	private static final long serialVersionUID = -1042780980585873395L;

	/*
	 * Fields
	 */

	// slit width
	protected double slitWidth, xCentre, yCentre;
	
	// Rotation angle
	protected double rotationAngle;
	
	/*
	 * GUI edit controls
	 */
	
	private transient LengthField slitWidthLengthField, xCentreLengthField, yCentreLengthField;
	private transient JFormattedTextField rotationAngleTextField;

	
	public Slit(String name, double slitWidth, double rotationAngle, double xCentre, double yCentre)
	{
		super(name);
		
		this.slitWidth = slitWidth;
		this.rotationAngle = rotationAngle;
		this.xCentre = xCentre;
		this.yCentre = yCentre;
	}
	
	public Slit()
	{
		this("Slit", 2.5e-4, 0, 0, 0);
	}

	@Override
	public String getApertureTypeName()
	{
		return "Slit aperture";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			int initialWidth = inputBeam.getWidth();
			int initialHeight = inputBeam.getHeight();
			
			// inputBeam.rotate(-rotationAngle, true);
			inputBeam.passThroughSlitAperture(slitWidth, MyMath.deg2rad(rotationAngle), xCentre, yCentre);
			// inputBeam.rotate(rotationAngle, true);
			inputBeam.changeDimensions(initialWidth, initialHeight);
		}
		
		return inputBeam;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow("Slit width", slitWidthLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Angle of slit with y direction", rotationAngleTextField, "&deg;", true));
		editPanel.add(UIBitsAndBobs.makeRow("Centre (", xCentreLengthField, ",", yCentreLengthField, ")", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		slitWidthLengthField = new LengthField(null);
		slitWidthLengthField.setLengthInMetres(slitWidth);
		
		rotationAngleTextField = UIBitsAndBobs.makeDoubleFormattedTextField(null);
		rotationAngleTextField.setValue(new Double(0));

		xCentreLengthField = new LengthField(null);
		xCentreLengthField.setLengthInMetres(xCentre);

		yCentreLengthField = new LengthField(null);
		yCentreLengthField.setLengthInMetres(yCentre);
	}

	@Override
	public void readWidgets()
	{
        if(slitWidthLengthField != null) slitWidth = slitWidthLengthField.getLengthInMetres();
        if(rotationAngleTextField != null) rotationAngle = ((Number)rotationAngleTextField.getValue()).doubleValue();
        if(xCentreLengthField != null) xCentre = xCentreLengthField.getLengthInMetres();
        if(yCentreLengthField != null) yCentre = yCentreLengthField.getLengthInMetres();
	}
	
//	@Override
//	public void propertyChange(PropertyChangeEvent e)
//	{
//	    Object source = e.getSource();
//	    
//	    if (source == slitWidthLengthField)
//	    {
//	        slitWidth = slitWidthLengthField.getLengthInMetres();
//	    }
//	    else if (source == rotationAngleTextField)
//	    {
//	        rotationAngle = ((Number)rotationAngleTextField.getValue()).doubleValue();
//	    }
//	    else if (source == xCentreLengthField)
//	    {
//	        xCentre = xCentreLengthField.getLengthInMetres();
//	    }
//	    else if (source == yCentreLengthField)
//	    {
//	        yCentre = yCentreLengthField.getLengthInMetres();
//	    }
//	    
//		// Fire an edit panel event
//		// editListener.editMade();
//	}

	public double getSlitWidth()
	{
		return slitWidth;
	}

	public void setSlitWidth(double slitWidth)
	{
		this.slitWidth = slitWidth;
	}

	public double getRotationAngle()
	{
		return rotationAngle;
	}

	public void setRotationAngle(double rotationAngle)
	{
		this.rotationAngle = rotationAngle;
	}

	public double getxCentre() {
		return xCentre;
	}

	public void setxCentre(double xCentre) {
		this.xCentre = xCentre;
	}

	public double getyCentre() {
		return yCentre;
	}

	public void setyCentre(double yCentre) {
		this.yCentre = yCentre;
	}
}