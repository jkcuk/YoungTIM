package javawaveoptics.ui;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.beans.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Creates a preview of an image file, to be used with a BitmapFileChooser.
 * 
 * Credit: http://download.oracle.com/javase/tutorial/uiswing/examples/components/FileChooserDemo2Project/src/components/ImagePreview.java
 * Tutorial: http://download.oracle.com/javase/tutorial/uiswing/components/filechooser.html
 * 
 * Modified by Sean to work with bitmap images (code in link above does not work
 * for .bmp), and modified to make image preview bigger and more informative.
 * 
 * @author Sean
 */
public class BitmapPreview extends JComponent implements Serializable, PropertyChangeListener
{
	private static final long serialVersionUID = -1867147620210425641L;

	ImageIcon thumbnail = null;

	File file = null;
	
	int width = 200;
	int height = 80; // The actual height is +20 more, to account for text below the image preview
	
	String defaultLabelNoImageMessage = "No image selected";
	
	JPanel textPanel = new JPanel();
	JLabel textPanelLabel = new JLabel(defaultLabelNoImageMessage);

	public BitmapPreview(BitmapFileChooser fileChooser)
	{
		setPreferredSize(new Dimension(width, height + 20));
		
		setLayout(new BorderLayout());
		
		textPanel.add(textPanelLabel);
		add(textPanel, BorderLayout.SOUTH);

		fileChooser.addPropertyChangeListener(this);
	}

	public void loadImage()
	{
		if(file == null)
		{
			thumbnail = null;
			textPanelLabel.setText(defaultLabelNoImageMessage);

			return;
		}
		BufferedImage image;
		
		try
		{
			image = ImageIO.read(file);
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();

			return;
		}
		
		if(image != null)
		{
			ImageIcon imageIcon = new ImageIcon(image);
			
			if(imageIcon.getIconWidth() > width - 10)
			{
				thumbnail = new ImageIcon(imageIcon.getImage().getScaledInstance(width - 10, -1, Image.SCALE_DEFAULT));
			}
			else
			{
				// no need to miniaturise

				thumbnail = imageIcon;
			}
			
			// Set text
			textPanelLabel.setText(image.getWidth() + " x " + image.getHeight() + " pixels");
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		boolean update = false;

		String prop = e.getPropertyName();

		if(BitmapFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop))
		{
			// The directory changed, so don't show an image.
			
			file = null;
			update = true;
		}
		else if(BitmapFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop))
		{
			// A file has been selected, find out which one.
			
			file = (File) e.getNewValue();
			update = true;
		}

		// Update the preview accordingly.
		if(update)
		{
			thumbnail = null;
			
			if(isShowing())
			{
				loadImage();
				
				revalidate();
				repaint();
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		if(thumbnail == null)
		{
			loadImage();
		}
		
		if(thumbnail != null)
		{
			int x = width / 2 - thumbnail.getIconWidth() / 2;
			int y = height / 2 - thumbnail.getIconHeight() / 2;

			if(y < 0)
			{
				y = 0;
			}

			if(x < 5)
			{
				x = 5;
			}
			
			thumbnail.paintIcon(this, g, x, y);
		}
	}
}
