package javawaveoptics.optics.component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;

public class BeamRotator extends AbstractSimpleOpticalComponent implements Serializable, PropertyChangeListener, ItemListener
{	
	private static final long serialVersionUID = 5424504744184150913L;
	
	/*
	 * Fields
	 */

	// Angle to rotate
	private double rotationAngle;
	
	/**
	 * factor by which the beam gets zoomed
	 */
	private double zoomFactor;
	
	// Whether or not to clip the rotated image
	private boolean clip;
	
	/*
	 * GUI edit controls
	 */
	
	// Angle edit control
	private transient JFormattedTextField rotationAngleTextField, zoomFactorTextField;
	
	// Clip edit control
	private transient JCheckBox editClipCheckBox;
	
	public BeamRotator(String name, double rotationAngle, double zoomFactor, boolean clip)
	{
		super(name);
		
		this.rotationAngle = rotationAngle;
		this.zoomFactor = zoomFactor;
		this.clip = clip;
	}
	
	public BeamRotator()
	{
		this("Beam rotator", 0, 1, true);
	}
	
	@Override
	public String getComponentTypeName()
	{
		return "Beam rotator";
	}
	
	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{			
			inputBeam.rotateAndZoom(rotationAngle, zoomFactor, clip);
		}
		
		return inputBeam;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
	
		// angle
		editPanel.add(UIBitsAndBobs.makeRow("Rotation angle", rotationAngleTextField, "&deg;", true));

		// zoom factor
		editPanel.add(UIBitsAndBobs.makeRow("Zoom factor (>0)", zoomFactorTextField, true));

		// clip?
		editPanel.add(UIBitsAndBobs.makeRow("Clip field", editClipCheckBox, true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		rotationAngleTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		rotationAngleTextField.setValue(new Double(rotationAngle));

		zoomFactorTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		zoomFactorTextField.setValue(new Double(zoomFactor));

		// Clip edit control
		editClipCheckBox = new JCheckBox();
		editClipCheckBox.setSelected(clip);
		editClipCheckBox.addItemListener(this);		
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

        if(rotationAngleTextField != null) rotationAngle = ((Number)rotationAngleTextField.getValue()).doubleValue();
    	if(zoomFactorTextField != null) zoomFactor = ((Number)zoomFactorTextField.getValue()).doubleValue();
    	if(editClipCheckBox != null) clip = editClipCheckBox.isSelected();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == rotationAngleTextField)
	    {
	        rotationAngle = ((Number)rotationAngleTextField.getValue()).doubleValue();
	        // System.out.println("new rotation angle ="+rotationAngle);
	    }
	    else if (source == zoomFactorTextField)
	    {
	    	zoomFactor = ((Number)zoomFactorTextField.getValue()).doubleValue();
//	    	// if the zoom factor is negative...
//	    	if(zoomFactor < 0)
//	    	{
//	    		// ... make the zoom factor the absolute value of that negative zoom factor...
//	    		zoomFactor = Math.abs(zoomFactor);
//		    	zoomFactorTextField.setValue(zoomFactor);
//		    	
//		    	// ... and add 180 degrees to the rotation angle
//		    	double r = rotationAngle+180;
//		    	rotationAngle = r-Math.floor(r/360)*360;
//		    	rotationAngleTextField.setValue(rotationAngle);
//	    	}
	        // System.out.println("new zoom factor ="+zoomFactor);
	    }
		    
		// Fire an edit panel event
		editListener.editMade();
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getSource();
		
		if(source.equals(editClipCheckBox))
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				clip = true;
			}
			else
			{
				clip = false;
			}
		}
	}
	
	@Override
	public String getFormattedName()
	{
		return getName() + " (" + Double.toString(rotationAngle) + "&deg;, x"+Double.toString(zoomFactor)+")";
	}

	public double getRotationAngle()
	{
		return rotationAngle;
	}

	public void setRotationAngle(double rotationAngle)
	{
		this.rotationAngle = rotationAngle;
	}

	public double getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}

	public boolean isClip()
	{
		return clip;
	}

	public void setClip(boolean clip)
	{
		this.clip = clip;
	}
}