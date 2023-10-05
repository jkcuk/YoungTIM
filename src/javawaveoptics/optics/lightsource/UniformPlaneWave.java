package javawaveoptics.optics.lightsource;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;

/**
 * Provides a uniform beam output with corresponding edit functionality.
 * 
 * @author Sean, Johannes
 */
public class UniformPlaneWave extends AbstractLightSource implements Serializable, PropertyChangeListener, ActionListener
{
	private static final long serialVersionUID = -5361806058271368977L;
	
	/*
	 * Fields
	 */
	
	/**
	 * wave number in x direction
	 */
	protected double xWaveNumber;
	
	/**
	 * wave number in y direction
	 */
	protected double yWaveNumber;
	
	/*
	 * GUI edit controls
	 */
	
	/**
	 * edit control for wave number in x direction
	 */
	private transient JFormattedTextField xWaveNumberTextField;

	/**
	 * edit control for wave number in y direction
	 */
	private transient JFormattedTextField yWaveNumberTextField;

	private transient JFormattedTextField phaseCycleInXTextField, phaseCycleInYTextField;
	private transient JButton setKButton;
	

	public UniformPlaneWave(String name, double xWaveNumber, double yWaveNumber)
	{
		super(name);
		
		this.xWaveNumber = xWaveNumber;
		this.yWaveNumber = yWaveNumber;
	}
	
	public UniformPlaneWave()
	{
		this("Uniform plane wave", 0, 0);
	}
	
	@Override
	public String getLightSourceTypeName()
	{
		return "Uniform plane wave";
	}
	
	public BeamCrossSection getBeamOutput(double physicalWidth, double physicalHeight, double wavelength, int plotWidth, int plotHeight)
	{
		BeamCrossSection beam = new BeamCrossSection(plotWidth, plotHeight, physicalWidth, physicalHeight, wavelength);
		
		beam.makeUniformPlaneWave(xWaveNumber, yWaveNumber);
		
		return beam;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		// editPanel = new JPanel();	// don't draw the titled border with nothing in it --- looks crap

		editPanel.add(UIBitsAndBobs.makeRow("Wave numbers: k<sub>x</sub>", xWaveNumberTextField, "/m, k<sub>y</sub>", yWaveNumberTextField, "/m", true));

		editPanel.add(UIBitsAndBobs.makeRow("Set to ", phaseCycleInXTextField, " 2&pi; phase cycles per width &amp; ", phaseCycleInYTextField, " 2&pi; phase cycles per height", setKButton, false));
	}
	
	
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		xWaveNumberTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		xWaveNumberTextField.setValue(new Double(xWaveNumber));

		yWaveNumberTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		yWaveNumberTextField.setValue(new Double(yWaveNumber));
		
		phaseCycleInXTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		phaseCycleInXTextField.setValue(new Double(0));

		phaseCycleInYTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		phaseCycleInYTextField.setValue(new Double(0));
		
		setKButton = new JButton("Go");
		setKButton.addActionListener(this);
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

    	if(xWaveNumberTextField != null) xWaveNumber = ((Number)xWaveNumberTextField.getValue()).doubleValue();
    	if(yWaveNumberTextField != null) yWaveNumber = ((Number)yWaveNumberTextField.getValue()).doubleValue();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == xWaveNumberTextField)
	    {
	    	xWaveNumber = ((Number)xWaveNumberTextField.getValue()).doubleValue();
	    }
	    else if (source == yWaveNumberTextField)
	    {
	    	yWaveNumber = ((Number)yWaveNumberTextField.getValue()).doubleValue();
	    }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    Object source = e.getSource();
	    
	    if (source == setKButton)
	    {
	    	xWaveNumberTextField.setValue(new Double(((Number)phaseCycleInXTextField.getValue()).doubleValue()*2.*Math.PI/lightSource.getPhysicalWidth()));
	    	yWaveNumberTextField.setValue(new Double(((Number)phaseCycleInYTextField.getValue()).doubleValue()*2.*Math.PI/lightSource.getPhysicalHeight()));
	    }
	}

}
