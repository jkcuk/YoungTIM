package javawaveoptics.optics.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;
import library.maths.Complex;
import library.maths.MyMath;

/**
 * A phase hologram of a cylindrical-lens spiral.
 * The spiral is either a logarithmic spiral, or an Archimedean spiral.
 * 
 * In the case of the logarithmic spiral, this is cylindrical lens of focal length <i>f</i>, bent into a logarithmic spiral of the form r = exp(b (phi-phi0)),
 * where r and phi are polar coordinates.
 * 
 * In the case of the Archimedean spiral, this is a cylindrical lens whose focal length varies along its length, bent into an Archimedean spiral of the form
 * r = b (phi - phi0).
 * The focal length is such that, if the centre of the cylindrical lens is a distance r from the centre, the focal length is f/r.
 * 
 * The parameter b controls how tightly the spiral is wound; phi0 controls the absolute rotation angle of the spiral.
 * 
 * @author Johannes
 */
public class CylindricalLensSpiral extends AbstractSimpleOpticalComponent
implements SimplePixelWiseOpticalComponentInterface, Serializable, PropertyChangeListener, ActionListener
{
	private static final long serialVersionUID = -588166946856356389L;

	private static final String COMPONENT_TYPE_NAME = "Cylindrical-lens spiral";
	
	/*
	 * Fields
	 */
	
	public enum CylindricalLensSpiralType
	{
		ARCHIMEDEAN("Archimedean"),
		LOGARITHMIC("Logarithmic");
		
		private String description;
		private CylindricalLensSpiralType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	/**
	 * type of the cylindrical-lens spiral
	 */
	private CylindricalLensSpiralType cylindricalLensSpiralType;

	/**
	 * the cylindrical lens's focal length (at distance r=1);
	 * the cross-section of the cylindrical lens is Phi(t) = (pi d^2)(lambda f), where d is the distance from the nearest point on the spiral
	 */
	private double focalLength;
	
	/**
	 * the centre of the cylindrical lens follows either the logarithmic spiral r = exp(b (phi-phi0)), or the Archimedean spiral r = b (phi-phi0);
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
	
	private transient JComboBox<CylindricalLensSpiralType> cylindricalLensSpiralTypeComboBox;
	private transient LengthField focalLengthField;
	private transient JFormattedTextField bTextField, phi0DegTextField;
	
	
	public CylindricalLensSpiral(String name, CylindricalLensSpiralType cylindricalLensSpiralType, double focalLength, double b, double phi0)
	{
		super(name);
		
		setCylindricalLensSpiralType(cylindricalLensSpiralType);
		setFocalLength(focalLength);
		setB(b);
		setPhi0(phi0);
	}
	
	/**
	 * Null constructor. Creates a lens with default values. This requires no
	 * parameters.
	 */
	public CylindricalLensSpiral()
	{
		this(COMPONENT_TYPE_NAME, CylindricalLensSpiralType.LOGARITHMIC, 1, 0.1, 0);
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
		// first solve the equation r = b (phi + nu*2*pi - phi0) (in the case of an Archimedean spiral) or
		// r = exp[b (phi + nu*2*pi - phi0)] (in the case of a logarithmic spiral)

		double nu;
		
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			nu = (r - b*(phi-phi0)) / b2pi;
			break;
		case LOGARITHMIC:
		default:
			// first solve the equation r == exp[b (phi + nu*2*pi - phi0)]
			nu = (Math.log(r) - b*(phi-phi0)) / b2pi;
		}
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
					
					// calculate the radial coordinate for the centre of the nth winding in the phi direction, and the focal length
					double rN, f;
					switch(cylindricalLensSpiralType)
					{
					case ARCHIMEDEAN:
						rN = b*(phi+n*2*Math.PI-phi0);
						f = focalLength/rN;
						break;
					case LOGARITHMIC:
					default:
						rN = Math.exp(b*(phi+n*2*Math.PI-phi0));
						f = focalLength;
					}
					
					// locally shift the phase of the amplitude cross-section
					double phaseShift = inputBeam.getLensPhase(f, MyMath.sqr(r-rN));
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
		
		// calculate the radial coordinate for the centre of the nth winding in the phi direction, and the focal length
		double rN, f;
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			rN = b*(phi+n*2*Math.PI-phi0);
			f = focalLength/rN;
			break;
		case LOGARITHMIC:
		default:
			rN = Math.exp(b*(phi+n*2*Math.PI-phi0));
			f = focalLength;
		}

		// locally shift the phase of the amplitude cross-section
		double phaseShift = inputBeam.getLensPhase(f, MyMath.sqr(r-rN));
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
		
		editPanel.add(UIBitsAndBobs.makeRow("A cylindrical lens of focal length (at r=1) ", focalLengthField, ",", true));
		editPanel.add(UIBitsAndBobs.makeRow("wound into a ", cylindricalLensSpiralTypeComboBox, " spiral", true));
		editPanel.add(UIBitsAndBobs.makeHTMLLabel("of the form r=exp[b*(phi-phi0)] (in the case of a logarithmic spiral)"));
		editPanel.add(UIBitsAndBobs.makeHTMLLabel("or r=b*(phi-phi0) (in the case of an Archimedean spiral),"));
		editPanel.add(UIBitsAndBobs.makeRow("where b=", bTextField, " and phi0=", phi0DegTextField, "&deg;", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		cylindricalLensSpiralTypeComboBox = new JComboBox<CylindricalLensSpiralType>(CylindricalLensSpiralType.values());
		cylindricalLensSpiralTypeComboBox.setToolTipText("Shape of the spiral the cylindrical lens is wound into");
		cylindricalLensSpiralTypeComboBox.addActionListener(this);
		cylindricalLensSpiralTypeComboBox.setSelectedItem(CylindricalLensSpiralType.LOGARITHMIC);
		cylindricalLensSpiralTypeComboBox.setMaximumSize(cylindricalLensSpiralTypeComboBox.getPreferredSize());
		
		focalLengthField = new LengthField(this);
		focalLengthField.setLengthInMetres(focalLength);
		
		bTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		bTextField.setValue(new Double(b));

		phi0DegTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		phi0DegTextField.setValue(new Double(MyMath.rad2deg(phi0)));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

		if(cylindricalLensSpiralTypeComboBox != null) cylindricalLensSpiralType = (CylindricalLensSpiralType)(cylindricalLensSpiralTypeComboBox.getSelectedItem());
        if(focalLengthField != null) setFocalLength(focalLengthField.getLengthInMetres());
        if(bTextField != null) setB(((Number)bTextField.getValue()).doubleValue());
        if(phi0DegTextField != null) setPhi0(MyMath.deg2rad(((Number)phi0DegTextField.getValue()).doubleValue()));
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
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		if (source == cylindricalLensSpiralTypeComboBox)
		{
			cylindricalLensSpiralType = (CylindricalLensSpiralType)(cylindricalLensSpiralTypeComboBox.getSelectedItem());
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

	public CylindricalLensSpiralType getCylindricalLensSpiralType() {
		return cylindricalLensSpiralType;
	}

	public void setCylindricalLensSpiralType(CylindricalLensSpiralType cylindricalLensSpiralType) {
		this.cylindricalLensSpiralType = cylindricalLensSpiralType;
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