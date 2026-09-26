package library.opticalSystem.laser;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import library.awt.*;
import library.opticalSystem.*;


// Note: Every Frame of the application needs a separate instance of the application's
//       menu bar, even if it the menus are exactly the same!

class Laser1DMenuBar extends MenuBar
implements ActionListener, Runnable, Serializable
{
	private static final long serialVersionUID = 143680869785284961L;

	private Laser1DControlsWindow
		controlsWindow;
		
	private MenuItem
		fileNew,
		fileOpen,
		fileSaveAs,
		fileSettings,
		fileQuit,
		resonatorSettings,
		beamCustomise,
		beamCalculateEigenmode,
		beamRoundTrip,
		beamRoundTripDensityPlot,
		beamNormalise;
	
	
	//
	// constructor
	//
	
	Laser1DMenuBar(Laser1DControlsWindow controlsWindow)
	{
		// create a new MenuBar
		super();
		
		this.controlsWindow = controlsWindow;

		//
		// initialise the menus and their menu items
		//
		
		// the File menu
		Menu file = new Menu("File");
		add(file);
		fileNew = new MenuItem("New...");
		fileNew.addActionListener(this);
		file.add(fileNew);
		fileOpen = new MenuItem("Open...");
		fileOpen.addActionListener(this);
		file.add(fileOpen);
		fileSaveAs = new MenuItem("Save As...");
		fileSaveAs.addActionListener(this);
		file.add(fileSaveAs);
		file.add(new MenuItem("-"));
		fileSettings = new MenuItem("WaveTrace Settings...");
		fileSettings.addActionListener(this);
		fileSettings.setShortcut(new MenuShortcut(KeyEvent.VK_W));
		file.add(fileSettings);
		file.add(new MenuItem("-"));
		fileQuit = new MenuItem("Quit");
		fileQuit.addActionListener(this);
		fileQuit.setShortcut(new MenuShortcut(KeyEvent.VK_Q));
		file.add(fileQuit);
		
		// the Resonator menu
		Menu resonator = new Menu("Resonator");
		add(resonator);
		resonatorSettings = new MenuItem("Settings...");
		resonatorSettings.setShortcut(new MenuShortcut(KeyEvent.VK_R));
		resonatorSettings.addActionListener(this);
		resonator.add(resonatorSettings);
		
		// the Beam menu
		Menu beam = new Menu("Light Beam");
		add(beam);
		beamCustomise = new MenuItem("Initialise...");
		beamCustomise.addActionListener(this);
		beam.add(beamCustomise);
		beam.add(new MenuItem("-"));
		beamCalculateEigenmode = new MenuItem("Calculate Eigenmode...");
		beamCalculateEigenmode.addActionListener(this);
		beamCalculateEigenmode.setShortcut(new MenuShortcut(KeyEvent.VK_E));
		// beamCalculateEigenmode.setEnabled(false);
		beam.add(beamCalculateEigenmode);
		beam.add(new MenuItem("-"));
		beamRoundTrip = new MenuItem("Simulate Round Trip Through Resonator");
		beamRoundTrip.addActionListener(this);
		beamRoundTrip.setShortcut(new MenuShortcut(KeyEvent.VK_T));
		beam.add(beamRoundTrip);
		beamRoundTripDensityPlot = new MenuItem("Density Plot of Next Round Trip...");
		beamRoundTripDensityPlot.addActionListener(this);
		beamRoundTripDensityPlot.setShortcut(new MenuShortcut(KeyEvent.VK_P));
		beam.add(beamRoundTripDensityPlot);
		beam.add(new MenuItem("-"));
		beamNormalise = new MenuItem("Normalise Power");
		beamNormalise.addActionListener(this);
		beam.add(beamNormalise);
	}
	
	public Laser1DMenuBar copy()
	{
		return new Laser1DMenuBar(controlsWindow);
	}
	
	
	//
	// ActionListener method
	//
	
	public synchronized void actionPerformed(ActionEvent ae)
	{
		// store the source of the command for use in the run() method, ...
		commandSource = ae.getSource();
		
		// ... which is started by this line
		(new Thread(this, "Laser1D menu command execution")).start();
	}
	
	private Object
		commandSource;
	
	//
	// Runnable method
	//
	
	public synchronized void run()
	{
		if(commandSource == fileNew)
		{
			controlsWindow.newLaser();
		}
		else if(commandSource == fileOpen)
		{
			FileDialog fd = new FileDialog(controlsWindow, "", FileDialog.LOAD);
			
			try
			{
				fd.setVisible(true);
	
				if(fd.getFile() != null)
				{
					controlsWindow.open(fd.getDirectory() + fd.getFile());
				}
			}
			catch(Exception e)
			{
				System.out.println("Exception during loading: " + e);
				// System.exit(0);
			}
			finally
			{
				// free system resources associated with dialog
				fd.dispose();
			}
		}
		else if(commandSource == fileSaveAs)
		{
			FileDialog fd = new FileDialog(controlsWindow, "", FileDialog.SAVE);
			
			try
			{
				fd.setVisible(true);
	
				if(fd.getFile() != null)
					controlsWindow.getLaser().save(fd.getDirectory() + fd.getFile());
			}
			catch(Exception e)
			{
				System.out.println("Exception during saving: " + e);
				// System.exit(0);
			}
			finally
			{
				// free system resources associated with dialog
				fd.dispose();
			}
		}
		else if(commandSource == fileSettings)
		{
			controlsWindow.getLaser().initialiseWaveTrace();
		}
		else if(commandSource == fileQuit)
		{
			System.exit(0);
		}
		else if(commandSource == resonatorSettings)
		{
			controlsWindow.getLaser().customiseResonator();
		}
		else if(commandSource == beamCustomise)
		{
			controlsWindow.getLaser().customiseBeam();
			controlsWindow.getLaser().updatePlot();
		}
		else if(commandSource == beamCalculateEigenmode)
		{
			controlsWindow.getLaser().setStatusString("Running... ");
			controlsWindow.getLaser().calculateEigenmode();
		}
		else if(commandSource == beamRoundTrip)
		{
			controlsWindow.getLaser().setStatusString("Running... ");
			controlsWindow.getLaser().passThroughResonator();
		}
		else if(commandSource == beamRoundTripDensityPlot)
		{
			controlsWindow.getLaser().setStatusString("Running... ");
			controlsWindow.getLaser().calculateIntensityListOverOneRoundTrip();
		}
		else if(commandSource == beamNormalise)
		{
			controlsWindow.getLaser().normalisePowerInBeam();
		}
		
		controlsWindow.getLaser().setStatusString("");
	}
}