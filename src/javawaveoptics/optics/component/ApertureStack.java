package javawaveoptics.optics.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.optics.aperture.AbstractAperture;
import javawaveoptics.optics.aperture.ApertureType;
import javawaveoptics.ui.JCProgressBar;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;

/**
 * A stack of apertures with a given separation between neighbouring apertures.
 * The apertures can be one of a number of different types.
 * 
 * @author johannes
 */
public class ApertureStack extends AbstractSimpleOpticalComponent implements Serializable, ActionListener, PropertyChangeListener
{
	private static final long serialVersionUID = -1864167530336980970L;

	/*
	 * Fields
	 */

	/**
	 * aperture that is being stacked
	 */
	private AbstractAperture aperture;
	
	/**
	 * number of apertures 
	 */
	private int noOfApertures;
	
	/**
	 * separation between apertures
	 */
	private double separation;

	/*
	 * GUI edit controls
	 */
	
	private transient JPanel apertureEditPanel;
	private transient JComboBox<ApertureType> apertureTypeComboBox;
	private transient JFormattedTextField noOfAperturesTextField;
	private transient LengthField separationLengthField;
	
	private transient LengthField thicknessLengthField;
	private transient JButton setSeparationButton;

	private transient JCProgressBar progressBar;
	

	
	public ApertureStack(String name, AbstractAperture aperture, int noOfApertures, double separation)
	{
		super(name);
		
		this.aperture = aperture;
		this.noOfApertures = noOfApertures;
		this.separation = separation;
	}
	
	public ApertureStack()
	{
		this("Aperture stack", ApertureType.getAperture(ApertureType.GRATING), 10, 1e-3);
	}

	@Override
	public String getComponentTypeName()
	{
		return "Aperture stack";
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow("Number of apertures", noOfAperturesTextField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Separation between apertures", separationLengthField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Overall thickness", thicknessLengthField, setSeparationButton, true));

		apertureTypeComboBox.addActionListener(this);
		apertureTypeComboBox.setActionCommand("Aperture selected");
		
		// Create aperture and its edit controls
		drawApertureEditControls();
		
		editPanel.add(UIBitsAndBobs.makeRow("Aperture type", apertureTypeComboBox, true));
		editPanel.add(apertureEditPanel);
		
		editPanel.add(progressBar);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		// aperture edit controls
		apertureEditPanel = new JPanel();		// The edit controls for the selected aperture type
		apertureTypeComboBox = new JComboBox<ApertureType>(ApertureType.values());	// The combo box for selecting a aperture type
		apertureTypeComboBox.setSelectedItem(ApertureType.getApertureType(aperture));
		
		// create the thicknessLengthField first, as changing the value of the noOfAperturesTextField and separationLengthField change its value
		thicknessLengthField = new LengthField(this);
		thicknessLengthField.setLengthInMetres(separation*(noOfApertures-1));
		
		noOfAperturesTextField = UIBitsAndBobs.makeIntFormattedTextField(this);
		noOfAperturesTextField.setValue(new Integer(noOfApertures));
		
		separationLengthField = new LengthField(this);
		separationLengthField.setLengthInMetres(separation);

//		// as noOfAperturesTextField, separationLengthField and thicknessLengthField change each other, add the listeners only after the initial values have been set
//		noOfAperturesTextField.addPropertyChangeListener("value", this);
//		separationLengthField.setPropertyChangeListener(this);
//		thicknessLengthField.setPropertyChangeListener(this);
		
		setSeparationButton = new JButton("Set separation accordingly");
		setSeparationButton.addActionListener(this);


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

    	if(separationLengthField != null) separation = separationLengthField.getLengthInMetres();
    	if(noOfAperturesTextField != null) noOfApertures = ((Number)noOfAperturesTextField.getValue()).intValue();
		if(thicknessLengthField != null) thicknessLengthField.setLengthInMetres(separation*(noOfApertures-1));
    	// if(apertureTypeComboBox != null) aperture = ApertureType.getAperture((ApertureType)(apertureTypeComboBox.getSelectedItem()));

		aperture.readWidgets();
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		    String command = event.getActionCommand();
		    	
		    if(command.equals("Aperture selected"))
		    {
		    	// A new aperture type has been selected
		    		
		    	aperture = ApertureType.getAperture((ApertureType)(apertureTypeComboBox.getSelectedItem()));
		    		
		    	drawApertureEditControls();
		    }
//		    else if (event.getSource() == setSeparationButton)
//		    {
//		    	separationLengthField.setLengthInMetres(thicknessLengthField.getLengthInMetres()/(((Number)noOfAperturesTextField.getValue()).intValue()-1));
//		    }
		    
		    editListener.editMade();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == separationLengthField)
	    {
	    	separation = separationLengthField.getLengthInMetres();
			if(thicknessLengthField != null) thicknessLengthField.setLengthInMetres(separation*(noOfApertures-1));
	    }
	    else if (source == noOfAperturesTextField)
	    {
	    	noOfApertures = ((Number)noOfAperturesTextField.getValue()).intValue();
	    	if(thicknessLengthField != null) thicknessLengthField.setLengthInMetres(separation*(noOfApertures-1));
	    }
//	    else if (source == thicknessLengthField)
//	    {
//	    	separationLengthField.setLengthInMetres(thicknessLengthField.getLengthInMetres()/(noOfApertures-1)); 
//	    }
//	    
//		// Fire an edit panel event
//		editListener.editMade();
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			if(progressBar == null)
			{
				// make a progress bar if there isn't one already
				progressBar = new JCProgressBar(0, 1);
				progressBar.setVisible(false);
			}

			progressBar.setVisible(true);
			inputBeam.passThroughApertureStack(aperture, noOfApertures, separation, progressBar, this);
			progressBar.setVisible(false);
		}
		
		return inputBeam;
	}

	private void drawApertureEditControls()
	{
		apertureEditPanel.removeAll();
		apertureEditPanel.add(aperture.getEditPanel());
		
		apertureEditPanel.revalidate();
		apertureEditPanel.repaint();
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
		return getName();
	}

	public AbstractAperture getAperture() {
		return aperture;
	}

	public void setApertureType(ApertureType apertureType) {
		aperture = ApertureType.getAperture(apertureType);
	}
}