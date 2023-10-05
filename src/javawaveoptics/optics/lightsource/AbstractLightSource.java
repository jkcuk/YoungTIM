package javawaveoptics.optics.lightsource;

import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.optics.component.LightSource;
import javawaveoptics.ui.UIBitsAndBobs;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Defines a generic beam. All beams require input parameters which specify the
 * nature of the beam. This class provides abstract functionality to produce an edit panel
 * and to produce a Beam object when required by the simulation.
 * 
 * @author Sean
 */
public abstract class AbstractLightSource implements Serializable
{
	private static final long serialVersionUID = 7448962301001384907L;
	
	/*
	 * Fields
	 */
	
	protected String name = "Abstract beam";
	
	protected LightSource lightSource;
	
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
	public AbstractLightSource(String name)
	{
		this.name = name;
	}
	
	public abstract String getLightSourceTypeName();
	
	/**
	 * Returns a beam constructed using the light source's properties
	 * 
	 * @return		Beam object
	 */
	public abstract BeamCrossSection getBeamOutput(double physicalWidth, double physicalHeight, double wavelength, int plotWidth, int plotHeight);
	
	/**
	 * Returns the edit panel associated with this beam.
	 * 
	 * @return
	 */
	public JPanel getEditPanel(LightSource lightSource)
	{		
		if(editPanel == null)
		{
			this.lightSource = lightSource;
			createEditPanel();
		}
		
		return editPanel;
	}
	
	/**
	 * Creates the edit panel for this beam type.
	 */
	protected void createEditPanel()
	{
		// (Re)initialise edit panel
		editPanel = new JPanel();
		
		/*
		 * GUI initialisation
		 */
		
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.setBorder(UIBitsAndBobs.getTitledBorder(getLightSourceTypeName() + " parameters"));
				
		// Initialise the edit controls
		initialiseWidgets();
	}
	
	protected void initialiseWidgets()
	{
		// Nothing to initialise here...
		// This should be overridden in child classes
	}
	
	public void readWidgets()
	{}
	
	public String toString()
	{
		return name;
	}
}
