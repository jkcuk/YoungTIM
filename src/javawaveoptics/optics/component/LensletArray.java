package javawaveoptics.optics.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;
import library.maths.MyMath;

/**
 * Rectangular array of lenslets.
 * Based on DovePrismArray
 * 
 * @author johannes
 */
public class LensletArray extends AbstractSimpleOpticalComponent implements Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = -178802900426242707L;

	/*
	 * Fields
	 */

	// array period
	protected double arrayPeriod;
	
	// focal length
	protected double focalLength;
	
	// Rotation angle (in degrees)
	protected double rotationAngle;
	
	// centre of one of the lenslets
	protected double xCentre, yCentre;
	
	/*
	 * GUI edit controls
	 */
	
	private transient JFormattedTextField
		rotationAngleTextField;
	
	private transient LengthField
		arrayPeriodLengthField,
		focalLengthLengthField,
		xCentreLengthField,
		yCentreLengthField;

	
	public LensletArray(String name, double arrayPeriod, double focalLength, double rotationAngle, double xCentre, double yCentre)
	{
		super(name);
		
		this.arrayPeriod = arrayPeriod;
		this.focalLength = focalLength;
		this.rotationAngle = rotationAngle;
		this.xCentre = xCentre;
		this.yCentre = yCentre;
	}
	
	public LensletArray()
	{
		this("Lenslet array", 1e-3, 1, 0, 0, 0);
	}

	@Override
	public String getComponentTypeName()
	{
		return "Lenslet array";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			inputBeam.passThroughLensletArray(arrayPeriod, focalLength, MyMath.deg2rad(rotationAngle), xCentre, yCentre);
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
		
		editPanel.add(UIBitsAndBobs.makeRow("Array period", arrayPeriodLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Focal length", focalLengthLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Rotation angle", rotationAngleTextField, "&deg;", true));
		editPanel.add(UIBitsAndBobs.makeRow("Centre (", xCentreLengthField, ",", yCentreLengthField, ")", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		arrayPeriodLengthField = new LengthField(this);
		arrayPeriodLengthField.setLengthInMetres(arrayPeriod);
		
		focalLengthLengthField = new LengthField(this);
		focalLengthLengthField.setLengthInMetres(focalLength);

		rotationAngleTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		rotationAngleTextField.setValue(Double.valueOf(rotationAngle));
		
		xCentreLengthField = new LengthField(this);
		xCentreLengthField.setLengthInMetres(xCentre);
		
		yCentreLengthField = new LengthField(this);
		yCentreLengthField.setLengthInMetres(yCentre);
	}

	@Override
	public void readWidgets()
	{
		super.readWidgets();

        if(arrayPeriodLengthField != null) arrayPeriod = arrayPeriodLengthField.getLengthInMetres();
        if(focalLengthLengthField != null) focalLength = focalLengthLengthField.getLengthInMetres();
        if(rotationAngleTextField != null) rotationAngle = ((Number)rotationAngleTextField.getValue()).doubleValue();
        if(xCentreLengthField != null) xCentre = xCentreLengthField.getLengthInMetres();
        if(yCentreLengthField != null) yCentre = yCentreLengthField.getLengthInMetres();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == arrayPeriodLengthField)
	    {
	        arrayPeriod = arrayPeriodLengthField.getLengthInMetres();
	    }
	    else if (source == focalLengthLengthField)
	    {
	        focalLength = focalLengthLengthField.getLengthInMetres();
	    }
	    else if (source == rotationAngleTextField)
	    {
	        rotationAngle = ((Number)rotationAngleTextField.getValue()).doubleValue();
	    }
	    else if (source == xCentreLengthField)
	    {
	        xCentre = xCentreLengthField.getLengthInMetres();
	    }
	    else if (source == yCentreLengthField)
	    {
	        yCentre = yCentreLengthField.getLengthInMetres();
	    }
		    
		// Fire an edit panel event
		editListener.editMade();
	}

	
	@Override
	public String getFormattedName()
	{
		return getName() + " (period="+arrayPeriod+", f="+focalLength+", @ " + rotationAngle + "&deg;, centre (" + xCentre + ", " + yCentre + ") )";
	}

	public double getArrayPeriod() {
		return arrayPeriod;
	}

	public void setArrayPeriod(double arrayPeriod) {
		this.arrayPeriod = arrayPeriod;
	}

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	public double getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(double rotationAngle) {
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