package javawaveoptics.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * @author Sean, Johannes
 * Simply displays an image at its original size.
 * Used by the ExtensiveAbstractWorkbenchComponent class to display the icon corresponding to a component.
 */
public class ImagePanelSuperSimple extends JPanel
{
	private static final long serialVersionUID = -4173316784234944097L;

	private BufferedImage image;

    public ImagePanelSuperSimple(BufferedImage image)
    {
    	super();
    	
    	setImage(image);
    }
    
    public void setImage(BufferedImage image)
    {
    	boolean isInitialisation = (this.image == null);
    	
		this.image = image;

		if(image != null)
    	{
    		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    	}
		
		if(!isInitialisation) repaint();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
    	super.paintComponent(g);
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
    }
}