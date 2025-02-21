package javawaveoptics.optics.lightsource;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;

public class LaguerreGaussianBeam extends GaussianBeam implements Serializable
{
	private static final long serialVersionUID = 3406086887414723477L;

	/*
	 * Fields
	 */
	
	// Laguerre polynomial parameters
	protected int lIndex;
	protected int pIndex;
	
	/*
	 * GUI edit controls
	 */
	
	// Laguerre label
	// private transient JLabel laguerreLabel;
	
	private transient JFormattedTextField
		lIndexTextField,
		pIndexTextField;
	
	public LaguerreGaussianBeam(String name, int lIndex, int pIndex, double waistSize, double xCentre, double yCentre)
	{
		super(name, waistSize, xCentre, yCentre);
		
		this.lIndex = lIndex;
		this.pIndex = pIndex;
	}
	
	public LaguerreGaussianBeam()
	{
		this("Laguerre-Gaussian beam", 0, 0, 1e-3, 0, 0);
	}

	@Override
	public String getLightSourceTypeName()
	{
		return "Laguerre-Gaussian beam";
	}

	@Override
	public BeamCrossSection getBeamOutput(double physicalWidth, double physicalHeight, double wavelength, int plotWidth, int plotHeight)
	{
		BeamCrossSection beam = new BeamCrossSection(plotWidth, plotHeight, physicalWidth, physicalHeight, wavelength);
		
		beam.makeLaguerreGaussian(lIndex, pIndex, w0, xCentre, yCentre);
		
		return beam;
	}
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow("Mode indices: <i>l</i>", lIndexTextField, ", <i>p</i>", pIndexTextField, "", true));
	}
	
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		// Laguerre label
		// laguerreLabel = new JLabel("<html><p>[ <strong>LG<sup>l</sup><sub>p</sub></strong> ]</p></html>");
		
		lIndexTextField = UIBitsAndBobs.makeIntFormattedTextField(this);
		lIndexTextField.setValue(Integer.valueOf(lIndex));

		pIndexTextField = UIBitsAndBobs.makeIntFormattedTextField(this);
		pIndexTextField.setValue(Integer.valueOf(pIndex));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();
		
        if(lIndexTextField != null) lIndex = ((Number)lIndexTextField.getValue()).intValue();
        if(pIndexTextField != null) pIndex = ((Number)pIndexTextField.getValue()).intValue();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		super.propertyChange(e);
		
	    Object source = e.getSource();
	    
	    if (source == lIndexTextField)
	    {
	        lIndex = ((Number)lIndexTextField.getValue()).intValue();
	    }
	    else if (source == pIndexTextField)
	    {
	        pIndex = ((Number)pIndexTextField.getValue()).intValue();
	    }
	}

	public int getL()
	{
		return lIndex;
	}

	public void setL(int l)
	{
		this.lIndex = l;
	}

	public int getP()
	{
		return pIndex;
	}

	public void setP(int p)
	{
		if(p >= 0)
		{
			this.pIndex = p;
		}
		else
		{
			throw new NumberFormatException("The specified value must be greater than or equal to 0.");
		}
	}
}
