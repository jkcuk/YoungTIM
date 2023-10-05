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


/**
 * Defines an either-or phase conjugate surface. Each pixel of some light field
 * incident on the surface will undergo phase conjugation if one of the hsb
 * components is over a certain threshold, otherwise it will be left unchanged.
 * 
 * @author adam
 */

public class EitherOrPhaseConjugateSurface extends AbstractSimpleOpticalComponent implements Serializable, ActionListener, KeyListener
{

	private static final long serialVersionUID = 2000911499570619834L;
	
	/*
	 * Fields
	 */
	
	// phaseConjugate bitmap file location
	File phaseConjugateImageFile = null;
	
	/*
	 * GUI variables
	 */

	// phase conjugate file chooser controls	
	private transient JPanel phaseConjugateFilePanel;		
	private transient BitmapFileChooser phaseConjugateFileChooser;		
	private transient JTextField phaseConjugateFileChooserTextField;
	private transient JButton phaseConjugateFileChooserButton;		
	private transient JLabel fileInformationField;
		
	public EitherOrPhaseConjugateSurface(String name) {
				
		super(name);		
	}

	// Null constructor. Creates a PhaseConjugateSurface with default values.
	// Requires no parameters.
	public EitherOrPhaseConjugateSurface()
	{	
		this("Phase-conjugate surface");
	}
	
	@Override
	public String getComponentTypeName() 
	{
		return "Phase-conjugate surface";
	}
	
	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam) {
		
		try
		{
			// Load the image file from disk
			BufferedImage image = ImageIO.read(phaseConjugateImageFile);
			
			// Detect the image dimensions
			int width = inputBeam.getWidth();
			int height = inputBeam.getHeight();

			if((inputBeam.getWidth() != image.getWidth()) || (inputBeam.getHeight() != image.getHeight()))
			{
				// scaledImage = new BufferedImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
				System.err.println("EitherOrPhaseConjugateSurface::fromInputBeamCalculateOutputBeam: wrong image dimensions; leaving beam unchanged");
				return inputBeam;
			}

			// Load data from the image
//			double[] phaseConjugateData = ImageUtilities.loadImageIntoArray(image);
			
			// Calculate the effect the phase conjugation has on each pixel
			for(int i = 0; i < width; i++)
			{
				for(int j = 0; j < height; j++)
				{
					// Fetch the RGB components of the pixel
					Color colour = new Color(image.getRGB(i, j));
					
					// Convert RGB to HSB
					float[] hsb = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
							
					// Phase conjugates for pixels where some hsb parameter is over a threshold
					// and leaves pixels under the threshold unchanged.
					// Then returns the modified light field.
					if(hsb[2] > 0.5) {
						inputBeam.phaseConjugateElement(i,j);
					}
					else {
					}					
				}				
			}
		}
		catch(IOException e)
		{
			// Specified file doesn't exist.
			System.err.println("PhaseConjugateSurface::fromInputBeamCalculateOutputBeam: Specified image file doesn't exist: " + e.getMessage());
		}
		return inputBeam;
	}
	
	
	
	/*
	 * GUI edit controls.
	 */
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Phase conjugate file chooser
		 */
		
		phaseConjugateFilePanel.setBorder(BorderFactory.createTitledBorder("phaseConjugate file"));
		phaseConjugateFilePanel.setLayout(new BoxLayout(phaseConjugateFilePanel, BoxLayout.Y_AXIS));
				
		// Set key listener
		phaseConjugateFileChooserTextField.addKeyListener(this);
		
		// addActionListener throws an error when ActionListener isn't cast (Why??)
		phaseConjugateFileChooserButton.addActionListener(this);
		phaseConjugateFileChooserButton.setActionCommand("Choose phaseConjugate File");
		
		phaseConjugateFilePanel.add(UIBitsAndBobs.makeRow(phaseConjugateFileChooserTextField, phaseConjugateFileChooserButton, true));
		phaseConjugateFilePanel.add(fileInformationField);

		/*
		 * Add all of it to the main layout
		 */
		
		editPanel.add(phaseConjugateFilePanel);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();

		// phaseConjugate file chooser controls
		phaseConjugateFilePanel = new JPanel();
		phaseConjugateFileChooser = new BitmapFileChooser(phaseConjugateImageFile);
		phaseConjugateFileChooserTextField = new JTextField(20);
		phaseConjugateFileChooserButton = new JButton("Browse...");
		
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
		
		if(source.equals(phaseConjugateFileChooserTextField))
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
		 * Validate phaseConjugate image file
		 */
		
		try
		{
			if(!phaseConjugateFileChooserTextField.getText().equals(""))
			{
				setphaseConjugateImageFile(new File(phaseConjugateFileChooserTextField.getText()));
				
				BufferedImage image = ImageIO.read(phaseConjugateImageFile);
				
				int width = image.getWidth();
				int height = image.getHeight();
				
				fileInformationField.setText("Image size: "+Integer.toString(width)+" \u2a09 "+Integer.toString(height)+" pixels.");
				
//				if(MathsUtilities.isPowerOfTwo(width))
//				{
//					phaseConjugateImageWidthTextBox.setBackground(Color.white);
//				}
//				else
//				{
//					phaseConjugateImageWidthTextBox.setBackground(Color.red);
//				}
//				
//				if(MathsUtilities.isPowerOfTwo(height))
//				{
//					phaseConjugateImageHeightTextBox.setBackground(Color.white);
//				}
//				else
//				{
//					phaseConjugateImageHeightTextBox.setBackground(Color.red);
//				}
			}
			
			// The specified file is blank or readable, so set text field to white
			// background.
			// Note: this doesn't necessarily mean it is a valid input - this is
			// specified by the image width/height boxes being red or white background
			phaseConjugateFileChooserTextField.setBackground(Color.white);
		}
		catch(IOException e)
		{
			// Set text field to red background
			phaseConjugateFileChooserTextField.setBackground(Color.red);
			
			fileInformationField.setText("Could not load file.");
		}
		catch(NullPointerException e)
		{
			// This exception is required as it is possible to break the program by
			// typing 'nul' into the file text box...
			
			// Set text field to red background
			phaseConjugateFileChooserTextField.setBackground(Color.red);
			
			fileInformationField.setText("Could not load file.");
		}
	}
	
	@Override
	// Had to add an actionPerformed() supertype method in AbstractOpticalComponent
	// not sure I understand why though
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		
		if(command.equals("Choose phaseConjugate File"))
		{
			// Set the selected file to the currently specified file
			phaseConjugateFileChooser.setSelectedFile(phaseConjugateImageFile);
			
			int returnValue = phaseConjugateFileChooser.showDialog(phaseConjugateFilePanel, "Load");
			
			if(returnValue == JFileChooser.APPROVE_OPTION)
			{
				// Set the file location in the text box
				phaseConjugateFileChooserTextField.setText(phaseConjugateFileChooser.getSelectedFile().getAbsolutePath());
				
				validateImageFile();
			}
		}
	}

	public File getphaseConjugateImageFile()
	{
		return phaseConjugateImageFile;
	}

	public void setphaseConjugateImageFile(File intensityImageFile)
	{
		this.phaseConjugateImageFile = intensityImageFile;
	}
}
