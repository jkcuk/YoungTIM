package javawaveoptics.ui.workbench;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javawaveoptics.optics.component.AbstractLightSourceComponent;
import javawaveoptics.optics.component.AbstractOpticalComponent;
import javawaveoptics.optics.component.BeamSplitter;
import javawaveoptics.optics.component.Hologram;
import javawaveoptics.optics.component.Plane;
import javawaveoptics.utility.ComponentImageNanny;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Defines a workbench component, representing an optical component in a visual sense
 * on the workbench. This class displays a graphic representative of the optical
 * component, and detects when the graphic has been right clicked, whereby it will
 * show a popup menu allowing editing, removal and insertion.
 * 
 * @author Sean
 */
public class ExtensiveWorkbenchOpticalComponent extends ExtensiveAbstractWorkbenchComponent implements ComponentImageNanny, Serializable, KeyListener
{
	private static final long serialVersionUID = 1L;
	
	private ExtensiveWorkbench extensiveWorkbench;
	private AbstractOpticalComponent opticalComponent;
	
	// the text field for editing the component's name
	protected transient JTextField nameTextBox;
	
	private Border defaultBorder = BorderFactory.createEmptyBorder(3,3,3,3);
	private Border selectedBorder = BorderFactory.createLineBorder(Color.blue, 3);
	
	/**
	 * Constructor. Reads the workbench component's icon from disk, displays it and
	 * sets up the right click popup menu.
	 * 
	 * @param component
	 * @param environment
	 */
	public ExtensiveWorkbenchOpticalComponent(ExtensiveWorkbench extensiveWorkbench, AbstractOpticalComponent opticalComponent)
	{
		super();
		
		setExtensiveWorkbench(extensiveWorkbench);
		setOpticalComponent(opticalComponent);
		
		setBorder(defaultBorder);
		
		// Name edit control
		nameTextBox = new JTextField(7);
		// Set the local key listener, for saving the edited text
		nameTextBox.addKeyListener(this);
		add(nameTextBox, BorderLayout.SOUTH);
		// setText(opticalComponent.getFormattedName());
		setOpticalComponentName(opticalComponent.getName());
		
		updateImage();

		setDefaultToolTipText();
	}

	/**
	 * set the component's tool-tip text to a sensible default message
	 */
	public void setDefaultToolTipText()
	{
		if(opticalComponent instanceof BeamSplitter)
		{
			setToolTipText("<html>Click to view component panel;<br>click on a red circle to show a different input or output optical train;<br>right-click for menu</html>");
		}
		else if(opticalComponent instanceof Hologram)
		{
			setToolTipText("<html>Click to view component panel;<br>click on the blue or red box (when shown) to toggle between<br>the hologram-pattern optical train and the input-beam optical train;<br>right-click for menu</html>");
		}
		else if(opticalComponent instanceof Plane)
		{
			setToolTipText("Click to view beam in this plane; right-click for menu");
		}
		else if(opticalComponent instanceof AbstractLightSourceComponent)
		{
			setToolTipText("Click to view and edit light-source parameters; right-click for menu");
		}
		else
		{
			setToolTipText("Click to view and edit optical-component parameters; right-click for menu");
		}
	}

	/**
	 * Sets the name of the optical component in the name text box.
	 * @param name
	 */
	public void setOpticalComponentName(String name)
	{
		nameTextBox.setText(name);
	}
	
	/**
	 * Sets the icon from the optical component's image.
	 */
	public void updateImage()
	{
		// setIcon(new ImageIcon(opticalComponent.getComponentImage(new File(System.getProperty("user.dir") + File.separator + "Graphics" + File.separator))));
		// setIcon(opticalComponent.getComponentImageIcon());
		BufferedImage componentImage = opticalComponent.getComponentImage();
		
		if(!opticalComponent.isComponentEnabled())
		{
			// the component is disabled; make it semi-translucent and draw a red line through it
			
			makeTranslucent(componentImage, 0.2f);
			
			// Create a graphics object - this allows us to do some drawing
			Graphics2D g = componentImage.createGraphics();

//			// Set up 'pen' colour
//			g.setColor(Color.white);
//			
//			// Scale and set the stroke size
//			g.setStroke(new BasicStroke((float) opticalComponent.getScaleFactor() * 10.0f));
//			g.drawLine(0, newComponentImage.getHeight()/2, newComponentImage.getWidth(), newComponentImage.getHeight()/2);

			// Set up 'pen' colour
			g.setColor(Color.red);
			
			// Scale and set the stroke size
			g.setStroke(new BasicStroke((float) opticalComponent.getScaleFactor() * 6.0f));
			g.drawLine(0, componentImage.getHeight()/2, componentImage.getWidth(), componentImage.getHeight()/2);
			
			setImage(componentImage, true);
		}
		else if(opticalComponent.isCalculating())
		{
			setCalculating();
		}
		else if(opticalComponent.isWarning())
		{
			setWarning(opticalComponent.getWarningMessage());
		}
		else
		{
			setImage(componentImage, true);
		}
	}
	
	public void setSelected(boolean selected)
	{
		if(selected)
		{
			setBorder(selectedBorder);
		}
		else
		{
			setBorder(defaultBorder);
		}
	}
	
	private void setCalculating()
	{
		// setImage(colourImage(opticalComponent.getComponentImage(), new Color(255, 0, 0, 0)));
		
		// the component is calculating; make it semi-translucent and draw a red frame around it
		
		BufferedImage componentImage = opticalComponent.getComponentImage();
		
		double fractionComplete;
		
		if(opticalComponent.isComponentImageIndicatingProgress())
		{
			fractionComplete = opticalComponent.getCalculationFractionComplete();
		}
		else
		{
			fractionComplete = 0.5;
		}

		// colourImage(componentImage, new Color(255, 0, 0, 128));
		reddenImage(componentImage, fractionComplete);

		// makeTranslucent(componentImage, 0.2f);
		
		// Create a graphics object - this allows us to do some drawing
		Graphics2D g = componentImage.createGraphics();

//		// Set up 'pen' colour
//		g.setColor(Color.white);
//		
//		// Scale and set the stroke size
//		g.setStroke(new BasicStroke((float) opticalComponent.getScaleFactor() * 10.0f));
//		g.drawLine(0, newComponentImage.getHeight()/2, newComponentImage.getWidth(), newComponentImage.getHeight()/2);

		// Set up 'pen' colour
		g.setColor(Color.red);
		
		// Scale and set the stroke size
		g.setStroke(new BasicStroke((float) opticalComponent.getScaleFactor() * 4.0f));
		int inset = (int)(opticalComponent.getScaleFactor() * 2.0f + 0.5);
		int w = componentImage.getWidth()-1, h = componentImage.getHeight()-1;
		g.drawLine(inset, inset, w-inset, inset);
		g.drawLine(w-inset, inset, w-inset, h-inset);
		g.drawLine(w-inset, h-inset, inset, h-inset);
		g.drawLine(inset, h-inset, inset, inset);
		
		setImage(componentImage, true);		
	}

	private void setWarning(String warningMessage)
	{
		// setImage(colourImage(opticalComponent.getComponentImage(), new Color(255, 0, 0, 0)));
		
		// the component is calculating; make it semi-translucent and draw a red frame around it
		
		BufferedImage componentImage = opticalComponent.getComponentImage();
		
		// Create a graphics object - this allows us to do some drawing
		Graphics2D g = componentImage.createGraphics();

//		// Set up 'pen' colour
//		g.setColor(Color.white);
//		
//		// Scale and set the stroke size
//		g.setStroke(new BasicStroke((float) opticalComponent.getScaleFactor() * 10.0f));
//		g.drawLine(0, newComponentImage.getHeight()/2, newComponentImage.getWidth(), newComponentImage.getHeight()/2);

		// Set up 'pen' colour
		g.setColor(Color.yellow);
		
		// Scale and set the stroke size
		g.setStroke(new BasicStroke((float) opticalComponent.getScaleFactor() * 6.0f));
		int inset = (int)(opticalComponent.getScaleFactor() * 3.0f + 0.5);
		int w = componentImage.getWidth()-1, h = componentImage.getHeight()-1;
		g.drawLine(inset, inset, w-inset, inset);
		g.drawLine(w-inset, inset, w-inset, h-inset);
		g.drawLine(w-inset, h-inset, inset, h-inset);
		g.drawLine(inset, h-inset, inset, inset);
		
		setImage(componentImage, false);
		
		setToolTipText(warningMessage);
	}
	
	public AbstractOpticalComponent getOpticalComponent()
	{
		return opticalComponent;
	}

	public void setOpticalComponent(AbstractOpticalComponent opticalComponent)
	{
		this.opticalComponent = opticalComponent;
		
		// make sure the optical component notifies this object when it starts and finishes calculating
		opticalComponent.setComponentImageNanny(this);
	}

	
	/**
	 * @return the extensiveWorkbench
	 */
	public ExtensiveWorkbench getExtensiveWorkbench() {
		return extensiveWorkbench;
	}

	/**
	 * @param extensiveWorkbench the extensiveWorkbench to set
	 */
	public void setExtensiveWorkbench(ExtensiveWorkbench extensiveWorkbench) {
		this.extensiveWorkbench = extensiveWorkbench;
	}

	@Override
	public void keyReleased(KeyEvent keyEvent)
	{
		Object source = keyEvent.getSource();
		
		if(source.equals(nameTextBox))
		{			
			try
			{
				opticalComponent.setName(nameTextBox.getText());
				
				// By default, we use a white background on the text box
				nameTextBox.setBackground(Color.white);
			}
			catch(Exception e)
			{
				// Set the background of the text box to red to indicate a problem
				nameTextBox.setBackground(Color.red);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{}
	
	@Override
	public void keyPressed(KeyEvent e)
	{}
}