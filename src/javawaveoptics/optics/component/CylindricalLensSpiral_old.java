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
 * A phase hologram of a cylindrical-lens spiral.
 * This is a cylindrical lens of focal length f, bent into a logarithmic spiral of the form r = exp(b (phi-phi0)), where r and phi are polar coordinates.
 * The parameter b controls how tightly the spiral is wound; phi0 controls the absolute rotation angle of the spiral.
 * 
 * @author Johannes
 */
public class CylindricalLensSpiral_old extends AbstractSimpleOpticalComponent implements SimplePixelWiseOpticalComponentInterface, Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = -588166946856356389L;

	private static final String COMPONENT_TYPE_NAME = "Cylindrical-lens spiral";
	
	/*
	 * Fields
	 */

	/**
	 * the cylindrical lens's focal length;
	 * the cross-section of the cylindrical lens is Phi(t) = (pi d^2)(lambda f), where d is the distance from the spiral of the form r = exp(b (phi - phi0))
	 */
	private double focalLength;
	
	/**
	 * the centre of the cylindrical lens follows the logarithmic spiral r = exp(b (phi-phi0));
	 * set this using setB(), so that deltaNu is pre-calculated accordingly!
	 */
	private double b;
	
	/**
	 * relative rotation angle of the spiral (in radians);
	 */
	private double phi0;
	
	// private variables
	
	/**
	 * 2*pi*b;
	 * pre-calculate it when b is being set
	 */
	private double b2pi;
	
	/**
	 * a variable that is required during the calculation of the phase;
	 * depends only on b, so pre-calculate it when b is being set
	 */
	private double deltaNu;
	
	/*
	 * GUI edit controls
	 */
	
	private transient LengthField focalLengthField;
	private transient JFormattedTextField bTextField, phi0DegTextField;
	
	
	public CylindricalLensSpiral_old(String name, double focalLength, double b, double phi0)
	{
		super(name);
		
		setFocalLength(focalLength);
		setB(b);
		setPhi0(phi0);
	}
	
	/**
	 * Null constructor. Creates a lens with default values. This requires no
	 * parameters.
	 */
	public CylindricalLensSpiral_old()
	{
		this(COMPONENT_TYPE_NAME, 1, 0.1, 0);
	}

	@Override
	public String getComponentTypeName()
	{
		return COMPONENT_TYPE_NAME;
	}
		
	/**
	 * @param r
	 * @param phi
	 * @return	the number of the winding on which the point with polar coordinates (r, phi) lies
	 */
	private double calculateWindingNumber(double r, double phi)
	{
		// first solve the equation r == exp[b (phi + nu*2*pi - phi0)]
		double nu = (Math.log(r) - b*(phi-phi0)) / b2pi;
		return Math.ceil(nu - deltaNu);
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			for(int j = 0; j < inputBeam.getHeight(); j++)
			{
				double y = inputBeam.getY(j);
				
				for(int i = 0; i < inputBeam.getWidth(); i++)
				{
					double x = inputBeam.getX(i);

					// calculate polar coordinates
					double r = Math.sqrt(x*x + y*y);
					double phi = Math.atan2(y, x);
					
					// calculate the winding number on which the pixel lies
					double n = calculateWindingNumber(r, phi);
					
					// calculate the radial coordinate for the centre of the nth winding in the phi direction
					double rN = Math.exp(b*(phi+n*2*Math.PI-phi0));
					
					// locally shift the phase of the amplitude cross-section
					double phaseShift = inputBeam.getLensPhase(focalLength, MyMath.sqr(r-rN));
					double cosPhaseShift = Math.cos(phaseShift);
					double sinPhaseShift = Math.sin(phaseShift);
					double re = inputBeam.getElementRe(i,j);
					double im = inputBeam.getElementIm(i,j);
				  
					// real part of (re + i im) exp(i phaseShift)
					// imaginary part of (re + i im) exp(i phaseShift)
					inputBeam.setElement(i,j,
							new Complex(re * cosPhaseShift - im * sinPhaseShift, re * sinPhaseShift + im * cosPhaseShift));
				}
			}
		}
		
		return inputBeam;
	}
	
	@Override
	public BeamCrossSection changePixelInInputBeam(int i, int j, BeamCrossSection inputBeam)
	{
		// first find the Cartesian coordinates for pixel (i, j)
		double x = inputBeam.getX(i);
		double y = inputBeam.getY(j);
		
		// calculate polar coordinates
		double r = Math.sqrt(x*x + y*y);
		double phi = Math.atan2(y, x);
		
		// calculate the winding number on which the pixel lies
		double n = calculateWindingNumber(r, phi);
		
		// calculate the radial coordinate for the centre of the nth winding in the phi direction
		double rN = Math.exp(b*(phi+n*2*Math.PI-phi0));
		
		// locally shift the phase of the amplitude cross-section
		double phaseShift = inputBeam.getLensPhase(focalLength, MyMath.sqr(r-rN));
		double cosPhaseShift = Math.cos(phaseShift);
		double sinPhaseShift = Math.sin(phaseShift);
		double re = inputBeam.getElementRe(i,j);
		double im = inputBeam.getElementIm(i,j);
	  
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
		
		editPanel.add(UIBitsAndBobs.makeRow("A cylindrical lens of focal length", focalLengthField, ",", true));
		editPanel.add(UIBitsAndBobs.makeRow("wound into a spiral of the form exp[", bTextField, "*(phi-", phi0DegTextField, "&deg;)]", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		focalLengthField = new LengthField(this);
		focalLengthField.setLengthInMetres(focalLength);
		
		bTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		bTextField.setValue(Double.valueOf(b));

		phi0DegTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		phi0DegTextField.setValue(Double.valueOf(MyMath.rad2deg(phi0)));
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == focalLengthField)
	    {
	        setFocalLength(focalLengthField.getLengthInMetres());
	    }
	    else if (source == bTextField)
	    {
	        setB(((Number)bTextField.getValue()).doubleValue());
	    }
	    else if (source == phi0DegTextField)
	    {
	        setPhi0(MyMath.deg2rad(((Number)phi0DegTextField.getValue()).doubleValue()));
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

	public double getB() {
		return b;
	}

	/**
	 * Set b, and pre-calculate b2pi and deltaNu
	 * @param b
	 */
	public void setB(double b) {
		this.b = b;
		b2pi = 2*Math.PI*b;
		deltaNu = Math.log(0.5*(1+Math.exp(b2pi))) / b2pi;
	}

	public double getPhi0() {
		return phi0;
	}

	public void setPhi0(double phi0) {
		this.phi0 = phi0;
	}

}