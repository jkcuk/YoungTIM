package javawaveoptics.optics.aperture;

import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;

/**
 * Defines a circular or annular aperture. A light beam passing through this component will be cropped using an annulus
 * of outerRadius and innerRadius, as defined by the user in the edit panel.
 * 
 * @author Sean
 * @author Johannes
 * @author Adam
 */
public class AnnularAperture extends AbstractAperture implements Serializable //, PropertyChangeListener
{
	private static final long serialVersionUID = -5806959756719268722L;

	/*
	 * Fields
	 */
	
	// Radius of the aperture
	double outerRadius, innerRadius, xCentre, yCentre;
	
	/*
	 * GUI edit controls
	 */
	
	private transient LengthField outerRadiusLengthField, innerRadiusLengthField, xCentreLengthField, yCentreLengthField;

	
	public AnnularAperture(String name, double outerRadius, double innerRadius, double xCentre, double yCentre)
	{
		super(name);
		
		this.outerRadius = outerRadius;
		this.innerRadius = innerRadius;
		this.xCentre = xCentre;
		this.yCentre = yCentre;
	}
	
	/**
	 * Null constructor. Creates a circular aperture with default values.
	 * This requires no parameters.
	 */
	public AnnularAperture()
	{
		this("Annular aperture", 1e-4, 0, 0, 0);
	}
	
	@Override
	public String getApertureTypeName()
	{
		return "Annular aperture";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			// System.out.println("CircularAperture::fromInputBeamCalculateOutputBeam: radius = "+radius+", xCentre = "+xCentre+", yCentre = "+yCentre);
			inputBeam.passThroughAnnularAperture(outerRadius, innerRadius, xCentre, yCentre);
		}
		
		return inputBeam;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Edit radius control
		 */
				
		editPanel.add(UIBitsAndBobs.makeRow("Outer radius", outerRadiusLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Inner radius", innerRadiusLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Centre (", xCentreLengthField, ",", yCentreLengthField, ")", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		outerRadiusLengthField = new LengthField(null);
		outerRadiusLengthField.setLengthInMetres(outerRadius);

		innerRadiusLengthField = new LengthField(null);
		innerRadiusLengthField.setLengthInMetres(innerRadius);

		xCentreLengthField = new LengthField(null);
		xCentreLengthField.setLengthInMetres(xCentre);

		yCentreLengthField = new LengthField(null);
		yCentreLengthField.setLengthInMetres(yCentre);
	}
	
	@Override
	public void readWidgets()
	{
        if(outerRadiusLengthField != null) outerRadius = outerRadiusLengthField.getLengthInMetres();
        if(innerRadiusLengthField != null) innerRadius = innerRadiusLengthField.getLengthInMetres();
        if(xCentreLengthField != null) xCentre = xCentreLengthField.getLengthInMetres();
        if(yCentreLengthField != null) yCentre = yCentreLengthField.getLengthInMetres();
	}
	
//	@Override
//	public void propertyChange(PropertyChangeEvent e)
//	{
//	    Object source = e.getSource();
//	    
//	    if (source == outerRadiusLengthField)
//	    {
//	        outerRadius = outerRadiusLengthField.getLengthInMetres();
//	    }
//	    else if (source == innerRadiusLengthField)
//	    {
//	        innerRadius = innerRadiusLengthField.getLengthInMetres();
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

	public double getOuterRadius()
	{
		return outerRadius;
	}

	public void setOuterRadius(double outerRadius)
	{
		this.outerRadius = outerRadius;
	}

	public double getInnerRadius() {
		return innerRadius;
	}

	public void setInnerRadius(double innerRadius) {
		this.innerRadius = innerRadius;
	}
}