package library.opticalSystem.laser;

import java.awt.event.*;


public class Laser1DClosingAdapter extends WindowAdapter
{
	Laser1D laser;
	
	public Laser1DClosingAdapter(Laser1D laser)
	{
		this.laser = laser;
	}
	
	public void windowClosing(WindowEvent we)
	{
		laser.closeAllWindows();
	}
}