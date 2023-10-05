package javawaveoptics.optics.plot;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.UIBitsAndBobs;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AnaglyphPlot extends AbstractPlot implements Serializable, PropertyChangeListener, ChangeListener
{
	private static final long serialVersionUID = 7939859812038682256L;

	/*
	 * Fields
	 */
	
	// Factor defining the pupil radius in terms of the overall physical size
	protected double pupilRadius;
	
	// interpupillary distance (IPD), i.e. eye separation
	protected double interpupillaryDistance;
	
	// position of the centre between the eyes
	protected double pupilsMidpointX, pupilsMidpointY;
	
	// Factor defining the offset between the red and blue images
	protected int percentageOffset;
	
	protected double focussingDistance;

	// exposure compensation factor, 2^(exposure compensation value)
	private double exposureCompensationValue = 0.0;

	// additional propagation distance, Delta z
	private double deltaZ = 0;
	private BeamCrossSection beamPropagated = null;	// beam copy, propagated by Delta z

	/*
	 * GUI edit controls
	 */
	
	private transient LengthField
		pupilRadiusLengthField,
		interpupillaryDistanceLengthField,
		pupilsMidpointXLengthField,
		pupilsMidpointYLengthField,
		focussingDistanceLengthField,
		deltaZLengthField;
	
	private transient JSlider overlapFactorSlider = new JSlider(JSlider.HORIZONTAL, -25, 25, 0);

	private transient JSpinner exposureCompensationSpinner;

	public AnaglyphPlot(String name, double pupilRadius, double interpupillaryDistance, double pupilsMidpointX, double pupilsMidpointY, double focussingDistance)
	{
		super(name);
		
		this.pupilRadius = pupilRadius;
		this.interpupillaryDistance = interpupillaryDistance;
		this.pupilsMidpointX = pupilsMidpointX;
		this.pupilsMidpointY = pupilsMidpointY;
		this.focussingDistance = focussingDistance;
	}

	public AnaglyphPlot(String name, double pupilRadius, double interpupillaryDistance)
	{
		this(
				name,
				pupilRadius,
				interpupillaryDistance,
				0, 0,	// pupils midpoint x, y
				1000	// default focussing distance, essentially infinity
			);
	}

	public AnaglyphPlot()
	{
		this("Anaglyph",
				2e-3,	// range of pupil diameter measured in Winn-et-al-1994 between 2 and 9mm, so radius between 1 and 4.5mm
				63e-3	// mean and median IPD -- see http://www.cl.cam.ac.uk/~nad10/pubs/EI5291A-05.pdf
			);
	}
	
	@Override
	public JComponent getControlPanelLeft()
	{
		if(exposureCompensationSpinner == null)
		{
			initialiseWidgets();
		}
		
		JPanel controlPanelLeft = new JPanel();
		controlPanelLeft.setLayout(new BoxLayout(controlPanelLeft, BoxLayout.X_AXIS));
		controlPanelLeft.add(exposureCompensationSpinner);
		
		return controlPanelLeft;
	}
	
	@Override
	public JComponent getSettingsPanel()
	{
		initialiseWidgets();
		
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(UIBitsAndBobs.makeRow("Pupil radius", pupilRadiusLengthField, true));
		container.add(UIBitsAndBobs.makeRow("Interpupillary distance", interpupillaryDistanceLengthField, true));
		container.add(UIBitsAndBobs.makeRow("Pupils midpoint x", pupilsMidpointXLengthField, true));
		container.add(UIBitsAndBobs.makeRow("Pupils midpoint y", pupilsMidpointYLengthField, true));
		container.add(UIBitsAndBobs.makeRow("\u0394z", deltaZLengthField, true));
		container.add(UIBitsAndBobs.makeRow("Focussing distance", focussingDistanceLengthField, true));
		container.add(UIBitsAndBobs.makeRow("Overlap", overlapFactorSlider, true));
		container.add(Box.createVerticalGlue());
				
		// Add a keystroke listener to this edit box
		overlapFactorSlider.addChangeListener(this);
				
		return container;
	}
	
	protected void initialiseWidgets()
	{
		pupilRadiusLengthField = new LengthField(this);
		pupilRadiusLengthField.setLengthInMetres(pupilRadius);
		
		interpupillaryDistanceLengthField = new LengthField(this);
		interpupillaryDistanceLengthField.setLengthInMetres(interpupillaryDistance);

		pupilsMidpointXLengthField = new LengthField(this);
		pupilsMidpointXLengthField.setLengthInMetres(pupilsMidpointX);

		pupilsMidpointYLengthField = new LengthField(this);
		pupilsMidpointYLengthField.setLengthInMetres(pupilsMidpointY);

		focussingDistanceLengthField = new LengthField(this);
		focussingDistanceLengthField.setLengthInMetres(focussingDistance);

		exposureCompensationSpinner = UIBitsAndBobs.createExposureCompensationSpinner(exposureCompensationValue);
		exposureCompensationSpinner.addChangeListener(this);
		
		deltaZLengthField = new LengthField(this);
	}
	
	@Override
	public BufferedImage getPlotImage(BeamCrossSection beam)
	{
		if(deltaZ == 0.0)
		{
			beamPropagated = beam;
		}
		else
		{
			beamPropagated = new BeamCrossSection(beam);
			beamPropagated.propagate(deltaZ);
		}

		int dataColumns = beamPropagated.getWidth();
		int dataRows = beamPropagated.getHeight();
		
		int offset = (int) (dataColumns * (double) percentageOffset / 100.0);
			
		BufferedImage image = new BufferedImage(dataColumns + 2*Math.abs(offset), dataRows, BufferedImage.TYPE_INT_RGB);
	
		// Set red and blue 'eyes' as copies of the current beam
		BeamCrossSection redEye = new BeamCrossSection(beamPropagated);		// Left eye
		BeamCrossSection blueEye = new BeamCrossSection(beamPropagated);		// Right eye
						
		// Simulate the effects of the pupils on the beams
		redEye.passThroughCircularAperture(pupilRadius, pupilsMidpointX-interpupillaryDistance/2, pupilsMidpointY);
		blueEye.passThroughCircularAperture(pupilRadius, pupilsMidpointX+interpupillaryDistance/2, pupilsMidpointY);
		
		// simulate the effects of the lenses on the beams
		redEye.passThroughLens(focussingDistance, pupilsMidpointX-interpupillaryDistance/2, pupilsMidpointY);
		blueEye.passThroughLens(focussingDistance, pupilsMidpointX+interpupillaryDistance/2, pupilsMidpointY);
		
		// Fourier transform the eyes

		// first, apply phase factors so that the Fourier transform is shifted by half an element in x and y
		// (so that the FT is centred in between elements)
		// redEye.applyCyclicRotationPhaseFactors(-0.5, -0.5);
		// blueEye.applyCyclicRotationPhaseFactors(-0.5, -0.5);

		// Fourier transform it
		redEye.swapQuadrants();
		redEye.inverseFourierTransform();
		blueEye.swapQuadrants();
		blueEye.inverseFourierTransform();
		
		// redEye.swapQuadrants();
		// blueEye.swapQuadrants();

			
		double exposureFactor = Math.pow(2, exposureCompensationValue);

		double maxIntensity = Math.max(redEye.getMaxIntensity(), blueEye.getMaxIntensity()) / exposureFactor;
			
		for(int x = -Math.abs(offset); x < dataColumns + Math.abs(offset); x++)
		{
			for(int y = 0; y < dataRows; y++)
			{
				double redIntensity, blueIntensity;
				
				if((0 < x + offset) && (x + offset < dataColumns))
				{
					redIntensity = redEye.getIntensity(
							(x + offset + dataColumns / 2) % dataColumns,
							(y + dataRows / 2) % dataRows
						);
				}
				else
				{
					redIntensity = 0;
				}
				
				if((0 < x - offset) && (x - offset < dataColumns))
				{
					blueIntensity = blueEye.getIntensity(
							(x - offset + dataColumns / 2) % dataColumns,
							(y + dataRows / 2) % dataRows
					);
				}
				else
				{
					blueIntensity = 0;
				}

				Color colour = new Color((float)Math.min(1.0, (redIntensity / maxIntensity)), 0, (float)Math.min(1.0, (blueIntensity / maxIntensity)));
	
				// Set the pixel; the addition and modulo ensures quadrant swapping
				image.setRGB(x + Math.abs(offset), dataRows - 1 - y, colour.getRGB());
			}
		}
			
		return image;
	}
	
	@Override
	public double getAspectRatio(BeamCrossSection beam)
	{
		int dataColumns = beam.getWidth();
		int offset = (int) (dataColumns * (double) percentageOffset / 100.0);

		return (beam.getPhysicalWidth() * (1 + 2.0 * Math.abs(offset) / (double) dataColumns)) / beam.getPhysicalHeight();
	}
		
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if (source == pupilRadiusLengthField)
	    {
	        pupilRadius = pupilRadiusLengthField.getLengthInMetres();
	    }
	    else if (source == interpupillaryDistanceLengthField)
	    {
	        interpupillaryDistance = interpupillaryDistanceLengthField.getLengthInMetres();
	    }
	    else if (source == pupilsMidpointXLengthField)
	    {
	        pupilsMidpointX = pupilsMidpointXLengthField.getLengthInMetres();
	    }
	    else if (source == pupilsMidpointYLengthField)
	    {
	        pupilsMidpointY = pupilsMidpointYLengthField.getLengthInMetres();
	    }
	    else if (source == deltaZLengthField)
		{
	    	deltaZ = deltaZLengthField.getLengthInMetres();
		}
	    else if (source == focussingDistanceLengthField)
	    {
	        focussingDistance = focussingDistanceLengthField.getLengthInMetres();
	    }
	    
		// Fire an edit event
		fireEditEvent();
	}

	
	@Override
	public void stateChanged(ChangeEvent event)
	{
		Object source = event.getSource();
		
		if(source.equals(overlapFactorSlider))
		{
			setOverlapFactor(overlapFactorSlider.getValue());
			
			// Fire an edit event
			fireEditEvent();
		}
		else if(source.equals(exposureCompensationSpinner))
		{
			exposureCompensationValue = ((Double)exposureCompensationSpinner.getValue()).doubleValue();

			fireEditEvent();
		}
	}

	public double getPupilRadius()
	{
		return pupilRadius;
	}

	public void setPupilRadius(double pupilRadius)
	{
		this.pupilRadius = pupilRadius;
	}

	public double getEyeSeparation()
	{
		return interpupillaryDistance;
	}

	public void setEyeSeparation(double eyeSeparation)
	{
		this.interpupillaryDistance = eyeSeparation;
	}

	public double getPupilsMidpointY() {
		return pupilsMidpointY;
	}

	public void setPupilsMidpointY(double pupilsMidpointY) {
		this.pupilsMidpointY = pupilsMidpointY;
	}

	public double getFocussingDistance() {
		return focussingDistance;
	}

	public void setFocussingDistance(double focussingDistance) {
		this.focussingDistance = focussingDistance;
	}

	public int getOverlapFactor()
	{
		return percentageOffset;
	}

	public void setOverlapFactor(int overlapFactor)
	{
		this.percentageOffset = overlapFactor;
	}
}
