package javawaveoptics.optics.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;
import library.maths.Complex;
import library.maths.MyMath;

public class Wedge extends AbstractSimpleOpticalComponent implements Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = 3830684919640717082L;
	
	/*
	 * Fields
	 */
	
	/**
	 * Angles (in degrees) w.r.t. z axis with which a ray incident in the z direction will travel after transmission,
	 * in the xz projection and yz projection, respectively
	 */
	protected double
		deflectionAngleXZ,
		deflectionAngleYZ;

	/*
	 * GUI edit controls
	 */
	
	// Topological charge edit control
	private transient JFormattedTextField
		deflectionAngleXZTextField,
		deflectionAngleYZTextField;
	
	public Wedge(String name, double deflectionAngleXZ, double deflectionAngleYZ)
	{
		super(name);
		
		this.deflectionAngleXZ = deflectionAngleXZ;
		this.deflectionAngleYZ = deflectionAngleYZ;
	}
	
	public Wedge()
	{
		this("Wedge", 0, 0);
	}
	
	@Override
	public String getComponentTypeName()
	{
		return "Wedge";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		int width = inputBeam.getWidth();
		int height = inputBeam.getHeight();
		
		double sinAlpha = Math.sin(MyMath.deg2rad(-deflectionAngleXZ));
		double sinBeta = Math.sin(MyMath.deg2rad(-deflectionAngleYZ));
		double k = 2.*Math.PI/inputBeam.getWavelength();
		
		// Calculate the effect the spiral has on each pixel
		for(int i = 0; i < width; i++)
		{
			double x = inputBeam.getX(i);
			
			for(int j = 0; j < height; j++)
			{
				double y = inputBeam.getY(j);
				
				double phase = -k*(x*sinAlpha + y*sinBeta);
				
				inputBeam.multiplyElement(i, j, Complex.expI(phase));
			}
		}
		
		return inputBeam;
	}
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow("Deflection angle (xz projection)", deflectionAngleXZTextField, "&deg;", true));
		editPanel.add(UIBitsAndBobs.makeRow("Deflection angle (yz projection)", deflectionAngleYZTextField, "&deg;", true));
	}

	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		deflectionAngleXZTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		deflectionAngleXZTextField.setValue(Double.valueOf(deflectionAngleXZ));

		deflectionAngleYZTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		deflectionAngleYZTextField.setValue(Double.valueOf(deflectionAngleYZ));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

    	if(deflectionAngleXZTextField != null) deflectionAngleXZ = ((Number)deflectionAngleXZTextField.getValue()).doubleValue();
    	if(deflectionAngleYZTextField != null) deflectionAngleYZ = ((Number)deflectionAngleYZTextField.getValue()).doubleValue();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == deflectionAngleXZTextField)
	    {
	    	deflectionAngleXZ = ((Number)deflectionAngleXZTextField.getValue()).doubleValue();
	    }
	    else if (source == deflectionAngleYZTextField)
	    {
	    	deflectionAngleYZ = ((Number)deflectionAngleYZTextField.getValue()).doubleValue();
	    }
		    
		// Fire an edit panel event
		editListener.editMade();
	}

	public double getDeflectionAngleXZ() {
		return deflectionAngleXZ;
	}

	public void setDeflectionAngleXZ(double deflectionAngleXZ) {
		this.deflectionAngleXZ = deflectionAngleXZ;
	}

	public double getDeflectionAngleYZ() {
		return deflectionAngleYZ;
	}

	public void setDeflectionAngleYZ(double deflectionAngleYZ) {
		this.deflectionAngleYZ = deflectionAngleYZ;
	}
}
