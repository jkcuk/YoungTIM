package javawaveoptics.optics.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;

import javawaveoptics.optics.BeamCrossSection;
// import javawaveoptics.optics.component.CylindricalLensSpiral;
import javawaveoptics.optics.component.CylindricalLensSpiral.CylindricalLensSpiralType;
import javawaveoptics.optics.component.CylindricalLensSpiral.WindingBoundaryPlacementType;
import javawaveoptics.ui.LengthField;
import javawaveoptics.ui.LengthUnitsComboBox;
import javawaveoptics.ui.UIBitsAndBobs;
import javawaveoptics.ui.workbench.ExtensiveWorkbench;
import javawaveoptics.ui.workbench.ExtensiveWorkbenchOpticalComponent;
import library.maths.MyMath;

/**
 * 
 * @author Johannes
 */
public class SpiralAdaptiveFresnelLens extends AbstractSimpleOpticalComponent implements ConvertableComponent, Serializable, PropertyChangeListener, ActionListener
{
	private static final long serialVersionUID = 4201640500868865672L;

	private static final String COMPONENT_TYPE_NAME = "Spiral adaptive Fresnel lens";

	/*
	 * Fields
	 */

	/**
	 * type of the cylindrical-lens spiral
	 */
	private CylindricalLensSpiralType cylindricalLensSpiralType;

	/**
	 * the winding parameter
	 */
	private double b;
	
	/**
	 * the focal length of the cylindrical lens at r=1 m
	 */
	private double f1;

	/**
	 * relative rotation angle of the two components (in radians);
	 */
	private double deltaTheta;
	
	/**
	 * z separation between the components
	 */
	private double deltaZ;
	
	/**
	 * the radial distance, r0, at which the winding width is calculated
	 */
	private double r0;
	
	/**
	 * winding width at radial distance r0
	 */
	private double w0;
	
	/**
	 * a specific rotation angle...
	 */
	private double deltaPhi0;
	
	/**
	 * ... for which the focal length takes this value:
	 */
	private double focalLength0;

	/**
	 * placement of the boundary between neighbouring windings
	 */
	private WindingBoundaryPlacementType windingBoundaryPlacement;
	
	private boolean alvarezLohmannWindingFocussing;
	
	private boolean azimuthalPhaseComponensation;
	
	private boolean showComponent1, showComponent2;
	
	
	
	public SpiralAdaptiveFresnelLens(
		String name, 
		CylindricalLensSpiralType cylindricalLensSpiralType, 
		double b,
		double f1,
		double deltaPhi,
		double deltaZ,
		double deltaPhi0,
		double focalLength0,
		double r0,
		double w0,
		WindingBoundaryPlacementType windingBoundaryPlacement,
		boolean alvarezLohmannWindingFocussing,
		boolean azimuthalPhaseComponensation,
		boolean showComponent1,
		boolean showComponent2
	)
	{
		super(name);
			
		setCylindricalLensSpiralType(cylindricalLensSpiralType);
		setB(b);
		setF1(f1);
		setDeltaTheta(deltaPhi);
		setDeltaZ(deltaZ);
		setDeltaPhi0(deltaPhi0);
		setFocalLength0(focalLength0);
		setR0(r0);
		setW0(w0);
		setWindingBoundaryPlacement(windingBoundaryPlacement);
		setAlvarezLohmannWindingFocussing(alvarezLohmannWindingFocussing);
		setAzimuthalPhaseComponensation(azimuthalPhaseComponensation);
		setShowComponent1(showComponent1);
		setShowComponent2(showComponent2);
	}
	
	/**
	 * Null constructor. Creates a lens with default values. This requires no
	 * parameters.
	 */
	public SpiralAdaptiveFresnelLens()
	{
		this(
			COMPONENT_TYPE_NAME,	// name
			CylindricalLensSpiralType.LOGARITHMIC,	// cylindricalLensSpiralType
			0.01,	// b
			1e-3,	// f1
			MyMath.deg2rad(10),	// deltaPhi
			0.0,	// deltaZ
			MyMath.deg2rad(10),	// deltaPhi0
			1,	// focalLength0
			1e-2,	// r0
			1e-3,	// w0
			WindingBoundaryPlacementType.HALF_WAY,	// windingBoundaryPlacement
			true,	// alvarezLohmannWindingFocussing
			true,	// azimuthalPhaseComponensation
			true,	// showComponent1
			true	// showComponent2
		);
	}

	@Override
	public String getComponentTypeName()
	{
		return COMPONENT_TYPE_NAME;
	}
	
	// public static double dPdDeltaPhi()
	
	public static double r0w02b(CylindricalLensSpiralType cylindricalLensSpiralType, double r0, double w0)
	{
		switch(cylindricalLensSpiralType)
		{
		case ARCHIMEDEAN:
			return w0/(2.*Math.PI);
		case FERMAT:
			double w02 = w0*w0;
			return Math.sqrt(4.*r0*r0*w02 - w02*w02)/(2.*Math.PI);
		case HYPERBOLIC:
			return (Math.sqrt(1./(w0*w0) + 1./(r0*r0)) - 1./w0)/Math.PI;
		case LOGARITHMIC:
		default:
			return Math.log(1. + w0/r0)/(2.*Math.PI);
		}
	}

	public static double focalLenth0DeltaPhi0b2f1(double focalLength0, double deltaPhi0, double b)
	{
		return focalLength0*b*deltaPhi0;
	}
	
	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		if(inputBeam != null)
		{
			// calculateBAndF1();

			System.out.println("SpiralAdaptiveFresnelLens::fromInputBeamCalculateOutputBeam: b = "+b+", f1 = "+f1+"m");
			
			if(showComponent1)
			{
				CylindricalLensSpiral cls1 = new CylindricalLensSpiral(
						"Component 1",	// name
						cylindricalLensSpiralType,
						f1,	// focalLength
						b, 
						0.5*deltaTheta,	// phi0
						windingBoundaryPlacement,
						alvarezLohmannWindingFocussing,
						azimuthalPhaseComponensation
				);
				
				cls1.fromInputBeamCalculateOutputBeam(inputBeam);
			}
			
			if(deltaZ != 0.0)
			{
				inputBeam.propagate(deltaZ);
			}
			
			if(showComponent2)
			{
				CylindricalLensSpiral cls2 = new CylindricalLensSpiral(
						"Component 2",	// name
						cylindricalLensSpiralType,
						-f1,	// focalLength
						b, 
						-0.5*deltaTheta,	// phi0
						windingBoundaryPlacement,
						alvarezLohmannWindingFocussing,
						azimuthalPhaseComponensation
				);
	
				cls2.fromInputBeamCalculateOutputBeam(inputBeam);
			}

//			inputBeam.propagate(-d/2);	// propagate from the waist plane (where we assume we are) to the first lens
//			inputBeam.passThroughCylindricalLens(f, MyMath.deg2rad(-axisAngleWithXAxis));
//			inputBeam.propagate(d);	// propagate from the first lens to the second lens
//			inputBeam.passThroughCylindricalLens(f, MyMath.deg2rad(-axisAngleWithXAxis));
//			inputBeam.propagate(-d/2);	// propagate from the second lens to the waist plane again
		}
		
		return inputBeam;
	}
	
	// ConvertableComponent methods
	
	@Override
	public String getConvertMenuItemText()
	{
		return "Convert to series of optical components";
	}
	
	/**
	 * 
	 */
	@Override
	public void convert(ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent)
	{
		System.out.println("SpiralAdaptiveFresnelLens::convert: Converting to two CylindricalLensSpirals");
		
		ExtensiveWorkbench workbench = workbenchOpticalComponent.getExtensiveWorkbench();
		
		int opticalTrainIndex = workbench.getOpticalTrainIndexOf(workbenchOpticalComponent);
		
		// first remove this optical component from the workbench, i.e. remove it from the optical environment, the optical component train, and the workbench
		// (from ExtensiveWorkbench.WorkbenchOpticalComponentPopupMenuActionListener.actionPerformed)
		
		// Remove component
		workbench.getOpticalEnvironment().remove(this);
		
		// Remove the component from the optical train and workbench
		workbench.getOpticalComponentTrain().remove(this);
		workbench.getWorkbenchComponents().remove(workbenchOpticalComponent);
				
		// add the new components
		// (from ExtensiveWorkbench.WorkbenchFlowArrowPopupMenuActionListener.actionPerformed)

		// component 1
		ExtensiveWorkbenchOpticalComponent component1 = workbench.insertComponent(
				opticalTrainIndex,	// index
				new CylindricalLensSpiral(
						"Component 1",	// name
						cylindricalLensSpiralType,
						f1,	// focalLength
						b, 
						0.5*deltaTheta,	// phi0
						windingBoundaryPlacement,
						alvarezLohmannWindingFocussing,
						azimuthalPhaseComponensation
						)	// component
				);
		
		// space between components
		workbench.insertComponent(
				opticalTrainIndex + 1,	// index
				new Distance(
						"Separation",	// name
						deltaZ	// distance
						)
				);
		
		// component 2
		workbench.insertComponent(
				opticalTrainIndex + 2,	// index
				new CylindricalLensSpiral(
						"Component 2",	// name
						cylindricalLensSpiralType,
						-f1,	// focalLength
						b, 
						-0.5*deltaTheta,	// phi0
						windingBoundaryPlacement,
						alvarezLohmannWindingFocussing,
						azimuthalPhaseComponensation
						)
				);

		workbench.selectAndShowComponent(component1);
	}
	
//	private void calculateBAndF1()
//	{
//		// calculate b
//		b = r0w02b(r0, w0);
//		
//		// calculate f1
//		f1 = focalLength0*b*deltaPhi0;
//
//		updateInfo();
//	}
	
	
	/*
	 * GUI edit controls
	 */
	
	private transient JComboBox<CylindricalLensSpiralType> cylindricalLensSpiralTypeComboBox;
	private transient JComboBox<WindingBoundaryPlacementType> windingBoundaryPlacementComboBox;
	private transient LengthField f1LengthField, deltaZLengthField, focalLength0LengthField, r0LengthField, w0LengthField;
	private transient JFormattedTextField bTextField, deltaThetaDegTextField, deltaPhi0DegTextField, FTextField;
	private transient JCheckBox alvarezLohmannWindingFocussingCheckBox, azimuthalPhaseComponensationCheckBox, showComponent1CheckBox, showComponent2CheckBox;
	private transient JTextPane infoTextPane;
	private transient JButton setBButton, setF1Button, setDeltaZButton;
	

	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
			
		editPanel.add(UIBitsAndBobs.makeHTMLLabel("A spiral adaptive Fresnel lens, comprising two cylindrical-lens-spiral components."));
		editPanel.add(UIBitsAndBobs.makeRow("Spiral shape:", cylindricalLensSpiralTypeComboBox, true));
		editPanel.add(UIBitsAndBobs.makeRow("Rotation angle between components, &Delta;&theta; = ", deltaThetaDegTextField, "&deg;", true));
		editPanel.add(UIBitsAndBobs.makeRow("Separation between components, &Delta;<i>z</i> = ", deltaZLengthField, setDeltaZButton, true));
		editPanel.add(UIBitsAndBobs.makeRow("Winding parameter, <i>b</i> = ", bTextField, true));
		editPanel.add(UIBitsAndBobs.makeRow("Cylindrical-lens focal length at <i>r</i> = 1 m, <i>f</i><sub>1</sub> = ", f1LengthField, true));
		editPanel.add(alvarezLohmannWindingFocussingCheckBox);
		editPanel.add(azimuthalPhaseComponensationCheckBox);
		editPanel.add(UIBitsAndBobs.makeRow(showComponent1CheckBox, showComponent2CheckBox, true));
		editPanel.add(UIBitsAndBobs.makeRow("Winding boundary ", windingBoundaryPlacementComboBox, true));
		editPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		editPanel.add(UIBitsAndBobs.makeRow("Resulting focal length, <i>F</i> = ", FTextField, "m", true));
		editPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		editPanel.add(UIBitsAndBobs.makeRow(setBButton, "from the winding <i>w</i> &#8773; ", w0LengthField, "at <i>r</i> = ", r0LengthField, "", true));
		editPanel.add(UIBitsAndBobs.makeRow(setF1Button, "from <i>b</i> and the focal length <i>F</i> = ", focalLength0LengthField, "for &Delta;&phi; = ", deltaPhi0DegTextField, "&deg;", true));
		// editPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		// editPanel.add(infoTextPane);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		cylindricalLensSpiralTypeComboBox = new JComboBox<CylindricalLensSpiralType>(CylindricalLensSpiralType.values());
		cylindricalLensSpiralTypeComboBox.setToolTipText("Shape of the spiral the cylindrical lenses forming the components are wound into");
		cylindricalLensSpiralTypeComboBox.addActionListener(this);
		cylindricalLensSpiralTypeComboBox.setSelectedItem(cylindricalLensSpiralType);
		cylindricalLensSpiralTypeComboBox.setMaximumSize(cylindricalLensSpiralTypeComboBox.getPreferredSize());
		
		bTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		bTextField.setValue(Double.valueOf(b));
		
		deltaZLengthField = new LengthField(this);
		deltaZLengthField.setLengthInMetres(deltaZ);
		deltaZLengthField.setToolTipText("Separation between the two components; be aware of aliasing issues!");
		
		setDeltaZButton = new JButton("<html>Set for distance winding focussing</html>");
		setDeltaZButton.setToolTipText("Works only for logarithmic-spiral shape with Alvarez-Lohmann winding focussing off");
		setDeltaZButton.addActionListener(this);
		enableOrDisableDeltaZButton();

		f1LengthField = new LengthField(this);
		f1LengthField.setLengthInMetres(f1);

		r0LengthField = new LengthField(this);
		r0LengthField.setLengthInMetres(r0);

		w0LengthField = new LengthField(this);
		w0LengthField.setLengthInMetres(w0);
		
		setBButton = new JButton("<html>Set <i>b</i></html>");
		setBButton.addActionListener(this);
		
		setF1Button = new JButton("<html>Set <i>f</i><sub>1</sub></html>");
		setF1Button.addActionListener(this);
		
		deltaThetaDegTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		deltaThetaDegTextField.setValue(Double.valueOf(MyMath.rad2deg(deltaTheta)));

		deltaPhi0DegTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		deltaPhi0DegTextField.setValue(Double.valueOf(MyMath.rad2deg(deltaPhi0)));
		
		focalLength0LengthField = new LengthField(this);
		focalLength0LengthField.setLengthInMetres(focalLength0);

		windingBoundaryPlacementComboBox = new JComboBox<WindingBoundaryPlacementType>(WindingBoundaryPlacementType.values());
		windingBoundaryPlacementComboBox.setToolTipText("Placement of the boundary between neighbouring windings");
		windingBoundaryPlacementComboBox.addActionListener(this);
		windingBoundaryPlacementComboBox.setSelectedItem(windingBoundaryPlacement);
		windingBoundaryPlacementComboBox.setMaximumSize(windingBoundaryPlacementComboBox.getPreferredSize());
		
		alvarezLohmannWindingFocussingCheckBox = new JCheckBox("Alvarez-Lohmann winding focussing");
		alvarezLohmannWindingFocussingCheckBox.setToolTipText("Should Alvarez-Lohmann winding focussing be used?");
		alvarezLohmannWindingFocussingCheckBox.addActionListener(this);
		alvarezLohmannWindingFocussingCheckBox.setSelected(alvarezLohmannWindingFocussing);
		alvarezLohmannWindingFocussingCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		
		azimuthalPhaseComponensationCheckBox = new JCheckBox("Azimuthal phase componensation");
		azimuthalPhaseComponensationCheckBox.setToolTipText("Should azimuthal phase compensation be used?");
		azimuthalPhaseComponensationCheckBox.addActionListener(this);
		azimuthalPhaseComponensationCheckBox.setSelected(azimuthalPhaseComponensation);
		azimuthalPhaseComponensationCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		showComponent1CheckBox = new JCheckBox("Show component 1");
		showComponent1CheckBox.setToolTipText("If true, takes into account the effect of component 1");
		showComponent1CheckBox.addActionListener(this);
		showComponent1CheckBox.setSelected(showComponent1);
		showComponent1CheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);

		showComponent2CheckBox = new JCheckBox("Show component 2");
		showComponent2CheckBox.setToolTipText("If true, takes into account the effect of component 2");
		showComponent2CheckBox.addPropertyChangeListener(this);
		showComponent2CheckBox.setSelected(showComponent2);
		showComponent2CheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		FTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
		FTextField.setEditable(false);
		FTextField.setEnabled(false);
		recalculateF();
		
		infoTextPane = new JTextPane();
		infoTextPane.setContentType("text/html");
		updateInfo();
		// infoTextField.setText(" -- not initialialised -- ");
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();

		if(cylindricalLensSpiralTypeComboBox != null) cylindricalLensSpiralType = (CylindricalLensSpiralType)(cylindricalLensSpiralTypeComboBox.getSelectedItem());
		if(bTextField != null) setB(
				((Number)bTextField.getValue()).doubleValue()
				// (double)bTextField.getValue()
			);
		if(deltaZLengthField != null) setDeltaZ(deltaZLengthField.getLengthInMetres());
		if(f1LengthField != null) setF1(f1LengthField.getLengthInMetres());
        if(r0LengthField != null) setR0(r0LengthField.getLengthInMetres());
        if(w0LengthField != null) setW0(w0LengthField.getLengthInMetres());
               // if(bTextField != null) setB(((Number)bTextField.getValue()).doubleValue());
        if(deltaThetaDegTextField != null) setDeltaTheta(MyMath.deg2rad(((Number)deltaThetaDegTextField.getValue()).doubleValue()));
        if(deltaPhi0DegTextField != null) setDeltaPhi0(MyMath.deg2rad(((Number)deltaPhi0DegTextField.getValue()).doubleValue()));
        if(focalLength0LengthField != null) setFocalLength0(focalLength0LengthField.getLengthInMetres());
        if(windingBoundaryPlacementComboBox != null) windingBoundaryPlacement = (WindingBoundaryPlacementType)(windingBoundaryPlacementComboBox.getSelectedItem());
        if(alvarezLohmannWindingFocussingCheckBox != null) alvarezLohmannWindingFocussing = alvarezLohmannWindingFocussingCheckBox.isSelected();
        if(azimuthalPhaseComponensationCheckBox != null) azimuthalPhaseComponensation = azimuthalPhaseComponensationCheckBox.isSelected();
        if(showComponent1CheckBox != null) showComponent1 = showComponent1CheckBox.isSelected();
        if(showComponent2CheckBox != null) showComponent2 = showComponent2CheckBox.isSelected();
	}
	
	public void recalculateF()
	{
		if(FTextField != null) FTextField.setValue(Double.valueOf(f1/(b*deltaTheta)));
	}
	
	public void updateInfo()
	{
		String s = "<html>Parameters: <i>b</i> = " + String.format("%3.2e", b)
				+ ", <i>f</i><sub>1</sub> = " + LengthUnitsComboBox.length2NiceString(f1)
				+ ", <i>F</i> = " + LengthUnitsComboBox.length2NiceString(f1/(b*deltaTheta))
				+ "</html>";
		
		// System.out.println("SpiralAdaptiveFresnelLens::updateInfo: "+s);
		if(infoTextPane != null) infoTextPane.setText(s);
	}
	
	public void enableOrDisableDeltaZButton()
	{
		if(setDeltaZButton != null) setDeltaZButton.setEnabled(
			(cylindricalLensSpiralType == CylindricalLensSpiralType.LOGARITHMIC) 
			&& !alvarezLohmannWindingFocussing
		);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
	    Object source = e.getSource();
	    
	    if(source.equals(deltaZLengthField))
	    {
			setDeltaZ(deltaZLengthField.getLengthInMetres());
	    }
	    else if(source.equals(r0LengthField))
	    {
	        setR0(r0LengthField.getLengthInMetres());
	        // calculateBAndF1();
	        // updateInfo();
	    }
	    else if (source.equals(focalLength0LengthField))
	    {
	        setFocalLength0(focalLength0LengthField.getLengthInMetres());
	        // calculateBAndF1();
	        // updateInfo();
	    }
	    else if (source.equals(deltaPhi0DegTextField))
	    {
	        setDeltaPhi0(MyMath.deg2rad(((Number)deltaPhi0DegTextField.getValue()).doubleValue()));
	        // calculateBAndF1();
	        // updateInfo();
	    }
	    else if (source.equals(w0LengthField))
	    {
	        setW0(w0LengthField.getLengthInMetres());
	        // calculateBAndF1();
	        // updateInfo();
	    }
	    else if (source.equals(bTextField))
	    {
	        setB(((Number)bTextField.getValue()).doubleValue());
	        recalculateF();
	    }
	    else if (source.equals(f1LengthField))
	    {
	    	setF1(f1LengthField.getLengthInMetres());
	    	recalculateF();
	    }
	    else if (source.equals(deltaThetaDegTextField))
	    {
	        setDeltaTheta(MyMath.deg2rad(((Number)deltaThetaDegTextField.getValue()).doubleValue()));
	        recalculateF();
	    }
	    
		// Fire an edit panel event
		editListener.editMade();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		if (source.equals(cylindricalLensSpiralTypeComboBox))
		{
			cylindricalLensSpiralType = (CylindricalLensSpiralType)(cylindricalLensSpiralTypeComboBox.getSelectedItem());

			enableOrDisableDeltaZButton();
		}
		else if(source.equals(setDeltaZButton))
		{
			deltaZ = f1*b*deltaTheta;
			deltaZLengthField.setLengthInMetres(deltaZ);
		}
	    else if (source.equals(alvarezLohmannWindingFocussingCheckBox))
	    {
	    	alvarezLohmannWindingFocussing = alvarezLohmannWindingFocussingCheckBox.isSelected();
	    	
	    	// System.out.println("SpiralAdaptiveFresnelLens::actionPerformed: alvarezLohmannWindingFocussing = "+alvarezLohmannWindingFocussing);
	    	
	    	enableOrDisableDeltaZButton();
	    }
	    else if(source.equals(azimuthalPhaseComponensationCheckBox))
	    {
	    	azimuthalPhaseComponensation = azimuthalPhaseComponensationCheckBox.isSelected();
	    }
		else if (source.equals(windingBoundaryPlacementComboBox))
		{
			windingBoundaryPlacement = (WindingBoundaryPlacementType)(windingBoundaryPlacementComboBox.getSelectedItem());
		}
		else if(source.equals(setBButton))
		{
			readWidgets();
			b = r0w02b(cylindricalLensSpiralType, r0, w0);
			bTextField.setValue(Double.valueOf(b));
			recalculateF();
		}
		else if(source.equals(setF1Button))
		{
			readWidgets();
			f1 = focalLength0*b*deltaPhi0;
			f1LengthField.setLengthInMetres(f1);
			recalculateF();
		}
	    else if (source.equals(showComponent1CheckBox))
	    {
	    	showComponent1 = showComponent1CheckBox.isSelected();
	    }
	    else if (source.equals(showComponent2CheckBox))
	    {
	    	showComponent2 = showComponent2CheckBox.isSelected();
	    }

		// Fire an edit panel event
		editListener.editMade();
	}
	
	/**
	 * @return the b
	 */
	public double getB() {
		return b;
	}

	/**
	 * @param b the b to set
	 */
	public void setB(double b) {
		this.b = b;
	}

	/**
	 * @return the f1
	 */
	public double getF1() {
		return f1;
	}

	/**
	 * @param f1 the f1 to set
	 */
	public void setF1(double f1) {
		this.f1 = f1;
	}

	/**
	 * @return the deltaTheta
	 */
	public double getDeltaTheta() {
		return deltaTheta;
	}

	/**
	 * @param deltaTheta
	 */
	public void setDeltaTheta(double deltaTheta) {
		this.deltaTheta = deltaTheta;
	}
	
	/**
	 * @return the deltaZ
	 */
	public double getDeltaZ() {
		return deltaZ;
	}

	/**
	 * @param deltaZ the deltaZ to set
	 */
	public void setDeltaZ(double deltaZ) {
		this.deltaZ = deltaZ;
	}

	/**
	 * @return the deltaPhi0
	 */
	public double getDeltaPhi0() {
		return deltaPhi0;
	}

	/**
	 * @param deltaPhi0 the deltaPhi0 to set
	 */
	public void setDeltaPhi0(double deltaPhi0) {
		this.deltaPhi0 = deltaPhi0;
	}

	/**
	 * @return the focalLength0
	 */
	public double getFocalLength0() {
		return focalLength0;
	}

	/**
	 * @param focalLength0 the focalLength0 to set
	 */
	public void setFocalLength0(double focalLength0) {
		this.focalLength0 = focalLength0;
	}

	/**
	 * @return the r0
	 */
	public double getR0() {
		return r0;
	}

	/**
	 * @param r0 the r0 to set
	 */
	public void setR0(double r0) {
		this.r0 = r0;
	}

	/**
	 * @return the w0
	 */
	public double getW0() {
		return w0;
	}

	/**
	 * @param w0 the w0 to set
	 */
	public void setW0(double w0) {
		this.w0 = w0;
	}

	public CylindricalLensSpiralType getCylindricalLensSpiralType() {
		return cylindricalLensSpiralType;
	}

	public void setCylindricalLensSpiralType(CylindricalLensSpiralType cylindricalLensSpiralType) {
		this.cylindricalLensSpiralType = cylindricalLensSpiralType;
	}

	/**
	 * @return the windingBoundaryPlacement
	 */
	public WindingBoundaryPlacementType getWindingBoundaryPlacement() {
		return windingBoundaryPlacement;
	}

	/**
	 * @param windingBoundaryPlacement the windingBoundaryPlacement to set
	 */
	public void setWindingBoundaryPlacement(WindingBoundaryPlacementType windingBoundaryPlacement) {
		this.windingBoundaryPlacement = windingBoundaryPlacement;
	}

	/**
	 * @return the alvarezLohmannWindingFocussing
	 */
	public boolean isAlvarezLohmannWindingFocussing() {
		return alvarezLohmannWindingFocussing;
	}

	/**
	 * @param alvarezLohmannWindingFocussing the alvarezLohmannWindingFocussing to set
	 */
	public void setAlvarezLohmannWindingFocussing(boolean alvarezLohmannWindingFocussing) {
		this.alvarezLohmannWindingFocussing = alvarezLohmannWindingFocussing;
	}

	/**
	 * @return the azimuthalPhaseComponensation
	 */
	public boolean isAzimuthalPhaseComponensation() {
		return azimuthalPhaseComponensation;
	}

	/**
	 * @param azimuthalPhaseComponensation the azimuthalPhaseComponensation to set
	 */
	public void setAzimuthalPhaseComponensation(boolean azimuthalPhaseComponensation) {
		this.azimuthalPhaseComponensation = azimuthalPhaseComponensation;
	}

	/**
	 * @return the showComponent1
	 */
	public boolean isShowComponent1() {
		return showComponent1;
	}

	/**
	 * @param showComponent1 the showComponent1 to set
	 */
	public void setShowComponent1(boolean showComponent1) {
		this.showComponent1 = showComponent1;
	}

	/**
	 * @return the showComponent2
	 */
	public boolean isShowComponent2() {
		return showComponent2;
	}

	/**
	 * @param showComponent2 the showComponent2 to set
	 */
	public void setShowComponent2(boolean showComponent2) {
		this.showComponent2 = showComponent2;
	}

	@Override
	public String getFormattedName()
	{
		return getName();	// + " (w0 = " + Double.toString(designWaistSize) + "m)";
	}
}