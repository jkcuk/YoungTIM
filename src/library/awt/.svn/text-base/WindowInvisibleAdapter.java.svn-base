package library.awt;

import java.awt.*;
import java.awt.event.*;

// frame.setWindowListener(new WindowInvisibleAdapter()) makes the frame
// INVISIBLE when the close box is clicked; it does NOT free the frame's
// system resources (see WindowDisposingAdapter);
// the frame can be made visible again (frame.setVisible(true))
public class WindowInvisibleAdapter extends WindowAdapter
{
	Frame frame;
	
	public WindowInvisibleAdapter(Frame frame)
	{
		this.frame = frame;
	}
	
	public void windowClosing(WindowEvent we)
	{
		// make the window invisible
		frame.setVisible(false);
	}
}