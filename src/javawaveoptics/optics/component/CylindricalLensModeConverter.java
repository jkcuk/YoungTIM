package javawaveoptics.optics.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;
import javawaveoptics.utility.MathsUtilities;
import library.maths.MyMath;

/**
 * Defines a cylindrical-lens pi/2 mode converter [1].
 * 
 * References:
 * [1] M. W. Beijersbergen et al., Astigmatic laser mode converters and transfer of orbital angular momentum, Opt. Commun.� 96� 123-132� (1993)
 * 
 * @author Johannes
 */
public class CylindricalLensModeConverter extends AbstractSimpleOpticalComponent implements Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = -6845578248735181549L;

	/*
	 * Fields
	 */

	// designed for waist size
	double designWaistSize;

	double axisAngleWithXAxis;

	/*
	 * GUI edit controls
	 */
	
	private transient LengthField designWaistSizeLengthField;
	private transient JFormattedTextField axisAngleWithXAxisTextField;
	
	
	public CylindricalLensModeConverter(String name, double waistSize, double axisAngleWithXAxis)
	{
		super(name);
		
		this.designWaistSize = waistSize;
		this.axisAngleWithXAxis = axisAngleWithXAxis;
	}
	
	/**
	 * Null constructor. Creates a lens with default values. This requires no
	 * parameters.
	 */
	public CylindricalLensModeConverter()
	{
		this("Cylindrical-lens mode converter", 1e-3, MyMath.deg2rad(45));
	}

	@Override
	public String getComponentTypeName()
	{
		return "Cylindrical-lens mode converter";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			// focal length of the cylindrical lenses
			double f = Math.PI * designWaistSize*designWaistSize / inputBeam.getWavelength() / (1 + 1./MathsUtilities.SQRT2);
			
			// separation between the cylindrical lenses
			double d = MathsUtilities.SQRT2 * f;
			
			inputBeam.propagate(-d/2);	// propagate from the waist plane (where we assume we are) to the first lens
			inputBeam.passThroughCylindricalLens(f, MyMath.deg2rad(-axisAngleWithXAxis));
			inputBeam.propagate(d);	// propagate from the first lens to the second lens
			inputBeam.passThroughCylindricalLens(f, MyMath.deg2rad(-axisAngleWithXAxis));
			inputBeam.propagate(-d/2);	// propagate from the second lens to the waist plane again
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
		
		editPanel.add(UIBitsAndBobs.makeRow("Designed for beam-waist size", designWaistSizeLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Axis angle with x axis", axisAngleWithXAxisTextField, "&deg;", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		designWaistSizeLengthField = new LengthField(this);
		designWaistSizeLengthField.setLengthInMetres(designWaistSize);
		
		axisAngleWithXAxisTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		axisAngleWithXAxisTextField.setValue(new Double(0));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

    	if(designWaistSizeLengthField != null) designWaistSize = designWaistSizeLengthField.getLengthInMetres();
        if(axisAngleWithXAxisTextField != null) axisAngleWithXAxis = ((Number)axisAngleWithXAxisTextField.getValue()).doubleValue();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == designWaistSizeLengthField)
	    {
	    	designWaistSize = designWaistSizeLengthField.getLengthInMetres();
	    }
	    else if (source == axisAngleWithXAxisTextField)
	    {
	        axisAngleWithXAxis = ((Number)axisAngleWithXAxisTextField.getValue()).doubleValue();
	    }
	    
		// Fire an edit panel event
		editListener.editMade();
	}
	
	@Override
	public String getFormattedName()
	{
		return getName();	// + " (w0 = " + Double.toString(designWaistSize) + "m)";
	}
}