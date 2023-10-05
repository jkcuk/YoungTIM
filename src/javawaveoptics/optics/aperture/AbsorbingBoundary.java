package javawaveoptics.optics.aperture;

import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;

/**
 * Defines an absorbing boundary.
 * 
 * @author Johannes
 */
public class AbsorbingBoundary extends AbstractAperture implements Serializable // , PropertyChangeListener
{
	private static final long serialVersionUID = 5456135135864408808L;

	/*
	 * Fields
	 */
	
	/**
	 * width of boundary, in pixels
	 */
	int widthOfAbsorbingBoundary;
	
	/*
	 * GUI edit controls
	 */
	
	private transient JFormattedTextField widthOfAbsorbingBoundaryTextField;

	
	public AbsorbingBoundary(String name, int widthOfAbsorbingBoundary)
	{
		super(name);
		
		this.widthOfAbsorbingBoundary = widthOfAbsorbingBoundary;
	}
	
	/**
	 * Null constructor. Creates an absorbing boundary with default values.
	 * This requires no parameters.
	 */
	public AbsorbingBoundary()
	{
		this("Absorbing boundary", 10);
	}
	
	@Override
	public String getApertureTypeName()
	{
		return "Absorbing boundary";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			inputBeam.passThroughAbsorbingBoundary(widthOfAbsorbingBoundary);
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

		editPanel.add(UIBitsAndBobs.makeRow("Absorbing boundary width", widthOfAbsorbingBoundaryTextField, "elements", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		widthOfAbsorbingBoundaryTextField = UIBitsAndBobs.makeIntFormattedTextField(null);
		widthOfAbsorbingBoundaryTextField.setValue(new Integer(widthOfAbsorbingBoundary));
	}
	
	@Override
	public void readWidgets()
	{
        if(widthOfAbsorbingBoundaryTextField != null) widthOfAbsorbingBoundary = ((Number)widthOfAbsorbingBoundaryTextField.getValue()).intValue();
	}
	
//	@Override
//	public void propertyChange(PropertyChangeEvent e)
//	{
//	    Object source = e.getSource();
//	    
//	    if (source == widthOfAbsorbingBoundaryTextField)
//	    {
//	        widthOfAbsorbingBoundary = ((Number)widthOfAbsorbingBoundaryTextField.getValue()).intValue();
//	    }
//	    
//		// Fire an edit panel event
//		// editListener.editMade();
//	}

	public int getWidthOfAbsorbingBoundary() {
		return widthOfAbsorbingBoundary;
	}

	public void setWidthOfAbsorbingBoundary(int widthOfAbsorbingBoundary) {
		this.widthOfAbsorbingBoundary = widthOfAbsorbingBoundary;
	}
	

}