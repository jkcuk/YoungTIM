package javawaveoptics.optics.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;

public class BeamExpander extends AbstractSimpleOpticalComponent implements Serializable, PropertyChangeListener
{	
	private static final long serialVersionUID = -1223767508311702105L;

	/*
	 * Fields
	 */

	double magnificationFactor;
	
	/*
	 * GUI edit controls
	 */
	
	// Angle edit control
	private transient JFormattedTextField magnificationFactorTextField;
	
	
	public BeamExpander(String name, double magnificationFactor)
	{
		super(name);
		
		this.magnificationFactor = magnificationFactor;
	}
	
	public BeamExpander()
	{
		this("Beam expander", 1);
	}
	
	@Override
	public String getComponentTypeName()
	{
		return "Beam expander";
	}
	
	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{			
			inputBeam.setPhysicalWidth(inputBeam.getPhysicalWidth() * Math.abs(magnificationFactor));
			inputBeam.setPhysicalHeight(inputBeam.getPhysicalHeight() * Math.abs(magnificationFactor));
			inputBeam.multiply(1./Math.abs(magnificationFactor));	// make sure power is conserved
			if(magnificationFactor < 0)
			{
				// for negative magnifications, flip the amplitude matrix
				inputBeam.flipLeftRightAndUpDown();
			}
		}
		
		return inputBeam;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Edit angle control
		 */
		
		editPanel.add(UIBitsAndBobs.makeRow("Magnification factor", magnificationFactorTextField, true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		magnificationFactorTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		magnificationFactorTextField.setValue(new Double(magnificationFactor));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

		 if(magnificationFactorTextField != null) magnificationFactor = ((Number)magnificationFactorTextField.getValue()).doubleValue();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == magnificationFactorTextField)
	    {
	        magnificationFactor = ((Number)magnificationFactorTextField.getValue()).doubleValue();
	    }
		    
		// Fire an edit panel event
		editListener.editMade();
	}

	@Override
	public String getFormattedName()
	{
		return getName() + " (\u2a09 " + Double.toString(magnificationFactor) + ")";
	}
}