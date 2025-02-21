package javawaveoptics.optics.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JCheckBox;
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
		ARCHIMEDEAN("<html>Archimedean (<i>R</i> = <i>b</i> &phi; m)</html>"),
		FERMAT("<html>Fermat (<i>R</i> = (<i>b</i> &phi;)<sup>(1/2)</sup> m) - Under construction!</html>"),
		HYPERBOLIC("<html>Hyperbolic (<i>R</i> = -1/(<i>b</i> &phi;) m)</html>"),
		LOGARITHMIC("<html>Logarithmic (<i>R</i> = exp(<i>b</i> &phi;) m)</html>");
		
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
	private double f1;
	
	/**
	 * the centre of the cylindrical lens follows either the logarithmic spiral r = exp(b (phi-phi0)), or the Archimedean spiral r = b (phi-phi0);
	 * set this using setB(), so that deltaNu is pre-calculated accordingly!
	 */
	private double b;
	
	/**
	 * relative rotation angle of the spiral (in radians);
	 */
	private double phi0;
	
	public enum WindingBoundaryPlacementType
	{
		HALF_WAY("Half-way between nodal lines of neighbouring windings"),
		ROTATED_SPIRAL("On the nodal-line spiral, rotated by 180°");
		// in the fullness of time, add an option to place the boundary such that there are no discontinuities between neighbouring windings
		
		private String description;
		private WindingBoundaryPlacementType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	/**
	 * placement of the boundary between neighbouring windings
	 */
	private WindingBoundaryPlacementType windingBoundaryPlacement;
	
	private boolean alvarezLohmannWindingFocussing;
	
	private boolean azimuthalPhaseComponensation;

	
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
	// private double deltaNu;
	
	/*
	 * GUI edit controls
	 */
	
	private transient JComboBox<CylindricalLensSpiralType> cylindricalLensSpiralTypeComboBox;
	private transient JComboBox<WindingBoundaryPlacementType> windingBoundaryPlacementComboBox;
	private transient LengthField focalLengthField;
	private transient JFormattedTextField bTextField, phi0DegTextField;
	private transient JCheckBox alvarezLohmannWindingFocussingCheckBox, azimuthalPhaseComponensationCheckBox;
	
	
	public CylindricalLensSpiral(
		String name, 
		CylindricalLensSpiralType cylindricalLensSpiralType, 
		double focalLength, 
		double b, 
		double phi0,
		WindingBoundaryPlacementType windingBoundaryPlacement,
		boolean alvarezLohmannWindingFocussing,
		boolean azimuthalPhaseComponensation
	)
	{
		super(name);
		
		setCylindricalLensSpiralType(cylindricalLensSpiralType);
		setFocalLength(focalLength);
		setB(b);
		setPhi0(phi0);
		setWindingBoundaryPlacement(windingBoundaryPlacement);
		setAlvarezLohmannWindingFocussing(alvarezLohmannWindingFocussing);
		setAzimuthalPhaseComponensation(azimuthalPhaseComponensation);
	}
	
	/**
	 * Null constructor. Creates a lens with default values. This requires no
	 * parameters.
	 */
	public CylindricalLensSpiral()
	{
		this(COMPONENT_TYPE_NAME, CylindricalLensSpiralType.LOGARITHMIC, 1, 0.1, 0, WindingBoundaryPlacementType.HALF_WAY, true, true);
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
//		// first solve the equation r = b (phi + nu*2*pi - phi0) (in the case of an Archimedean spiral) or
//		// r = exp[b (phi + nu*2*pi - phi0)] (in the case of a logarithmic spiral)
//
//		double nu;
//		
//		switch(cylindricalLensSpiralType)
//		{
//		case ARCHIMEDEAN:
//			nu = (r - b*(phi-phi0)) / b2pi;
//			break;
//		case LOGARITHMIC:
//		default:
//			// first solve the equation r == exp[b (phi + nu*2*pi - phi0)]
//			nu = (Math.log(r) - b*(phi-phi0)) / b2pi;
//		}
//		return Math.ceil(nu - deltaNu);

		double phiRotated = phi - phi0;
		
		// see adaptiveFresnelLensCalculations.nb
		switch(windingBoundaryPlacement)
		{
		case ROTATED_SPIRAL:
			switch(cylindricalLensSpiralType)
			{
			case ARCHIMEDEAN:
				return Math.floor(0.5 + (r - b*phiRotated)/b2pi);
			case FERMAT:
				return Math.floor(0.5 + (r*r/2 - b*phiRotated)/b2pi);
			case HYPERBOLIC:
				return Math.floor(0.5 - (b*phiRotated + 1./r)/b2pi);
			case LOGARITHMIC:
			default:
				return Math.floor(0.5+((Math.log(r) - b*phiRotated)/b2pi));
			}
		case HALF_WAY:
		default:
			switch(cylindricalLensSpiralType)
			{
			case ARCHIMEDEAN:
				return Math.floor(0.5 + (r - b*phiRotated)/b2pi);
			case FERMAT:
				double r2 = r*r;
				return Math.floor(0.5 + (b*Math.PI*Math.PI + r2*r2 - 2*b*r2*phiRotated)/(4*b*Math.PI*r2));
			case HYPERBOLIC:
				double b2r = b*2*r;
				return Math.floor(0.5 - (b*(phiRotated + Math.sqrt(Math.PI*Math.PI + 1/(b2r*b2r))) + 1./(2.*r))/b2pi);
			case LOGARITHMIC:
			default:
				return Math.floor(0.5+((Math.log(2*r/(Math.exp(-b2pi)+1)) - b*(phiRotated+Math.PI))/b2pi));
			}
		}
	}

	private Complex calculateAlteredPixelValue(BeamCrossSection inputBeam, int i, int j, double x, double y)
	{
		// calculate polar coordinates
		double r = Math.sqrt(x*x + y*y);
		double phi = Math.atan2(y, x);
		
		// calculate the winding number on which the pixel lies...
		double n = calculateWindingNumber(r, phi);
		// ... and from that the value of psi (the unbounded azimuthal parameter)
		double psi = phi+n*2*Math.PI-phi0;
		
		// calculate the radial coordinate for the centre of the nth winding in the phi direction, and the focal length
		double R, f;
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			R = b*psi;
			f = f1/R;
			break;
		case FERMAT:
			R = Math.sqrt(2.*b*psi);
			f = f1/(R*R);
			break;
		case HYPERBOLIC:
			R = -1./(b*psi);
			f = f1*R;
			break;
		case LOGARITHMIC:
		default:
			R = Math.exp(b*psi);
			f = f1;
		}
		
		// locally shift the phase of the amplitude cross-section
		double phaseShift = inputBeam.getLensPhase(f, MyMath.sqr(r-R));
		if(alvarezLohmannWindingFocussing) {
			// calculate a
			double a;
			switch(cylindricalLensSpiralType)
			{
			case ARCHIMEDEAN:
				a = -2./f1;
				break;
			case FERMAT:
				a = -3.*R/f1;
				break;
			case HYPERBOLIC:
				a = 0;
				break;
			case LOGARITHMIC:
			default:
				a = -1./(R*f1);
			}
			phaseShift += inputBeam.getAlvarezLohmannLensPartPhase(a, r-R);
		}
		
		// double bPsi;
		if(azimuthalPhaseComponensation) {
			switch(cylindricalLensSpiralType)
			{
			case ARCHIMEDEAN:
				// (b psi)^3 k / (6 f1) = R^3 k / (6 f R) = R^2 k / (6 f)
				phaseShift += R*R*inputBeam.getWavenumber()/(6.*f);
				break;
			case FERMAT:
				// (b psi)^2 k / (2 f1) = (b psi)^2 k / (2 f 2 b psi) = b psi k / (4 f) = R^2 k / (8 f)
				phaseShift += R*R*inputBeam.getWavenumber()/(8.*f);
				break;
			case HYPERBOLIC:
				// -k/(2 b psi f1) = k R / (2 f1)
				phaseShift += inputBeam.getWavenumber()*R/(2.*f1);
				break;
			case LOGARITHMIC:
			default:
				// exp(2 b psi) k / (4 f1)
				phaseShift += R*R*inputBeam.getWavenumber()/(4.*f1);
			}
		}
		
		double cosPhaseShift = Math.cos(phaseShift);
		double sinPhaseShift = Math.sin(phaseShift);
		double re = inputBeam.getElementRe(i,j);
		double im = inputBeam.getElementIm(i,j);
	  
		// real part of (re + i im) exp(i phaseShift)
		// imaginary part of (re + i im) exp(i phaseShift)
		return new Complex(re * cosPhaseShift - im * sinPhaseShift, re * sinPhaseShift + im * cosPhaseShift);
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

					inputBeam.setElement(i, j, calculateAlteredPixelValue(inputBeam, i, j, x, y));					
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
		
		inputBeam.setElement(i, j, calculateAlteredPixelValue(inputBeam, i, j, x, y));

		return inputBeam;
	}


	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Edit focal length control
		 */
		
		editPanel.add(UIBitsAndBobs.makeRow("A cylindrical lens of focal length (at <i>r</i> = 1 m) ", focalLengthField, ",", true));
		editPanel.add(UIBitsAndBobs.makeRow("wound into a ", cylindricalLensSpiralTypeComboBox, " spiral", true));
		editPanel.add(UIBitsAndBobs.makeRow("where <i>b</i> =", bTextField, " and &theta;<sub>0</sub> =", phi0DegTextField, "&deg;.", true));
		editPanel.add(UIBitsAndBobs.makeRow("Winding boundary ", windingBoundaryPlacementComboBox, true));
		editPanel.add(alvarezLohmannWindingFocussingCheckBox);
		editPanel.add(azimuthalPhaseComponensationCheckBox);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		cylindricalLensSpiralTypeComboBox = new JComboBox<CylindricalLensSpiralType>(CylindricalLensSpiralType.values());
		cylindricalLensSpiralTypeComboBox.setToolTipText("Shape of the spiral the cylindrical lens is wound into");
		cylindricalLensSpiralTypeComboBox.addActionListener(this);
		cylindricalLensSpiralTypeComboBox.setSelectedItem(cylindricalLensSpiralType);
		cylindricalLensSpiralTypeComboBox.setMaximumSize(cylindricalLensSpiralTypeComboBox.getPreferredSize());
		
		focalLengthField = new LengthField(this);
		focalLengthField.setLengthInMetres(f1);
		
		bTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		bTextField.setValue(Double.valueOf(b));

		phi0DegTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		phi0DegTextField.setValue(Double.valueOf(MyMath.rad2deg(phi0)));
		
		windingBoundaryPlacementComboBox = new JComboBox<WindingBoundaryPlacementType>(WindingBoundaryPlacementType.values());
		windingBoundaryPlacementComboBox.setToolTipText("Placement of the boundary between neighbouring windings");
		windingBoundaryPlacementComboBox.addActionListener(this);
		windingBoundaryPlacementComboBox.setSelectedItem(windingBoundaryPlacement);
		windingBoundaryPlacementComboBox.setMaximumSize(windingBoundaryPlacementComboBox.getPreferredSize());
		
		alvarezLohmannWindingFocussingCheckBox = new JCheckBox("Alvarez-Lohmann winding focussing");
		alvarezLohmannWindingFocussingCheckBox.setToolTipText("Should Alvarez-Lohmann winding focussing be used?");
		alvarezLohmannWindingFocussingCheckBox.addPropertyChangeListener(this);
		alvarezLohmannWindingFocussingCheckBox.setSelected(alvarezLohmannWindingFocussing);
		alvarezLohmannWindingFocussingCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		azimuthalPhaseComponensationCheckBox = new JCheckBox("Azimuthal phase componensation");
		azimuthalPhaseComponensationCheckBox.setToolTipText("Should azimuthal phase componensation be used?");
		azimuthalPhaseComponensationCheckBox.addPropertyChangeListener(this);
		azimuthalPhaseComponensationCheckBox.setSelected(azimuthalPhaseComponensation);
		azimuthalPhaseComponensationCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

		if(cylindricalLensSpiralTypeComboBox != null) cylindricalLensSpiralType = (CylindricalLensSpiralType)(cylindricalLensSpiralTypeComboBox.getSelectedItem());
        if(focalLengthField != null) setFocalLength(focalLengthField.getLengthInMetres());
        if(bTextField != null) setB(((Number)bTextField.getValue()).doubleValue());
        if(phi0DegTextField != null) setPhi0(MyMath.deg2rad(((Number)phi0DegTextField.getValue()).doubleValue()));
        if(windingBoundaryPlacementComboBox != null) windingBoundaryPlacement = (WindingBoundaryPlacementType)(windingBoundaryPlacementComboBox.getSelectedItem());
        if(alvarezLohmannWindingFocussingCheckBox != null) alvarezLohmannWindingFocussing = alvarezLohmannWindingFocussingCheckBox.isSelected();
        if(azimuthalPhaseComponensationCheckBox != null) azimuthalPhaseComponensation = azimuthalPhaseComponensationCheckBox.isSelected();
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
	    else if (source == alvarezLohmannWindingFocussingCheckBox)
	    {
	    	alvarezLohmannWindingFocussing = alvarezLohmannWindingFocussingCheckBox.isSelected();
	    }
	    else if(source.equals(azimuthalPhaseComponensationCheckBox))
	    {
	    	azimuthalPhaseComponensation = azimuthalPhaseComponensationCheckBox.isSelected();
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
		else if (source == windingBoundaryPlacementComboBox)
		{
			windingBoundaryPlacement = (WindingBoundaryPlacementType)(windingBoundaryPlacementComboBox.getSelectedItem());
		}
	    
		// Fire an edit panel event
		editListener.editMade();
	}

	@Override
	public String getFormattedName()
	{
		return "f = " + Double.toString(f1) + "m";
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
		return f1;
	}

	public void setFocalLength(double focalLength)
	{
		this.f1 = focalLength;
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
		// deltaNu = Math.log(0.5*(1+Math.exp(b2pi))) / b2pi;
	}

	public double getPhi0() {
		return phi0;
	}

	public void setPhi0(double phi0) {
		this.phi0 = phi0;
	}

	/**
	 * @return the windingBoundaryPlacement
	 */
	public WindingBoundaryPlacementType getWindingBoundaryPlacement() {
		return windingBoundaryPlacement;
	}

	/**
	 * @param windingBoundaryPlacement the windingBoundaryPlacement to set
	 */
	public void setWindingBoundaryPlacement(WindingBoundaryPlacementType windingBoundaryPlacement) {
		this.windingBoundaryPlacement = windingBoundaryPlacement;
	}

	/**
	 * @return the alvarezLohmannWindingFocussing
	 */
	public boolean isAlvarezLohmannWindingFocussing() {
		return alvarezLohmannWindingFocussing;
	}

	/**
	 * @param alvarezLohmannWindingFocussing the alvarezLohmannWindingFocussing to set
	 */
	public void setAlvarezLohmannWindingFocussing(boolean alvarezLohmannWindingFocussing) {
		this.alvarezLohmannWindingFocussing = alvarezLohmannWindingFocussing;
	}

	/**
	 * @return the azimuthalPhaseComponensation
	 */
	public boolean isAzimuthalPhaseComponensation() {
		return azimuthalPhaseComponensation;
	}

	/**
	 * @param azimuthalPhaseComponensation the azimuthalPhaseComponensation to set
	 */
	public void setAzimuthalPhaseComponensation(boolean azimuthalPhaseComponensation) {
		this.azimuthalPhaseComponensation = azimuthalPhaseComponensation;
	}

}