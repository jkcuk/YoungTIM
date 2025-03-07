package javawaveoptics.optics.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;
import library.maths.Complex;
import library.maths.MyMath;

/**
 * Defines a thin cylindrical lens.
 * Based on the Lens class.
 * 
 * @author Johannes
 */
public class CylindricalLens extends AbstractSimpleOpticalComponent implements SimplePixelWiseOpticalComponentInterface, Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = -6127995421406431186L;

	/*
	 * Fields
	 */

	// Focal length of the lens
	double focalLength;
	
	double angleOfModulationWithXDirection;
	
	/*
	 * GUI edit controls
	 */
	
	private transient LengthField focalLengthField;
	
	private transient JFormattedTextField angleOfModulationWithXDirectionTextField;
	
	
	public CylindricalLens(String name, double focalLength, double axisAngleWithXAxis)
	{
		super(name);
		
		this.focalLength = focalLength;
		this.angleOfModulationWithXDirection = axisAngleWithXAxis;
	}
	
	/**
	 * Null constructor. Creates a lens with default values. This requires no
	 * parameters.
	 */
	public CylindricalLens()
	{
		this("Cylindrical lens", 1, 0);
	}

	@Override
	public String getComponentTypeName()
	{
		return "Cylindrical lens";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			inputBeam.passThroughCylindricalLens(focalLength, MyMath.deg2rad(-angleOfModulationWithXDirection));
		}
		
		return inputBeam;
	}
	
	@Override
	public BeamCrossSection changePixelInInputBeam(int i, int j, BeamCrossSection inputBeam)
	{
		double phaseShift, cosPhaseShift, sinPhaseShift, re, im;
		double sinAxisAngleWithXAxis = Math.sin(MyMath.deg2rad(-angleOfModulationWithXDirection));
		double cosAxisAngleWithXAxis = Math.cos(MyMath.deg2rad(-angleOfModulationWithXDirection));
  
		double ySin = inputBeam.getY(j) * sinAxisAngleWithXAxis;
		double xCos = inputBeam.getX(i) * cosAxisAngleWithXAxis;
				
		double r = xCos - ySin;	// distance from axis
				
		// locally shift the phase of the amplitude cross-section
		phaseShift = inputBeam.getLensPhase(focalLength, r*r);
		cosPhaseShift = Math.cos(phaseShift);
		sinPhaseShift = Math.sin(phaseShift);
		re = inputBeam.getElementRe(i,j);
		im = inputBeam.getElementIm(i,j);
	  
		// real part of (re + i im) exp(i phaseShift)
		// imaginary part of (re + i im) exp(i phaseShift)
		inputBeam.setElement(i,j,
				new Complex(re * cosPhaseShift - im * sinPhaseShift, re * sinPhaseShift + im * cosPhaseShift));

		return inputBeam;
	}


	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Edit focal length control
		 */
		
		editPanel.add(UIBitsAndBobs.makeRow("Focal length", focalLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Angle of phase modulation with x direction", angleOfModulationWithXDirectionTextField, "&deg;", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		focalLengthField = new LengthField(this);
		focalLengthField.setLengthInMetres(focalLength);
		
		angleOfModulationWithXDirectionTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		angleOfModulationWithXDirectionTextField.setValue(Double.valueOf(0));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

        if(focalLengthField != null) focalLength = focalLengthField.getLengthInMetres();
        if(angleOfModulationWithXDirectionTextField != null) angleOfModulationWithXDirection = ((Number)angleOfModulationWithXDirectionTextField.getValue()).doubleValue();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == focalLengthField)
	    {
	        focalLength = focalLengthField.getLengthInMetres();
			// System.out.println("Cylindrical lens: focal length="+focalLength);			
	    }
	    else if (source == angleOfModulationWithXDirectionTextField)
	    {
	        angleOfModulationWithXDirection = ((Number)angleOfModulationWithXDirectionTextField.getValue()).doubleValue();
			// System.out.println("Cylindrical lens: angleOfModulationWithXDirection="+angleOfModulationWithXDirection);			
	    }
	    
		// Fire an edit panel event
		editListener.editMade();
	}
	
	@Override
	public String getFormattedName()
	{
		return "f = " + Double.toString(focalLength) + "m";
		// return getName() + " (f = " + Double.toString(focalLength) + "m)";
	}

	public double getFocalLength()
	{
		return focalLength;
	}

	public void setFocalLength(double focalLength)
	{
		this.focalLength = focalLength;
	}
}