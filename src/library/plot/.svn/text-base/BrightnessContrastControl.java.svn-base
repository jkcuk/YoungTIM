package library.plot;

import java.awt.*;
import java.awt.event.*;

class BrightnessContrastControl extends Canvas
implements MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 1588877219502444158L;
	
	/* The two variables brightness and contrast define a curve, called here the
	   intensity - plot value curve, which specifies the relative intensity with
	   which a given relative plot value is shown.  Both the relative intensity
	   and the relative plot value can take on values between 0 and 1.
	   
	   The intensity - plot value relation is given by the expression
	   
	     intensity = brightness + contrast * (plotValue - 1/2),
	     
	   i.e. at relative plot value 1/2 the intensity is given by brightness,
	   and the intensity difference between the brightnest and darkest point in the
	   plot is given by contrast.
	*/
	private double brightness, contrast;
	// coordinates of intersections of intensity - plot value curve with
	// square (0,1) x (0,1)
	private double v1, i1, v2, i2;
	private int x1, y1, x2, y2; // screen coordinates of above intersections
	private int mouseNear = 0;
	private int selected = 0;
	private final int CATCH_RADIUS = 3;
	private BrightnessContrastControlListener bccListener;
	
	public BrightnessContrastControl(int width, int height)
	{
		super();
		
		super.setSize(width, height);
		
		// setBackground(Color.orange);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void addBrightnessContrastControlListener
	(BrightnessContrastControlListener bccListener)
	{
		this.bccListener = bccListener;

		// inform listener of current settings
		if(bccListener != null)
			bccListener.brightnessContrastChanged(brightness, contrast);
	}
	
	public void setBrightness(double brightness)
	{
		this.brightness = brightness;

		// inform listener
		if(bccListener != null)
			bccListener.brightnessContrastChanged(brightness, contrast);
	}
	
	public void setContrast(double contrast)
	{
		this.contrast = contrast;

		// inform listener
		if(bccListener != null)
			bccListener.brightnessContrastChanged(brightness, contrast);
	}
	
	public double getBrightness()
	{
		return brightness;
		// return y2intensity(y1) - getContrast() * (x2plotValue(x1)-0.5);
	}
	
	public double getContrast()
	{
		return contrast;
		// return (y2intensity(y1)-y2intensity(y2)) / (x2plotValue(x1)-x2plotValue(x2));
	}
	
	// calculates the y coordinate (used for plotting, ranging between 0 and
	// getSize().height-1) as a function of the relative intensity, which ranges
	// between 0 and 1
	private int intensity2y(double intensity)
	{
		return CATCH_RADIUS + (int)((1-intensity) * (getSize().height-1-2*CATCH_RADIUS)); 
	}
	
	private double y2intensity(int y)
	{
		return 1 - (double)(y-CATCH_RADIUS)/(double)(getSize().height-1-2*CATCH_RADIUS);
	}
	
	// calculates the x coordinate (used for plotting, ranging between 0 and
	// getSize().width-1) as a function of the relative value of the list to be
	// plotted, which ranges from 0 to 1
	private int plotValue2x(double plotValue)
	{
		return CATCH_RADIUS + (int)(plotValue * (getSize().width - 1-2*CATCH_RADIUS));
	}
	
	private double x2plotValue(int x)
	{
		return (double)(x-CATCH_RADIUS)/(double)(getSize().width-1-2*CATCH_RADIUS);
	}
	
	// calculate the coordinates (v1, i1) and (v2, i2) of the intersections
	// of the intensity - plot value curve with the interval (0,1) x (0,1)
	private void calculateIntersections()
	{
		// calculate the intensity for plot value 0 (the darkest point in the plot)
		i1 = brightness - contrast/2;
		
		// if this is not within the interval (0, 1), make it zero and set v1 to the
		// corresponding plot value
		if(i1 < 0)
		{
			i1 = 0.0;
			v1 = 0.5 - brightness / contrast; // the plot value where the intensity is zero
		}
		else v1 = 0.0;
		
		// calculate the intensity for plot value 1 (the brightest point in the plot)
		i2 = brightness + contrast/2;
		
		// if this is not within the interval (0, 1), make it 1 and set v2 to the
		// corresponding plot value	
		if(i2 > 1)
		{
			i2 = 1.0;
			v2 = 0.5 + (1-brightness) / contrast;
		}
		else v2 = 1.0;
		
		// screen coordinates relative the Canvas of the intersections
		x1 = plotValue2x(v1);
		y1 = intensity2y(i1);
		x2 = plotValue2x(v2);
		y2 = intensity2y(i2);
	}
	
	public void paint(Graphics g)
	{
		// draw a rectangle around the whole thing
		g.setColor(Color.black);
		g.drawRect(CATCH_RADIUS, CATCH_RADIUS,
			getSize().width-1-2*CATCH_RADIUS, getSize().height-1-2*CATCH_RADIUS);
		
		// draw the intensity(value) curve
		g.setColor(Color.red);
		
		calculateIntersections();

		g.drawLine(CATCH_RADIUS, getSize().height-1-CATCH_RADIUS, x1, y1);
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x2, y2, getSize().width-1-CATCH_RADIUS, CATCH_RADIUS);
		
		g.setColor(Color.black);
		
		if(mouseNear == 1)
			g.drawOval(x1-CATCH_RADIUS, y1-CATCH_RADIUS, 2*CATCH_RADIUS, 2*CATCH_RADIUS);
		if(mouseNear == 2)
			g.drawOval(x2-CATCH_RADIUS, y2-CATCH_RADIUS, 2*CATCH_RADIUS, 2*CATCH_RADIUS);

		if(selected == 1)
			g.fillOval(x1-CATCH_RADIUS, y1-CATCH_RADIUS, 2*CATCH_RADIUS, 2*CATCH_RADIUS);
		if(selected == 2)
			g.fillOval(x2-CATCH_RADIUS, y2-CATCH_RADIUS, 2*CATCH_RADIUS, 2*CATCH_RADIUS);
	}


	///////////////////////////
	// MouseListener methods //
	///////////////////////////
	
	// Invoked when the mouse has been clicked on a component
	public void mouseClicked(MouseEvent me)
	{}
 
	// Invoked when the mouse enters a component. 
	public void mouseEntered(MouseEvent me)
	{}
	
	// Invoked when the mouse exits a component.
	public void mouseExited(MouseEvent me)
	{
		if(mouseNear != 0)
		{
			mouseNear = 0;
			repaint();
		}
	}
	 
	// Invoked when a mouse button has been pressed on a component.
	public void mousePressed(MouseEvent me)
	{
		if(mouseNear != 0)
		{
			selected = mouseNear;
			repaint();
		}
	}
	
	// Invoked when a mouse button has been released on a component.
	public void mouseReleased(MouseEvent me)
	{
		if(selected != 0)
		{
			selected = 0;
			repaint();
		}
	}


	/////////////////////////////////
	// MouseMotionListener methods //
	/////////////////////////////////

	// Invoked when a mouse button is pressed on a component and then dragged.
	public void mouseDragged(MouseEvent me)
	{
		if(selected != 0)
		{
			switch(selected)
			{
				case 1:
					v1 = x2plotValue(me.getX());
					i1 = y2intensity(me.getY());
					
					if(v1 >= v2) v1 = v2 - 1e-6;
					if(i1 >= i2) i1 = i2 - 1e-6;
					
					break;
				case 2:
					v2 = x2plotValue(me.getX());
					i2 = y2intensity(me.getY());

					if(v1 >= v2) v2 = v1 + 1e-6;
					if(i1 >= i2) i2 = i1 + 1e-6;
					
					break;
			}
			
			// calculate new brightness and contrast
			contrast = (i1 - i2) / (v1 - v2);
			brightness = i1 - contrast * (v1 - 0.5);
			
			repaint();
			
			// inform listener
			if(bccListener != null)
				bccListener.brightnessContrastChanged(brightness, contrast);
		}
	}

	// Invoked when the mouse button has been moved on a component (with no buttons no down).
	public void mouseMoved(MouseEvent me)
	{
		int dx, dy;
		int newMouseNear = 0;
		
		// is the mouse near one of the end points?
		dx = me.getX() - x1;
		dy = me.getY() - y1;
		
		if(dx*dx + dy*dy < CATCH_RADIUS*CATCH_RADIUS)
		{
			newMouseNear = 1;
		}
		else
		{
			dx = me.getX() - x2;
			dy = me.getY() - y2;
		
			if(dx*dx + dy*dy < CATCH_RADIUS*CATCH_RADIUS)
			{
				newMouseNear = 2;
			}
			else newMouseNear = 0;
		}
		
		if(newMouseNear != mouseNear)
		{
			mouseNear = newMouseNear;
			repaint();
		}
	}
}