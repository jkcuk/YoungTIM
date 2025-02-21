package javawaveoptics.optics.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.JCPanel;
import javawaveoptics.ui.UIBitsAndBobs;
import library.maths.Complex;

/**
 * Turns a light beam into a hologram.
 * This can happen in various ways, e.g. by simply replacing the field with phase factors [1]
 * 
 * [1] M. Reicherter, T. Haist, E. U. Wagemann and H. J. Tiziani, "Optical particle trapping with
 * computer-generated holograms written on a liquid-crystal display", Opt. Lett.� 24� 608-610� (1999)
 * 
 * @author Johannes
 */
public class Hologrammifier extends AbstractSimpleOpticalComponent implements Serializable, PropertyChangeListener, ActionListener
{
	private static final long serialVersionUID = 3534911291321554049L;
	
	public enum HologrammifierType
	{
		PHASE("Phase"),
		INTENSITY("Intensity");
		
		private String description;
		private HologrammifierType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	/*
	 * Fields
	 */
	
	protected HologrammifierType hologrammifierType;

	// phase-hologram parameters
	// phase step height, in units of 2�
	double phaseStepHeightFactor;
	
	// intensity hologram parameters
	
	/*
	 * GUI edit controls
	 */
	
	private transient JCPanel phasePanel;
	private transient JComboBox hologrammifierTypeComboBox;

	private transient JFormattedTextField phaseStepHeightFactorField;

	public Hologrammifier(String name, HologrammifierType hologrammifierType, double phaseStepHeightFactor)
	{
		super(name);
		
		setHologrammifierType(hologrammifierType);
		this.phaseStepHeightFactor = phaseStepHeightFactor;
	}
	
	/**
	 * Null constructor. Creates a hologrammifier with default values. This requires no
	 * parameters.
	 */
	public Hologrammifier()
	{
		this("Hologrammifier", HologrammifierType.PHASE, 1);
	}

	@Override
	public String getComponentTypeName()
	{
		return "Hologrammifier";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			int
				width = inputBeam.getWidth(),
				height = inputBeam.getHeight();

			switch(hologrammifierType)
			{
			case INTENSITY:
				for(int i=0; i<width; i++)
					for(int j=0; j<height; j++)
					{
						// set the amplitude to the intensity
						inputBeam.setElement(i, j, inputBeam.getElement(i, j).getAbsSqr());
					}
				break;
			case PHASE:
			default:
				for(int i=0; i<width; i++)
					for(int j=0; j<height; j++)
					{
						// set the amplitude to exp(I phase*stepHeightFactor)
						inputBeam.setElement(i, j, Complex.expI(inputBeam.getElement(i, j).getArg()*phaseStepHeightFactor));
					}
				break;
			}
		}
		
		return inputBeam;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Edit focal length control
		 */
		
		editPanel.add(UIBitsAndBobs.makeRow("Hologram type", hologrammifierTypeComboBox, true));
		editPanel.add(phasePanel);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		hologrammifierTypeComboBox = new JComboBox<HologrammifierType>(HologrammifierType.values());
		hologrammifierTypeComboBox.setSelectedItem(hologrammifierType);
		hologrammifierTypeComboBox.addActionListener(this);
				
		phaseStepHeightFactorField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		phaseStepHeightFactorField.setValue(Double.valueOf(phaseStepHeightFactor));
		
		phasePanel = UIBitsAndBobs.makeRow("Phase step height", phaseStepHeightFactorField, "* 2 pi", true);
		phasePanel.setVisible(hologrammifierType == HologrammifierType.PHASE);
	}

	@Override
	public void readWidgets()
	{
		super.readWidgets();

		if(hologrammifierTypeComboBox != null) setHologrammifierType((HologrammifierType)(hologrammifierTypeComboBox.getSelectedItem()));
        if(phaseStepHeightFactorField != null) phaseStepHeightFactor = ((Number)phaseStepHeightFactorField.getValue()).doubleValue();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == phaseStepHeightFactorField)
	    {
	        phaseStepHeightFactor = ((Number)phaseStepHeightFactorField.getValue()).doubleValue();
	    }
	    
		// Fire an edit panel event
		editListener.editMade();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == hologrammifierTypeComboBox)
	    {
			setHologrammifierType((HologrammifierType)(hologrammifierTypeComboBox.getSelectedItem()));
			phasePanel.setVisible(hologrammifierType == HologrammifierType.PHASE);
	    }
	    
		// Fire an edit panel event
		editListener.editMade();
	}
	
	@Override
	public String getFormattedName()
	{
		return getName();
	}

	public HologrammifierType getHologrammifierType() {
		return hologrammifierType;
	}

	public void setHologrammifierType(HologrammifierType hologrammifierType) {
		this.hologrammifierType = hologrammifierType;
	}

	/**
	 * @return	the phase step height, in units of 2 pi
	 */
	public double getPhaseStepHeight()
	{
		return phaseStepHeightFactor;
	}

	public void setPhaseStepHeight(double phaseStepHeight)
	{
		this.phaseStepHeightFactor = phaseStepHeight;
	}
}