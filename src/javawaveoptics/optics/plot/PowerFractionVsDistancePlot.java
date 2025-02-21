package javawaveoptics.optics.plot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import library.maths.MyMath;
import library.plot.ScaleTicks;

public class PowerFractionVsDistancePlot extends AbstractPlot implements Serializable, ActionListener, PropertyChangeListener, ItemListener, ZoomListener
{
	private static final long serialVersionUID = 3751575058664643688L;

	/*
	 * Fields
	 */
	
	private double xCentre = 0, yCentre = 0, maxDistance = 1e-2;
	private int numberOfDistanceSteps = 100;
	
	private BeamCrossSection beam;
	private double[] powerFractions;
	
	/**
	 * the plot type, i.e. one of the list in AbstractPlot
	 */
	private boolean logPlot = false, showGrid = false;
	private double zoomX = 1.0, zoomY = 1.0;
	// private int antiAliasingFactor = 2;
	
	/*
	 * GUI edit controls
	 */

	private transient JComboBox<LinePlotType> plotTypeComboBox;
	private transient LengthField xCentreLengthField, yCentreLengthField, maxDistanceLengthField;
	private transient JFormattedTextField numberOfDistanceStepsField;
	private transient JButton calculatePowerFractionsButton, saveDataButton;
	private transient JCheckBox logPlotCheckBox, showGridCheckBox;
	private transient JCProgressBar progressBar;

	public PowerFractionVsDistancePlot(String name)
	{
		super(name);
	}
	
	public PowerFractionVsDistancePlot()
	{
		this("Power in radius");
	}
	
	@Override
	public boolean isAspectRatioFixed()
	{
		return false;
	}
	
	public double index2distance(int index) {
		return maxDistance * index / (numberOfDistanceSteps - 1.);
	}
	
	public double distance2index(double distance) {
		return distance * (numberOfDistanceSteps - 1.) / maxDistance;
	}
	
	/**
	 * call through CrossSectionWorker
	 */
	public void calculatePowerFractions()
	{
		if(beam == null)
		{
			powerFractions = null;
			return;
		}

		// show the progress bar, and set its parameters		
		progressBar.setVisible(true);
		progressBar.setMaximum(beam.getWidth());
		progressBar.setValue(0);

		double powers[] = new double[numberOfDistanceSteps];
		for(int index=0; index<numberOfDistanceSteps; index++) { powers[index] = 0.; }
		
		double totalPower = 0;

		// now go through all the pixels
		for(int i=0; i<beam.getWidth(); i++) {
			double deltaX = beam.getX(i) - xCentre;
			double deltaX2 = deltaX * deltaX;
			
			for(int j=0; j<beam.getHeight(); j++) {
				double deltaY = beam.getY(j) - yCentre;
				double deltaY2 = deltaY * deltaY;
				
				double distance = Math.sqrt(deltaX2 + deltaY2);
				int indexOfDistance = // Math.min(
						(int)Math.ceil(distance2index(distance));
						// , numberOfDistanceSteps-1);
				double power = beam.getIntensity(i, j);

				for(int index=indexOfDistance; index<numberOfDistanceSteps; index++) {
					powers[index] += power;
				}
				totalPower += power;
			}
			
			// update progress bar
			progressBar.setValue(i);
		}
		
		// now calculate the power fractions from the powers
		powerFractions = new double[numberOfDistanceSteps];
		for(int index=0; index<numberOfDistanceSteps; index++) {
			powerFractions[index] = powers[index] / totalPower;
		}

		// hide the progress bar again		
		progressBar.setVisible(false);
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
		if(powerFractions != null)
		{
			double
				minValue, maxValue, value;
			
			int h, v, hOld = 0, vOld = 0; // these variables hold horizontal/vertical positions
			double powerFractionMin, powerFractionMax, distanceMin, distanceMax;
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
			minValue = (logPlot?1e-4:0);
			maxValue = 1;
			
			// System.out.println("minValue = " + minValue + ", maxValue = " + maxValue);
			
			distanceMin = 0;
			distanceMax = maxDistance;

			if(logPlot)
			{
				powerFractionMin = MyMath.log10(minValue);
				powerFractionMax = MyMath.log10(maxValue);
			}
			else
			{
				powerFractionMin = minValue;
				powerFractionMax = maxValue;
			}

			///////////////////////////////////
			// plot a grid in the background //
			///////////////////////////////////

			if(showGrid)
			{
				g.setColor(Color.gray);
				String sy;

				if(logPlot) sy = "1E";
				else sy = "";

				ScaleTicks
				stx = new ScaleTicks(distanceMin, distanceMax, width),
				sty = new ScaleTicks(powerFractionMin, powerFractionMax, height);

				// the x grid
				double tick = stx.getFirstTick();
				for(i=0; i<stx.getNumberOfTicks(); i++)
				{
					h = (int)((double)(tick - distanceMin) * (double)width /
							(double)(distanceMax - distanceMin));
					g.drawLine(h, 0, h, height-1);
					g.drawString(""+(float)tick, h+2, height-1-2);
					tick += stx.getTickSeparation();
					if(Math.abs(tick) < Math.abs(1e-10*stx.getTickSeparation())) tick = 0.0;
				}

				// the y grid
				tick = sty.getFirstTick();
				for(i=0; i<sty.getNumberOfTicks(); i++)
				{
					v = (int)((height-1) * (powerFractionMax-tick)/(powerFractionMax - powerFractionMin));
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

			value = powerFractions[0];
			if(logPlot)
			{
				if(value > 0) value = MyMath.log10(value);
				else value = powerFractionMin;
			}

			h = 0;
			v = (int)((height-1) * (powerFractionMax-value)/(powerFractionMax - powerFractionMin));

			// create image
			for(i=1; i < numberOfDistanceSteps; i++)
			{
				value = powerFractions[i];
				if(logPlot)
				{
					if(value > 0) value = MyMath.log10(value);
					else value = powerFractionMin;
				}

				hOld = h;
				vOld = v;
				h = (int)((double)i * (double)(width-1) / (double)(numberOfDistanceSteps-1));
				v = (int)((height-1) * (powerFractionMax-value)/(powerFractionMax - powerFractionMin));
				
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
		if(plotTypeComboBox == null)
		{
			initialiseWidgets();
		}
		
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		container.add(UIBitsAndBobs.makeHTMLLabel("Power fraction (ordinate) contained within"));
		container.add(UIBitsAndBobs.makeHTMLLabel("a circle of radius between 0 and max. radius"));
		container.add(UIBitsAndBobs.makeHTMLLabel("(abscissa) of the centre."));
		
		JPanel centrePanel = new JPanel();
		centrePanel.setLayout(new BoxLayout(centrePanel, BoxLayout.Y_AXIS));
		centrePanel.setBorder(UIBitsAndBobs.getTitledBorder("Centre"));
		centrePanel.add(UIBitsAndBobs.makeRow("<i>x</i>", xCentreLengthField, true));
		centrePanel.add(UIBitsAndBobs.makeRow("<i>y</i>", yCentreLengthField, true));
		container.add(centrePanel);


		container.add(UIBitsAndBobs.makeRow("Max. radius", maxDistanceLengthField, true));
		container.add(UIBitsAndBobs.makeRow("Number of steps", numberOfDistanceStepsField, true));
		
		container.add(logPlotCheckBox);
		container.add(showGridCheckBox);
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
		
		controlPanelRight.add(calculatePowerFractionsButton);
		
		controlPanelRight.add(saveDataButton);
		
		return controlPanelRight;
	}

	protected void initialiseWidgets()
	{
		plotTypeComboBox = new JComboBox<LinePlotType>(LinePlotType.values());
		plotTypeComboBox.setToolTipText("Part of the complex field to plot");
		plotTypeComboBox.addActionListener(this);
		plotTypeComboBox.setSelectedItem(LinePlotType.INTENSITY);
		plotTypeComboBox.setMaximumSize(plotTypeComboBox.getPreferredSize());

		xCentreLengthField = new LengthField(this);
		xCentreLengthField.setLengthInMetres(xCentre);

		yCentreLengthField = new LengthField(this);
		yCentreLengthField.setLengthInMetres(yCentre);

		maxDistanceLengthField = new LengthField(this);
		maxDistanceLengthField.setMaximumSize(maxDistanceLengthField.getPreferredSize());
		maxDistanceLengthField.setLengthInMetres(maxDistance);

		numberOfDistanceStepsField = UIBitsAndBobs.makeIntFormattedTextField(this);
		numberOfDistanceStepsField.setValue(Integer.valueOf(numberOfDistanceSteps));

		calculatePowerFractionsButton = new JButton("(Re)calculate");
		calculatePowerFractionsButton.setEnabled(beam != null);
		calculatePowerFractionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		calculatePowerFractionsButton.addActionListener(this);
		
		saveDataButton = new JButton("Save data");
		saveDataButton.setEnabled(beam != null);
		saveDataButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveDataButton.addActionListener(this);
		
		logPlotCheckBox = new JCheckBox("log plot");
		logPlotCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		logPlotCheckBox.setSelected(logPlot);
		logPlotCheckBox.addItemListener(this);

		showGridCheckBox = new JCheckBox("show grid");
		showGridCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		showGridCheckBox.setSelected(showGrid);
		showGridCheckBox.addItemListener(this);
		
		// see http://docs.oracle.com/javase/tutorial/uiswing/components/progress.html
		progressBar = new JCProgressBar(0, 1);
		progressBar.setVisible(false);
	}
	
	@Override
	public boolean getShowFitButton()
	{
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		if(source == calculatePowerFractionsButton)
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
					for(int i=0; i<numberOfDistanceSteps; i++)
						pw.println(index2distance(i) + ", " + powerFractions[i]);	// TODO also save distances, not just power fractions
					pw.close();
					fos.close(); // necessary?
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
	    
	    if (source == numberOfDistanceStepsField)
	    {
	    	numberOfDistanceSteps = ((Number)numberOfDistanceStepsField.getValue()).intValue();
	    	if(numberOfDistanceSteps < 0)
	    	{
	    		numberOfDistanceSteps = 0;
	    		numberOfDistanceStepsField.setText(""+numberOfDistanceSteps);
	    	}
	    	
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();	    	
	    }
	    else if (source == xCentreLengthField)
	    {
	    	xCentre = xCentreLengthField.getLengthInMetres();
	    	
	    	if(beam != null)
	    	{
	    		// make sure the coordinate value is in range
	    		if(xCentre < -0.5*beam.getPhysicalWidth())
	    		{
	    			xCentre = -0.5*beam.getPhysicalWidth();
	    			xCentreLengthField.setLengthInMetres(xCentre);
	    		}
	    		if(xCentre >= 0.5*beam.getPhysicalWidth())
	    		{
	    			xCentre = 0.5*beam.getPhysicalWidth();
	    			xCentreLengthField.setLengthInMetres(xCentre);
	    		}

	    		fireEditEvent();
	    	}
	    }
	    else if (source == yCentreLengthField)
	    {
	    	yCentre = yCentreLengthField.getLengthInMetres();
	    	
	    	if(beam != null)
	    	{
	    		// make sure the coordinate value is in range
	    		if(yCentre < -0.5*beam.getPhysicalHeight())
	    		{
	    			yCentre = -0.5*beam.getPhysicalHeight();
	    			yCentreLengthField.setLengthInMetres(yCentre);
	    		}
	    		if(yCentre >= 0.5*beam.getPhysicalHeight())
	    		{
	    			yCentre = 0.5*beam.getPhysicalHeight();
	    			yCentreLengthField.setLengthInMetres(yCentre);
	    		}

	    		fireEditEvent();
	    	}
	    }
	    else if (source == maxDistanceLengthField)
	    {
	    	maxDistance = maxDistanceLengthField.getLengthInMetres();
	    	
	    	if(beam != null)
	    	{
	    		// make sure the value is in range
	    		if(maxDistance < 0)
	    		{
	    			maxDistance = 0;
	    			maxDistanceLengthField.setLengthInMetres(maxDistance);
	    		}
	    		if(maxDistance >= beam.getPhysicalDiagonal())
	    		{
	    			maxDistance = beam.getPhysicalDiagonal();
	    			maxDistanceLengthField.setLengthInMetres(maxDistance);
	    		}

	    		fireEditEvent();
	    	}
	    }
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getSource();
		
		if(source.equals(logPlotCheckBox))
		{
			logPlot = (e.getStateChange() == ItemEvent.SELECTED);
			
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();
		}
		else if(source.equals(showGridCheckBox))
		{
			showGrid = (e.getStateChange() == ItemEvent.SELECTED);
			
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();
		}
	}

	@Override
	public void componentResized(ComponentEvent arg0)
	{
		// just re-draw the image
		fireEditEvent();
	}

	public BeamCrossSection getBeam() {
		return beam;
	}

	public void setBeam(BeamCrossSection beam) {
		this.beam = beam;
		
		if(beam != null)
		{
			// make the calculate-power-fractions and save-data buttons active
			calculatePowerFractionsButton.setEnabled(true);
			saveDataButton.setEnabled(true);

			calculatePowerFractions();
		}
		else
		{
			// set the array of power fractions to null
			powerFractions = null;
			
			// disable the calculate-power-fractions and save-data buttons
			calculatePowerFractionsButton.setEnabled(false);
			saveDataButton.setEnabled(false);
		}
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
			System.out.println("--- Calculating power fractions ---");
			
			// Disable buttons temporarily
			calculatePowerFractionsButton.setEnabled(false);
			saveDataButton.setEnabled(false);
            
            // Set cursor to 'wait' animation
			calculatePowerFractionsButton.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
            /*
             * Simulation
             */

			calculatePowerFractions();
			
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

			// Enable buttons again
			calculatePowerFractionsButton.setEnabled(true);
			saveDataButton.setEnabled(true);
			
            // Turn off the wait cursor
			calculatePowerFractionsButton.getRootPane().setCursor(null);
            
            // Tell console we're done
            System.out.println("--- End of power-fractions calculation ---");
        }
	}


	@Override
	public void setZoomFactors(double zoomX, double zoomY)
	{
		this.zoomX = zoomX;
		this.zoomY = zoomY;
		
		fireEditEvent();
	}
}
