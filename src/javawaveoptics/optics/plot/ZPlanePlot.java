package javawaveoptics.optics.plot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.JCProgressBar;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import library.optics.LightBeamCrossSection2D;

public class ZPlanePlot extends AbstractPlot implements Serializable, ActionListener, ChangeListener, PropertyChangeListener
{
	private static final long serialVersionUID = -1482857613411817108L;

	/*
	 * Fields
	 */
	
	private double coordinateValue = 0;
	private TransverseCoordinate coordinate = TransverseCoordinate.Y;	// one of AbstractPlot.ASPECT_RATIO_COORDINATE_*
	// private int aspectRatio = AbstractPlot.ASPECT_RATIO_SCREEN_AREA;	// one of AbstractPlot.ASPECT_RATIO_*
	private double deltaZMin = -1e-1;
	private double deltaZMax = 1e-1;
	private int numberOfZSteps = 100;
	
	// exposure compensation factor, 2^(exposure compensation value)
	private double exposureCompensationValue = 0.0;
	
	private BeamCrossSection beam;
	private LightBeamCrossSection2D crossSection;
	
	/**
	 * the plot type, i.e. one of the list in AbstractPlot
	 */
	private AreaPlotType plotType = AreaPlotType.INTENSITY;	
	
	/*
	 * GUI edit controls
	 */

	private transient JComboBox<AreaPlotType> plotTypeComboBox;
	private transient JComboBox<TransverseCoordinate> coordinateComboBox;	// , aspectRatioComboBox;
	private transient LengthField coordinateValueLengthField, deltaZMinLengthField, deltaZMaxLengthField;
	private transient JFormattedTextField numberOfZStepsField;
	private transient JButton calculateCrossSectionButton;
	private transient JSpinner exposureCompensationSpinner;
	private transient JCProgressBar progressBar;

	public ZPlanePlot(String name, AreaPlotType plotType)
	{
		super(name);
		this.plotType = plotType;
	}
	
	public ZPlanePlot()
	{
		this("Z plane", AreaPlotType.INTENSITY);
	}
	
	@Override
	public boolean isAspectRatioFixed()
	{
		return false;
	}
	
	public void calculateCrossSection()
	{
		if(beam == null)
		{
			crossSection = null;
			return;
		}
		
        // Set cursor to 'wait' animation
		calculateCrossSectionButton.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		// show the progress bar, and set its parameters		
		progressBar.setVisible(true);
		progressBar.setMaximum(numberOfZSteps);
		progressBar.setValue(0);

		// the index number in the beam's amplitude matrix that corresponds to the chosen coordinate and value
		int coordinateIndex;
		if(coordinate == TransverseCoordinate.X)
		{
			coordinateIndex = beam.getI(coordinateValue);
		}
		else
		{
			coordinateIndex = beam.getJ(coordinateValue);
		}
		
		double widthOrHeight = (coordinate == TransverseCoordinate.X)?beam.getPhysicalHeight():beam.getPhysicalWidth();
		
		crossSection = new LightBeamCrossSection2D(
				numberOfZSteps,	// width
				beam.getWidth(),	// height
				deltaZMax - deltaZMin,	// physical width
				widthOrHeight,	// physical height
				beam.getWavelength()
			);

		class WaveWorker implements Runnable
		{
			public WaveWorker(BeamCrossSection beam, LightBeamCrossSection2D crossSection, TransverseCoordinate coordinate, int coordinateIndex)
			{
				this.beam = beam;
				this.crossSection = crossSection;
				this.coordinate = coordinate;
				this.coordinateIndex = coordinateIndex;
			}
			
			private BeamCrossSection beam;
			private LightBeamCrossSection2D crossSection;
			private TransverseCoordinate coordinate;
			private int coordinateIndex, zIndex;
			private double deltaZ;
			public void setZIndexAndDeltaZ(int zIndex, double deltaZ)
			{
				this.zIndex = zIndex;
				this.deltaZ=deltaZ;
			}
			public void run()
			{
				// create a copy of the beam cross section, and propagate it to the required z value
				BeamCrossSection beamPropagated = new BeamCrossSection(beam);
				beamPropagated.propagate(deltaZ);

				if(coordinate == TransverseCoordinate.X)
				{
					for(int j = 0; j < beam.getHeight(); j++)
					{
						crossSection.setElement(zIndex, j, beamPropagated.getElement(coordinateIndex, j));
					}
				}
				else
				{
					for(int i = 0; i < beam.getWidth(); i++)
					{
						crossSection.setElement(zIndex, i, beamPropagated.getElement(i, coordinateIndex));
					}
				}
			}
		}

		// work out how many processors there are...
		int nthreads=Runtime.getRuntime().availableProcessors();
		
		// ... and make an array of worker objects, which tell the threads what to do
		WaveWorker[] workers= new WaveWorker[nthreads];
		for(int i=0; i<nthreads; i++) workers[i]=new WaveWorker(beam, crossSection, coordinate, coordinateIndex);

		// now calculate the image
		for(int zIndex = 0; zIndex < numberOfZSteps; zIndex += nthreads)
		{			
			// update progress bar
			progressBar.setValue(zIndex);

			Thread[] threads=new Thread[nthreads];
			for(int i=0; i<nthreads; i++) threads[i]=new Thread(workers[i]); //make new threads for the workers
			for(int i=0; i<nthreads && zIndex+i<numberOfZSteps; i++)
			{
				// z coordinate of current plane, relative to plane
				double deltaZ = deltaZMin + (deltaZMax - deltaZMin) * (zIndex+i) / (numberOfZSteps + 1);

				workers[i].setZIndexAndDeltaZ(zIndex+i, deltaZ);				//assign one line of the image to each worker object
				threads[i].start();								//and set them going
			}
			try
			{
				for(int i=0; i<nthreads; i++) threads[i].join();	//wait for all the workers to finish
			}
			catch (InterruptedException e)
			{
				// don't do anything, assuming (hoping?) that something sensible happened
				// System.out.println("ZPlanePlot::calculateCrossSection: ");
				// e.printStackTrace();
			}
		}
		
		// hide the progress bar again		
		progressBar.setVisible(false);
		
        // Set cursor to 'wait' animation
		calculateCrossSectionButton.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	

	@Override
	public BufferedImage getPlotImage(BeamCrossSection beam)
	{
		// calculateXZCrossSection(beam);
		setBeam(beam);
		
		// if the xz cross section hasn't been calculated (yet), return null
		if(crossSection == null) return null;
		
		int dataColumns = crossSection.getWidth();
		int dataRows = crossSection.getHeight();
			
		BufferedImage image = new BufferedImage(dataColumns, dataRows, BufferedImage.TYPE_INT_RGB );

		double exposureFactor = Math.pow(2, exposureCompensationValue);
		
		if(plotType == AreaPlotType.INTENSITY)
		{
			double maxIntensity = crossSection.getMaxIntensity() / exposureFactor;
			
			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					double intensity = crossSection.getIntensity(x, y);
					
					image.setRGB(x, dataRows - 1 - y, Color.HSBtoRGB(1, 0, (float)Math.min(1.0, (intensity/maxIntensity))));
				}
			}
		}
		else if(plotType == AreaPlotType.LOG_INTENSITY)
		{
			double
				logMaxIntensity = Math.log10(crossSection.getMaxIntensity() / exposureFactor),
				logMinIntensity = Math.log10(crossSection.getMinIntensity() / exposureFactor);
			if(logMaxIntensity - logMinIntensity > AbstractPlot.maxLogIntensityDecades)
			{
				// too much dynamic range
				logMinIntensity = logMaxIntensity - AbstractPlot.maxLogIntensityDecades;
			}
			
			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					double logIntensity = Math.log10(crossSection.getIntensity(x, y));
					
					image.setRGB(x, dataRows - 1 - y,
							Color.HSBtoRGB(1, 0, (float)Math.max(.0, (logIntensity - logMinIntensity)/(logMaxIntensity - logMinIntensity))));
				}
			}
		}
		else if(plotType == AreaPlotType.PHASE_COLOUR)
		{
			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					// phase, normalised to the range 0 to 1
					double phase = Math.atan2(crossSection.getElementIm(x, y), crossSection.getElementRe(x, y)) / (2 * Math.PI) + 0.5;

					image.setRGB(x, dataRows - 1 - y, Color.HSBtoRGB((float)phase, 1, 1));
				}
			}
		}
		else if(plotType == AreaPlotType.PHASE_GRAYSCALE)
		{
			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					// phase, normalised to the range 0 to 1
					double phase = Math.atan2(crossSection.getElementIm(x, y), crossSection.getElementRe(x, y)) / (2 * Math.PI) + 0.5;

					image.setRGB(x, dataRows - 1 - y, Color.HSBtoRGB(0, 0, (float)phase));
				}
			}
		}
		else if(plotType == AreaPlotType.PHASE_AND_INTENSITY)
		{
			double maxIntensity = crossSection.getMaxIntensity() / exposureFactor;

			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					float hue = (float) (Math.atan2(crossSection.getElementIm(x, y), crossSection.getElementRe(x, y)) / (2 * Math.PI) + 0.5);
					float brightness = (float)Math.min(1.0, (crossSection.getIntensity(x, y) / maxIntensity));
					
					image.setRGB(x, dataRows - 1 - y, Color.HSBtoRGB(hue, 1, brightness));
				}
			}
		}
		else if(plotType == AreaPlotType.REAL_PART)
		{
			double maxField = crossSection.getMaxAbsRe() / Math.sqrt(exposureFactor);
			
			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					double re = crossSection.getElementRe(x, y);
					
					image.setRGB(x, dataRows - 1 - y, Color.HSBtoRGB(((re>0)?(float)0.5:0), 1, (float)Math.min(1.0, (Math.abs(re)/maxField))));
				}
			}
		}
		else if(plotType == AreaPlotType.IMAGINARY_PART)
		{
			double maxField = crossSection.getMaxAbsIm() / Math.sqrt(exposureFactor);
			
			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					double im = crossSection.getElementIm(x, y);
					
					image.setRGB(x, dataRows - 1 - y, Color.HSBtoRGB(((im>0)?(float)0.5:0), 1, (float)Math.min(1.0, (Math.abs(im)/maxField))));
				}
			}
		}
		
		return image;
	}
		
	@Override
	public JComponent getSettingsPanel()
	{
		if(plotTypeComboBox == null)
		{
			initialiseWidgets();
		}
		
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		// container.add(UIBitsAndBobs.makeRow("Plot", plotTypeComboBox));
		container.add(UIBitsAndBobs.makeRow("Plot plane", coordinateComboBox, "=", coordinateValueLengthField, true));
		container.add(UIBitsAndBobs.makeRow("\u0394z<sub>min</sub>", deltaZMinLengthField, true));
		container.add(UIBitsAndBobs.makeRow("\u0394z<sub>max</sub>", deltaZMaxLengthField, true));
		container.add(UIBitsAndBobs.makeRow("Number of steps", numberOfZStepsField, true));
		// container.add(UIBitsAndBobs.makeRow("Aspect ratio", aspectRatioComboBox));
		container.add(progressBar);
		container.add(Box.createVerticalGlue());

		return new JScrollPane(container);
	}

	@Override
	public JComponent getControlPanelLeft()
	{
		if(plotTypeComboBox == null)
		{
			initialiseWidgets();
		}
		
		JPanel controlPanelLeft = new JPanel();
		controlPanelLeft.setLayout(new BoxLayout(controlPanelLeft, BoxLayout.X_AXIS));
		controlPanelLeft.add(plotTypeComboBox);
		controlPanelLeft.add(exposureCompensationSpinner);
		
		return controlPanelLeft;
	}
	
	@Override
	public JComponent getControlPanelRight()
	{
		if(plotTypeComboBox == null)
		{
			initialiseWidgets();
		}
		
		JPanel controlPanelRight = new JPanel();
		controlPanelRight.setLayout(new BoxLayout(controlPanelRight, BoxLayout.X_AXIS));
		
		controlPanelRight.add(calculateCrossSectionButton);
		
		return controlPanelRight;
	}

	protected void initialiseWidgets()
	{
		plotTypeComboBox = new JComboBox<AreaPlotType>(AreaPlotType.values());
		plotTypeComboBox.setToolTipText("Part of the complex field to plot");
		plotTypeComboBox.addActionListener(this);
		plotTypeComboBox.setSelectedItem(plotType);
		plotTypeComboBox.setMaximumSize(plotTypeComboBox.getPreferredSize());

		exposureCompensationSpinner = UIBitsAndBobs.createExposureCompensationSpinner(exposureCompensationValue);
		exposureCompensationSpinner.addChangeListener(this);

		coordinateComboBox = new JComboBox<TransverseCoordinate>(TransverseCoordinate.values());
		coordinateComboBox.addActionListener(this);
		coordinateComboBox.setSelectedItem(coordinate);
		coordinateComboBox.setMaximumSize(coordinateComboBox.getPreferredSize());

		coordinateValueLengthField = new LengthField(this);
		coordinateValueLengthField.setLengthInMetres(coordinateValue);

		deltaZMinLengthField = new LengthField(this);
		deltaZMinLengthField.setMaximumSize(deltaZMinLengthField.getPreferredSize());
		// deltaZMinLengthField.setAlignmentX(Component.LEFT_ALIGNMENT);
		deltaZMinLengthField.setLengthInMetres(deltaZMin);

		deltaZMaxLengthField = new LengthField(this);
		deltaZMaxLengthField.setMaximumSize(deltaZMaxLengthField.getPreferredSize());
		// deltaZMaxLengthField.setAlignmentX(Component.LEFT_ALIGNMENT);
		deltaZMaxLengthField.setLengthInMetres(deltaZMax);

		numberOfZStepsField = UIBitsAndBobs.makeIntFormattedTextField(this);
		// numberOfZStepsField.setAlignmentX(Component.LEFT_ALIGNMENT);
		numberOfZStepsField.setValue(new Integer(numberOfZSteps));
		
//		aspectRatioComboBox = new JComboBox();
//		aspectRatioComboBox.addActionListener(this);
//		aspectRatioComboBox.addItem(AbstractPlot.ASPECT_RATIO_AUTOMATIC_DESCRIPTION);
//		aspectRatioComboBox.addItem(AbstractPlot.ASPECT_RATIO_SCREEN_AREA_DESCRIPTION);
//		aspectRatioComboBox.setSelectedItem(AbstractPlot.ASPECT_RATIO_AUTOMATIC);
//		aspectRatioComboBox.setMaximumSize(aspectRatioComboBox.getPreferredSize());

		calculateCrossSectionButton = new JButton("(Re)calculate");
		calculateCrossSectionButton.setEnabled(beam != null);
		calculateCrossSectionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		calculateCrossSectionButton.addActionListener(this);
		
		// see http://docs.oracle.com/javase/tutorial/uiswing/components/progress.html
		progressBar = new JCProgressBar(0, 1);
		progressBar.setVisible(false);
	}
	
//	@Override
//	public double getAspectRatio(BeamCrossSection beam)
//	{
//		// if the xz cross section hasn't been calculated (yet), return 1
//		if(crossSection == null) return 1;
//
//		if(aspectRatio == AbstractPlot.ASPECT_RATIO_AUTOMATIC)
//		{
//			// System.out.println("Aspect ratio = " + crossSection.getPhysicalWidth() / crossSection.getPhysicalHeight());
//			return crossSection.getPhysicalWidth() / crossSection.getPhysicalHeight();
//		}
//		else
//		{
//			Dimension screenSize = plotImagePanel.getScrollPaneSize();
//			// System.out.println("Aspect ratio = " + screenSize.getWidth() / screenSize.getHeight());
//			return screenSize.getWidth() / screenSize.getHeight();
//		}
//	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		if (source == plotTypeComboBox)
		{
			plotType = (AreaPlotType)(plotTypeComboBox.getSelectedItem());
			
			if(exposureCompensationSpinner != null)
			{
				// make the exposureCompensationSpinner invisible when it's not needed, i.e. in the phase-only plots
				exposureCompensationSpinner.setEnabled((plotType != AreaPlotType.PHASE_COLOUR) && (plotType != AreaPlotType.PHASE_GRAYSCALE));
			}

			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();
		}
		else if (source == coordinateComboBox)
		{
			coordinate = (TransverseCoordinate)(coordinateComboBox.getSelectedItem());
			
			// Fire an edit event, i.e. make sure plot is re-drawn
			// fireEditEvent();
		}
//		else if (source == aspectRatioComboBox)
//		{
//			aspectRatio = aspectRatioComboBox.getSelectedIndex();
//			
//			// Fire an edit event, i.e. make sure plot is re-drawn
//			fireEditEvent();
//		}
		else if(source == calculateCrossSectionButton)
		{
			// Define thread
			Thread thread = new Thread(new CrossSectionWorker());

			try
			{
				// Start thread
				thread.start();
			}
			catch(Exception exception)
			{
				exception.printStackTrace();
			}
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == numberOfZStepsField)
	    {
	    	numberOfZSteps = ((Number)numberOfZStepsField.getValue()).intValue();
	    	
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();	    	
	    }
	    else if (source == coordinateValueLengthField)
	    {
	    	coordinateValue = coordinateValueLengthField.getLengthInMetres();
	    	
	    	fireEditEvent();
	    }
	    else if (source == deltaZMinLengthField)
	    {
	    	deltaZMin = deltaZMinLengthField.getLengthInMetres();
	    	
	    	fireEditEvent();
	    }
	    else if (source == deltaZMaxLengthField)
	    {
	    	deltaZMax = deltaZMaxLengthField.getLengthInMetres();
	    	
	    	fireEditEvent();
	    }
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		exposureCompensationValue = ((Double)exposureCompensationSpinner.getValue()).doubleValue();

		fireEditEvent();
	}

	public BeamCrossSection getBeam() {
		return beam;
	}

	public void setBeam(BeamCrossSection beam) {
		this.beam = beam;
		calculateCrossSectionButton.setEnabled(beam != null);
	}
	
	
	/************************
	 * Thread functionality *
	 ************************/
	
	/**
	 * Defines a thread which calculates the cross section.
	 * 
	 * @author Johannes
	 */
	private class CrossSectionWorker extends SwingWorker<Void, Void>
	{
		public CrossSectionWorker()
		{
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
			System.out.println("--- Calculating z cross section ---");
			
			// Disable buttons temporarily
			calculateCrossSectionButton.setEnabled(false);
            
            // Set cursor to 'wait' animation
			calculateCrossSectionButton.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
            /*
             * Simulation
             */

			calculateCrossSection();
			
			// We're not returning anything meaningful, but alas we must return something
			return null;
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
            
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();

            // Re-enable buttons
			calculateCrossSectionButton.setEnabled(true);
            
            // Turn off the wait cursor
			calculateCrossSectionButton.getRootPane().setCursor(null);
            
            // Tell console we're done
            System.out.println("--- End of z cross section calculation ---");
        }
	}

}
