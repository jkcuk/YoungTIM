package javawaveoptics.optics.component;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.BitmapFileChooser;
import javawaveoptics.ui.UIBitsAndBobs;
import javawaveoptics.utility.ImageUtilities;
import javawaveoptics.utility.SimulationException;
import library.maths.Complex;

/**
 * Defines a hologram component. A light beam incident on this component will be altered using the
 * intensity and phase patterns defined in bitmap files provided to this component using the edit panel.
 * 
 * @author Sean
 */
public class HologramFromBitmap extends AbstractSimpleOpticalComponent implements Serializable, ActionListener, KeyListener
{
	private static final long serialVersionUID = 3830684919640717082L;
	
	/*
	 * Fields
	 */

	// Hologram bitmap file location
	File hologramImageBitmap = null;
	
	/*
	 * GUI edit controls
	 */
	
	// Hologram file chooser controls	
	private transient JPanel hologramFilePanel;
	
	private transient BitmapFileChooser hologramFileChooser;
	
	private transient JTextField hologramFileChooserTextField;
	private transient JButton hologramFileChooserButton;
	
	private transient JLabel fileInformationField;
	
	public HologramFromBitmap(String name)
	{
		super(name);
	}
	
	public HologramFromBitmap()
	{
		this("Hologram (from bitmap)");
	}

	@Override
	public String getComponentTypeName()
	{
		return "Hologram (from bitmap)";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	throws SimulationException
	{
		try
		{
			// Load the image from disk
			BufferedImage image = ImageIO.read(hologramImageBitmap);
			
			// Detect the image dimensions
			int width = image.getWidth();
			int height = image.getHeight();
			
			// Check width and height are powers of two
			if(width != inputBeam.getWidth())
			{
				throw new SimulationException("HologramFromBitmap::fromInputBeamCalculateOutputBeam", "The specified hologram's width ("+width+") does not match the input beam's width ("+inputBeam.getWidth()+").");
			}
			
			if(height != inputBeam.getHeight())
			{
				throw new SimulationException("HologramFromBitmap::fromInputBeamCalculateOutputBeam", "The specified hologram's height ("+height+") does not match the input beam's height ("+inputBeam.getHeight()+").");
			}
			
			// Load data from the image
			double[] hologramData = ImageUtilities.image2HologramTypeArray(image);
			
			// Calculate the effect the hologram has on each pixel
			for(int i = 0; i < width; i++)
			{
				for(int j = 0; j < height; j++)
				{
					inputBeam.multiplyElement(i, j, new Complex(hologramData[2 * (width * j + i)], hologramData[2 * (width * j + i) + 1]));
				}
			}
		}
		catch(IOException e)
		{
			// Specified file doesn't exist.
			throw new SimulationException("HologramFromBitmap::fromInputBeamCalculateOutputBeam", "Specified image file doesn't exist: " + e.getMessage());
			// System.err.println("HologramFromBitmap::fromInputBeamCalculateOutputBeam: Specified image file doesn't exist: " + e.getMessage());
		}
		catch(IllegalArgumentException e)
		{
			// File or image is null OR the width or height is not the same as the input beam
			// (NumberFormatException is caught by IllegalArgumentException)
			throw new SimulationException("HologramFromBitmap::fromInputBeamCalculateOutputBeam", "Specified file is invalid: " + e.getMessage());
			// System.err.println("HologramFromBitmap::fromInputBeamCalculateOutputBeam: Specified file is invalid: " + e.getMessage());
		}
		
		return inputBeam;
	}
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Hologram file chooser
		 */
		
		hologramFilePanel.setBorder(BorderFactory.createTitledBorder("Hologram file"));
		hologramFilePanel.setLayout(new BoxLayout(hologramFilePanel, BoxLayout.Y_AXIS));
				
		// Set key listener
		hologramFileChooserTextField.addKeyListener(this);
		
		hologramFileChooserButton.addActionListener(this);
		hologramFileChooserButton.setActionCommand("Choose hologram file");
		
		hologramFilePanel.add(UIBitsAndBobs.makeRow(hologramFileChooserTextField, hologramFileChooserButton, true));
		hologramFilePanel.add(fileInformationField);

		/*
		 * Add all of it to the main layout
		 */
		
		editPanel.add(hologramFilePanel);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		// Hologram file chooser controls
		hologramFilePanel = new JPanel();
		hologramFileChooser = new BitmapFileChooser(hologramImageBitmap);
		hologramFileChooserTextField = new JTextField(20);
		hologramFileChooserButton = new JButton("Browse...");
		
		fileInformationField = new JLabel("No file selected.");
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		
	}

	@Override
	public void keyReleased(KeyEvent keyEvent)
	{
		Object source = keyEvent.getSource();
		
		if(source.equals(hologramFileChooserTextField))
		{
			// Validate the image file
			validateImageFile();
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		
	}

	protected void validateImageFile()
	{		
		/*
		 * Validate hologram image file
		 */
		
		try
		{
			if(!hologramFileChooserTextField.getText().equals(""))
			{
				setHologramImageFile(new File(hologramFileChooserTextField.getText()));
				
				BufferedImage image = ImageIO.read(hologramImageBitmap);
				
				int width = image.getWidth();
				int height = image.getHeight();
				
				fileInformationField.setText("Image size: "+Integer.toString(width)+" \u2a09 "+Integer.toString(height)+" pixels.");
				
//				if(MathsUtilities.isPowerOfTwo(width))
//				{
//					hologramImageWidthTextBox.setBackground(Color.white);
//				}
//				else
//				{
//					hologramImageWidthTextBox.setBackground(Color.red);
//				}
//				
//				if(MathsUtilities.isPowerOfTwo(height))
//				{
//					hologramImageHeightTextBox.setBackground(Color.white);
//				}
//				else
//				{
//					hologramImageHeightTextBox.setBackground(Color.red);
//				}
			}
			
			// The specified file is blank or readable, so set text field to white
			// background.
			// Note: this doesn't necessarily mean it is a valid input - this is
			// specified by the image width/height boxes being red or white background
			hologramFileChooserTextField.setBackground(Color.white);
		}
		catch(IOException e)
		{
			// Set text field to red background
			hologramFileChooserTextField.setBackground(Color.red);
			
			fileInformationField.setText("Could not load file (IO Exception: " + e.getMessage() + ").");
		}
		catch(NullPointerException e)
		{
			// This exception is required as it is possible to break the program by
			// typing 'nul' into the file text box...
			
			// Set text field to red background
			hologramFileChooserTextField.setBackground(Color.red);
			
			fileInformationField.setText("Could not load file.");
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		
		if(command.equals("Choose hologram file"))
		{
			// Set the selected file to the currently specified file
			hologramFileChooser.setSelectedFile(hologramImageBitmap);
			
			int returnValue = hologramFileChooser.showDialog(hologramFilePanel, "Load");
			
			if(returnValue == JFileChooser.APPROVE_OPTION)
			{
				// Set the file location in the text box
				hologramFileChooserTextField.setText(hologramFileChooser.getSelectedFile().getAbsolutePath());
				
				validateImageFile();
			}
		}
	}

	public File getHologramImageFile()
	{
		return hologramImageBitmap;
	}

	public void setHologramImageFile(File intensityImageFile)
	{
		this.hologramImageBitmap = intensityImageFile;
	}
}
