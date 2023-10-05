package javawaveoptics.optics.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.optics.aperture.AbstractAperture;
import javawaveoptics.optics.aperture.ApertureType;
import javawaveoptics.ui.UIBitsAndBobs;

/**
 * An aperture that can be one of a number of different types.
 * Based on Sean's LightSource component
 * 
 * @author johannes
 *
 */
public class Aperture extends AbstractSimpleOpticalComponent implements Serializable, ActionListener
{
	private static final long serialVersionUID = -8748873004884984777L;
	
//	// aperture-type constants;
//	// must agree with the order in which components are added in initialiseApertures
//	public static final int
//		ABSORBING_BOUNDARY = 0,
//		CIRCULAR_APERTURE = 1,
//		DOUBLE_SLIT = 2,
//		GRATING = 3,
//		POLYGON = 4,
//		SLIT = 5,
//		GAUSSIAN_APERTURE = 6;
	
	/*
	 * Fields
	 */

	// List of light sources
	// private ArrayList<AbstractAperture> apertures;
	
	// Selected light source
	private AbstractAperture aperture;
	
	/*
	 * GUI edit controls
	 */
	
	private transient JPanel apertureEditPanel;
	private transient JComboBox<ApertureType> apertureTypeComboBox;

	
	public Aperture(String name, AbstractAperture aperture)
	{
		super(name);
		
		// initialiseApertures();
		// apertures = getListOfApertures();
		this.aperture = aperture;
	}
	
	public Aperture()
	{
		this("Aperture", ApertureType.getAperture(ApertureType.CIRCULAR_APERTURE));
	}

	@Override
	public String getComponentTypeName()
	{
		return "Aperture";
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Aperture-type drop down box
		 */
		
		apertureTypeComboBox.addActionListener(this);
		apertureTypeComboBox.setActionCommand("Aperture selected");
		
		// Create aperture and its edit controls
		drawApertureEditControls();
		
		editPanel.add(UIBitsAndBobs.makeRow("Aperture type", apertureTypeComboBox, true));
		editPanel.add(apertureEditPanel);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		// aperture edit controls
		apertureEditPanel = new JPanel();		// The edit controls for the selected aperture type
		apertureTypeComboBox = new JComboBox<ApertureType>(ApertureType.values());	// The combo box for selecting a aperture type
		
		// Set the combo box model from list of aperture types
		// apertureTypeComboBox.setModel(new DefaultComboBoxModel(apertures.toArray()));
		
		// Set beam selection
		apertureTypeComboBox.setSelectedItem(ApertureType.getApertureType(aperture));
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

    	// if(apertureTypeComboBox != null) aperture = ApertureType.getAperture((ApertureType)apertureTypeComboBox.getSelectedItem());

		aperture.readWidgets();
	}

//	private transient boolean inPropertyChange = false;
//	
//	@Override
//	public void propertyChange(PropertyChangeEvent e)
//	{
//		if(!inPropertyChange)
//		{
//			inPropertyChange = true;
//
//			Object source = e.getSource();
//
//			// nothing to do here, at least for the moment
//			
//			// Fire an edit panel event
//			if(editListener != null) editListener.editMade();
//
//			inPropertyChange = false;
//		}
//	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
//		if(!inPropertyChange)
//		{
//			inPropertyChange = true;

		    String command = event.getActionCommand();
		    	
		    if(command.equals("Aperture selected"))
		    {
		    	// A new aperture type has been selected
		    		
		    	aperture = ApertureType.getAperture((ApertureType)apertureTypeComboBox.getSelectedItem());
		    		
		    	drawApertureEditControls();
		    }
			
//			inPropertyChange = false;
//		}
		    
		// Fire an edit panel event
		if(editListener != null) editListener.editMade();
	}

//	public static ArrayList<AbstractAperture> getListOfApertures()
////	private void initialiseApertures()
//	{
////		apertures = new ArrayList<AbstractAperture>();
//		ArrayList<AbstractAperture> listOfApertures = new ArrayList<AbstractAperture>();
//
//		// the order in which the apertures are added here must agree with the aperture-type constants defined at the top!
//		listOfApertures.add(new AbsorbingBoundary());
//		listOfApertures.add(new AnnularAperture());
//		listOfApertures.add(new DoubleSlit());
//		listOfApertures.add(new Grating());
//		listOfApertures.add(new PolygonalAperture());
//		listOfApertures.add(new Slit());
//		listOfApertures.add(new GaussianAperture());
//		
//		// selectedAperture = apertures.get(CIRCULAR_APERTURE));
//		return listOfApertures;
//	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			return aperture.fromInputBeamCalculateOutputBeam(inputBeam);
		}
		
		return inputBeam;
	}

	private void drawApertureEditControls()
	{
		apertureEditPanel.removeAll();
		apertureEditPanel.add(aperture.getEditPanel());
		
		apertureEditPanel.revalidate();
		apertureEditPanel.repaint();
	}

	@Override
	public String getFormattedName()
	{
		return getName();
	}

	public AbstractAperture getSelectedAperture() {
		return aperture;
	}

	public void setApertureType(ApertureType apertureType) {
		aperture = ApertureType.getAperture(apertureType);
	}
}