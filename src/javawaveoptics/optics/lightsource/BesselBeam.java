package javawaveoptics.optics.lightsource;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;


/**
 * Provides a Bessel beam output with corresponding edit functionality.
 * 
 * @author Johannes
 */
public class BesselBeam extends AbstractLightSource implements Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = -6152805227476277904L;
	
	/*
	 * Fields
	 */
	
	// Topological charge
	protected double topologicalCharge;
	
	// radial wave number
	protected double radialWaveNumber;
	
	// Coordinates of centre
	protected double xCentre, yCentre;
	
	/*
	 * GUI edit controls
	 */
	
	// Topological charge edit control
	private transient JFormattedTextField topologicalChargeTextField;

	// radial wave number edit control
	private transient JFormattedTextField radialWaveNumberTextField;

	private transient LengthField
		xCentreLengthField,
		yCentreLengthField;
		
	public BesselBeam(String name, double topologicalCharge, double radialWaveNumber, double xCentre, double yCentre)
	{
		super(name);
		
		this.topologicalCharge = topologicalCharge;
		this.radialWaveNumber = radialWaveNumber;
		this.xCentre = xCentre;
		this.yCentre = yCentre;
	}
	
	public BesselBeam()
	{
		this("Bessel beam", 0, 1, 0, 0);
	}
	
	@Override
	public String getLightSourceTypeName()
	{
		return "Bessel beam";
	}
	
	public BeamCrossSection getBeamOutput(double physicalWidth, double physicalHeight, double wavelength, int plotWidth, int plotHeight)
	{
		BeamCrossSection beam = new BeamCrossSection(plotWidth, plotHeight, physicalWidth, physicalHeight, wavelength);
		
		beam.makeBessel(topologicalCharge, radialWaveNumber, xCentre, yCentre);
		
		return beam;
	}
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow("Topological charge, <i>m</i>", topologicalChargeTextField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Radial wave number, &beta;", radialWaveNumberTextField, "1/m", true));

		editPanel.add(UIBitsAndBobs.makeRow("Centre (", xCentreLengthField, ",", yCentreLengthField, ")", true));
	}
	
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		topologicalChargeTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		topologicalChargeTextField.setValue(Double.valueOf(topologicalCharge));

		radialWaveNumberTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		radialWaveNumberTextField.setValue(Double.valueOf(radialWaveNumber));

		xCentreLengthField = new LengthField(this);
		xCentreLengthField.setLengthInMetres(xCentre);

		yCentreLengthField = new LengthField(this);
		yCentreLengthField.setLengthInMetres(yCentre);
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

		if(topologicalChargeTextField != null) topologicalCharge = ((Number)topologicalChargeTextField.getValue()).doubleValue();
    	if(radialWaveNumberTextField != null) radialWaveNumber = ((Number)radialWaveNumberTextField.getValue()).doubleValue();
        if(xCentreLengthField != null) xCentre = xCentreLengthField.getLengthInMetres();
        if(yCentreLengthField != null) yCentre = yCentreLengthField.getLengthInMetres();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == topologicalChargeTextField)
	    {
	        topologicalCharge = ((Number)topologicalChargeTextField.getValue()).doubleValue();
	    }
	    else if (source == radialWaveNumberTextField)
	    {
	    	radialWaveNumber = ((Number)radialWaveNumberTextField.getValue()).doubleValue();
	    }
	    else if (source == xCentreLengthField)
	    {
	        xCentre = xCentreLengthField.getLengthInMetres();
	    }
	    else if (source == yCentreLengthField)
	    {
	        yCentre = yCentreLengthField.getLengthInMetres();
	    }
	}

	public double getTopologicalCharge()
	{
		return topologicalCharge;
	}

	public void setTopologicalCharge(double topologicalCharge)
	{
		this.topologicalCharge = topologicalCharge;
	}

	public double getRadialWaveNumber()
	{
		return radialWaveNumber;
	}

	public void setRadialWaveNumber(double radialWaveNumber)
	{
		this.radialWaveNumber = radialWaveNumber;
	}

	public double getxCentre()
	{
		return xCentre;
	}

	public void setxCentre(double xCentre)
	{
		this.xCentre = xCentre;
	}

	public double getyCentre()
	{
		return yCentre;
	}

	public void setyCentre(double yCentre)
	{
		this.yCentre = yCentre;
	}
}
