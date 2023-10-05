package javawaveoptics.optics.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.optics.lightsource.*;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.PowersOf2ComboBox;
import javawaveoptics.ui.UIBitsAndBobs;

/**
 * Defines a beam in the program. This object outputs a user-defined beam which can then be further
 * manipulated by other components.
 * 
 * This class provides a graphical user interface for choosing from a list the desired type of beam.
 * 
 * This object takes no inputs. If it is given an input, it does nothing with it; it outputs only the beam
 * specified by the user in the edit panel. The UI should not allow this object to be attached as an output
 * of some other component, as it does not make sense to do so.
 * 
 * @author Sean
 * @author Johannes
 */
public class LightSource extends AbstractLightSourceComponent implements Serializable, ActionListener, PropertyChangeListener
{
	private static final long serialVersionUID = -8019351688260664912L;
	
	public static final int
		BEAM_GAUSSIAN = 0,
		BEAM_HERMITE_GAUSSIAN = 1,
		BEAM_LAGUERRE_GAUSSIAN = 2,
		BEAM_BESSEL = 3,
		BEAM_UNIFORM_PLANE_WAVE = 4,
		BEAM_FROM_BITMAP = 5;

	
	/*
	 * Fields
	 */
	
	// List of light sources
	private ArrayList<AbstractLightSource> lightSources;
	
	// Selected light source
	// private AbstractLightSource selectedLightSource;
	
	// Physical dimensions
	private double
		physicalWidth,
		physicalHeight;
	
	// Beam wavelength
	private double wavelength;
	
	// Beam cross section matrix representation dimensions
	private int
		amplitudeMatrixColumns,
		amplitudeMatrixRows;
	
	private int beamType = BEAM_GAUSSIAN;
	
	/*
	 * GUI edit controls
	 */
	
	// Physical width edit control
	private transient LengthField
//		representedSideLengthField,
		physicalWidthLengthField,
		physicalHeightLengthField,
		waveLengthField;
	
	private transient PowersOf2ComboBox
		amplitudeMatrixColumnsComboBox,
		amplitudeMatrixRowsComboBox;
	
//	private transient ArraySizesComboBox
//		amplitudeMatrixArraySizeComboBox;
	
	
	// Panel holding the beam type combo box
	private transient JPanel beamEditPanel;
	private transient JComboBox beamTypeComboBox;
	
	/**
	 * Constructor. Defines a light source object in terms of its physical properties and the intended
	 * resolution of the cross sectional representation.
	 * 
	 * @param name				User-friendly name
	 * @param physicalWidth		Physical width of the cross section (in metres)
	 * @param physicalHeight	Physical height of the cross section (in metres)
	 * @param wavelength		Wavelength of the beam (in metres)
	 * @param plotWidth			Width of the cross sectional representation (in pixels)
	 * @param plotHeight		Height of the cross sectional representation (in pixels)
	 */
	public LightSource(String name, double physicalWidth, double physicalHeight, double wavelength, int amplitudeMatrixColumns, int amplitudeMatrixRows, int beamType)
	{
		super(name);
		
		this.physicalWidth = physicalWidth;
		this.physicalHeight = physicalHeight;
		this.wavelength = wavelength;
		this.amplitudeMatrixColumns = amplitudeMatrixColumns;
		this.amplitudeMatrixRows = amplitudeMatrixRows;
		this.beamType = beamType;
		
		// Set up the light sources
		initialiseLightSources();
	}
	
	/**
	 * Null constructor. Creates a light source with default values. This requires no
	 * parameters.
	 */
	public LightSource()
	{
		this(	"Light source",
				1e-2, 1e-2,	// 1cm x 1cm
				632.8e-9,	// He-Ne
				256, 256,	// amplitude-matrix size
				BEAM_GAUSSIAN
			);
	}
	
	@Override
	public String getComponentTypeName()
	{
		return "Light source";
	}
	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
				
//		editPanel.add(UIBitsAndBobs.makeRow("Amplitude matrix dimensions", amplitudeMatrixArraySizeComboBox));
		editPanel.add(UIBitsAndBobs.makeRow("Amplitude matrix size", amplitudeMatrixColumnsComboBox, " \u2a09 ", amplitudeMatrixRowsComboBox, "", true));
//		editPanel.add(UIBitsAndBobs.makeRow("Side length of represented square", representedSideLengthField));
		editPanel.add(UIBitsAndBobs.makeRow("Represented physical area", physicalWidthLengthField, " \u2a09 ", physicalHeightLengthField, "", true));
		editPanel.add(UIBitsAndBobs.makeRow("Wavelength", waveLengthField, true));
	
		
		/*
		 * Beam type drop down box
		 */
		
		beamTypeComboBox.addActionListener(this);
		// beamTypeComboBox.setActionCommand("Beam Selected");
		
		// Create beam and its edit controls
		drawBeamEditControls();
		
		editPanel.add(UIBitsAndBobs.makeRow("Beam type", beamTypeComboBox, true));
		editPanel.add(beamEditPanel);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
//		amplitudeMatrixArraySizeComboBox = new ArraySizesComboBox();
//		amplitudeMatrixArraySizeComboBox.setValue(amplitudeMatrixColumns);
//		amplitudeMatrixArraySizeComboBox.addActionListener(this);

		amplitudeMatrixColumnsComboBox = new PowersOf2ComboBox();
		amplitudeMatrixColumnsComboBox.setValue(amplitudeMatrixColumns);
		amplitudeMatrixColumnsComboBox.addActionListener(this);

		amplitudeMatrixRowsComboBox = new PowersOf2ComboBox();
		amplitudeMatrixRowsComboBox.setValue(amplitudeMatrixRows);
		amplitudeMatrixRowsComboBox.addActionListener(this);

//		representedSideLengthField = new LengthField(this);
//		representedSideLengthField.setLengthInMetres(physicalWidth);

		physicalWidthLengthField = new LengthField(this);
		physicalHeightLengthField = new LengthField(this);

		// don't set the values until both the width and the height length fields exist
		physicalWidthLengthField.setLengthInMetres(physicalWidth);
		physicalHeightLengthField.setLengthInMetres(physicalHeight);

		waveLengthField = new LengthField(this);
		waveLengthField.setLengthInMetres(wavelength);
		
		// Beam edit controls
		beamEditPanel = new JPanel();		// The edit controls for the selected beam type
		beamTypeComboBox = new JComboBox();	// The combo box for selecting a beam type
		
		// Set the combo box model from list of light sources
		beamTypeComboBox.setModel(new DefaultComboBoxModel(lightSources.toArray()));
		
		// Set beam selection
		beamTypeComboBox.setSelectedIndex(beamType);
		// beamTypeComboBox.setSelectedItem(selectedLightSource);
	}

	@Override
	public void readWidgets()
	{
		super.readWidgets();

		if(amplitudeMatrixColumnsComboBox != null) amplitudeMatrixColumns = amplitudeMatrixColumnsComboBox.getValue();
		if(amplitudeMatrixRowsComboBox != null) amplitudeMatrixRows = amplitudeMatrixRowsComboBox.getValue();
		if(physicalWidthLengthField != null) physicalWidth = physicalWidthLengthField.getLengthInMetres();
		if(physicalHeightLengthField != null) physicalHeight = physicalHeightLengthField.getLengthInMetres();
		if(waveLengthField != null) wavelength = waveLengthField.getLengthInMetres();
    	if(beamTypeComboBox != null) beamType = beamTypeComboBox.getSelectedIndex();
    	getSelectedLightSource().readWidgets();
	}
		
	private transient boolean inPropertyChange = false;
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		if(!inPropertyChange)
		{
			inPropertyChange = true;

			Object source = e.getSource();

//			if (source == representedSideLengthField)
//			{
//				physicalWidth = representedSideLengthField.getLengthInMetres();
//				physicalHeight = representedSideLengthField.getLengthInMetres();
//			}
			if (source == physicalWidthLengthField)
			{
				physicalWidth = physicalWidthLengthField.getLengthInMetres();

				adjustPhysicalHeight();
			}
			else if (source == physicalHeightLengthField)
			{
				physicalHeight = physicalHeightLengthField.getLengthInMetres();

				adjustPhysicalWidth();
			}
			else if (source == waveLengthField)
			{
				wavelength = waveLengthField.getLengthInMetres();
			}

			// Fire an edit panel event
			if(editListener != null) editListener.editMade();

			inPropertyChange = false;
		}
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(!inPropertyChange)
		{
			inPropertyChange = true;

			Object source = event.getSource();
			
//			if (source == amplitudeMatrixArraySizeComboBox)
//			{
//				amplitudeMatrixColumns = amplitudeMatrixArraySizeComboBox.getValue();
//				amplitudeMatrixRows = amplitudeMatrixArraySizeComboBox.getValue();
//			}
			if (source == amplitudeMatrixColumnsComboBox)
			{
				amplitudeMatrixColumns = amplitudeMatrixColumnsComboBox.getValue();

				adjustPhysicalWidth();
			}
			else if (source == amplitudeMatrixRowsComboBox)
			{
				amplitudeMatrixRows = amplitudeMatrixRowsComboBox.getValue();
			
				adjustPhysicalHeight();
			}
		    else if (source == beamTypeComboBox)
		    {
		    	// A new beam type has been selected
		    	
		    	beamType = beamTypeComboBox.getSelectedIndex();
		    	
		    	// selectedLightSource = (AbstractLightSource) beamTypeComboBox.getSelectedItem();
		    		
		    	drawBeamEditControls();
		    }
			
			inPropertyChange = false;
		}
	}
	
	/**
	 * adjust the physical height so that separation between neighbouring elements is the same in x and y directions
	 */
	public void adjustPhysicalHeight()
	{
		physicalHeight = physicalWidth * amplitudeMatrixRows / amplitudeMatrixColumns;
		if(physicalHeightLengthField != null) physicalHeightLengthField.setLengthInMetres(physicalHeight);
	}
	
	/**
	 * adjust the physical width so that separation between neighbouring elements is the same in x and y directions
	 */
	public void adjustPhysicalWidth()
	{
		physicalWidth = physicalHeight * amplitudeMatrixColumns / amplitudeMatrixRows;
		if(physicalWidthLengthField != null) physicalWidthLengthField.setLengthInMetres(physicalWidth);
	}

	private void initialiseLightSources()
	{
		lightSources = new ArrayList<AbstractLightSource>();

		// the order in which these are added needs to match the constants for identifying the beam types
		lightSources.add(new GaussianBeam());
		lightSources.add(new HermiteGaussianBeam());
		lightSources.add(new LaguerreGaussianBeam());
		lightSources.add(new BesselBeam());
		lightSources.add(new UniformPlaneWave());
		lightSources.add(new BeamFromBitmap());
		
		// selectedLightSource = lightSources.get(beamType);
	}
	
	@Override
	public BeamCrossSection getOutputLightBeam()
	{
		return getSelectedLightSource().getBeamOutput(physicalWidth, physicalHeight, wavelength, amplitudeMatrixColumns, amplitudeMatrixRows);
	}
	
	private void drawBeamEditControls()
	{
		beamEditPanel.removeAll();
		beamEditPanel.add(getSelectedLightSource().getEditPanel(this));
		
		beamEditPanel.revalidate();
		beamEditPanel.repaint();
	}
	
	public int getAmplitudeMatrixColumns() {
		return amplitudeMatrixColumns;
	}

	/**
	 * Set the number of columns in the amplitude matrix to that specified.
	 * Adjust the physical width such that the horizontal and vertical separation between neighbouring elements is the same.
	 * @param amplitudeMatrixColumns
	 */
	public void setAmplitudeMatrixColumns(int amplitudeMatrixColumns)
	{
		this.amplitudeMatrixColumns = amplitudeMatrixColumns;
		adjustPhysicalWidth();
	}

	public int getAmplitudeMatrixRows()
	{
		return amplitudeMatrixRows;
	}

	/**
	 * Set the number of rows in the amplitude matrix to that specified.
	 * Adjust the physical height such that the horizontal and vertical separation between neighbouring elements is the same.
	 * @param amplitudeMatrixRows
	 */
	public void setAmplitudeMatrixRows(int amplitudeMatrixRows)
	{
		this.amplitudeMatrixRows = amplitudeMatrixRows;
		adjustPhysicalHeight();
	}

	public double getPhysicalWidth()
	{
		return physicalWidth;
	}

	/**
	 * Set the physical width to that specified.
	 * Adjust the physical height such that the horizontal and vertical separation between neighbouring elements is the same.
	 * @param physicalWidth
	 */
	public void setPhysicalWidth(double physicalWidth)
	{
		this.physicalWidth = physicalWidth;
		adjustPhysicalHeight();
	}

	public double getPhysicalHeight()
	{
		return physicalHeight;
	}

	/**
	 * Set the physical height to that specified.
	 * Adjust the physical width such that the horizontal and vertical separation between neighbouring elements is the same.
	 * @param physicalHeight
	 */
	public void setPhysicalHeight(double physicalHeight)
	{
		this.physicalHeight = physicalHeight;
		adjustPhysicalWidth();
	}
	
	public double getWavelength()
	{
		return wavelength;
	}

	public void setWavelength(double wavelength)
	{
		this.wavelength = wavelength;
	}
	
	public int getBeamType()
	{
		return beamType;
	}
	
	public void setBeamType(int beamType)
	{
		this.beamType = beamType;
		
		if(beamTypeComboBox != null) beamTypeComboBox.setSelectedIndex(beamType);
	}
	
	public AbstractLightSource getSelectedLightSource()
	{
		return lightSources.get(beamType);
		// return selectedLightSource;
	}

//	public void setSelectedLightSource(AbstractLightSource selectedLightSource) {
//		this.selectedLightSource = selectedLightSource;
//	}
}