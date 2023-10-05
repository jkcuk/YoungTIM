package javawaveoptics.optics.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.OpticalComponentEditListener;
import javawaveoptics.ui.UIBitsAndBobs;
import javawaveoptics.utility.ImageableInterface;
import javawaveoptics.utility.ImageableLightSourceInterface;

/**
 * Defines an optical component that is an image of a plane elsewhere in the optical environment, provided
 * that plane exists and has data in it.
 * If this is not the case, then it acts like a light source.
 * Handy for initialising the beam in a resonator etc.
 * 
 * @author Johannes
 */
public class ImageOfPlane extends AbstractLightSourceComponent
implements Serializable, ImageableLightSourceInterface, OpticalComponentEditListener, ActionListener
{
	private static final long serialVersionUID = 8273726931660037847L;

	/*
	 * Fields
	 */
	
	private boolean initialiseToNull;
	private LightSource lightSource;
	private ImageOfPlaneNonInitialising imageOfPlane;
	
	/*
	 * GUI edit controls
	 */
	
	private transient JTabbedPane lightSourceOrImageTabbedPane;
	
	private static String[] initialisationTypes = { "Nothing", "Light source" };
	private transient JComboBox initialisationComboBox;
	
	private transient JPanel lightSourceEditPanel;


	public ImageOfPlane(String name, ImageOfPlaneNonInitialising imageOfPlane, LightSource lightSource, boolean initialiseToNull)
	{
		super(name);
		
		this.imageOfPlane = imageOfPlane;
		imageOfPlane.setSearchTreeStartComponent(this);
		imageOfPlane.setEditPanelListener(this);	// make this component's edit listener listen to the image
		imageOfPlane.setShowBorderAndNameTextBox(false);

		this.initialiseToNull = initialiseToNull;
		
		this.lightSource = lightSource;
		lightSource.setShowBorderAndNameTextBox(false);
	}
	
	public ImageOfPlane(String name)
	{
		this(
				name,
				new ImageOfPlaneNonInitialising(),
				new LightSource(),
				true
			);
	}

	public ImageOfPlane(String name, ImageableInterface object)
	{
		this(
				name,
				new ImageOfPlaneNonInitialising(),
				new LightSource(),
				true
			);
		
		setSelectedImageableComponent(object);
	}

	public ImageOfPlane(ImageableInterface object)
	{
		this(
				"Image of " + object.getName(),
				new ImageOfPlaneNonInitialising(),
				new LightSource(),
				true
			);
		
		setSelectedImageableComponent(object);
	}

	public ImageOfPlane()
	{
		this(
				"Image",
				new ImageOfPlaneNonInitialising(),
				new LightSource(),
				true
			);
	}
	
	@Override
	public String getComponentTypeName()
	{
		return "Image of a plane";
	}

	@Override
	public BeamCrossSection getOutputLightBeam()
	{
		if(
				(imageOfPlane != null) &&
				(imageOfPlane.getSelectedImageableComponent() != null) &&
				(imageOfPlane.getSelectedImageableComponent() != Plane.NO_PLANE) &&
				(imageOfPlane.getSelectedImageableComponent().isCopyOfBeamPresent())
			)
		{
			// great, take the output from imageOfPlane
		
			return imageOfPlane.getOutputLightBeam();
		}
		else
		{
			if(initialiseToNull) return null;
			else return lightSource.getOutputLightBeam();
		}
	}
		
	/**
	 * Overrides the default behaviour of the parent class so that the list of planes
	 * to image is updated before the edit panel is viewed.
	 */
	@Override
	public JComponent getEditPanel()
	{
		// First check whether or not the edit panel has been instantiated. If it has
		// been, then the images's object combo box might be out of date and must therefore
		// be refreshed.
		if(editPanel != null)
		{			
			// calling the image's getEditPanel method does this
			imageOfPlane.getEditPanel();
		}
		
		// Return the edit panel, complete with up-to-date combo box.
		return super.getEditPanel();
	}

	private void drawLightSourceEditControls()
	{
		lightSourceEditPanel.removeAll();
		if(!initialiseToNull) lightSourceEditPanel.add(lightSource.getEditPanel());
		
		lightSourceEditPanel.revalidate();
		lightSourceEditPanel.repaint();
	}

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		imageOfPlane.createEditPanel();	// necessary?
		lightSourceOrImageTabbedPane.addTab("Image", imageOfPlane.getEditPanel());

		JPanel initialisationPanel = new JPanel();
		initialisationPanel.setLayout(new BoxLayout(initialisationPanel, BoxLayout.Y_AXIS));
		
		drawLightSourceEditControls();
		
		initialisationPanel.add(UIBitsAndBobs.makeRow("Initialise to", initialisationComboBox, true));
		initialisationPanel.add(lightSourceEditPanel);
		// lightSource.createEditPanel();	// necessary?
		
		lightSourceOrImageTabbedPane.addTab("Initialisation", initialisationPanel);
		
		editPanel.add(lightSourceOrImageTabbedPane);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		imageOfPlane.initialiseWidgets();

		lightSourceEditPanel = new JPanel();
		initialisationComboBox = new JComboBox(initialisationTypes);
		if(initialiseToNull)
		{
			initialisationComboBox.setSelectedItem("Nothing");
		}
		else
		{
			initialisationComboBox.setSelectedItem("Light source");
		}
		initialisationComboBox.addActionListener(this);

		lightSource.initialiseWidgets();
		
		lightSourceOrImageTabbedPane = new JTabbedPane();
	}

	@Override
	public void readWidgets()
	{
		super.readWidgets();

		if(initialisationComboBox != null) initialiseToNull = initialisationComboBox.getSelectedItem().equals("Nothing");

		if(imageOfPlane != null) imageOfPlane.readWidgets();
		if(lightSource != null) lightSource.readWidgets();
	}
	
	@Override
	public String getFormattedName()
	{
		return imageOfPlane.getFormattedName();
	}

	@Override
	public void editMade() {
		// Fire an edit panel event
		if(editListener != null) editListener.editMade();
	}

	@Override
	public void redraw() {
		// Fire an edit panel event
		if(editListener != null) editListener.redraw();
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();

		if (source == initialisationComboBox)
		{
			initialiseToNull = initialisationComboBox.getSelectedItem().equals("Nothing");
			
			// Fire an edit event, i.e. make sure plot is re-drawn
			drawLightSourceEditControls();
		}
	}

	public LightSource getLightSource() {
		return lightSource;
	}

	public void setLightSource(LightSource lightSource) {
		this.lightSource = lightSource;
	}

	public ImageOfPlaneNonInitialising getImageOfPlane() {
		return imageOfPlane;
	}

	public void setImageOfPlane(ImageOfPlaneNonInitialising imageOfPlane) {
		this.imageOfPlane = imageOfPlane;
	}
	
	public ImageableInterface getSelectedImageableComponent()
	{
		return getImageOfPlane().getSelectedImageableComponent();
	}

	public void setSelectedImageableComponent(ImageableInterface c)
	{
		getImageOfPlane().setSelectedImageableComponent(c);
	}

	public boolean isInitialiseToNull() {
		return initialiseToNull;
	}

	public void setInitialiseToNull(boolean initialiseToNull) {
		this.initialiseToNull = initialiseToNull;
	}
	
	public void resetRoundTripCounter()
	{
		imageOfPlane.resetRoundTripCounter();
	}

}