package javawaveoptics.optics.lightsource;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;


/**
 * Provides a Gaussian beam output with corresponding edit functionality.
 * 
 * @author Sean
 */
public class GaussianBeam extends AbstractLightSource implements Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = -5361806058271368977L;
	
	/*
	 * Fields
	 */
	
	// Gaussian beam waist
	protected double w0;
	
	// Coordinates of centre
	protected double xCentre, yCentre;
	
	/*
	 * GUI edit controls
	 */
	
	private transient LengthField
		waistSizeLengthField,
		xCentreLengthField,
		yCentreLengthField;
		
	public GaussianBeam(String name, double waist, double xCentre, double yCentre)
	{
		super(name);
		
		this.w0 = waist;
		this.xCentre = xCentre;
		this.yCentre = yCentre;
	}
	
	public GaussianBeam()
	{
		this("Gaussian beam", 1e-3, 0, 0);
	}
	
	@Override
	public String getLightSourceTypeName()
	{
		return "Gaussian beam";
	}
	
	public BeamCrossSection getBeamOutput(double physicalWidth, double physicalHeight, double wavelength, int plotWidth, int plotHeight)
	{
		BeamCrossSection beam = new BeamCrossSection(plotWidth, plotHeight, physicalWidth, physicalHeight, wavelength);
		
		beam.makeGaussian(w0, xCentre, yCentre);
		
		return beam;
	}
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow("Waist size, <i>w</i><sub>0</sub> = ", waistSizeLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Centre = (", xCentreLengthField, ",", yCentreLengthField, ")", true));
	}
	
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		waistSizeLengthField = new LengthField(this);
		waistSizeLengthField.setLengthInMetres(w0);

		xCentreLengthField = new LengthField(this);
		xCentreLengthField.setLengthInMetres(xCentre);

		yCentreLengthField = new LengthField(this);
		yCentreLengthField.setLengthInMetres(yCentre);
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

        if(waistSizeLengthField != null) w0 = waistSizeLengthField.getLengthInMetres();
        if(xCentreLengthField != null) xCentre = xCentreLengthField.getLengthInMetres();
        if(yCentreLengthField != null) yCentre = yCentreLengthField.getLengthInMetres();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == waistSizeLengthField)
	    {
	        w0 = waistSizeLengthField.getLengthInMetres();
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

	public double getWaist()
	{
		return w0;
	}

	public void setWaist(double waist)
	{
		this.w0 = waist;
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
