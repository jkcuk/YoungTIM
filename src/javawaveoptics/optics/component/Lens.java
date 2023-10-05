package javawaveoptics.optics.component;

import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.JCPanel;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;

/**
 * Defines a thin lens. Light incident on this component will undergo a phase shift as a function of the
 * distance from the optical axis. The user defines a focal length for the lens which the class uses
 * to calculate the phase shift to apply to to light beam a given distance from the optical axis.
 * 
 * @author Sean
 */
public class Lens extends AbstractSimpleOpticalComponent implements Serializable //, PropertyChangeListener
{
	private static final long serialVersionUID = 3534911291321554049L;
	
	/*
	 * Fields
	 */

	// Focal length of the lens
	protected double focalLength;
	
	/*
	 * GUI edit controls
	 */
	
	protected transient JCPanel focalLengthPanel;
	protected transient LengthField focalLengthField;

	
	public Lens(String name, double focalLength)
	{
		super(name);
		
		this.focalLength = focalLength;
	}
	
	/**
	 * Null constructor. Creates a lens with default values. This requires no
	 * parameters.
	 */
	public Lens()
	{
		this("Lens", 1);
	}

	@Override
	public String getComponentTypeName()
	{
		return "Lens";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			inputBeam.passThroughLens(focalLength);
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
		
		editPanel.add(focalLengthPanel);
		focalLengthField.setToolTipText("<html>Edit the focal length, <i>f</i>, of the lens;<br><i>f</i> can be positive or negative</html>");
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		focalLengthField = new LengthField(null);
		focalLengthField.setLengthInMetres(focalLength);
		focalLengthPanel = UIBitsAndBobs.makeRow("Focal length", focalLengthField, true);
	}

	@Override
	public void readWidgets()
	{
		super.readWidgets();

		if(focalLengthField != null) focalLength = focalLengthField.getLengthInMetres();
	}
	
//	@Override
//	public void propertyChange(PropertyChangeEvent e)
//	{
//	    Object source = e.getSource();
//	    
//	    if (source == focalLengthField)
//	    {
//	        focalLength = focalLengthField.getLengthInMetres();
//	    }
//	    
//		// Fire an edit panel event
//		editListener.editMade();
//	}
	
	@Override
	public String getFormattedName()
	{
		return "f = " + Double.toString(focalLength) + "m";
		// return getName() + " (f = " + Double.toString(focalLength) + "m)";
	}

	public double getFocalLength()
	{
		return focalLength;
	}

	public void setFocalLength(double focalLength)
	{
		this.focalLength = focalLength;
	}
}