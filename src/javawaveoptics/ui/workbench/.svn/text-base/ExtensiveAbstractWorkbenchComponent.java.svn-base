package javawaveoptics.ui.workbench;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.JPanel;

import javawaveoptics.ui.ImagePanelSuperSimple;

/**
 * Defines a generic workbench component, consisting of a label and corresponding graphic.
 * This class must be subclassed by a relevant implementation of a workbench component.
 * 
 * @author Johannes
 */
public abstract class ExtensiveAbstractWorkbenchComponent extends JPanel implements Serializable
{
	private static final long serialVersionUID = 5516595939141373111L;
	
	// the height of the component image on the screen
	public static final int COMPONENT_IMAGE_HEIGHT = 100;
	
	protected ImagePanelSuperSimple componentImagePanel;
	
	public ExtensiveAbstractWorkbenchComponent()
	{
		super(new BorderLayout());
	}
	
	public BufferedImage makeTranslucent(BufferedImage source, float alpha)
	{
	   Graphics2D g2d = source.createGraphics();
	   g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, alpha));
	   g2d.drawImage(source,0,0,null);
	   g2d.dispose();
	   return source;
	}
	
	/**
	 * from http://stackoverflow.com/questions/4248104/applying-a-tint-to-an-image-in-java
	 * @param source
	 * @param newColor	 alpha needs to be zero, e.g. new Color(red, green, blue, 0)
	 * @return
	 */
	public BufferedImage colourImage(BufferedImage source, Color newColour)
	{
	    // BufferedImage img = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TRANSLUCENT);
	    Graphics2D graphics = source.createGraphics();
	    graphics.setXORMode(newColour);
	    graphics.drawImage(source, null, 0, 0);
	    graphics.dispose();
	    return source;
	}
	
	public static void reddenImage(BufferedImage source, double fractionCompleted)
	{
		// the coordinates of the centre of the "pulse"
		double
			xCentre = source.getWidth() * (2*fractionCompleted-0.5),
			yCentre = source.getHeight() / 2,
			w02 = source.getHeight()*source.getHeight()/8;
		// int xMin = Math.max((int)xCentre - source.getWidth()/2, 0);
		// int xMax = Math.min((int)xCentre + source.getWidth()/2, source.getWidth());
//		int xMin, xMax;
//		if(fractionCompleted < 0.5)
//		{
//			xMin = 0;
//			xMax = Math.min((int)(3+(source.getWidth()-3) * fractionCompleted / 0.5 + 0.5), source.getWidth());
//		}
//		else
//		{
//			xMin = Math.min((int)((source.getWidth()-3) * (fractionCompleted - 0.5) / 0.5 + 0.5), source.getWidth());
//			xMax = source.getWidth();
//		}
	    for(int x=0; x < source.getWidth(); x++) // (int x = xMin; x < xMax; x++)
	    {
	    	// calculate the distance from the centre of the "pulse"
	    	double dx = x-xCentre;
	    	// double c = Math.cos(2*Math.PI*dx/source.getWidth());
	    	// double gb = 0.5-0.5*c;
	        for (int y = 0; y < source.getHeight(); y++)
	        {
	        	double
	        		dy = y-yCentre,
	        		dr2 = dx*dx + dy*dy,
	        		gb = 1-Math.exp(-dr2 / w02);
	            Color colour = new Color(source.getRGB(x, y));
	            Color newColour = new Color(colour.getRed(), (int)(colour.getGreen()*gb), (int)(colour.getBlue()*gb));

	            source.setRGB(x, y, newColour.getRGB());
	        }
	    }
	}

	public void setImage(BufferedImage image, boolean setStandardToolTipText)
	{
		if(componentImagePanel == null)
		{
			componentImagePanel = new ImagePanelSuperSimple(image);
			add(componentImagePanel, BorderLayout.CENTER);
		}
		else
		{
			componentImagePanel.setImage(image);
		}
		
		setMaximumSize(getPreferredSize());
		revalidate();
		
		if(setStandardToolTipText && (this instanceof ExtensiveWorkbenchOpticalComponent))
		{
			((ExtensiveWorkbenchOpticalComponent)this).setDefaultToolTipText();
		}
	}
	
	public ImagePanelSuperSimple getComponentImagePanel() {
		return componentImagePanel;
	}

	public void setComponentImagePanel(ImagePanelSuperSimple componentImagePanel) {
		this.componentImagePanel = componentImagePanel;
	}
}
