package javawaveoptics.optics.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;
import library.maths.Complex;

public class SpiralPhasePlate extends AbstractSimpleOpticalComponent implements Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = 3830684919640717082L;
	
	/*
	 * Fields
	 */
	
	// Topological charge
	protected double topologicalCharge;
	
	/*
	 * GUI edit controls
	 */
	
	// Topological charge edit control
	private transient JFormattedTextField topologicalChargeTextField;
	
	public SpiralPhasePlate(String name, double topologicalCharge)
	{
		super(name);
		
		this.topologicalCharge = topologicalCharge;
	}
	
	public SpiralPhasePlate()
	{
		this("Spiral phase plate", 0);
	}
	
	@Override
	public String getComponentTypeName()
	{
		return "Spiral phase plate";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		int width = inputBeam.getWidth();
		int height = inputBeam.getHeight();
		
		// Calculate the effect the spiral has on each pixel
		for(int i = 0; i < width; i++)
		{
			double x = inputBeam.getX(i);
			
			for(int j = 0; j < height; j++)
			{
				double y = inputBeam.getY(j);
				
				double phase = topologicalCharge * Math.atan2(y, x);
				
				inputBeam.multiplyElement(i, j, new Complex(Math.cos(phase), Math.sin(phase)));
			}
		}
		
		return inputBeam;
	}
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow("Topological charge", topologicalChargeTextField, true));
	}

	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		topologicalChargeTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		topologicalChargeTextField.setValue(Double.valueOf(topologicalCharge));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

        if(topologicalChargeTextField != null) topologicalCharge = ((Number)topologicalChargeTextField.getValue()).doubleValue();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == topologicalChargeTextField)
	    {
	        topologicalCharge = ((Number)topologicalChargeTextField.getValue()).doubleValue();
	    }
		    
		// Fire an edit panel event
		editListener.editMade();
	}


	public double getTopologicalCharge()
	{
		return topologicalCharge;
	}

	public void setTopologicalCharge(double topologicalCharge)
	{
		this.topologicalCharge = topologicalCharge;
	}
}
