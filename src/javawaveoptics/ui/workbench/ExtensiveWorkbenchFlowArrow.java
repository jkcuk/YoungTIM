package javawaveoptics.ui.workbench;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Defines a workbench flow arrow. These are designed to be placed in between workbench optical
 * components, and provide functionality for adding new optical components in its place.
 * 
 * @author Sean
 */
public class ExtensiveWorkbenchFlowArrow extends ExtensiveAbstractWorkbenchComponent implements Serializable
{
	private static final long serialVersionUID = 1538810099770083347L;
	
	// The position this component would take on the optical train if it were an
	// optical component. This is useful to know for adding new components in the
	// right position.
	int opticalTrainPosition;
	
	BufferedImage flowArrowImage;
	
	// private Border defaultBorder = BorderFactory.createEmptyBorder(3,3,3,3);
	private Border defaultBorder = BorderFactory.createEmptyBorder(3,0,3,0);
	
	public ExtensiveWorkbenchFlowArrow(int opticalTrainPosition)
	{
		super();
		
		this.opticalTrainPosition = opticalTrainPosition;
		
		int componentImageWidth = (int)(COMPONENT_IMAGE_HEIGHT * 25./150.);	// default width
			
		// Load flow arrow graphic
		// for some reason, .bmp files don't load here, but .png files do
		java.net.URL imgURL = getClass().getResource("FlowArrow.png");
	    if (imgURL != null) {
	        // setIcon(new ImageIcon(imgURL));
	    	try {
	    		BufferedImage image = ImageIO.read(imgURL);
	    		
	    		// create a scaled copy of the image
	    		double scaleFactor = (double)ExtensiveAbstractWorkbenchComponent.COMPONENT_IMAGE_HEIGHT / (double)image.getHeight();
	    		componentImageWidth = (int)((double)image.getWidth() * scaleFactor);
	    		
//	    		BufferedImage scaledImage = new BufferedImage(componentImageWidth, ExtensiveAbstractWorkbenchComponent.COMPONENT_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
//	    		AffineTransform at = new AffineTransform();
//	    		at.scale(scaleFactor, scaleFactor);
//	    		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//	    		scaledImage = scaleOp.filter(image, scaledImage);
	    		
	    		// Create new (blank) image of required (scaled) size
	    		BufferedImage scaledImage = new BufferedImage(componentImageWidth, ExtensiveAbstractWorkbenchComponent.COMPONENT_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

	    		// Paint scaled version of image to new image
	    		Graphics2D graphics2D = scaledImage.createGraphics();
	    		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    		graphics2D.drawImage(image, 0, 0, componentImageWidth, ExtensiveAbstractWorkbenchComponent.COMPONENT_IMAGE_HEIGHT, null);

	    		// clean up
	    		graphics2D.dispose();

	    		setImage(scaledImage, true);
			} catch (IOException e) {
		        System.err.println("Couldn't load file FlowArrow.png");
		        setImage(new BufferedImage(componentImageWidth, COMPONENT_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB), true);
			}
	    } else {
	        System.err.println("Couldn't find file FlowArrow.png");
	        setImage(new BufferedImage(componentImageWidth, COMPONENT_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB), true);
	    }

//		File file = new File(System.getProperty("user.dir") + File.separator + "Graphics" + File.separator + "FlowArrow.bmp");
//		
//		try
//		{
//			flowArrowImage = ImageIO.read(file);
//		}
//		catch (IOException e)
//		{
//			System.out.println("Failed to load graphic for flow arrow. Using default instead.");
//			
//			flowArrowImage = new BufferedImage(25, 150, BufferedImage.TYPE_INT_RGB);
//		}
		
		setBorder(defaultBorder);
//		setIcon(new ImageIcon(flowArrowImage));
		
		setToolTipText("Right-click for Insert... menu");
	}

	public int getOpticalTrainPosition()
	{
		return opticalTrainPosition;
	}

	public void setOpticalTrainPosition(int opticalTrainPosition)
	{
		this.opticalTrainPosition = opticalTrainPosition;
	}
}
