package javawaveoptics.optics.plot;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.UIBitsAndBobs;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FourierTransformPlot extends AbstractPlot implements Serializable, ItemListener, ActionListener, ChangeListener
{
	private static final long serialVersionUID = 4630975198007156522L;

	/*
	 * Fields
	 */

	/**
	 * the plot type, i.e. one of the list in AbstractPlot
	 */
	private AreaPlotType plotType = AreaPlotType.INTENSITY;	
	
	// exposure compensation factor, 2^(exposure compensation value)
	private double exposureCompensationValue = 0.0;
	
	boolean halfElementShift;	// zero frequency is centred on an element
	boolean quadrantSwapping;	// quadrant swapping?

	/*
	 * GUI edit controls
	 */

	private transient JComboBox<AreaPlotType> plotTypeComboBox;
	private transient JSpinner exposureCompensationSpinner;

	private boolean showHalfElementShiftCheckbox, showQuadrantSwappingCheckbox;
	
	private transient JCheckBox halfElementShiftCheckBox;
	private transient JCheckBox quadrantSwappingCheckBox;
	
	/**
	 * Creates a Fourier transform plot of size [width, height] pixels, and with a specified user-friendly
	 * name.
	 * 
	 */
	public FourierTransformPlot(String name, boolean quadrantSwapping, boolean halfElementShift, AreaPlotType plotType, boolean showHalfElementShiftCheckbox, boolean showQuadrantSwappingCheckbox)
	{
		super(name);
		
		this.quadrantSwapping = quadrantSwapping;
		this.halfElementShift = halfElementShift;
		this.plotType = plotType;
		this.showHalfElementShiftCheckbox = showHalfElementShiftCheckbox;
		this.showQuadrantSwappingCheckbox = showQuadrantSwappingCheckbox;
	}
	
	/**
	 * Creates a Fourier transform plot of size [width, height] pixels with a default name.
	 * 
	 */
	public FourierTransformPlot()
	{
		this("Fourier spectrum", true, false, AreaPlotType.INTENSITY, false, false);
	}

	/**
	 * Returns an image panel containing the Fourier transform plot.
	 */
	@Override
	public BufferedImage getPlotImage(BeamCrossSection beam)
	{
		int dataColumns = beam.getWidth();
		int dataRows = beam.getHeight();
			
		BufferedImage image = new BufferedImage(dataColumns, dataRows, BufferedImage.TYPE_INT_RGB);

		// make a copy of the current beam
		BeamCrossSection beamCopy = new BeamCrossSection(beam);
		
		if(halfElementShift)
		{
			// first, apply phase factors so that the Fourier transform is shifted by half an element in x and y
			// (so that the FT is centred in between elements)
			beamCopy.applyCyclicRotationPhaseFactors(-0.5, -0.5);
		}

		// Fourier transform it
		beamCopy.swapQuadrants();
		beamCopy.inverseFourierTransform();
		
		if(quadrantSwapping)
		{
			beamCopy.swapQuadrants();
		}

		double exposureFactor = Math.pow(2, exposureCompensationValue);
		
		if(plotType == AreaPlotType.INTENSITY)
		{
			double maxIntensity = beamCopy.getMaxIntensity() / exposureFactor;
			
			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					double intensity = beamCopy.getIntensity(x, y);
					
					image.setRGB(x, dataRows - 1 - y, Color.HSBtoRGB(1, 0, (float)Math.min(1.0, (intensity/maxIntensity))));
				}
			}
		}
		else if(plotType == AreaPlotType.LOG_INTENSITY)
		{
			double
				logMaxIntensity = Math.log10(beamCopy.getMaxIntensity() / exposureFactor),
				logMinIntensity = Math.log10(beamCopy.getMinIntensity() / exposureFactor);
			if(logMaxIntensity - logMinIntensity > AbstractPlot.maxLogIntensityDecades)
			{
				// too much dynamic range
				logMinIntensity = logMaxIntensity - AbstractPlot.maxLogIntensityDecades;
			}
			
			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					double logIntensity = Math.log10(beamCopy.getIntensity(x, y));
					
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
					double phase = Math.atan2(beamCopy.getElementIm(x, y), beamCopy.getElementRe(x, y)) / (2 * Math.PI) + 0.5;

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
					double phase = Math.atan2(beamCopy.getElementIm(x, y), beamCopy.getElementRe(x, y)) / (2 * Math.PI) + 0.5;

					image.setRGB(x, dataRows - 1 - y, Color.HSBtoRGB(0, 0, (float)phase));
				}
			}
		}
		else if(plotType == AreaPlotType.PHASE_AND_INTENSITY)
		{
			double maxIntensity = beamCopy.getMaxIntensity() / exposureFactor;

			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					float hue = (float) (Math.atan2(beamCopy.getElementIm(x, y), beamCopy.getElementRe(x, y)) / (2 * Math.PI) + 0.5);
					float brightness = (float)Math.min(1.0, (beamCopy.getIntensity(x, y) / maxIntensity));
					
					image.setRGB(x, dataRows - 1 - y, Color.HSBtoRGB(hue, 1, brightness));
				}
			}
		}
		else if(plotType == AreaPlotType.REAL_PART)
		{
			double maxField = beamCopy.getMaxAbsRe() / Math.sqrt(exposureFactor);
			
			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					double re = beamCopy.getElementRe(x, y);
					
					image.setRGB(x, dataRows - 1 - y, Color.HSBtoRGB(((re>0)?(float)0.5:0), 1, (float)Math.min(1.0, (Math.abs(re)/maxField))));
				}
			}
		}
		else if(plotType == AreaPlotType.IMAGINARY_PART)
		{
			double maxField = beamCopy.getMaxAbsIm() / Math.sqrt(exposureFactor);
			
			for(int x = 0; x < dataColumns; x++)
			{
				for(int y = 0; y < dataRows; y++)
				{
					double im = beamCopy.getElementIm(x, y);
					
					image.setRGB(x, dataRows - 1 - y, Color.HSBtoRGB(((im>0)?(float)0.5:0), 1, (float)Math.min(1.0, (Math.abs(im)/maxField))));
				}
			}
		}

//		double maxIntensity = beamCopy.getMaxIntensity();
//			
//		for(int x = 0; x < dataColumns; x++)
//		{
//			for(int y = 0; y < dataRows; y++)
//			{
//				float intensity = (float) (beamCopy.getIntensity(x, y) / maxIntensity);
//				Color colour = new Color(intensity, intensity, intensity);
//				
//				// This creates a quadrant-unswapped image
//				// image.setRGB(x, dataRows - 1 - y, colour.getRGB());
//									
//				// Set the pixel; the addition and modulo ensures quadrant swapping
//				// image.setRGB((x + dataColumns / 2) % dataColumns, (y + dataRows / 2) % dataRows, colour.getRGB());
//				image.setRGB(x, dataRows - 1 - y, colour.getRGB());
//			}
//		}
		
		return image;
	}
	
	@Override
	public double getAspectRatio(BeamCrossSection beam)
	{
		// note that the aspect ratio of the FT is always 1 here, as the element spacing in x and y is the same!
		return 1;	// /super.getAspectRatio(beam);
	}

//	@Override
//	public JComponent getPlotEditPanel()
//	{
//		initialiseWidgets();
//		
//		JPanel container = new JPanel();
//		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
//
//		if(showHalfElementShiftCheckbox)
//		{
//			container.add(quadrantSwappingCheckBox);
//		}
//		
//		if(showQuadrantSwappingCheckbox)
//		{
//			container.add(halfElementShiftCheckBox);
//		}
//
//		// container.add(UIBitsAndBobs.makeRow("Plot", plotTypeComboBox));
//		container.add(plotTypeComboBox);
//
//		return container;
//	}
	
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
		
		if(showHalfElementShiftCheckbox)
		{
			controlPanelRight.add(quadrantSwappingCheckBox);
		}
			
		if(showQuadrantSwappingCheckbox)
		{
			controlPanelRight.add(halfElementShiftCheckBox);
		}
		
		return controlPanelRight;
	}
	
	@Override
	public boolean getShowSettingsButton()
	{
		return false;
	}
	
	protected void initialiseWidgets()
	{
		quadrantSwappingCheckBox = new JCheckBox("Quadrant swapping");
		quadrantSwappingCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		quadrantSwappingCheckBox.setSelected(quadrantSwapping);
		quadrantSwappingCheckBox.addItemListener(this);	
		
		halfElementShiftCheckBox = new JCheckBox("Half-element shift");
		halfElementShiftCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		halfElementShiftCheckBox.setSelected(halfElementShift);
		halfElementShiftCheckBox.addItemListener(this);
		
		plotTypeComboBox = new JComboBox<AreaPlotType>(AreaPlotType.values());
		plotTypeComboBox.setToolTipText("Part of the complex field to plot");
		plotTypeComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		plotTypeComboBox.addActionListener(this);
		plotTypeComboBox.setSelectedItem(plotType);
		plotTypeComboBox.setMaximumSize(plotTypeComboBox.getPreferredSize());

		exposureCompensationSpinner = UIBitsAndBobs.createExposureCompensationSpinner(exposureCompensationValue);
		exposureCompensationSpinner.addChangeListener(this);
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getSource();
		
		if(source.equals(quadrantSwappingCheckBox))
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				quadrantSwapping = true;
			}
			else
			{
				quadrantSwapping = false;
			}
			
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();
		}
		else if(source.equals(halfElementShiftCheckBox))
		{
			if(e.getStateChange() == ItemEvent.SELECTED)
			{
				halfElementShift = true;
			}
			else
			{
				halfElementShift = false;
			}
			
			// Fire an edit event, i.e. make sure plot is re-drawn
			fireEditEvent();
		}
	}
	
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
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		exposureCompensationValue = ((Double)exposureCompensationSpinner.getValue()).doubleValue();

		fireEditEvent();
	}
}
