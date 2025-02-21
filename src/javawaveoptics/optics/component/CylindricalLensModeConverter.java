package javawaveoptics.optics.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;
import javawaveoptics.ui.workbench.ExtensiveWorkbench;
import javawaveoptics.ui.workbench.ExtensiveWorkbenchOpticalComponent;
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
public class CylindricalLensModeConverter extends AbstractSimpleOpticalComponent implements ConvertableComponent, Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = -6845578248735181549L;

	/*
	 * Fields
	 */

	// designed for waist size
	double designWaistSize;
	
	double designWavelength;

	double axisAngleWithXAxis;

	/*
	 * GUI edit controls
	 */
	
	private transient LengthField designWaistSizeLengthField, designWavelengthLengthField;
	private transient JFormattedTextField axisAngleWithXAxisTextField;
	
	
	public CylindricalLensModeConverter(String name, double waistSize, double designWavelength, double axisAngleWithXAxis)
	{
		super(name);
		
		this.designWaistSize = waistSize;
		this.designWavelength = designWavelength;
		this.axisAngleWithXAxis = axisAngleWithXAxis;
	}
	
	/**
	 * Null constructor. Creates a lens with default values. This requires no
	 * parameters.
	 */
	public CylindricalLensModeConverter()
	{
		this("Cylindrical-lens mode converter", 1e-3, 632.8e-9, MyMath.deg2rad(45));
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
			double wavelength = designWavelength;	// inputBeam.getWavelength()
			
			// focal length of the cylindrical lenses
			double f = Math.PI * designWaistSize*designWaistSize / wavelength / (1 + 1./MathsUtilities.SQRT2);
			
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

	// ConvertableComponent methods
	
	@Override
	public String getConvertMenuItemText()
	{
		return "Convert to series of optical components";
	}
	
	/**
	 * 
	 */
	@Override
	public void convert(ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent)
	{
		System.out.println("SpiralAdaptiveFresnelLens::convert: Converting to two CylindricalLensSpirals");
		
		ExtensiveWorkbench workbench = workbenchOpticalComponent.getExtensiveWorkbench();
		
		int opticalTrainIndex = workbench.getOpticalTrainIndexOf(workbenchOpticalComponent);
		
		// first remove this optical component from the workbench, i.e. remove it from the optical environment, the optical component train, and the workbench
		// (from ExtensiveWorkbench.WorkbenchOpticalComponentPopupMenuActionListener.actionPerformed)
		
		// Remove component
		workbench.getOpticalEnvironment().remove(this);
		
		// Remove the component from the optical train and workbench
		workbench.getOpticalComponentTrain().remove(this);
		workbench.getWorkbenchComponents().remove(workbenchOpticalComponent);
				
		// add the new components
		// (from ExtensiveWorkbench.WorkbenchFlowArrowPopupMenuActionListener.actionPerformed)

		double wavelength = designWavelength;	// inputBeam.getWavelength()
		
		// focal length of the cylindrical lenses
		double f = Math.PI * designWaistSize*designWaistSize / wavelength / (1 + 1./MathsUtilities.SQRT2);
		
		// separation between the cylindrical lenses
		double d = MathsUtilities.SQRT2 * f;
		
		// propagate from beam waist to first cylindrical lens
		workbench.insertComponent(
				opticalTrainIndex,	// index
				new Distance(
						"Distance",	// name
						-d/2	// distance
					)
				);

		// cylindrical lens 1
		ExtensiveWorkbenchOpticalComponent cylindricalLens1 = workbench.insertComponent(
				opticalTrainIndex + 1,	// index
				new CylindricalLens(
						"Cyl. lens 1",	// name
						f,	// focalLength
						MyMath.deg2rad(-axisAngleWithXAxis)
					)
				);
		
		// space between cyl. lenses
		workbench.insertComponent(
				opticalTrainIndex + 2,	// index
				new Distance(
						"Separation",	// name
						d	// distance
						)
				);
		
		// cylindrical lens 2
		workbench.insertComponent(
				opticalTrainIndex + 3,	// index
				new CylindricalLens(
						"Cyl. lens 2",	// name
						f,	// focalLength
						MyMath.deg2rad(-axisAngleWithXAxis)
					)
				);
		
		// propagate from second cylindrical lens to beam waist
		workbench.insertComponent(
				opticalTrainIndex + 4,	// index
				new Distance(
						"Distance",	// name
						d/2	// distance
					)
				);

		workbench.selectAndShowComponent(cylindricalLens1);
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Edit focal length control
		 */
		
		editPanel.add(UIBitsAndBobs.makeRow("Designed for beam-waist size", designWaistSizeLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Designed for wavelength", designWavelengthLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Axis angle with x axis", axisAngleWithXAxisTextField, "&deg;", true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		designWaistSizeLengthField = new LengthField(this);
		designWaistSizeLengthField.setLengthInMetres(designWaistSize);
		
		designWavelengthLengthField = new LengthField(this);
		designWavelengthLengthField.setLengthInMetres(designWavelength);
		
		axisAngleWithXAxisTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		axisAngleWithXAxisTextField.setValue(Double.valueOf(0));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

    	if(designWaistSizeLengthField != null) designWaistSize = designWaistSizeLengthField.getLengthInMetres();
    	if(designWavelengthLengthField != null) designWavelength = designWavelengthLengthField.getLengthInMetres();
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
	    else if (source == designWavelengthLengthField)
	    {
	    	designWavelength = designWavelengthLengthField.getLengthInMetres();
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