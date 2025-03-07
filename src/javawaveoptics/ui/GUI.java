package javawaveoptics.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javawaveoptics.optics.ComponentInput;
import javawaveoptics.optics.ComponentOutput;
import javawaveoptics.optics.component.AbstractOpticalComponent;
import javawaveoptics.optics.component.ImageOfPlane;
import javawaveoptics.optics.component.ImageOfPlaneNonInitialising;
import javawaveoptics.optics.component.Plane;
import javawaveoptics.optics.environment.AbstractOpticalEnvironment;
import javawaveoptics.ui.workbench.AbstractWorkbench;
import javawaveoptics.ui.workbench.WorkbenchFactory;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * Graphical user interface allowing editing of almost everything to do with the
 * optical components.
 * 
 * @author Sean
 */
public class GUI extends AbstractUI implements ActionListener, OpticalEnvironmentChangeListener
{
	private static final long serialVersionUID = 1487674944980975848L;
	
	/*
	 * Component workbench
	 */
	
	// Workbench
	protected AbstractWorkbench componentWorkbench;
	
	/*
	 * Simulation controls
	 */
	
	// Button panel
	private JPanel buttonPanel = new JPanel();
	
	private transient JFormattedTextField roundTripsTextField;
		
	// Buttons
	private transient JButton simulateButton = new JButton("Simulate");
	// private JButton simulateLotsButton = new JButton("Simulate 10 times");
	private transient JButton loadButton = new JButton("Load");
	private transient JButton saveButton = new JButton("Save");
	private transient JButton clearPlanesDataButton = new JButton("Clear data in all planes");
	
	/**
	 * Constructor. Displays the graphical user interface components.
	 */
	public GUI(AbstractOpticalEnvironment opticalEnvironment)
	{
		super(opticalEnvironment);
		
		// Set the change listener to this
		// No, actually, don't, as we don't think we need this any longer
		// opticalEnvironment.setChangeListener(this);
		
		/*
		 * Component visualisation
		 */
		
		// Create workbench
		componentWorkbench = WorkbenchFactory.createWorkbench(opticalEnvironment);
		
		// Set workbench minimum size
		componentWorkbench.setMinimumSize(new Dimension(300, 250));
		
		// Set button panel minimum and maximum size equal so it doesn't change size at all when the window is resized
		//buttonPanel.setMinimumSize(new Dimension(200, 35));
		//buttonPanel.setMaximumSize(new Dimension(6000, 35));
		
		/*
		 * Simulation control
		 */
		
		buttonPanel.add(simulateButton);
		// buttonPanel.add(simulateLotsButton);
		
		roundTripsTextField = UIBitsAndBobs.makeIntFormattedTextField(null);
		roundTripsTextField.setValue(Integer.valueOf(1));
		if(componentWorkbench.showResearchButtons())
		{
			buttonPanel.add(roundTripsTextField);
			buttonPanel.add(new JLabel("round trips"));
			buttonPanel.add(clearPlanesDataButton);
		}
		
		
		if(componentWorkbench.showLoadAndSaveButtons())
		{
			buttonPanel.add(loadButton);
			buttonPanel.add(saveButton);
		}
		

		// tooltips
		roundTripsTextField.setToolTipText("No of round trips to simulate");
		simulateButton.setToolTipText("Simulate propagation of beam through the optical system");
		loadButton.setToolTipText("Load optical system from .tim file");
		saveButton.setToolTipText("Save optical system to .tim file");
		clearPlanesDataButton.setToolTipText("Clear the data in all planes and reset round-trip counters in all images of planes");
		
		// This will get the operating system specific preferred height and then set
		// the minimum and maximum sizes based on this preferred height. This means
		// that whenever the window is resized, the button panel stays the same size --
		// its most natural size for the operating system -- at all times.
		int buttonPanelPreferredHeight = buttonPanel.getPreferredSize().height;
		
		buttonPanel.setMinimumSize(new Dimension(100, buttonPanelPreferredHeight));
		buttonPanel.setMaximumSize(new Dimension(10000, buttonPanelPreferredHeight));
		
		// Set simulate button action commands
		simulateButton.addActionListener(this);
		// simulateButton.setActionCommand("Simulate");
		
		// Set simulate lots button action commands
		// simulateLotsButton.addActionListener(this);
		// simulateLotsButton.setActionCommand("Simulate Lots");
		
		// Set simulate lots button enabled or disabled
		// simulateLotsButton.setEnabled(opticalEnvironment.isImageableLightSourcePresent());
		
		// Set load and save button action commands
		loadButton.addActionListener(this);
		saveButton.addActionListener(this);
		
		// Set load and save button action commands
		loadButton.setActionCommand("Load");
		saveButton.setActionCommand("Save");
		
		clearPlanesDataButton.addActionListener(this);
		clearPlanesDataButton.setActionCommand("Clear data");
		
		showGUI();
	}
	
	private void showGUI()
	{
		setLayout(new BorderLayout());
		
		add(componentWorkbench, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
//		// Clear everything
//		removeAll();
//		
//		/*
//		 * Main window layout management
//		 */
//		
//		// Create the layout engine
//		GroupLayout windowLayout = new GroupLayout(this);
//		
//		// Create layout groups
//		ParallelGroup windowLayoutHorizontalGroup = windowLayout.createParallelGroup();
//		SequentialGroup windowLayoutVerticalGroup = windowLayout.createSequentialGroup();
//		
//		// Set the layout groups
//		windowLayout.setHorizontalGroup(windowLayoutHorizontalGroup);
//		windowLayout.setVerticalGroup(windowLayoutVerticalGroup);
//		
//		// Set layout manager
//		setLayout(windowLayout);
//		
//		// Add the tabbed pane (which itself contains the workbench container) to the main panel		
//		windowLayoutVerticalGroup.addComponent(componentWorkbench);
//		windowLayoutHorizontalGroup.addComponent(componentWorkbench);
//		
//		// Add buttons to layout
//		windowLayoutHorizontalGroup.addComponent(buttonPanel);
//		windowLayoutVerticalGroup.addComponent(buttonPanel);
	}
	
	private SimulateWorker simulateWorker;
	
	/**
	 * Deals with action events appropriately.
	 * 
	 * @author Sean
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		
		if(command.equals("Simulate"))
		{
	        int roundTrips = ((Number)roundTripsTextField.getValue()).intValue();

			// Define thread
	        simulateWorker = new SimulateWorker(roundTrips);
			Thread thread = new Thread(simulateWorker);

			try
			{
				simulateButton.setText("Stop");
				
				// Start thread
				thread.start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(command.equals("Stop"))
		{
			simulateWorker.cancel(true);
		}
//		else if(command.equals("Simulate Lots"))
//		{
//			// Define thread
//			Thread thread = new Thread(new SimulateWorker(10));
//
//			try
//			{
//				// Start thread
//				thread.start();
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
		else if(command.equals("Load"))
		{
			// Set up file chooser
			TIMFileChooser fileChooser = new TIMFileChooser();
			
			// Set default directory to current directory
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
			
			// Get return value from file chooser
			int returnValue = fileChooser.showDialog(buttonPanel, "Load");
			
			if(returnValue == JFileChooser.APPROVE_OPTION)
			{
				// A TIM file has been chosen... try opening it
				
				try
				{
					FileInputStream fileInputStream = new FileInputStream(fileChooser.getSelectedFile());
					ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
					
					opticalEnvironment = (AbstractOpticalEnvironment) objectInputStream.readObject();
					objectInputStream.close();
					
					// Create a new workbench for the loaded environment
					componentWorkbench = WorkbenchFactory.createWorkbench(opticalEnvironment);
					
					// Refresh the GUI
					showGUI();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				catch(ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}
		}
		else if(command.equals("Save"))
		{
			// Set up file chooser
			TIMFileChooser fileChooser = new TIMFileChooser();
			
			// Set default file target to file called 'environment.tim' in current directory
			fileChooser.setSelectedFile(new File(System.getProperty("user.dir") + "\\environment.tim"));
			
			// Get return value from file chooser
			int returnValue = fileChooser.showSaveDialog(buttonPanel);
			
			if(returnValue == JFileChooser.APPROVE_OPTION)
			{
				try
				{
					FileOutputStream fileOutputStream = new FileOutputStream(fileChooser.getSelectedFile());
					ObjectOutputStream objectOutputSteam = new ObjectOutputStream(fileOutputStream);
					
					objectOutputSteam.writeObject(opticalEnvironment);
					objectOutputSteam.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		else if(command.equals("Clear data"))
		{
			clearDataInAllPlanesRecursively(componentWorkbench.getStartComponent(), null);
		}
	}
	
	private void clearDataInAllPlanesRecursively(AbstractOpticalComponent component, AbstractOpticalComponent parent)
	{
		if(component != null)
		{
			// if this component is imageable, add it to the list
			if(component instanceof Plane)
			{
				// clear the plane's data
				((Plane)component).clearData();
			}
			else if(component instanceof ImageOfPlaneNonInitialising)
			{
				// reset the image's round-trip counter
				((ImageOfPlaneNonInitialising)component).resetRoundTripCounter();
			}
			else if(component instanceof ImageOfPlane)
			{
				// reset the image's round-trip counter
				((ImageOfPlane)component).resetRoundTripCounter();
			}
			
			// now go through all the component's inputs and outputs and follow them up
			if(component.getComponentOutputs() != null)
			{
				for(ComponentInput output : component.getComponentOutputs())
				{
					if(output != null)
					{
						AbstractOpticalComponent nextComponent = output.getComponent();
						
						// if component 1's output is component 2, then component 2's input is component 1;
						// to avoid getting stuck in an infinite loop, check that we are not re-visiting the same component
						if(!nextComponent.equals(parent))
						{
							clearDataInAllPlanesRecursively(nextComponent, component);
						}
					}
				}
			}
			
			if(component.getComponentInputs() != null)
			{
				for(ComponentOutput input : component.getComponentInputs())
				{
					if(input != null)
					{
						AbstractOpticalComponent nextComponent = input.getComponent();
						
						// if component 1's output is component 2, then component 2's input is component 1;
						// to avoid getting stuck in an infinite loop, check that we are not re-visiting the same component
						if(!nextComponent.equals(parent))
						{
							clearDataInAllPlanesRecursively(nextComponent, component);
						}
					}
				}
			}
		}
	}

	@Override
	public void changeMade(OpticalEnvironmentChangeEvent event)
	{
		// Check if the optical environment still contains an imageable light source
		// simulateLotsButton.setEnabled(opticalEnvironment.isImageableLightSourcePresent());
	}
	
	/************************
	 * Thread functionality *
	 ************************/
	
	/**
	 * Defines a thread which runs the simulation.
	 * 
	 * @author Sean
	 */
	private class SimulateWorker extends SwingWorker<Void, Void>
	{
		// Number of times to run the simulation (for resonators, etc.)
		private int numberOfTimes;
		
//		public SimulateWorker()
//		{
//			this(1);
//		}
		
		public SimulateWorker(int numberOfTimes)
		{
			this.numberOfTimes = numberOfTimes;
		}
		
		/**
		 * The thread functionality - running the simulation in the background
		 */
		@Override
		protected Void doInBackground() throws Exception
		{
			/*
			 * Initial setup
			 */
			
			// Tell console we're starting
			System.out.println("--- Start of simulation ---");
			
			// Disable buttons temporarily
            simulateButton.setText("Stop");
            loadButton.setEnabled(false);
            saveButton.setEnabled(false);
            clearPlanesDataButton.setEnabled(false);
            
            // Set cursor to 'wait' animation
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
            /*
             * Simulation
             */
            
            if(numberOfTimes > 1)
            {
           		for(int n = 1; n <= numberOfTimes; n++)
           		{
                   	if(!isCancelled())
                   	{
            			System.out.println("\nSIMULATION " + n + "...");
            		
            			simulate();
            		}
            	}
            }
            else
            {
            	simulate();
            }
			
			// We're not returning anything meaningful, but alas we must return
			// something
			return null;
		}
		
		/**
		 * The actual simulation code
		 */
		private void simulate()
		{
			// Get the first component in the optical component train (as good a place to start as any)...			
			AbstractOpticalComponent startComponent = componentWorkbench.getStartComponent();
			
			// ...and calculate all its inputs and outputs, sending the outputs as inputs to their respectively connected components.
			startComponent.calculateAndDealWithInputsAndOutputs();
		}
		
		/**
		 * Code that is run when the simulation(s) is/are finished.
		 */
        @Override
        public void done()
        {
        	/*
        	 * Final settings
        	 */
        	
        	// Send beep
            // Toolkit.getDefaultToolkit().beep();
            
            // Re-enable buttons
            simulateButton.setText("Simulate");
            loadButton.setEnabled(true);
            saveButton.setEnabled(true);
            clearPlanesDataButton.setEnabled(true);
            
            // Turn off the wait cursor
            setCursor(null);
            
            // Tell console we're done
            System.out.println("--- End of simulation ---");
        }
	}
}
