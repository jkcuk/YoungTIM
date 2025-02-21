package javawaveoptics.optics.component;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.JCProgressBar;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;

/**
 * Defines a propagation distance. The user specifies the distance to propagate the light beam cross
 * section, and the class then works out the effect this has on the light beam (such as dispersion, phase,
 * etc.)
 * 
 * @author Sean
 * @author Johannes
 */
public class Distance extends AbstractSimpleOpticalComponent implements Serializable, PropertyChangeListener, ItemListener
{
	private static final long serialVersionUID = 151323036937115173L;

	/*
	 * Fields
	 */
	
	// Distance to propagate
	private double distance, stepSize;
	private int widthOfAbsorbingBoundary;
	private boolean BPM;
	
	/*
	 * GUI edit controls
	 */
	
	private transient LengthField distanceLengthField, stepSizeLengthField;
	private transient JFormattedTextField widthOfAbsorbingBoundaryTextField;
	private transient JCheckBox BPMCheckBox;
	private transient JPanel BPMPanel;
	private transient JCProgressBar progressBar;
	
	
	public Distance(String name, double distance, double stepSize, int widthOfAbsorbingBoundary, boolean BPM)
	{
		super(name);
	
		this.distance = distance;
		this.stepSize = stepSize;
		this.widthOfAbsorbingBoundary = widthOfAbsorbingBoundary;
		this.BPM = BPM;
	}
	
	public Distance(String name, double distance)
	{
		this(name, distance, 1., 0, false);
	}
	
	/**
	 * Null constructor. Creates a propagation with default values. This requires
	 * no parameters.
	 */
	public Distance()
	{
		this("Distance", 1);
	}

	@Override
	public String getComponentTypeName()
	{
		return "Distance";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			if(BPM)
			{
				if(progressBar == null)
				{
					// make a progress bar if there isn't one already
					progressBar = new JCProgressBar(0, 1);
					progressBar.setVisible(false);
				}

				progressBar.setVisible(true);
				inputBeam.propagateBPM(distance, stepSize, widthOfAbsorbingBoundary, progressBar, this);
				progressBar.setVisible(false);
			}
			else
				inputBeam.propagate(distance);
		}
		
		return inputBeam;
	}
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Edit distance control
		 */
		
		editPanel.add(UIBitsAndBobs.makeRow("Propagation distance", distanceLengthField, true));
		distanceLengthField.setToolTipText("<html>Edit the propagation distance, <i>\u0394z</i>;<br><i>\u0394z</i> can be positive (forward propagation) or negative (backward propagation)</html>");
		
		/*
		 * BPM stuff
		 */
		
		BPMCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		editPanel.add(BPMCheckBox);

		BPMPanel = new JPanel();
		BPMPanel.setLayout(new BoxLayout(BPMPanel, BoxLayout.Y_AXIS));
		BPMPanel.setBorder(UIBitsAndBobs.getTitledBorder("Beam-propagation method parameters"));
		
		BPMPanel.add(UIBitsAndBobs.makeRow("Step size", stepSizeLengthField, true));
		BPMPanel.add(UIBitsAndBobs.makeRow("Absorbing boundary width", widthOfAbsorbingBoundaryTextField, "elements", true));

		BPMPanel.add(progressBar);

		BPMPanel.setVisible(BPM);
		editPanel.add(BPMPanel);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		distanceLengthField = new LengthField(this);
		distanceLengthField.setLengthInMetres(distance);
		
		BPMCheckBox = new JCheckBox("Beam-propagation method");
		BPMCheckBox.setSelected(BPM);
		BPMCheckBox.addItemListener(this);

		stepSizeLengthField = new LengthField(this);
		stepSizeLengthField.setLengthInMetres(stepSize);

		widthOfAbsorbingBoundaryTextField = UIBitsAndBobs.makeIntFormattedTextField(this);
		widthOfAbsorbingBoundaryTextField.setValue(Integer.valueOf(widthOfAbsorbingBoundary));

		// see http://docs.oracle.com/javase/tutorial/uiswing/components/progress.html
		if(progressBar == null)
		{
			progressBar = new JCProgressBar(0, 1);
			progressBar.setVisible(false);
		}
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

        if(distanceLengthField != null) distance = distanceLengthField.getLengthInMetres();
		if(BPMCheckBox != null) BPM = BPMCheckBox.isSelected();
        if(stepSizeLengthField != null) stepSize = stepSizeLengthField.getLengthInMetres();
        if(widthOfAbsorbingBoundaryTextField != null) widthOfAbsorbingBoundary = ((Number)widthOfAbsorbingBoundaryTextField.getValue()).intValue();
	}
	
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getItemSelectable();

		if (source == BPMCheckBox)
	    {
			BPM = BPMCheckBox.isSelected();
			BPMPanel.setVisible(BPM);
			// stepSizeLengthField.setEnabled(BPM);
			// widthOfAbsorbingBoundaryTextField.setEnabled(BPM);
	    }
	}

	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == distanceLengthField)
	    {
	        distance = distanceLengthField.getLengthInMetres();
	    }
	    else if (source == stepSizeLengthField)
	    {
	        stepSize = stepSizeLengthField.getLengthInMetres();
	    }
	    else if (source == widthOfAbsorbingBoundaryTextField)
	    {
	        widthOfAbsorbingBoundary = ((Number)widthOfAbsorbingBoundaryTextField.getValue()).intValue();
	    }

	    
		// Fire an edit panel event
		editListener.editMade();
	}

	/**
	 * @return is the component image used as a progress bar?  normally false (override if necessary)
	 */
	@Override
	public boolean isComponentImageIndicatingProgress()
	{
		return true;
	}

	@Override
	public String getFormattedName()
	{
		return "\u0394z = " + Double.toString(distance) + "m";
		// return getName() + " (d = " + Double.toString(distance) + "m)";
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getStepSize() {
		return stepSize;
	}

	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
	}

	public int getWidthOfAbsorbingBoundary() {
		return widthOfAbsorbingBoundary;
	}

	public void setWidthOfAbsorbingBoundary(int widthOfAbsorbingBoundary) {
		this.widthOfAbsorbingBoundary = widthOfAbsorbingBoundary;
	}

	public boolean isBPM() {
		return BPM;
	}

	public void setBPM(boolean bPM) {
		BPM = bPM;
	}
}