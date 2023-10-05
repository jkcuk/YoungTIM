package javawaveoptics.optics.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.UIManager;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.optics.ComponentInput;
import javawaveoptics.optics.ComponentOutput;
import javawaveoptics.ui.ClickableInputOutputShape;
import javawaveoptics.ui.ClickableInterface;
import javawaveoptics.ui.UIBitsAndBobs;
import javawaveoptics.utility.SimulationException;
import library.maths.Complex;

/**
 * Defines a mirror.
 * 
 * Inputs 0 and 1 correspond to the amplitudes incident on the mirrors left and right sides.
 * Outputs 0 and 1 correspond to the amplitudes leaving the mirror's left and right sides.
 * 
 * The amplitudes of the outputs are calculated using the Equations in Fig. 5 of Ref. 1, which become in our case
 * 
 *   output0 = r input0 + i t input1
 *   output1 = r input1 + i t input0
 *   
 * These equations imply a pi/2 phase shift upon transmission.
 * If the mirror is lossless,
 * 
 *   r^2 + t^2 = 1.
 * 
 * References
 * [1] C. Bond, D. Brown, A. Freise, and K. A. Strain, "Interferometer techniques for gravitational-wave detection", Living Rev. Relativity 19, 3 (2016)
 * 
 * @author Johannes
 */
public class Mirror extends AbstractOpticalComponent implements Serializable, ActionListener, ClickableInterface, PropertyChangeListener, ItemListener
{
	private static final long serialVersionUID = -4910536071997455232L;

	// The padding given to the beam wires drawn on the image, i.e. the number of pixels
	// from the top that the 0th wire will be drawn to and the number of pixels from the
	// bottom the nth wire will be drawn to.
	// NOTE: This is the unscaled padding value!
	private static final int beamWireYPadding = 25;
	
	// Similarly, the x-direction padding. The 0th input will be placed at beamWireXPadding,
	// and the nth input will be placed at (width - beamWireXPadding).
	// NOTE: This is the unscaled padding value!
	private static final int beamWireXPadding = 50;
	
	// List of clickable shapes for inputs and outputs
	private transient ArrayList<ClickableInputOutputShape> inputClickableShapes;
	private transient ArrayList<ClickableInputOutputShape> outputClickableShapes;
	
	/*
	 * Fields
	 */

	/**
	 * if true, the mirror is lossless, which means r^2 + t^2 = 1
	 */
	private boolean lossless;

	/**
	 * amplitude reflection coefficient, r
	 */
	private double reflectionCoefficient;

	/**
	 * amplitude transmission coefficient, t
	 */
	private double transmissionCoefficient;
	
	/*
	 * GUI edit controls
	 */

	// lossless checkbox
	private transient JCheckBox losslessCheckBox;

	// reflection and transmission coefficients
	private transient JFormattedTextField reflectionCoefficientTextField, transmissionCoefficientTextField;
	
	// Swap inputs button
	private transient JButton swapInputsButton;
	private transient JButton swapOutputsButton;

	/**
	 * Important note regarding the use of mirrors:
	 * 
	 * When creating an optical environment for Young TIM to load, be aware that by
	 * default a mirror will always be drawn with an input connected to its first
	 * (top) arm. If you ever connect the first component (the component created by
	 * addFirstComponent() to a mirror, make sure it is connected to the first
	 * mirror input.
	 * 
	 * @param name
	 */
	public Mirror(String name, boolean lossless, double reflectionCoefficient, double transmissionCoefficient)
	{
		super(name, 2, 2);
		
		this.lossless = lossless;
		this.reflectionCoefficient = reflectionCoefficient;
		this.transmissionCoefficient = transmissionCoefficient;
	}
	
	/**
	 * Lossless mirror.  The transmission coefficient, t, is given by the equation r^2 + t^2 = 1.
	 * @param name
	 * @param reflectionCoefficient	r
	 */
	public Mirror(String name, double reflectionCoefficient)
	{
		this(name, true, reflectionCoefficient, Math.sqrt(1.0 - reflectionCoefficient*reflectionCoefficient));
	}
	
	/**
	 * Null constructor. Creates a beam splitter with default values. This requires
	 * no parameters.
	 */
	public Mirror()
	{
		this("Mirror", 0.99);
	}
	
	@Override
	public String getComponentTypeName()
	{
		return "Mirror";
	}

	@Override
	protected ArrayList<BeamCrossSection> simulate(ArrayList<BeamCrossSection> inputs)
	throws SimulationException
	{
		if(!componentEnabled)
		{
			return inputs;
		}

		// Retrieve the inputs
		BeamCrossSection input0 = inputs.get(0);
		BeamCrossSection input1 = inputs.get(1);
		
		// Create an ArrayList that holds the outputs
		ArrayList<BeamCrossSection> outputs = new ArrayList<BeamCrossSection>();
		
		// Create (null by default) output beam objects
		BeamCrossSection output0 = null;
		BeamCrossSection output1 = null;
		
		// Check if inputs are null or not, and deal with them appropriately
		if((input0 == null) && (input1 == null))
		{
			// no input is connected
			throw new SimulationException("BeamSplitter::simulate", "Both inputs to beam splitter \"" + name + "\" are null.");
		}
		else
		{
			// check if both inputs non-null
			if((input0 != null) && (input1 != null))
			{
				// yes, both inputs are non-null
				// now check that they are compatible!
				if(
					(input0.getWidth() != input1.getWidth()) ||
					(input0.getHeight() != input1.getHeight()) ||
					(input0.getPhysicalWidth() != input1.getPhysicalWidth()) ||
					(input0.getPhysicalHeight() != input1.getPhysicalHeight()) ||
					(input0.getWavelength() != input1.getWavelength())
				)
				{
					// they are not compatible!
					throw new SimulationException("BeamSplitter::simulate", "The inputs to " + name + " are incompatible");
				}
			}
			else
			{
				// one of the inputs is null, the other one isn't
				// no need to show a warning message as this is already done by AbstractOpticalComponents
				// System.err.println("Warning: one of the inputs to beam splitter \"" + name + "\" is null.");
				
				// find the non-null input
				BeamCrossSection nonNullInput = (input0 == null) ? input1 : input0;
				
				// create a dark beam that's compatible with the non-null input
				BeamCrossSection darkInput = new BeamCrossSection(nonNullInput.getWidth(), nonNullInput.getHeight(), nonNullInput.getPhysicalWidth(), nonNullInput.getPhysicalHeight(), nonNullInput.getWavelength());
				
				// set the null input to the dark beam
				if(input0 == null)
				{
					// input 0 was the null input
					input0 = darkInput;
				}
				else
				{
					// input 1 was the null input
					input1 = darkInput;
				}
			}
		
			// Copy inputs into new outputs, ready to be manipulated
			output0 = new BeamCrossSection(input0);
			output1 = new BeamCrossSection(input1);
			
			// multiply both by reflection coefficient
			output0.multiply(reflectionCoefficient);
			output1.multiply(reflectionCoefficient);
			
			// multiply the transmitted beams by i*transmissionCoefficient...
			BeamCrossSection transmitted0 = new BeamCrossSection(input0);
			BeamCrossSection transmitted1 = new BeamCrossSection(input1);

			transmitted0.multiply(new Complex(0, transmissionCoefficient));
			transmitted1.multiply(new Complex(0, transmissionCoefficient));
			
			// ... and add them to the outputs, which already contain the reflected beams
			output0.add(transmitted1);
			output1.add(transmitted0);
		}
		
		// add outputs to the list
		outputs.add(output0);
		outputs.add(output1);
		
		return outputs;
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		// lossless
		editPanel.add(UIBitsAndBobs.makeRow("Lossless", losslessCheckBox, true));
		
		// reflection coefficient
		editPanel.add(UIBitsAndBobs.makeRow("Reflection coefficient", reflectionCoefficientTextField, true));

		// transmission coefficient
		editPanel.add(UIBitsAndBobs.makeRow("Transmission coefficient", transmissionCoefficientTextField, true));

		/*
		 * Swap inputs and outputs buttons
		 */
		
		// Add an action listener to the buttons
		swapInputsButton.addActionListener(this);
		swapOutputsButton.addActionListener(this);
		
		// Set action commands
		swapInputsButton.setActionCommand("Swap inputs");
		swapOutputsButton.setActionCommand("Swap outputs");

		editPanel.add(UIBitsAndBobs.makeRow(swapInputsButton, swapOutputsButton, true));
		// editPanel.add(swapInputsButton);
		// editPanel.add(swapOutputsButton);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		// lossless
		losslessCheckBox = new JCheckBox();
		losslessCheckBox.setSelected(lossless);

		// reflection coefficient
		reflectionCoefficientTextField = UIBitsAndBobs.makeDoubleFormattedTextField();
		reflectionCoefficientTextField.setValue(new Double(reflectionCoefficient));

		// transmission coefficient
		transmissionCoefficientTextField = UIBitsAndBobs.makeDoubleFormattedTextField();
		transmissionCoefficientTextField.setValue(new Double(transmissionCoefficient));
		
		// add listeners
		losslessCheckBox.addItemListener(this);		
		reflectionCoefficientTextField.addPropertyChangeListener("value", this);
		transmissionCoefficientTextField.addPropertyChangeListener("value", this);
		
		updateWidgets();
		
		// Swap inputs button
		swapInputsButton = new JButton("Swap inputs");
		
		// Swap outputs button
		swapOutputsButton = new JButton("Swap outputs");
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

		if(losslessCheckBox != null) lossless = losslessCheckBox.isSelected();
        if(reflectionCoefficientTextField != null) reflectionCoefficient = ((Number)reflectionCoefficientTextField.getValue()).doubleValue();
        if(transmissionCoefficientTextField != null) transmissionCoefficient = ((Number)transmissionCoefficientTextField.getValue()).doubleValue();
	}
	
	/**
	 * update the widgets in response to changed values
	 */
	private void updateWidgets()
	{
		transmissionCoefficientTextField.setEnabled(!lossless);
	}
	
	private void setTransmissionCoeffient()
	{
		// calculate t^2
		double t2 = 1.0 - reflectionCoefficient*reflectionCoefficient;
		
		// is it <0?
		if(t2 < 0)
		{
			// set transmission coefficient to zero...
			transmissionCoefficientTextField.setValue(0);
			
			// ... and indicate that something is wrong
			transmissionCoefficientTextField.setBackground(Color.RED);
		}
		else
		{
			// t^2 >= 0, so all good

			// set transmission coefficient to sqrt(t^2)...
			transmissionCoefficientTextField.setValue(new Double(Math.sqrt(1.0 - reflectionCoefficient*reflectionCoefficient)));
			
			// ... and indicate that all is good
			transmissionCoefficientTextField.setBackground(UIManager.getColor("TextField.background"));
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == reflectionCoefficientTextField)
	    {
	        reflectionCoefficient = ((Number)reflectionCoefficientTextField.getValue()).doubleValue();
	        if(lossless) setTransmissionCoeffient();
	    }
	    else if (source == transmissionCoefficientTextField)
	    {
	        transmissionCoefficient = ((Number)transmissionCoefficientTextField.getValue()).doubleValue();
	        // if(lossless) reflectionCoefficientTextField.setValue(new Double(Math.sqrt(1.0 - transmissionCoefficient*transmissionCoefficient)));
	    }
		    
		// Fire an edit panel event
		editListener.editMade();
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getSource();
		
		if(source.equals(losslessCheckBox))
		{
			lossless = losslessCheckBox.isSelected();
			updateWidgets();
	        if(lossless) setTransmissionCoeffient();
	        else transmissionCoefficientTextField.setBackground(UIManager.getColor("TextField.background"));
		}
	}
	
	/**
	 * Overrides the default functionality of getComponentImage().
	 * Copied from BeamSplitter class.
	 */
	@Override
	public BufferedImage getComponentImage()
	{		
		// System.out.println("BeamSplitter::getComponentImage: hello!");
		
		// Get default component background image
		BufferedImage backgroundImage = super.getComponentImage();
		
		/*
		 * Create a scaled copy of the image
		 */
		
		// Get the scaled dimensions
		int componentImageHeight = backgroundImage.getHeight();
		int componentImageWidth = backgroundImage.getWidth();
		
		// Create default image of the correct size
		BufferedImage image = new BufferedImage(componentImageWidth, componentImageHeight, BufferedImage.TYPE_INT_RGB);
		
		/*
		 * Draw the 'light wires'
		 */
		
		// Create a graphics object - this allows us to do some drawing
		Graphics2D g = image.createGraphics();
		
		// Draw the background image as the background to our 'wired' image
		g.drawImage(backgroundImage, 0, 0, null);
				
		// Set up 'pen' colour
		g.setColor(Color.red);
		
		// Scale and set the stroke size
		g.setStroke(new BasicStroke((float) scaleFactor * 3.0f));
		
		// Scale the padding
		int scaledBeamWireYPadding = (int) (scaleFactor * (double) beamWireYPadding);
		int scaledBeamWireXPadding = (int) (scaleFactor * (double) beamWireXPadding);
		
		// Create lists of input/output shapes
		inputClickableShapes = new ArrayList<ClickableInputOutputShape>();
		outputClickableShapes = new ArrayList<ClickableInputOutputShape>();
		
		// Add input 'wires'
		for(int a = 0; a < componentInputs.length; a++)
		{
			// Set the y-position of the current beam wire so that it is distributed within the available height equally
			int yPosition = scaledBeamWireYPadding + ((componentImageHeight - 2 * scaledBeamWireYPadding) / (componentInputs.length - 1)) * a;
			
			if(a == opticalTrainInputIndex)
			{
				// System.out.println("opticalTrainInputIndex = a = " + opticalTrainInputIndex);
				
				CubicCurve2D curve = new CubicCurve2D.Double();
				
				// curve.setCurve(0, 75, 50 / 3, 75, 100 / 3, yPosition, 50, yPosition);
				curve.setCurve(0, componentImageHeight / 2, scaledBeamWireXPadding / 3, componentImageHeight / 2, 2 * scaledBeamWireXPadding / 3, yPosition, scaledBeamWireXPadding, yPosition);
				
				g.draw(curve);
			}
			else
			{
				// Draw a circle
				Ellipse2D circle = new Ellipse2D.Double(scaledBeamWireXPadding - 10, yPosition - 5, 10, 10);
				
				inputClickableShapes.add(new ClickableInputOutputShape(circle, a));
				
				g.draw(circle);
			}
		}
		
		// Add output 'wires'
		for(int a = 0; a < componentOutputs.length; a++)
		{
			// Set the y-position of the current beam wire so that it is distributed within the available height equally
			int yPosition = scaledBeamWireYPadding + ((componentImageHeight - 2 * scaledBeamWireYPadding) / (componentInputs.length - 1)) * a;
			
			if(a == opticalTrainOutputIndex)
			{
				// System.out.println("opticalTrainOutputIndex = a = " + opticalTrainOutputIndex);
				
				CubicCurve2D curve = new CubicCurve2D.Double();
				
				// curve.setCurve(0, 75, 50 / 3, 75, 100 / 3, yPosition, 50, yPosition);
				curve.setCurve(componentImageWidth, componentImageHeight / 2, componentImageWidth - scaledBeamWireXPadding / 2, componentImageHeight / 2, componentImageWidth - scaledBeamWireXPadding / 2, yPosition, componentImageWidth - scaledBeamWireXPadding, yPosition);
				
				g.draw(curve);
			}
			else
			{
				// Draw a circle
				Ellipse2D circle = new Ellipse2D.Double(componentImageWidth - scaledBeamWireXPadding, yPosition - 5, 10, 10);
				
				outputClickableShapes.add(new ClickableInputOutputShape(circle, a));
				
				g.draw(circle);
			}
		}
		
		return image;
	}
	
	@Override
	public void dealWithImageMouseEvent(MouseEvent event)
	{
		Point point = event.getPoint();

		// System.out.println("Click at " + point + ", on screen = (" + event.getXOnScreen() + ", " + event.getYOnScreen() + ")");
				
		for(int a = 0; a < componentInputs.length; a++)
		{
			for(ClickableInputOutputShape clickableShape : inputClickableShapes)
			{
				if(clickableShape.getInputOutputNumber() == a)
				{
					Rectangle shape = clickableShape.getShape().getBounds();
					
					// System.out.println("shape for input " + a + ": " + shape);
					
					if(shape.contains(point))
					{
						System.out.println("Setting index of visible input of \"" + getName() + "\" to " + a + ".");
						opticalTrainInputIndex = a;
					}
					
					break;
				}
			}
		}
		
		for(int a = 0; a < componentOutputs.length; a++)
		{
			for(ClickableInputOutputShape clickableShape : outputClickableShapes)
			{
				if(clickableShape.getInputOutputNumber() == a)
				{
					Rectangle shape = clickableShape.getShape().getBounds();
					
					if(shape.contains(point))
					{
						System.out.println("Setting index of visible output of \"" + getName() + "\" to " + a + ".");
						opticalTrainOutputIndex = a;
					}
					
					break;
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		
		if(command.equals("Swap inputs"))
		{
			ComponentOutput input0 = componentInputs[0];
			ComponentOutput input1 = componentInputs[1];
			
			componentInputs[0] = input1;
			componentInputs[1] = input0;
			
			editListener.redraw();
		}
		else if(command.equals("Swap outputs"))
		{
			ComponentInput output0 = componentOutputs[0];
			ComponentInput output1 = componentOutputs[1];
			
			componentOutputs[0] = output1;
			componentOutputs[1] = output0;
			
			editListener.redraw();
		}
	}
}