package javawaveoptics.run;

import javawaveoptics.optics.environment.AbstractOpticalEnvironment;
import javawaveoptics.optics.environment.OpticalEnvironmentFactory;
import javawaveoptics.optics.environment.OpticalEnvironmentFactory.OpticalEnvironmentType;
import javawaveoptics.ui.GUI;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

/**
 * Applet version of the program. Runs the program within a single window, allowing it to be embedded
 * onto websites, mobile devices, etc.
 * 
 * @author Sean
 */
public class YoungTIMApplet extends JApplet
{
	// Serial number
	private static final long serialVersionUID = -809748810809314615L;
	
	// The optical environment in use
	private AbstractOpticalEnvironment environment;
	
	/**
	 * Main program code. This creates a new thread in which to run the GUI in order to maintain thread
	 * safety.
	 */
	public void init()
	{
		super.init();
		
		/*
		 * Set window dimensions
		 */
		
		int width, height;
		
		try
		{
			width = Integer.parseInt(getParameter("width"));
		}
		catch(NumberFormatException e)
		{
			width = 770;
		}
		
		try
		{
			height = Integer.parseInt(getParameter("height"));
		}
		catch(NumberFormatException e)
		{
			height = 670;
		}
		
		setSize(width, height);
		
		/*
		 * Set up the optical environment
		 */
		
//		int type;
//		
//		try
//		{
//			type = Integer.parseInt(getParameter("environment"));
//		}
//		catch(NumberFormatException e)
//		{
//			type = 0;
//		}
		
		environment = OpticalEnvironmentFactory.createOpticalEnvironment(OpticalEnvironmentType.DOUBLE_SLIT);
		
		/*
		 * Show the GUI
		 */
		
		try
		{
			SwingUtilities.invokeAndWait(
				new Runnable()
				{
					public void run()
					{
						// Set the system-specific look and feel						
						// Weirdly some GUI settings such as red backgrounded
						// text boxes don't work in Ubuntu 11.04 using this setting
						// but do work in the Java default (Java default is used
						// when this setting is not specified)
						try
					    {
					    	//UIManager.setLookAndFeel(
					       // 	UIManager.getSystemLookAndFeelClassName());
					    }
					    catch(Exception e)
					    {
					    	// Do nothing
					    }
						
						setupGUI();
					}
				}
			);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates and shows the graphical user interface.
	 */
	private void setupGUI()
	{		
		GUI gui = new GUI(environment);
		
		add(gui);
	}
}
