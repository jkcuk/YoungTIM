package javawaveoptics.optics.aperture;

import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;
import library.maths.MyMath;

/**
 * An aperture in the shape of a regular <i>numberOfSides</i>-sided polygon of inradius (i.e. centre-to-sides radius) <i>radius</i>
 * @author johannes
 *
 */
public class PolygonalAperture extends AbstractAperture implements Serializable //, PropertyChangeListener
{
	private static final long serialVersionUID = 4898688661968949159L;

	/*
	 * Fields
	 */

	// number of sides
	protected int numberOfSides;

	// slit width
	protected double radius, xCentre, yCentre;
	
	// Rotation angle of first side
	protected double rotationAngle;
		
	/*
	 * GUI edit controls
	 */
	
	private transient JFormattedTextField numberOfSidesField;
	private transient LengthField radiusLengthField, xCentreLengthField, yCentreLengthField;
	private transient JFormattedTextField rotationAngleTextField;

	
	public PolygonalAperture(String name, int numberOfSides, double radius, double rotationAngle, double xCentre, double yCentre)
	{
		super(name);
		
		this.numberOfSides = numberOfSides;
		this.radius = radius;
		this.rotationAngle = rotationAngle;
		this.xCentre = xCentre;
		this.yCentre = yCentre;
	}
	
	public PolygonalAperture()
	{
		this("Polygonal aperture", 3, 2.5e-4, 0, 0, 0);
	}

	@Override
	public String getApertureTypeName()
	{
		return "Polygonal aperture";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
//			int initialWidth = inputBeam.getWidth();
//			int initialHeight = inputBeam.getHeight();
			
			// inputBeam.rotate(-rotationAngle, true);
			for(int i = 0; i < numberOfSides; i++)
			{
				inputBeam.passPastKnifeEdge(radius, MyMath.deg2rad(360./numberOfSides*i+rotationAngle), xCentre, yCentre);
			}
			// inputBeam.rotate(rotationAngle, true);
			// inputBeam.changeDimensions(initialWidth, initialHeight);
		}
		
		return inputBeam;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow("Number of sides", numberOfSidesField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Radius (centre-to-sides)", radiusLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Angle of first side with y direction", rotationAngleTextField, "&deg;", true));
		editPanel.add(UIBitsAndBobs.makeRow("Centre (", xCentreLengthField, ",", yCentreLengthField, ")", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		numberOfSidesField = UIBitsAndBobs.makeIntFormattedTextField(null);
		numberOfSidesField.setValue(Integer.valueOf(numberOfSides));
		
		radiusLengthField = new LengthField(null);
		radiusLengthField.setLengthInMetres(radius);
		
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
    	if(numberOfSidesField != null) numberOfSides = ((Number)numberOfSidesField.getValue()).intValue();
        if(radiusLengthField != null) radius = radiusLengthField.getLengthInMetres();
        if(rotationAngleTextField != null) rotationAngle = ((Number)rotationAngleTextField.getValue()).doubleValue();
        if(xCentreLengthField != null) xCentre = xCentreLengthField.getLengthInMetres();
        if(yCentreLengthField != null) yCentre = yCentreLengthField.getLengthInMetres();
	}
	
//	@Override
//	public void propertyChange(PropertyChangeEvent e)
//	{
//	    Object source = e.getSource();
//	    
//	    if (source == numberOfSidesField)
//	    {
//	    	numberOfSides = ((Number)numberOfSidesField.getValue()).intValue();
//	    }
//	    else if (source == radiusLengthField)
//	    {
//	        radius = radiusLengthField.getLengthInMetres();
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

	public int getNumberOfSides() {
		return numberOfSides;
	}

	public void setNumberOfSides(int numberOfSides) {
		this.numberOfSides = numberOfSides;
	}

	public double getRadius()
	{
		return radius;
	}

	public void setRadius(double radius)
	{
		this.radius = radius;
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