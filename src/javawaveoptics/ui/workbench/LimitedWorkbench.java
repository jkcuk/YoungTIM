package javawaveoptics.ui.workbench;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javawaveoptics.optics.component.AbstractOpticalComponent;
import javawaveoptics.optics.component.Plane;
import javawaveoptics.optics.environment.AbstractOpticalEnvironment;
import javawaveoptics.optics.environment.LimitedExampleOpticalEnvironment;

public class LimitedWorkbench extends AbstractWorkbench implements KeyListener
{
	private static final long serialVersionUID = 2546854883184843322L;
	
	LimitedExampleOpticalEnvironment exampleOpticalEnvironment;
	
	JLabel
	titleLabel = new JLabel("*** Circular aperture diffraction simulator ***"),
	editCircularApertureRadiusLabel = new JLabel("Aperture radius"),
	editCircularApertureRadiusUnitsLabel = new JLabel("m"),
	editDistanceLabel = new JLabel(", propagation distance"),
	editDistanceUnitsLabel = new JLabel("m");

	JTextField
	editCircularApertureRadiusTextField = new JTextField(6),
	editDistanceTextField = new JTextField(6);
	
	public LimitedWorkbench(AbstractOpticalEnvironment opticalEnvironment)
	{
		// We don't care about a supplied optical environment
		this();
	}
	
	public LimitedWorkbench()
	{
		super(null);
		
		exampleOpticalEnvironment = new LimitedExampleOpticalEnvironment();
		opticalEnvironment = exampleOpticalEnvironment;
		
		Plane plane = exampleOpticalEnvironment.getPlane();
		
		setLayout(new BorderLayout());
		
		// the title
		JPanel titlePanel = new JPanel();
		titlePanel.add(titleLabel);
		add(titlePanel, BorderLayout.NORTH);

		// the plot
		add(plane.getStandalonePlotTabbedPane(), BorderLayout.NORTH);
		
		JPanel editPanel = new JPanel();
		// TODO: get the layout a bit nicer...
		// editPanel.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		editCircularApertureRadiusTextField.addKeyListener(this);
		editDistanceTextField.addKeyListener(this);
		
		editCircularApertureRadiusTextField.setText(Double.toString(exampleOpticalEnvironment.getCircularAperture().getOuterRadius()));
		editDistanceTextField.setText(Double.toString(exampleOpticalEnvironment.getPropagate().getDistance()));
		
		editPanel.add(editCircularApertureRadiusLabel);
		editPanel.add(editCircularApertureRadiusTextField);
		editPanel.add(editCircularApertureRadiusUnitsLabel);
		editPanel.add(editDistanceLabel);
		editPanel.add(editDistanceTextField);
		editPanel.add(editDistanceUnitsLabel);
		// editPanel.validate();
		
		add(editPanel, BorderLayout.SOUTH);
		// validate();
	}

	@Override
	public AbstractOpticalComponent getStartComponent()
	{
		return opticalEnvironment.getStartComponent();
	}

	@Override
	public void keyPressed(KeyEvent keyEvent)
	{
		
	}

	@Override
	public void keyReleased(KeyEvent keyEvent)
	{
		Object source = keyEvent.getSource();
		
		if(source.equals(editCircularApertureRadiusTextField))
		{
			try
			{
				exampleOpticalEnvironment.getCircularAperture().setOuterRadius(Double.parseDouble(editCircularApertureRadiusTextField.getText()));
				
				// By default, we use a white background on the text box
				editCircularApertureRadiusTextField.setBackground(Color.white);
			}
			catch(NumberFormatException e)
			{
				// Set the background of the text box to red to indicate a problem
				editCircularApertureRadiusTextField.setBackground(Color.red);
			}
		}
		else if(source.equals(editDistanceTextField))
		{
			try
			{
				exampleOpticalEnvironment.getPropagate().setDistance(Double.parseDouble(editDistanceTextField.getText()));
				
				// By default, we use a white background on the text box
				editDistanceTextField.setBackground(Color.white);
			}
			catch(NumberFormatException e)
			{
				// Set the background of the text box to red to indicate a problem
				editDistanceTextField.setBackground(Color.red);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent keyEvent)
	{
		
	}
	
	@Override
	public boolean showLoadAndSaveButtons()
	{
		return false;
	}

	@Override
	public boolean showResearchButtons()
	{
		return true;
	}
}
