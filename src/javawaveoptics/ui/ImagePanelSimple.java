package javawaveoptics.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.JPanel;

/**
 * Creates a Swing component displaying an image of specified width and height, scaling the image if
 * necessary.
 * 
 * @author Sean
 */
public class ImagePanelSimple extends JPanel implements Serializable
{
	private static final long serialVersionUID = 1938601871389911591L;
	
	// restrict the image width and height to the values below in order to avoid memory trouble
	private static final int
		IMAGE_WIDTH_MAX = 10000,
		IMAGE_HEIGHT_MAX = 10000;
	
	// Image object
	private BufferedImage sourceImage;
	
	// Scaled instance of image
	private Image scaledImage;
	
	// Dimensions
	private int imageWidth, imageHeight,
		imageWidthOld = -1,
		imageHeightOld = -1;
	private double zoomFactorX, zoomFactorY;
	
	private boolean needsRedrawing = true;
	
	private ImageSizeProblemListener imageSizeProblemListener;
	
	/**
	 * @param image
	 * @param zoomFactorX
	 * @param zoomFactorY
	 */
	public ImagePanelSimple(BufferedImage image, double zoomFactorX, double zoomFactorY, ImageSizeProblemListener imageSizeProblemListener)
	{
		this.zoomFactorX = zoomFactorX;
		this.zoomFactorY = zoomFactorY;
		this.imageSizeProblemListener = imageSizeProblemListener;
		
        setBackground(Color.lightGray);

		setImageAndRepaint(image);
	}
	
	public ImagePanelSimple()
	{
		this(null, 1.0, 1.0, null);
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		
		// System.out.println("ImagePanelSimple::paint: needsRedrawing = " + needsRedrawing + ", imageWidth = " + imageWidth + ", imageHeight = " + imageHeight);
		
		if(sourceImage == null)
		{
			// g.fillRect(0, 0, imageWidth, imageHeight);
			g.drawString("\u2014 no data \u2014", 10, 10);			
			needsRedrawing = false;
		}
		else
		{			
			if((scaledImage == null) || needsRedrawing)
			{
				// Image to be displayed hasn't been created yet or needs redrawing - create it!
				
				// System.out.println("ImagePanelSimple::paint: (re)drawing...");				
				
				// Scale image
				scaledImage = sourceImage.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
			}
			
	        int w = getWidth();
	        int h = getHeight();
	        int x = (int)((w - imageWidth)/2);
	        int y = (int)((h - imageHeight)/2);

	        try
	        {
	        	g.drawImage(scaledImage, x, y, null);
	        	if(imageSizeProblemListener != null) imageSizeProblemListener.dealWithImageSizeProblem(true);	    		
	    		needsRedrawing = false;
	        }
	        catch(java.lang.OutOfMemoryError e)
	        {
	        	System.err.println("Image too large, causing memory problems.  Drawing abandoned.  Try reducing the zoom factor.");
	        	if(imageSizeProblemListener != null) imageSizeProblemListener.dealWithImageSizeProblem(false);
	        	// e.printStackTrace();
	        }
		}
	}

	public BufferedImage getImage()
	{
		return sourceImage;
	}
	
	private void calculateImageWidthAndHeight()
	{
		// System.out.println("zoomFactorX = "+ zoomFactorX + ", zoomFactorY = "+ zoomFactorY);

		int requestedImageWidth, requestedImageHeight;
		boolean tooWide, tooTall;
		
		if(sourceImage != null)
		{
			requestedImageWidth = (int)(sourceImage.getWidth() * zoomFactorX);
			if(requestedImageWidth < 1) requestedImageWidth = 1;
			
			requestedImageHeight = (int)(sourceImage.getHeight() * zoomFactorY);
			if(requestedImageHeight < 1) requestedImageHeight = 1;
		}
		else
		{
			requestedImageWidth = 16;
			requestedImageHeight = 16;
		}
		
		// check if the size is within the allowed boundaries
		// first the width
		if(requestedImageWidth <= IMAGE_WIDTH_MAX)
		{
			tooWide = false;
			imageWidth = requestedImageWidth;
		}
		else
		{
			tooWide = true;
			imageWidth = IMAGE_WIDTH_MAX;
		}
		
		// then the height
		if(requestedImageHeight <= IMAGE_WIDTH_MAX)
		{
			tooTall = false;
			imageHeight = requestedImageHeight;
		}
		else
		{
			tooTall = true;
			imageHeight = IMAGE_HEIGHT_MAX;
		}
		
		if((imageWidth != imageWidthOld) || (imageHeight != imageHeightOld))
		{
			imageWidthOld = imageWidth;
			imageHeightOld = imageHeight;
			
			setPreferredSize(new Dimension(imageWidth, imageHeight));
			revalidate();
		}

		// let the colling methods know in case there was a problem
		if(tooWide)
		{
			System.out.println("Requested image width (" + requestedImageWidth + ") exceeds maximum allowed image width (" + IMAGE_WIDTH_MAX + ").");
			// throw new ImageSizeException("Requested image width (" + requestedImageWidth + ") exceeds maximum allowed image width (" + IMAGE_WIDTH_MAX + ").");
		}
		
		if(tooTall)
		{
			System.out.println("Requested image height (" + requestedImageHeight + ") exceeds maximum allowed image height (" + IMAGE_HEIGHT_MAX + ").");
			// throw new ImageSizeException("Requested image height (" + requestedImageHeight + ") exceeds maximum allowed image height (" + IMAGE_HEIGHT_MAX + ").");
		}
	}
	
	public void setImageAndRepaint(BufferedImage image)
	{
		sourceImage = image;
		needsRedrawing = true;
		
		calculateImageWidthAndHeight();
		
		repaint();
	}

	public double getZoomFactorX() {
		return zoomFactorX;
	}

	public double getZoomFactorY() {
		return zoomFactorY;
	}
	
	/**
	 * set zoom factors without redrawing
	 * @param zoomFactorX
	 * @param zoomFactorY
	 */
	public void setZoomFactors(double zoomFactorX, double zoomFactorY)
	{
		// System.out.println("ImagePanelSimple::setZoomFactors: zoomFactorX = " + zoomFactorX + ", zoomFactorY = " + zoomFactorY);
		this.zoomFactorX = zoomFactorX;
		this.zoomFactorY = zoomFactorY;

		needsRedrawing = true;
		calculateImageWidthAndHeight();
	}

	public void setZoomFactorsAndRepaint(double zoomFactorX, double zoomFactorY)
	{
		setZoomFactors(zoomFactorX, zoomFactorY);
		revalidate();
		repaint();
	}

	public ImageSizeProblemListener getImageSizeProblemListener() {
		return imageSizeProblemListener;
	}

	public void setImageSizeProblemListener(
			ImageSizeProblemListener imageSizeProblemListener) {
		this.imageSizeProblemListener = imageSizeProblemListener;
	}
}
