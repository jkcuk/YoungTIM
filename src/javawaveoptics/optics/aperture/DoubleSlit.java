package javawaveoptics.optics.aperture;

import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;
import library.maths.MyMath;

public class DoubleSlit extends AbstractAperture implements Serializable //, PropertyChangeListener
{
	private static final long serialVersionUID = 6873634258736627530L;

	/*
	 * Fields
	 */

	// slit width
	protected double slitWidth, slitSeparation, xCentre, yCentre;
	
	// Rotation angle
	protected double rotationAngle;
	
	/*
	 * GUI edit controls
	 */
	
	private transient LengthField slitWidthLengthField, slitSeparationLengthField, xCentreLengthField, yCentreLengthField;
	private transient JFormattedTextField rotationAngleTextField;

	
	public DoubleSlit(String name, double slitSeparation, double slitWidth, double rotationAngle, double xCentre, double yCentre)
	{
		super(name);
		
		this.slitWidth = slitWidth;
		this.slitSeparation = slitSeparation;
		this.rotationAngle = rotationAngle;
		this.xCentre = xCentre;
		this.yCentre = yCentre;
	}
	
	public DoubleSlit()
	{
		this("Double slit", 1e-3, 2.5e-4, 0, 0, 0);
	}

	@Override
	public String getApertureTypeName()
	{
		return "Double-slit aperture";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			int initialWidth = inputBeam.getWidth();
			int initialHeight = inputBeam.getHeight();
			
			// inputBeam.rotate(-rotationAngle, true);
			inputBeam.passThroughDoubleSlitAperture(slitSeparation, slitWidth, MyMath.deg2rad(rotationAngle), xCentre, yCentre);
			// inputBeam.rotate(rotationAngle, true);
			inputBeam.changeDimensions(initialWidth, initialHeight);
		}
		
		return inputBeam;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow("Slit separation (centre-to-centre)", slitSeparationLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Slit width", slitWidthLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Angle of slits with y direction", rotationAngleTextField, "&deg;", true));
		editPanel.add(UIBitsAndBobs.makeRow("Centre (", xCentreLengthField, ",", yCentreLengthField, ")", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		slitWidthLengthField = new LengthField(null);
		slitWidthLengthField.setLengthInMetres(slitWidth);
		
		slitSeparationLengthField = new LengthField(null);
		slitSeparationLengthField.setLengthInMetres(slitSeparation);

		rotationAngleTextField = UIBitsAndBobs.makeDoubleFormattedTextField(null);
		rotationAngleTextField.setValue(Double.valueOf(rotationAngle));

		xCentreLengthField = new LengthField(null);
		xCentreLengthField.setLengthInMetres(xCentre);

		yCentreLengthField = new LengthField(null);
		yCentreLengthField.setLengthInMetres(yCentre);
	}
	
	@Override
	public void readWidgets()
	{
        if(slitWidthLengthField != null) slitWidth = slitWidthLengthField.getLengthInMetres();
        if(slitSeparationLengthField != null) slitSeparation = slitSeparationLengthField.getLengthInMetres();
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
//	    else if (source == slitSeparationLengthField)
//	    {
//	        slitSeparation = slitSeparationLengthField.getLengthInMetres();
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

	public double getSlitSeparation() {
		return slitSeparation;
	}

	public void setSlitSeparation(double slitSeparation) {
		this.slitSeparation = slitSeparation;
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