package javawaveoptics.optics.lightsource;

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
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.BitmapFileChooser;
import javawaveoptics.utility.ImageUtilities;
import javawaveoptics.utility.MathsUtilities;

/**
 * Defines a bitmap light source, which is a light source derived from a bitmap file. The chosen bitmap
 * file is used to create a corresponding light beam.
 * 
 * @author Sean
 */
public class BeamFromBitmap extends AbstractLightSource implements Serializable, ActionListener, KeyListener
{
	private static final long serialVersionUID = -8699743570986098905L;

	/*
	 * Fields
	 */
	
	protected String name = "Beam profile from bitmap";
	
	// Bitmap file location
	private File imageFile = null;
	
	/*
	 * GUI edit controls
	 */
	
	// Panel to hold file chooser
	private transient JPanel fileChooserPanel;
	
	// Bitmap location chooser
	private transient BitmapFileChooser fileChooser;
	private transient JTextField fileChooserTextField;
	private transient JButton fileChooserButton;
	
	// Image width edit control (disabled)
	private transient JTextField imageWidthTextBox;
	private transient JLabel imageWidthLabel;
	private transient JLabel imageWidthUnitsLabel;
	
	// Image height edit control (disabled)
	private transient JTextField imageHeightTextBox;
	private transient JLabel imageHeightLabel;
	private transient JLabel imageHeightUnitsLabel;
	
	/*
	 * GUI edit controls
	 */
	
	public BeamFromBitmap()
	{
		super("Beam profile from bitmap");
	}
	
	@Override
	public String getLightSourceTypeName()
	{
		return "Beam profile from bitmap";
	}
	
	public BeamCrossSection getBeamOutput(double physicalWidth, double physicalHeight, double wavelength, int plotWidth, int plotHeight)
	{
		BeamCrossSection beam = new BeamCrossSection(plotWidth, plotHeight, physicalWidth, physicalHeight, wavelength);
		
		try
		{
			// Load the image from disk
			BufferedImage image = ImageIO.read(imageFile);
			
			// Detect the image dimensions
			int width = image.getWidth();
			int height = image.getHeight();
			
			// Check width and height are powers of two
			if(!MathsUtilities.isPowerOfTwo(width))
			{
				throw new NumberFormatException("[Bitmap Loader] The specified image's width is not a power of two.");
			}
			
			if(!MathsUtilities.isPowerOfTwo(height))
			{
				throw new NumberFormatException("[Bitmap Loader] The specified image's height is not a power of two.");
			}
			
			// Load data from the image
			double[] data = ImageUtilities.image2BeamTypeArray(image);
			
			// Set the beam's data
			beam.setData(data);
			
			return beam;
		}
		catch(IOException e)
		{
			// Specified file doesn't exist.
			System.err.println("[Bitmap Loader] Specified image file doesn't exist: " + e.getMessage());
		}
		catch(IllegalArgumentException e)
		{
			// File or image is null OR the width or height is not a power of two.
			// (NumberFormatException is caught by IllegalArgumentException)
			System.err.println("[Bitmap Loader] Specified file is invalid: " + e.getMessage());
		}
		
		return null;
	}
	
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		// Panel to hold file chooser
		fileChooserPanel = new JPanel();
		
		// Bitmap location chooser
		fileChooser = new BitmapFileChooser("Load .bmp", true);
		fileChooserTextField = new JTextField(20);
		fileChooserButton = new JButton("Browse...");
		
		// Image width edit control (disabled)
		imageWidthTextBox = new JTextField(3);
		imageWidthLabel = new JLabel("Image Width");
		imageWidthUnitsLabel = new JLabel("cells");
		
		// Image height edit control (disabled)
		imageHeightTextBox = new JTextField(3);
		imageHeightLabel = new JLabel("Image Height");
		imageHeightUnitsLabel = new JLabel("cells");
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

		// TODO nothing to do here, I think
	}
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * File chooser
		 */
		
		// File chooser layout manager
		GroupLayout fileChooserPanelLayout = new GroupLayout(fileChooserPanel);
		ParallelGroup fileChooserPanelHorizontalGroup = fileChooserPanelLayout.createParallelGroup();
		SequentialGroup fileChooserPanelVerticalGroup = fileChooserPanelLayout.createSequentialGroup();
		fileChooserPanelLayout.setHorizontalGroup(fileChooserPanelHorizontalGroup);
		fileChooserPanelLayout.setVerticalGroup(fileChooserPanelVerticalGroup);
		fileChooserPanelLayout.setAutoCreateGaps(true);
		
		// Set layout
		fileChooserPanel.setLayout(fileChooserPanelLayout);
		
		// Set text field key listener
		fileChooserTextField.addKeyListener(this);
		
		// Set file chooser action listener
		fileChooserButton.addActionListener(this);
		fileChooserButton.setActionCommand("Choose File");

		// Set file chooser panel border
		fileChooserPanel.setBorder(BorderFactory.createTitledBorder("File"));
		
		// Add file chooser to layout groups
		fileChooserPanelHorizontalGroup.addGroup(
				fileChooserPanelLayout.createSequentialGroup()
				.addComponent(fileChooserTextField)
				.addComponent(fileChooserButton)
			);
			
		fileChooserPanelVerticalGroup.addGroup(
				fileChooserPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(fileChooserTextField)
				.addComponent(fileChooserButton)
		);
		
		/*
		 * Image width text box
		 */
		
		// Disable text box
		imageWidthTextBox.setEnabled(false);
		
		// Add to layout groups
		fileChooserPanelHorizontalGroup.addGroup(
				fileChooserPanelLayout.createSequentialGroup()
				.addComponent(imageWidthLabel)
				.addComponent(imageWidthTextBox)
				.addComponent(imageWidthUnitsLabel)
			);
			
		fileChooserPanelVerticalGroup.addGroup(
				fileChooserPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(imageWidthLabel)
				.addComponent(imageWidthTextBox)
				.addComponent(imageWidthUnitsLabel)
		);
		
		/*
		 * Image height text box
		 */
		
		// Disable text box
		imageHeightTextBox.setEnabled(false);
		
		// Add to layout groups
		fileChooserPanelHorizontalGroup.addGroup(
				fileChooserPanelLayout.createSequentialGroup()
				.addComponent(imageHeightLabel)
				.addComponent(imageHeightTextBox)
				.addComponent(imageHeightUnitsLabel)
			);
			
		fileChooserPanelVerticalGroup.addGroup(
				fileChooserPanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(imageHeightLabel)
				.addComponent(imageHeightTextBox)
				.addComponent(imageHeightUnitsLabel)
		);
		
		/*
		 * Add the whole lot to the main layout
		 */
		
		editPanel.add(fileChooserPanel);
	}
	
	public void validateImageFile()
	{		
		try
		{
			if(!fileChooserTextField.getText().equals(""))
			{
				setImageFile(new File(fileChooserTextField.getText()));
				
				BufferedImage image = ImageIO.read(imageFile);
				
				int width = image.getWidth();
				int height = image.getHeight();
				
				imageWidthTextBox.setText(Integer.toString(width));
				imageHeightTextBox.setText(Integer.toString(height));
				
				if(MathsUtilities.isPowerOfTwo(width))
				{
					imageWidthTextBox.setBackground(Color.white);
				}
				else
				{
					imageWidthTextBox.setBackground(Color.red);
				}
				
				if(MathsUtilities.isPowerOfTwo(height))
				{
					imageHeightTextBox.setBackground(Color.white);
				}
				else
				{
					imageHeightTextBox.setBackground(Color.red);
				}
			}
			
			// The specified file is blank or readable, so set text field to white
			// background.
			// Note: this doesn't necessarily mean it is a valid input - this is
			// specified by the image width/height boxes being red or white background
			fileChooserTextField.setBackground(Color.white);
		}
		catch(IOException e)
		{
			// Set text field to red background
			fileChooserTextField.setBackground(Color.red);
			
			// Reset width and height text boxes
			imageWidthTextBox.setText("");
			imageHeightTextBox.setText("");
			imageWidthTextBox.setBackground(Color.white);
			imageHeightTextBox.setBackground(Color.white);
		}
		catch(NullPointerException e)
		{
			// This exception is required as it is possible to break the program by
			// typing 'nul' into the file text box...
			
			// Set text field to red background
			fileChooserTextField.setBackground(Color.red);
			
			// Reset width and height text boxes
			imageWidthTextBox.setText("");
			imageHeightTextBox.setText("");
			imageWidthTextBox.setBackground(Color.white);
			imageHeightTextBox.setBackground(Color.white);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		
		if(command.equals("Choose File"))
		{
			// Set the selected file to the currently specified file
			fileChooser.setSelectedFile(imageFile);
			
			int returnValue = fileChooser.showDialog(editPanel, "Load");
			
			if(returnValue == JFileChooser.APPROVE_OPTION)
			{
				// Set the file location in the text box
				fileChooserTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
				
				// Validate the selected image file
				validateImageFile();
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		
	}

	@Override
	public void keyReleased(KeyEvent keyEvent)
	{
		Object source = keyEvent.getSource();
		
		if(source.equals(fileChooserTextField))
		{
			// Validate the image file
			validateImageFile();
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		
	}

	public File getImageFile()
	{
		return imageFile;
	}

	public void setImageFile(File imageFile)
	{
		this.imageFile = imageFile;
	}
}