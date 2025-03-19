package javawaveoptics.optics.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;

/**
 * Scales the intensity of a light beam in one way out of a number of ways.
 * 
 * @author Johannes
 */
public class NeutralDensityFilter extends AbstractSimpleOpticalComponent implements Serializable, PropertyChangeListener, ActionListener
{
	private static final long serialVersionUID = 760982217080336305L;

	/*
	 * Fields
	 */

	public enum ScalingType
	{
		FACTOR("multiply intensity by factor"),
		OPTICAL_DENSITY("optical density"),
		MAX_INTENSITY("set maximum intensity to"),
		POWER("set power in beam to");
		
		private String description;
		private ScalingType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	ScalingType scalingType;

	// the number can be the intensity scaling factor, the maximum intensity, ...
	double number;

	/*
	 * GUI edit controls
	 */
	
	private transient JComboBox<ScalingType> scalingTypeComboBox;
	private transient JFormattedTextField numberField;
	
//	private static String[] scalingTypes = {
//		"multiply intensity by factor",
//		"optical density",
//		"set maximum intensity to",
//		"set power in beam to"
//	};

	public NeutralDensityFilter(String name, ScalingType scalingType, double number)
	{
		super(name);
		
		this.scalingType = scalingType;
		this.number = number;
	}
	
	/**
	 * Null constructor. Creates a neutral-density filter with default values. This requires no
	 * parameters.
	 */
	public NeutralDensityFilter()
	{
		this("ND filter", ScalingType.FACTOR, 1);
	}
	
	@Override
	public NeutralDensityFilter clone()
	{
		return new NeutralDensityFilter(name, scalingType, number);
	}

	@Override
	public String getComponentTypeName()
	{
		return "Neutral-density filter";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			int
				width = inputBeam.getWidth(),
				height = inputBeam.getHeight();
	
			// String scalingType = scalingTypes[scalingTypeComboBox.getSelectedIndex()];
			
			double amplitudeFactor;
			
			// calculate the intensity scaling factor
			if(scalingType.equals("multiply intensity by factor"))
			{
				amplitudeFactor = Math.sqrt(number);			
			}
			else if(scalingType.equals("optical density"))
			{
				amplitudeFactor = Math.pow(10, -0.5*number);
			}
			else if(scalingType.equals("set maximum intensity to"))
			{
				// find the maximum intensity
				double maxIntensity = 0;
				
				for(int i=0; i<width; i++)
					for(int j=0; j<height; j++)
					{
						double intensity = inputBeam.getElement(i, j).getAbsSqr();
						
						if(intensity > maxIntensity) maxIntensity = intensity;
					}
				
				amplitudeFactor = Math.sqrt(number / maxIntensity);
			}
			else if(scalingType.equals("set power in beam to"))
			{
				amplitudeFactor = Math.sqrt(number / inputBeam.getPower());
			}
			else
			{
				System.err.println("NeutralDensityFilter::fromInputBeamCalculateOutputBeam: Unknown combo-box choice: " + scalingType);
				amplitudeFactor = 1.;
			}
			
			System.out.println("Amplitude is scaled by a factor " + amplitudeFactor);
			
			if(amplitudeFactor != 1.)
			{
				// actually scale the amplitude now
				for(int i=0; i<width; i++)
					for(int j=0; j<height; j++)
					{
						inputBeam.setElement(i, j, inputBeam.getElement(i, j).multiply(amplitudeFactor));
					}
			}
		}
		
		return inputBeam;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		editPanel.add(UIBitsAndBobs.makeRow(scalingTypeComboBox, numberField, true));
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		numberField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		numberField.setValue(Double.valueOf(number));
		
		scalingTypeComboBox = new JComboBox<ScalingType>(ScalingType.values());
		scalingTypeComboBox.setToolTipText("Type of scaling performed by this component");
		scalingTypeComboBox.addActionListener(this);
		scalingTypeComboBox.setSelectedItem(scalingType);
		scalingTypeComboBox.setMaximumSize(scalingTypeComboBox.getPreferredSize());
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

		scalingType = (ScalingType)(scalingTypeComboBox.getSelectedItem());
		
        if(numberField != null) number = ((Number)numberField.getValue()).doubleValue();
        // scalingTypeComboBox is read out in fromInputBeamCalculateOutputBeam
	}

	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == numberField)
	    {
	        number = ((Number)numberField.getValue()).doubleValue();
	    }
	    
		// Fire an edit panel event
		editListener.editMade();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(scalingTypeComboBox))
		{
			scalingType = (ScalingType)(scalingTypeComboBox.getSelectedItem());
		}
	}
	
	@Override
	public String getFormattedName()
	{
		return getName();
	}
}