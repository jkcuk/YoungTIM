package javawaveoptics.optics.aperture;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;
import library.maths.MyMath;

public class Grating extends AbstractAperture implements Serializable, ItemListener // , PropertyChangeListener
{
	private static final long serialVersionUID = -7516856841613269209L;

	/*
	 * Fields
	 */

	protected double gratingPeriod, slitWidth, xCentre, yCentre;
	
	/**
	 * true if the edges are soft, i.e. if the "hard" step function in the transmission function is replaced with a cos-shaped 
	 * function that interpolates between 0 and 1 over the edgeWidth;
	 * soft edges (try to) avoid diffraction at those edges, and instead make the "walls" of the grating essentially absorbing
	 */
	protected boolean softEdges;
	protected double edgeWidth;
	
	// Rotation angle
	protected double rotationAngle;
	
	private boolean showAlsoPerpendicularGrating;
	
	/*
	 * GUI edit controls
	 */
	
	private transient LengthField gratingPeriodLengthField, slitWidthLengthField, xCentreLengthField, yCentreLengthField, edgeWidthLengthField;
	private transient JFormattedTextField rotationAngleTextField;
	private transient JCheckBox showAlsoPerpendicularGratingCheckBox, softEdgesCheckBox;

	
	public Grating(String name, double gratingPeriod, double slitWidth, double rotationAngle, double xCentre, double yCentre, boolean softEdges, double edgeWidth, boolean showAlsoPerpendicularGrating)
	{
		super(name);
		
		this.gratingPeriod = gratingPeriod;
		this.slitWidth = slitWidth;
		this.rotationAngle = rotationAngle;
		this.xCentre = xCentre;
		this.yCentre = yCentre;
		this.softEdges = softEdges;
		this.edgeWidth = edgeWidth;
		this.showAlsoPerpendicularGrating = showAlsoPerpendicularGrating;
	}
	
	public Grating()
	{
		this("Grating", 1e-3, 2.5e-4, 0, 0, 0, false, 0, false);
	}

	@Override
	public String getApertureTypeName()
	{
		return "Grating";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			// int initialWidth = inputBeam.getWidth();
			// int initialHeight = inputBeam.getHeight();
			
			// inputBeam.rotate(-rotationAngle, true);
			inputBeam.passThroughGrating(gratingPeriod, slitWidth, MyMath.deg2rad(rotationAngle), xCentre, yCentre, softEdges, edgeWidth);
			if(showAlsoPerpendicularGrating)
				inputBeam.passThroughGrating(gratingPeriod, slitWidth, MyMath.deg2rad(rotationAngle+90), xCentre, yCentre, softEdges, edgeWidth);

			// inputBeam.rotate(rotationAngle, true);
			// inputBeam.changeDimensions(initialWidth, initialHeight);
		}
		
		return inputBeam;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow("Grating period", gratingPeriodLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Slit width", slitWidthLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow(softEdgesCheckBox, ", Edge width", edgeWidthLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Angle of slits with y direction", rotationAngleTextField, "&deg;", true));
		editPanel.add(UIBitsAndBobs.makeRow("Centre (", xCentreLengthField, ",", yCentreLengthField, ")", true));
		
		showAlsoPerpendicularGratingCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		editPanel.add(showAlsoPerpendicularGratingCheckBox);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		gratingPeriodLengthField = new LengthField(null);
		gratingPeriodLengthField.setLengthInMetres(gratingPeriod);
		
		slitWidthLengthField = new LengthField(null);
		slitWidthLengthField.setLengthInMetres(slitWidth);
		
		softEdgesCheckBox = new JCheckBox("Soft edges");
		softEdgesCheckBox.setSelected(softEdges);
		softEdgesCheckBox.addItemListener(this);

		edgeWidthLengthField = new LengthField(null);
		edgeWidthLengthField.setLengthInMetres(edgeWidth);
		
		rotationAngleTextField = UIBitsAndBobs.makeDoubleFormattedTextField(null);
		rotationAngleTextField.setValue(new Double(rotationAngle));

		xCentreLengthField = new LengthField(null);
		xCentreLengthField.setLengthInMetres(xCentre);

		yCentreLengthField = new LengthField(null);
		yCentreLengthField.setLengthInMetres(yCentre);
		
		showAlsoPerpendicularGratingCheckBox = new JCheckBox("Show also perpendicular grating");
		showAlsoPerpendicularGratingCheckBox.setSelected(showAlsoPerpendicularGrating);
		showAlsoPerpendicularGratingCheckBox.addItemListener(this);
	}

	@Override
	public void readWidgets()
	{
        if(gratingPeriodLengthField != null) gratingPeriod = gratingPeriodLengthField.getLengthInMetres();
        if(slitWidthLengthField != null) slitWidth = slitWidthLengthField.getLengthInMetres();
		if(softEdgesCheckBox != null) softEdges = softEdgesCheckBox.isSelected();
    	if(edgeWidthLengthField != null) edgeWidth = edgeWidthLengthField.getLengthInMetres();
        if(rotationAngleTextField != null) rotationAngle = ((Number)rotationAngleTextField.getValue()).doubleValue();
        if(xCentreLengthField != null) xCentre = xCentreLengthField.getLengthInMetres();
        if(yCentreLengthField != null) yCentre = yCentreLengthField.getLengthInMetres();
		if(showAlsoPerpendicularGratingCheckBox != null) showAlsoPerpendicularGrating = showAlsoPerpendicularGratingCheckBox.isSelected();
	}
	
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getItemSelectable();

//		if (source == showAlsoPerpendicularGratingCheckBox)
//	    {
//			showAlsoPerpendicularGrating = showAlsoPerpendicularGratingCheckBox.isSelected();
//	    }
//		else 
		if (source == softEdgesCheckBox)
	    {
			softEdges = softEdgesCheckBox.isSelected();
			edgeWidthLengthField.setEnabled(softEdges);
	    }

	}

//	@Override
//	public void propertyChange(PropertyChangeEvent e)
//	{
//	    Object source = e.getSource();
//	    
//	    if (source == gratingPeriodLengthField)
//	    {
//	        gratingPeriod = gratingPeriodLengthField.getLengthInMetres();
//	    }
//	    else if (source == slitWidthLengthField)
//	    {
//	        slitWidth = slitWidthLengthField.getLengthInMetres();
//	    }
//	    else if (source == edgeWidthLengthField)
//	    {
//	    	edgeWidth = edgeWidthLengthField.getLengthInMetres();
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