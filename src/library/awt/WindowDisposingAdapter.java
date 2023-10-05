package library.awt;

import java.awt.*;
import java.awt.event.*;

// frame.setWindowListener(new WindowDisposingAdapter()) makes the frame
// invisible AND REMOVES ITS SYSTEM RESOURCES when the close box is clicked;
// the frame cannot (or should not) be made visible again
public class WindowDisposingAdapter extends WindowAdapter
{
	Frame frame;
	
	public WindowDisposingAdapter(Frame frame)
	{
		this.frame = frame;
	}
	
	public void windowClosing(WindowEvent we)
	{
		// get rid of the window altogether
		frame.dispose();
	}
}