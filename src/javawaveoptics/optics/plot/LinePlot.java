package javawaveoptics.optics.plot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

import library.maths.Complex;
import library.maths.MyMath;
import library.optics.LightBeamCrossSection1D;
import library.plot.ScaleTicks;

public class LinePlot extends AbstractPlot implements Serializable, ActionListener, PropertyChangeListener, ItemListener, ZoomListener
{
	private static final long serialVersionUID = -6564729852295184468L;

	/*
	 * Fields
	 */
	
	private double coordinate1Value = 0, coordinate2Value = 0;
	private Coordinate
		coordinate1 = Coordinate.Z,
		coordinate2 = Coordinate.Y;
	private double zStart = -1e-1;
	private double zEnd = 1e-1;
	private int numberOfZSteps = 100;
	
	private BeamCrossSection beam;
	private LightBeamCrossSection1D crossSection;
	
	/**
	 * the plot type, i.e. one of the list in AbstractPlot
	 */
	private LinePlotType plotType = LinePlotType.INTENSITY;	
	private boolean logPlot = false, showGrid = false;
	private double zoomX = 1.0, zoomY = 1.0;
	// private int antiAliasingFactor = 2;
	
	/*
	 * GUI edit controls
	 */

	private transient JComboBox<LinePlotType> plotTypeComboBox;
	private transient JComboBox<Coordinate> coordinate1ComboBox, coordinate2ComboBox;	// , aspectRatioComboBox;
	private transient LengthField coordinate1ValueLengthField, coordinate2ValueLengthField, zStartLengthField, zEndLengthField;
	private transient JFormattedTextField numberOfZStepsField;
	private transient JButton calculateCrossSectionButton, saveDataButton;
	private transient JCheckBox logPlotCheckBox, showGridCheckBox;
	private transient JCProgressBar progressBar;
	private transient JPanel zPanel;

	public LinePlot(String name, LinePlotType plotType)
	{
		super(name);
		this.plotType = plotType;
	}
	
	public LinePlot()
	{
		this("Line", LinePlotType.INTENSITY);
	}
	
	@Override
	public boolean isAspectRatioFixed()
	{
		return false;
	}
	
	public void calculateTransverseCrossSection()
	{
		double z = 0;
		double length = 0;
		Coordinate coordinate = Coordinate.X;
		int index = 0;	// index of x or y coordinate
		int steps = 0;
		
		if(coordinate1 == coordinate2)
		{
			System.err.println("LinePlot::calculateTransverseCrossSection: error: coordinates 1 and 2 are the same.");
			System.exit(-1);	// stop!
		}
		
		switch(coordinate1)
		{
			case X:
				coordinate = Coordinate.X;
				index = beam.getI(coordinate1Value);
				if(index < 0) index = 0;
				if(index >= beam.getWidth()) index = beam.getWidth() - 1;
				break;
			case Y:
				coordinate = Coordinate.Y;
				index = beam.getJ(coordinate1Value);
				if(index < 0) index = 0;
				if(index >= beam.getHeight()) index = beam.getHeight() - 1;
				break;
			case Z:
				z = coordinate1Value;
				break;
		}
		
		switch(coordinate2)
		{
			case X:
				coordinate = Coordinate.X;
				index = beam.getI(coordinate2Value);
				if(index < 0) index = 0;
				if(index >= beam.getWidth()) index = beam.getWidth() - 1;
				break;
			case Y:
				coordinate = Coordinate.Y;
				index = beam.getJ(coordinate2Value);
				if(index < 0) index = 0;
				if(index >= beam.getHeight()) index = beam.getHeight() - 1;
				break;
			case Z:
				z = coordinate2Value;
				break;
		}
		
		switch(coordinate)
		{
		case X:
			steps = beam.getHeight();
			length = beam.getPhysicalHeight();
			break;
		case Y:
			steps = beam.getWidth();
			length = beam.getPhysicalWidth();
			break;
		case Z:
			break;
		}
		
		crossSection = new LightBeamCrossSection1D(
				steps,	// length in elements
				length,	// represented physical length
				beam.getWavelength()
			);
		
		// propagate the beam into the correct z plane
		BeamCrossSection beamPropagated = new BeamCrossSection(beam);
		beamPropagated.propagate(z);

		switch(coordinate)
		{
		case X:
			for(int j = 0; j < beam.getHeight(); j++)
			{
				crossSection.setAmplitude(j, beamPropagated.getElement(index, j));
				
				// output all the numbers on the console, which allows creation of a comma-separated file
				// System.out.println(beamPropagated.getElement(index, j)+((j<beam.getHeight()-1)?", ":""));
			}
			break;
		case Y:
			for(int i = 0; i < beam.getWidth(); i++)
			{
				crossSection.setAmplitude(i, beamPropagated.getElement(i, index));

				// output all the numbers on the console, which allows creation of a comma-separated file
				// System.out.println(beamPropagated.getElement(i, index)+((i<beam.getWidth()-1)?", ":""));
			}
			break;
		case Z:
			break;
		}
	}
	
	public void calculateLongitudinalCrossSection()
	{
		// show the progress bar, and set its parameters		
		progressBar.setVisible(true);
		progressBar.setMaximum(numberOfZSteps);
		progressBar.setValue(0);

		// set i and j, the index number in the beam's amplitude matrix that corresponds to the chosen x and y values
		int i = 0, j = 0;
		
		if(coordinate1 == Coordinate.X)
		{
			// coordinate 1 is x, coordinate 2 has to be y
			i = beam.getI(coordinate1Value);
			j = beam.getJ(coordinate2Value);
		}
		else
		{
			// coordinate 1 has to be y, coordinate 2 has to be x
			i = beam.getI(coordinate2Value);
			j = beam.getJ(coordinate1Value);
		}
		
		crossSection = new LightBeamCrossSection1D(
				numberOfZSteps,	// length in elements
				zEnd - zStart,	// represented physical length
				beam.getWavelength()
			);
		
		// make sure that i and j are within range
		if(i < 0) i = 0;
		if(i >= beam.getWidth()) i = beam.getWidth() - 1;
		if(j < 0) j = 0;
		if(j >= beam.getHeight()) j = beam.getHeight() - 1;

		// calculate the step size, deltaZ
		double deltaZ = 0;
		if(numberOfZSteps > 1)
		{
			deltaZ = (zEnd - zStart) / (numberOfZSteps - 1);
		}
		
		// now propagate successively to all the other z values
		for(int s = 0; s < numberOfZSteps; s++)
		{
			// z coordinate of current plane, relative to plane
			double z = zStart + deltaZ * s;
			// System.out.println("z = " + z + " (step " + (s+1) + " out of " + numberOfZSteps + ")");

			// create a copy of the beam cross section, and propagate it to the required z value
			BeamCrossSection beamPropagated = new BeamCrossSection(beam);
			beamPropagated.propagate(z);
			
			crossSection.setAmplitude(s, beamPropagated.getElement(i, j));
			
			// output all the numbers on the console, which allows creation of a comma-separated file
			// System.out.println(beamPropagated.getElement(i, j)+((s<numberOfZSteps-1)?", ":""));
			
			// update progress bar
			progressBar.setValue(s);
		}
		
		// hide the progress bar again		
		progressBar.setVisible(false);
	}
	
	/**
	 * call through CrossSectionWorker
	 */
	public void calculateCrossSection()
	{
		if(beam == null)
		{
			crossSection = null;
			return;
		}
		
        // Set cursor to 'wait' animation; already done in CrossSectionWorker
		// calculateCrossSectionButton.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		if(isLongitudinalPlot())
		{
			calculateLongitudinalCrossSection();
		}
		else
		{
			calculateTransverseCrossSection();
		}
	}
	
	/**
	 * @param c
	 * @return	the relevant double value of the complex number c
	 */
	private double getDoubleValue(Complex c)
	{
		switch(plotType)
		{
		case INTENSITY:
			return c.getAbsSqr();
		case PHASE:
			return c.getArg();
		case IMAGINARY_PART:
			return c.im;
		case REAL_PART:
		default:
			return c.re;
		}
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
		if(crossSection != null)
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
			if(logPlot)
			{
				// for log plot take only values greater than zero into account
			
				// start off with first value greater than zero
				for(i=0; (i<crossSection.getWidth()) && (getDoubleValue(crossSection.getAmplitude(i))<=0); i++);

				if(i<crossSection.getWidth())
				{
					minValue = maxValue = getDoubleValue(crossSection.getAmplitude(i));

					// see whether the remaining values are greater or less
					for(; i<crossSection.getWidth(); i++)
					{
						value = getDoubleValue(crossSection.getAmplitude(i));

						if( value > 0 )
						{
							if(value < minValue) minValue = value;
							else if(value > maxValue) maxValue = value;
						}
					}
				}
				else
				{
					minValue = maxValue = 0;
				}
			}
			else // not log plot
			{
				minValue = maxValue = getDoubleValue(crossSection.getAmplitude(0));

				for(i = 0; i<crossSection.getWidth(); i++)
				{
					value = getDoubleValue(crossSection.getAmplitude(i));
					
					if(value < minValue) minValue = value;
					else if(value > maxValue) maxValue = value;
				}
			}
			
			// System.out.println("minValue = " + minValue + ", maxValue = " + maxValue);
			
			if(isLongitudinalPlot())
			{
				xMin = zStart;
				xMax = zEnd;
			}
			else
			{
				xMin = crossSection.getPhysicalPosition(0);
				xMax = crossSection.getPhysicalPosition(crossSection.getWidth()-1);
			}

			if(logPlot)
			{
				yMin = MyMath.log10(minValue);
				yMax = MyMath.log10(maxValue);
			}
			else
			{
				yMin = minValue;
				yMax = maxValue;
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

			value = getDoubleValue(crossSection.getAmplitude(0));
			if(logPlot)
			{
				if(value > 0) value = MyMath.log10(value);
				else value = yMin;
			}

			h = 0;
			v = (int)((height-1) * (yMax-value)/(yMax - yMin));

			// create image
			for(i=1; i < crossSection.getWidth(); i++)
			{
				value = getDoubleValue(crossSection.getAmplitude(i));
				if(logPlot)
				{
					if(value > 0) value = MyMath.log10(value);
					else value = yMin;
				}

				hOld = h;
				vOld = v;
				h = (int)((double)i * (double)(width-1) / (double)(crossSection.getWidth()-1));
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
		if(plotTypeComboBox == null)
		{
			initialiseWidgets();
		}
		
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(UIBitsAndBobs.makeRow("", coordinate1ComboBox, "=", coordinate1ValueLengthField, true));
		container.add(UIBitsAndBobs.makeRow("", coordinate2ComboBox, "=", coordinate2ValueLengthField, true));
		container.add(zPanel);
		container.add(logPlotCheckBox);
		container.add(showGridCheckBox);
		container.add(progressBar);
		showOrHideLongitudinalComponents();
		
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
		
		controlPanelRight.add(calculateCrossSectionButton);
		
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

		coordinate1ComboBox = new JComboBox<Coordinate>(Coordinate.values());
		coordinate1ComboBox.addActionListener(this);
		coordinate1ComboBox.setSelectedItem(coordinate1);
		coordinate1ComboBox.setMaximumSize(coordinate1ComboBox.getPreferredSize());

		coordinate2ComboBox = new JComboBox<Coordinate>(Coordinate.values());
		coordinate2ComboBox.addActionListener(this);
		coordinate2ComboBox.setSelectedItem(coordinate2);
		coordinate2ComboBox.setMaximumSize(coordinate2ComboBox.getPreferredSize());

		coordinate1ValueLengthField = new LengthField(this);
		coordinate1ValueLengthField.setLengthInMetres(coordinate1Value);

		coordinate2ValueLengthField = new LengthField(this);
		coordinate2ValueLengthField.setLengthInMetres(coordinate2Value);

		zStartLengthField = new LengthField(this);
		zStartLengthField.setMaximumSize(zStartLengthField.getPreferredSize());
		zStartLengthField.setLengthInMetres(zStart);

		zEndLengthField = new LengthField(this);
		zEndLengthField.setMaximumSize(zEndLengthField.getPreferredSize());
		zEndLengthField.setLengthInMetres(zEnd);

		numberOfZStepsField = UIBitsAndBobs.makeIntFormattedTextField(this);
		numberOfZStepsField.setValue(new Integer(numberOfZSteps));

		calculateCrossSectionButton = new JButton("(Re)calculate");
		calculateCrossSectionButton.setEnabled(beam != null);
		calculateCrossSectionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		calculateCrossSectionButton.addActionListener(this);
		
		saveDataButton = new JButton("Save data");
		saveDataButton.setEnabled(beam != null);
		saveDataButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveDataButton.addActionListener(this);
		
		zPanel = new JPanel();
		zPanel.setLayout(new BoxLayout(zPanel, BoxLayout.Y_AXIS));
		zPanel.add(UIBitsAndBobs.makeRow("\u0394z<sub>min</sub>", zStartLengthField, true));
		zPanel.add(UIBitsAndBobs.makeRow("\u0394z<sub>max</sub>", zEndLengthField, true));
		zPanel.add(UIBitsAndBobs.makeRow("Number of steps", numberOfZStepsField, true));
		zPanel.setMaximumSize(zPanel.getPreferredSize());
		
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
	
	/**
	 * @return	true if the plot is in a longitudinal ((x,z) or (y,z)) plane, false if it is in a transverse (x,y) plane
	 */
	private boolean isLongitudinalPlot()
	{
		return (coordinate1 != Coordinate.Z) && (coordinate2 != Coordinate.Z);
	}
	
	private void showOrHideLongitudinalComponents()
	{
		if(zPanel != null)
		{
			zPanel.setVisible(isLongitudinalPlot());
		}
		
		if(calculateCrossSectionButton != null)
		{
			calculateCrossSectionButton.setVisible(isLongitudinalPlot());
		}
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

		if (source == plotTypeComboBox)
		{
			plotType = (LinePlotType)(plotTypeComboBox.getSelectedItem());
			
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();
		}
		else if (source == coordinate1ComboBox)
		{
			coordinate1 = (Coordinate)(coordinate1ComboBox.getSelectedItem());
			
			// make sure the selected coordinates are not the same
			if(coordinate1 == coordinate2)
			{
				if(coordinate1 == Coordinate.Z)
				{
					coordinate2 = Coordinate.Y;
				}
				else
				{
					coordinate2 = Coordinate.Z;
				}
				coordinate2ComboBox.setSelectedItem(coordinate2);
			}
			
			showOrHideLongitudinalComponents();
			
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();
		}
		else if (source == coordinate2ComboBox)
		{
			coordinate2 = (Coordinate)(coordinate2ComboBox.getSelectedItem());
			
			// make sure the selected coordinates are not the same
			if(coordinate1 == coordinate2)
			{
				if(coordinate2 == Coordinate.Z)
				{
					coordinate1 = Coordinate.Y;
				}
				else
				{
					coordinate1 = Coordinate.Z;
				}
				coordinate1ComboBox.setSelectedItem(coordinate1);
			}

			showOrHideLongitudinalComponents();

			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();
		}
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
		else if(source == saveDataButton)
		{
			// TODO
			// System.out.println("Hi!");
			crossSection.saveDataInCSVFormat();
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == numberOfZStepsField)
	    {
	    	numberOfZSteps = ((Number)numberOfZStepsField.getValue()).intValue();
	    	if(numberOfZSteps < 0)
	    	{
	    		numberOfZSteps = 0;
	    		numberOfZStepsField.setText(""+numberOfZSteps);
	    	}
	    	
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();	    	
	    }
	    else if (source == coordinate1ValueLengthField)
	    {
	    	coordinate1Value = coordinate1ValueLengthField.getLengthInMetres();
	    	
	    	if(beam != null)
	    	{
	    		// make sure the coordinate value is in range
	    		if(coordinate1 == Coordinate.X)
	    		{
	    			int i = beam.getI(coordinate1Value);
	    			if(i < 0)
	    			{
	    				coordinate1Value = beam.getX(0);
	    				coordinate1ValueLengthField.setLengthInMetres(coordinate1Value);
	    			}
	    			if(i >= beam.getWidth())
	    			{
	    				coordinate1Value = beam.getX(beam.getWidth()-1);
	    				coordinate1ValueLengthField.setLengthInMetres(coordinate1Value);
	    			}
	    		}
	    		else if(coordinate1 == Coordinate.Y)
	    		{
	    			int j = beam.getJ(coordinate1Value);
	    			if(j < 0)
	    			{
	    				coordinate1Value = beam.getY(0);
	    				coordinate1ValueLengthField.setLengthInMetres(coordinate1Value);
	    			}
	    			if(j >= beam.getHeight())
	    			{
	    				coordinate1Value = beam.getY(beam.getHeight()-1);
	    				coordinate1ValueLengthField.setLengthInMetres(coordinate1Value);
	    			}
	    		}

	    		fireEditEvent();
	    	}
	    }
	    else if (source == coordinate2ValueLengthField)
	    {
	    	coordinate2Value = coordinate2ValueLengthField.getLengthInMetres();

	    	if(beam != null)
	    	{
	    		// make sure the coordinate value is in range
	    		if(coordinate2 == Coordinate.X)
	    		{
	    			int i = beam.getI(coordinate2Value);
	    			if(i < 0)
	    			{
	    				coordinate2Value = beam.getX(0);
	    				coordinate2ValueLengthField.setLengthInMetres(coordinate2Value);
	    			}
	    			if(i >= beam.getWidth())
	    			{
	    				coordinate2Value = beam.getX(beam.getWidth()-1);
	    				coordinate2ValueLengthField.setLengthInMetres(coordinate2Value);
	    			}
	    		}
	    		else if(coordinate2 == Coordinate.Y)
	    		{
	    			int j = beam.getJ(coordinate2Value);
	    			if(j < 0)
	    			{
	    				coordinate2Value = beam.getY(0);
	    				coordinate2ValueLengthField.setLengthInMetres(coordinate2Value);
	    			}
	    			if(j >= beam.getHeight())
	    			{
	    				coordinate2Value = beam.getY(beam.getHeight()-1);
	    				coordinate2ValueLengthField.setLengthInMetres(coordinate2Value);
	    			}
	    		}

	    		fireEditEvent();
	    	}
	    }
	    else if (source == zStartLengthField)
	    {
	    	zStart = zStartLengthField.getLengthInMetres();
	    	
	    	fireEditEvent();
	    }
	    else if (source == zEndLengthField)
	    {
	    	zEnd = zEndLengthField.getLengthInMetres();
	    	
	    	fireEditEvent();
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
			// make the refresh cross-section and save data buttons active
			calculateCrossSectionButton.setEnabled(true);
			saveDataButton.setEnabled(true);

			// if the plot is in a transverse plane
			if(isLongitudinalPlot())
			{
				// set the cross section to null
				// crossSection = null;	
			}
			else
			{
				// it's a transverse plot; calculate the new transverse cross section
				calculateTransverseCrossSection();
			}
		}
		else
		{
			// set the cross section to null
			crossSection = null;
			
			// disable the calculate-cross-section and save-data buttons
			calculateCrossSectionButton.setEnabled(false);
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
			System.out.println("--- Calculating z cross section ---");
			
			// Disable buttons temporarily
			calculateCrossSectionButton.setEnabled(false);
			saveDataButton.setEnabled(false);
            
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

			// Enable buttons again
			calculateCrossSectionButton.setEnabled(true);
			saveDataButton.setEnabled(true);
			
            // Turn off the wait cursor
			calculateCrossSectionButton.getRootPane().setCursor(null);
            
            // Tell console we're done
            System.out.println("--- End of line cross section calculation ---");
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
