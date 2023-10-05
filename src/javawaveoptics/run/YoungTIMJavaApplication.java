package javawaveoptics.run;

import java.awt.Dimension;

import javawaveoptics.optics.environment.AbstractOpticalEnvironment;
import javawaveoptics.optics.environment.OpticalEnvironmentFactory;
import javawaveoptics.optics.environment.OpticalEnvironmentFactory.OpticalEnvironmentType;
import javawaveoptics.ui.GUI;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * JavaApplication version of the program. 
 * 
 * @author Sean, Johannes
 */
public class YoungTIMJavaApplication
{	
	/**
	 * Main program code. This creates a new thread in which to run the GUI in order to maintain thread
	 * safety.
	 */
	public static void main(final String[] args)
	{	
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
						
						setupGUI(args);
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
	private static void setupGUI(String[] args)
	{
		/*
		 * Set window dimensions
		 */
		
		int width, height;
		
		try
		{
			width = Integer.parseInt(args[1]);
		}
		catch(NumberFormatException e)
		{
			width = 750;
		}
		catch(IndexOutOfBoundsException e)
		{
			width = 750;
		}
		
		try
		{
			height = Integer.parseInt(args[2]);
		}
		catch(NumberFormatException e)
		{
			height = 600;
		}
		catch(IndexOutOfBoundsException e)
		{
			height = 600;
		}
		
		/*
		 * Set up the optical environment
		 */
		
		// OpticalEnvironmentType type = OpticalEnvironmentType.FRACTAL_LASER_M3A6;	// fractal-laser optical environment
		// OpticalEnvironmentType type = OpticalEnvironmentType.RESONATOR_INTERFACE;
		OpticalEnvironmentType type = OpticalEnvironmentType.DOUBLE_SLIT;	// fractal-laser optical environment
				
		// Create the optical environment
		AbstractOpticalEnvironment environment = OpticalEnvironmentFactory.createOpticalEnvironment(type);
		
		// Create the GUI
		GUI gui = new GUI(environment);
		
		// Create a frame to put the GUI in
		JFrame container = new JFrame();
		container.add(gui);
		
		container.setMinimumSize(new Dimension(400, 500));
		
		// Set up the frame
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setSize(new Dimension(width, height));
		container.setTitle("Young TIM");
		container.setVisible(true);
	}
}
