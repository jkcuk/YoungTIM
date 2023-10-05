package javawaveoptics.optics.aperture;

import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.optics.lightsource.UniformPlaneWave;
import javawaveoptics.ui.UIBitsAndBobs;
import library.list.ComplexList2D;
import library.optics.LightBeamCrossSection2D;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Defines a generic aperture component. All apertures require input parameters which specify the
 * nature of the aperture. This class provides abstract functionality to produce an edit panel
 * and to modify a Beam object when required by the simulation.
 * 
 * @author Sean
 */
public abstract class AbstractAperture implements Serializable
{
	private static final long serialVersionUID = 4407094041085526129L;

	/*
	 * Fields
	 */
	
	protected String name = "Abstract aperture";
	
	/*
	 * GUI edit controls
	 */
	
	// Edit panel
	protected transient JPanel editPanel;
		
	/**
	 * Constructor.
	 * 
	 * @param name		Name to give this beam type
	 */
	public AbstractAperture(String name)
	{
		this.name = name;
	}
	
	public abstract String getApertureTypeName();
	
	/**
	 * Modify the beam
	 * @param inputBeam
	 * @return
	 */
	public abstract BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam);
	
	/**
	 * @param inputBeam
	 * @return	an array of transmission coefficients so that element (i, j) multiplies element (i, j) of inputBeam
	 */
	public ComplexList2D getTransmissionCoefficients(LightBeamCrossSection2D inputBeam)
	{
		// create a uniform plane wave, i.e. a beam whose amplitude is 1 everywhere, ...
		BeamCrossSection c = (new UniformPlaneWave()).getBeamOutput(
				inputBeam.getPhysicalWidth(),
				inputBeam.getPhysicalHeight(),
				inputBeam.getWavelength(),
				inputBeam.getWidth(),
				inputBeam.getHeight()
			);
		
		// ... and pass it through the aperture, multiplying each element with the aperture's transmission coefficient,
		// resulting in a beam whose amplitude-matrix elements are the transmission factors
		fromInputBeamCalculateOutputBeam(c);
		
		// return those transmission factors
		return c;
		// return c.getData();
//		double[][] t = new double[c.getWidth()][c.getHeight()];
//		for(int i=0; i<c.getWidth(); i++)
//			for(int j=0; j<c.getHeight(); j++)
//				t[i][j] = c.getElementRe(i, j);
	}
	
	/**
	 * Returns the edit panel associated with this aperture
	 * 
	 * @return
	 */
	public JPanel getEditPanel()
	{		
		if(editPanel == null)
		{
			createEditPanel();
		}
		
		return editPanel;
	}
	
	/**
	 * Creates the edit panel for this aperture.
	 */
	protected void createEditPanel()
	{
		// (Re)initialise edit panel
		editPanel = new JPanel();
		
		/*
		 * GUI initialisation
		 */
		
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.setBorder(UIBitsAndBobs.getTitledBorder(getApertureTypeName() + " parameters"));
				
		// Initialise the edit controls
		initialiseWidgets();
	}
	
	protected void initialiseWidgets()
	{
		// Nothing to initialise here...
		// This should be overridden in child classes
	}
	
	/**
	 * read the widgets and set the values of internal variables accordingly
	 */
	public void readWidgets()
	{}

	public String toString()
	{
		return name;
	}
}
