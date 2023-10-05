package javawaveoptics.optics.lightsource;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;

public class HermiteGaussianBeam extends GaussianBeam implements Serializable
{
	private static final long serialVersionUID = -5792268754410347309L;

	/*
	 * Fields
	 */
	
	// mode indices
	protected int mIndex, nIndex;
	
	/*
	 * GUI edit controls
	 */
		
	private transient JFormattedTextField
		mIndexTextField,
		nIndexTextField;
	
	/**
	 * @param name
	 * @param mIndex	number of horizontal nodes
	 * @param nIndex	number of vertical nodes
	 * @param w0	waist size
	 * @param xCentre
	 * @param yCentre
	 */
	public HermiteGaussianBeam(String name, int mIndex, int nIndex, double w0, double xCentre, double yCentre)
	{
		super(name, w0, xCentre, yCentre);
		
		this.mIndex = mIndex;
		this.nIndex = nIndex;
	}
	
	public HermiteGaussianBeam()
	{
		this("Hermite-Gaussian beam", 0, 0, 1e-3, 0, 0);
	}

	@Override
	public String getLightSourceTypeName()
	{
		return "Hermite-Gaussian beam";
	}

	@Override
	public BeamCrossSection getBeamOutput(double physicalWidth, double physicalHeight, double wavelength, int plotWidth, int plotHeight)
	{
		BeamCrossSection beam = new BeamCrossSection(plotWidth, plotHeight, physicalWidth, physicalHeight, wavelength);
		
		beam.makeHermiteGaussian(mIndex, nIndex, w0, xCentre, yCentre);
		
		return beam;
	}
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow("Mode indices: m", mIndexTextField, ", n", nIndexTextField, "", true));
	}
	
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		mIndexTextField = UIBitsAndBobs.makeIntFormattedTextField(this);
		mIndexTextField.setValue(new Integer(mIndex));

		nIndexTextField = UIBitsAndBobs.makeIntFormattedTextField(this);
		nIndexTextField.setValue(new Integer(nIndex));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();
		
        if(mIndexTextField != null) mIndex = ((Number)mIndexTextField.getValue()).intValue();
        if(nIndexTextField != null) nIndex = ((Number)nIndexTextField.getValue()).intValue();
	}
		
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		super.propertyChange(e);
		
	    Object source = e.getSource();
	    
	    if (source == mIndexTextField)
	    {
	        mIndex = ((Number)mIndexTextField.getValue()).intValue();
	    }
	    else if (source == nIndexTextField)
	    {
	        nIndex = ((Number)nIndexTextField.getValue()).intValue();
	    }
	}
}
