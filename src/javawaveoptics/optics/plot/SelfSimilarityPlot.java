package javawaveoptics.optics.plot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.JCProgressBar;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;
import javawaveoptics.ui.ZoomListener;
import javawaveoptics.utility.ImageUtilities;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import library.plot.ScaleTicks;

public class SelfSimilarityPlot extends AbstractPlot implements Serializable, ActionListener, PropertyChangeListener, ItemListener, ZoomListener
{
	private static final long serialVersionUID = -803482715605202530L;

	/*
	 * Fields
	 */
	
	/**
	 * The self-similarity is measured by calculating the "distance" between the beam and the beam, stretched (from the centre) by a factor <i>stretchFactor</i>
	 */
	private double stretchFactor = 1.0;
	
	/**
	 * Only an area around the beam centre of size width*measurementWidthFraction x height*measurementHeightFraction is being compared
	 */
	private double measurementWidthFraction = 0.5;

	/**
	 * Only an area around the beam centre of size width*measurementWidthFraction x height*measurementHeightFraction is being compared
	 */
	private double measurementHeightFraction = 0.5;
	
	public enum IntensityDifferenceMeasureType
	{
		EUCLIDEAN("Euclidean distance"),
		NORMALISED_SQUARED_EUCLIDEAN("Normalised squared Euclidean distance"),
		IMED("Image Euclidean distance (IMED)");
		
		private String description;
		private IntensityDifferenceMeasureType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	private IntensityDifferenceMeasureType intensityDifferenceMeasureType = IntensityDifferenceMeasureType.EUCLIDEAN;
	
	
	private double deltaZMin = -1e-1;
	private double deltaZMax = 1e-1;
	private int numberOfZSteps = 100;
	
	private boolean showGrid = false;
	private double zoomX = 1.0, zoomY = 1.0;

	/**
	 * the current beam cross-section
	 */
	private BeamCrossSection beam;
	
	/**
	 * array of self-similarity measures, one for each z plane
	 */
	private double[] selfSimilarityMeasure;
		
	/*
	 * GUI edit controls
	 */

	private JComboBox<IntensityDifferenceMeasureType> intensityDifferenceMeasureTypeComboBox;
	private transient LengthField deltaZMinLengthField, deltaZMaxLengthField;
	private transient JFormattedTextField stretchFactorField, measurementWidthFractionField, measurementHeightFractionField, numberOfZStepsField;
	private transient JButton calculateSelfSimilarityMeasureButton, saveDataButton, updateButton;
	private transient JCheckBox showGridCheckBox;
	private transient JCProgressBar progressBar;

	public SelfSimilarityPlot(String name, double magnification)
	{
		super(name);
		this.stretchFactor = magnification;
	}
	
	public SelfSimilarityPlot()
	{
		this("Self-similarity", 1.0);
	}
	
	@Override
	public boolean isAspectRatioFixed()
	{
		return false;
	}
	
	public void calculateSelfSimilarityMeasure()
	{
		if(beam == null)
		{
			selfSimilarityMeasure = null;
			saveDataButton.setEnabled(selfSimilarityMeasure != null);

			return;
		}
		
        // Set cursor to 'wait' animation
		calculateSelfSimilarityMeasureButton.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		// show the progress bar, and set its parameters		
		progressBar.setVisible(true);
		progressBar.setMaximum(numberOfZSteps);
		progressBar.setValue(0);
		updateButton.setVisible(true);
		
		// create a space in memory where the self-similarity coefficients will go
		selfSimilarityMeasure = new double[numberOfZSteps];
		
		class SelfSimilarityMeasureWorker implements Runnable
		{
			private BeamCrossSection beam;
			private double[] selfSimilarityMeasure;
			private int zIndex;
			private double deltaZ;

			public SelfSimilarityMeasureWorker(BeamCrossSection beam, double[] selfSimilarityMeasure)
			{
				this.beam = beam;
				this.selfSimilarityMeasure = selfSimilarityMeasure;
			}
			
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

				// now create a copy of this propagated beam...
				BeamCrossSection beamPropagatedAndStretched = new BeamCrossSection(beamPropagated);
				// ...and stretch it by <i>stretchFactor</i>
				beamPropagatedAndStretched.rotateAndZoom(
						0,	// (stretchFactor<0)?180:0,	// rotation angle; rotate by 180 degrees to effect negative stretch factor
						stretchFactor,	// Math.abs(stretchFactor),	// stretch factor
						true
					);
				
				// calculate the start and end indices of the central area over which the beams are being compared
				int iMin = (int)(beam.getWidth()*(1-measurementWidthFraction)/2.);
				int iMax = beam.getWidth() - 1 - (int)(beam.getWidth()*(1-measurementWidthFraction)/2.);
				int jMin = (int)(beam.getHeight()*(1-measurementHeightFraction)/2.);
				int jMax = beam.getHeight() - 1 - (int)(beam.getHeight()*(1-measurementHeightFraction)/2.);

				// calculate similarity measure -- see https://en.wikipedia.org/wiki/Similarity_measure
				switch(intensityDifferenceMeasureType)
				{
				case NORMALISED_SQUARED_EUCLIDEAN:
					selfSimilarityMeasure[zIndex] = ImageUtilities.calculateNormalisedSquaredEuclideanDistance(beamPropagated, beamPropagatedAndStretched, iMin, iMax, jMin, jMax);
					break;
				case IMED:
					selfSimilarityMeasure[zIndex] = ImageUtilities.calculateIMED(beamPropagated, beamPropagatedAndStretched, 1, iMin, iMax, jMin, jMax);
					break;
				case EUCLIDEAN:
				default:
					selfSimilarityMeasure[zIndex] = ImageUtilities.calculateEuclideanDistance(beamPropagated, beamPropagatedAndStretched, iMin, iMax, jMin, jMax);
				}
			}
		}

		// work out how many processors there are...
		int nthreads=Runtime.getRuntime().availableProcessors();
		
		// ... and make an array of worker objects, which tell the threads what to do
		SelfSimilarityMeasureWorker[] workers= new SelfSimilarityMeasureWorker[nthreads];
		for(int i=0; i<nthreads; i++) workers[i]=new SelfSimilarityMeasureWorker(beam, selfSimilarityMeasure);

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
		updateButton.setVisible(false);
		
        // Set cursor to default
		calculateSelfSimilarityMeasureButton.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		// enable the "Save data..." button
		saveDataButton.setEnabled(selfSimilarityMeasure != null);
	}
	

	@Override
	public BufferedImage getPlotImage(BeamCrossSection beam)
	{
		// calculateXZCrossSection(beam);
		setBeam(beam);
		
		return getPlotImageWithoutSettingBeam();
	}
		
	public BufferedImage getPlotImageWithoutSettingBeam()
	{
		if(selfSimilarityMeasure != null)
		{
			double
				minValue, maxValue, value;
			
			int h, v, hOld = 0, vOld = 0; // these variables hold horizontal/vertical positions
			double yMin, yMax, xMin, xMax;
			int i;
			
			// establish the size of the plot
			int width = (int)(plotImagePanel.getViewportWidth() * zoomX);
			int height = (int)(plotImagePanel.getViewportHeight() * zoomY);
			// do something half-sensible if, for some reason, these values are zero
			if(width == 0) width = 640;
			if(height == 0) height = 400;

			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB );
			Graphics2D g = image.createGraphics();

			// find maximum and minimum of data
			minValue = maxValue = selfSimilarityMeasure[0];

			for(i = 0; i<selfSimilarityMeasure.length; i++)
			{
				value = selfSimilarityMeasure[i];

				if(value < minValue) minValue = value;
				else if(value > maxValue) maxValue = value;
			}
			
			// System.out.println("minValue = " + minValue + ", maxValue = " + maxValue);
			
			xMin = deltaZMin;
			xMax = deltaZMax;

			yMin = minValue;
			yMax = maxValue;

			///////////////////////////////////
			// plot a grid in the background //
			///////////////////////////////////

			if(showGrid)
			{
				g.setColor(Color.gray);
				String sy;

				sy = "";

				ScaleTicks
				stx = new ScaleTicks(xMin, xMax, width),
				sty = new ScaleTicks(yMin, yMax, height);

				// the x grid
				double tick = stx.getFirstTick();
				for(i=0; i<stx.getNumberOfTicks(); i++)
				{
					h = (int)((double)(tick - xMin) * (double)width /
							(double)(xMax - xMin));
					g.drawLine(h, 0, h, height-1);
					g.drawString(""+(float)tick, h+2, height-1-2);
					tick += stx.getTickSeparation();
					if(Math.abs(tick) < Math.abs(1e-10*stx.getTickSeparation())) tick = 0.0;
				}

				// the y grid
				tick = sty.getFirstTick();
				for(i=0; i<sty.getNumberOfTicks(); i++)
				{
					v = (int)((height-1) * (yMax-tick)/(yMax - yMin));
					g.drawLine(0, v, width-1, v);
					g.drawString(sy+(float)tick, 2, v-2);
					tick += sty.getTickSeparation();
					if(Math.abs(tick) < Math.abs(1e-10*sty.getTickSeparation())) tick = 0.0;
				}
			}

			////////////////////
			// plot the curve //
			////////////////////

			g.setColor(Color.white);

			value = selfSimilarityMeasure[0];

			h = 0;
			v = (int)((height-1) * (yMax-value)/(yMax - yMin));

			// create image
			for(i=1; i < selfSimilarityMeasure.length; i++)
			{
				value = selfSimilarityMeasure[i];

				hOld = h;
				vOld = v;
				h = (int)((double)i * (double)(width-1) / (double)(selfSimilarityMeasure.length-1));
				v = (int)((height-1) * (yMax-value)/(yMax - yMin));
				
				// System.out.println("(h,v) = (" + h + ", " + v + ")");

				g.drawLine(hOld, vOld, h, v);
			}

			return image;
		}
		
		return null;
	}
		
	@Override
	public JComponent getSettingsPanel()
	{
		if(deltaZMinLengthField == null)
		{
			initialiseWidgets();
		}
		
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		container.add(UIBitsAndBobs.makeRow("Scale factor", stretchFactorField, true));
		
		container.add(UIBitsAndBobs.makeRow("Measure", intensityDifferenceMeasureTypeComboBox, true));
		
		JPanel measurementArea = new JPanel();
		measurementArea.setLayout(new BoxLayout(measurementArea, BoxLayout.Y_AXIS));
		measurementArea.setBorder(UIBitsAndBobs.getTitledBorder("Measurement area"));
		measurementArea.add(new JLabel("central rectangle, "));
		measurementArea.add(UIBitsAndBobs.makeRow("width&nbsp;fraction", measurementWidthFractionField, true));
		measurementArea.add(UIBitsAndBobs.makeRow("height&nbsp;fraction", measurementHeightFractionField, true));
		container.add(measurementArea);

		// container.add(UIBitsAndBobs.makeRow("Plot", plotTypeComboBox));
		container.add(UIBitsAndBobs.makeRow("\u0394z<sub>min</sub>", deltaZMinLengthField, true));
		container.add(UIBitsAndBobs.makeRow("\u0394z<sub>max</sub>", deltaZMaxLengthField, true));
		container.add(UIBitsAndBobs.makeRow("Number of steps", numberOfZStepsField, true));
		
		container.add(showGridCheckBox);

		// container.add(UIBitsAndBobs.makeRow("Aspect ratio", aspectRatioComboBox));
		container.add(progressBar);
		container.add(updateButton);
		container.add(Box.createVerticalGlue());

		return new JScrollPane(container);
	}

	@Override
	public JComponent getControlPanelLeft()
	{
		if(deltaZMinLengthField == null)
		{
			initialiseWidgets();
		}
		
		JPanel controlPanelLeft = new JPanel();
		controlPanelLeft.setLayout(new BoxLayout(controlPanelLeft, BoxLayout.X_AXIS));
		
		return controlPanelLeft;
	}
	
	@Override
	public JComponent getControlPanelRight()
	{
		if(deltaZMinLengthField == null)
		{
			initialiseWidgets();
		}
		
		JPanel controlPanelRight = new JPanel();
		controlPanelRight.setLayout(new BoxLayout(controlPanelRight, BoxLayout.X_AXIS));
		
		controlPanelRight.add(calculateSelfSimilarityMeasureButton);
		controlPanelRight.add(saveDataButton);
		
		return controlPanelRight;
	}

	protected void initialiseWidgets()
	{
		stretchFactorField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		stretchFactorField.setValue(Double.valueOf(stretchFactor));
		
		intensityDifferenceMeasureTypeComboBox = new JComboBox<IntensityDifferenceMeasureType>(IntensityDifferenceMeasureType.values());
		intensityDifferenceMeasureTypeComboBox.addActionListener(this);
		intensityDifferenceMeasureTypeComboBox.setSelectedItem(intensityDifferenceMeasureType);
		
		measurementWidthFractionField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		measurementWidthFractionField.setValue(Double.valueOf(measurementWidthFraction));

		measurementHeightFractionField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		measurementHeightFractionField.setValue(Double.valueOf(measurementHeightFraction));
		
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
		numberOfZStepsField.setValue(Integer.valueOf(numberOfZSteps));

		showGridCheckBox = new JCheckBox("show grid");
		showGridCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		showGridCheckBox.setSelected(showGrid);
		showGridCheckBox.addItemListener(this);
		
		calculateSelfSimilarityMeasureButton = new JButton("(Re)calculate");
		calculateSelfSimilarityMeasureButton.setEnabled(beam != null);
		calculateSelfSimilarityMeasureButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		calculateSelfSimilarityMeasureButton.addActionListener(this);
		
		saveDataButton = new JButton("Save data");
		saveDataButton.setEnabled(selfSimilarityMeasure != null);
		saveDataButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveDataButton.addActionListener(this);
		
		// see http://docs.oracle.com/javase/tutorial/uiswing/components/progress.html
		progressBar = new JCProgressBar(0, 1);
		progressBar.setVisible(false);
		
		updateButton = new JButton("Update plot");
		updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		updateButton.addActionListener(this);
		updateButton.setVisible(false);
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

		if(source == intensityDifferenceMeasureTypeComboBox)
		{
			intensityDifferenceMeasureType = (IntensityDifferenceMeasureType)(intensityDifferenceMeasureTypeComboBox.getSelectedItem());
		}
		else if(source == updateButton)
		{
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();	    			
		}
		else if(source == calculateSelfSimilarityMeasureButton)
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
		else if(source == saveDataButton)
		{
			Frame f = new Frame();
			FileDialog fd = new FileDialog(f, "Save as CSV...", FileDialog.SAVE);
				
			// save
			try
			{
				fd.setVisible(true);
				
				if(fd.getFile() != null)
				{
					FileOutputStream fos = new FileOutputStream(fd.getDirectory() + fd.getFile());
					PrintWriter pw = new PrintWriter(fos);
					
					// ... and the data
					for(int i=0; i<selfSimilarityMeasure.length; i++)
						pw.println("" + selfSimilarityMeasure[i]);
						
					pw.close();
				}
			}
			catch(Exception exception)
			{
				System.out.println("Exception during saving: " + exception);
				// System.exit(0);
			}
			finally
			{
				// free system resources associated with frame and dialog
				f.dispose();
				fd.dispose();
			}
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == stretchFactorField)
	    {
	    	stretchFactor = ((Number)stretchFactorField.getValue()).doubleValue();
	    }
	    else if(source == measurementWidthFractionField)
	    {
	    	measurementWidthFraction = ((Number)measurementWidthFractionField.getValue()).doubleValue();
	    }
	    else if(source == measurementHeightFractionField)
	    {
	    	measurementHeightFraction = ((Number)measurementHeightFractionField.getValue()).doubleValue();
	    }
	    else if (source == numberOfZStepsField)
	    {
	    	numberOfZSteps = ((Number)numberOfZStepsField.getValue()).intValue();
	    }
	    else if (source == deltaZMinLengthField)
	    {
	    	deltaZMin = deltaZMinLengthField.getLengthInMetres();
	    }
	    else if (source == deltaZMaxLengthField)
	    {
	    	deltaZMax = deltaZMaxLengthField.getLengthInMetres();
	    }
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getSource();
		
		if(source.equals(showGridCheckBox))
		{
			showGrid = (e.getStateChange() == ItemEvent.SELECTED);
			
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();
		}
	}

	@Override
	public void setZoomFactors(double zoomX, double zoomY)
	{
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		
		fireEditEvent();
	}

	public BeamCrossSection getBeam() {
		return beam;
	}

	public void setBeam(BeamCrossSection beam) {
		this.beam = beam;
		calculateSelfSimilarityMeasureButton.setEnabled(beam != null);
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
			System.out.println("--- Calculating self-similarity measures ---");
			
			// Disable buttons temporarily
			calculateSelfSimilarityMeasureButton.setEnabled(false);
            
            // Set cursor to 'wait' animation
			calculateSelfSimilarityMeasureButton.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
            /*
             * Simulation
             */

			calculateSelfSimilarityMeasure();
			
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
			calculateSelfSimilarityMeasureButton.setEnabled(true);
            
            // Turn off the wait cursor
			calculateSelfSimilarityMeasureButton.getRootPane().setCursor(null);
            
            // Tell console we're done
            System.out.println("--- End of calculation of self-similarity measures ---");
        }
	}
}
