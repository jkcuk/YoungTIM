package javawaveoptics.optics.environment;
import java.io.Serializable;


import javawaveoptics.optics.component.Aperture;
import javawaveoptics.optics.component.Distance;
import javawaveoptics.optics.component.EitherOrComponent;
import javawaveoptics.optics.component.ImageOfPlane;
import javawaveoptics.optics.component.Lens;
import javawaveoptics.optics.aperture.AnnularAperture;
import javawaveoptics.optics.component.Plane;
import javawaveoptics.ui.workbench.WorkbenchFactory;


public class KatiesOpticalEnvironment extends AbstractOpticalEnvironment implements Serializable
{	
	private static final long serialVersionUID = -6799062685393746193L;
	
	//setting constants defining distances
	
	private static final double DISTANCE_AS = 0.5;	// length of resonator
	private static final double DISTANCE_AEyes = 0.5;	// distance from mirror A to eyes
	private static final double RIGHT_EYE_X_CENTRE = 0.002;
	private static final double RIGHT_EYE_PUPIL_RADIUS = 0.001;
	private static final double CIRCULAR_APERTURE_RADIUS = 0.003;
	private static final int X_AMPLITUDE_DIMENSION = 1024;
	private static final int Y_AMPLITUDE_DIMENSION = 2;
	private static final double LENS_IN_A_FOCAL_LENGTH = -0.25;
	private static final double LENS_IN_S_FOCAL_LENGTH = 0.5;
		
	public KatiesOpticalEnvironment()
	{
		// Set up optical environment to use the extensive workbench for GUI applications
		super(WorkbenchFactory.TYPE_EXTENSIVE);
		
		// create all the optical components and set their parameters as far as is possible just now...
		ImageOfPlane imageOfA = new ImageOfPlane();
		Lens lensInS = new Lens("Lens in S", LENS_IN_S_FOCAL_LENGTH);
		Lens lensInA = new Lens("Lens in A", LENS_IN_A_FOCAL_LENGTH);
		Distance propagateA2S = new Distance("A to S", DISTANCE_AS);
		Distance propagateS2A = new Distance("S to A", DISTANCE_AS);
		Distance propagateA2Eyes = new Distance("A to eyes ", DISTANCE_AEyes);
		Distance propagateEyes2A = new Distance("Eyes back to A", -DISTANCE_AEyes); 
		Plane planeS = new Plane("S");
		// planeS.getStandalonePlotTabbedPane().setSelectedIndex(X_SECTION_INDEX);
		Plane planeSApertured = new Plane("S (behind aperture etc)");
		// planeSApertured.getStandalonePlotTabbedPane().setSelectedIndex(X_SECTION_INDEX);
		Plane planeA = new Plane("A");
		// planeA.getStandalonePlotTabbedPane().setSelectedIndex(X_SECTION_INDEX);
		Plane planeEyes = new Plane("Eyes");
		// planeEyes.getStandalonePlotTabbedPane().setSelectedIndex(X_SECTION_INDEX);
		Plane planeAFiltered = new Plane("A, filtered" );
		// planeAFiltered.getStandalonePlotTabbedPane().setSelectedIndex(X_SECTION_INDEX);
		Aperture apertureInS = new Aperture(
				"aperture in S",
				new AnnularAperture(
						"Annular aperture",	// name
						CIRCULAR_APERTURE_RADIUS,	// outerRadius
						0,	// innerRadius
						0,	// xCentre
						0	// yCentre
					)
			);
//		apertureInS.setApertureType(Aperture.CIRCULAR_APERTURE);
//		((AnnularAperture)(apertureInS.getSelectedAperture())).setOuterRadius(CIRCULAR_APERTURE_RADIUS);
		Aperture eyePupil = new Aperture(
				"eye pupil",
				new AnnularAperture(
						"Annular aperture",	// name
						RIGHT_EYE_PUPIL_RADIUS,	// outerRadius
						0,	// innerRadius
						RIGHT_EYE_X_CENTRE,	// xCentre
						0	// yCentre
					)
			);
//		eyePupil.setApertureType(Aperture.CIRCULAR_APERTURE);
//		((AnnularAperture)(eyePupil.getSelectedAperture())).setOuterRadius(RIGHT_EYE_PUPIL_RADIUS);
//		((AnnularAperture)(eyePupil.getSelectedAperture())).setxCentre(RIGHT_EYE_X_CENTRE);
		EitherOrComponent eitherOr = new EitherOrComponent("Either-or Component");
		
		// now form links between the components, as required
		// make image's object the plane S
		imageOfA.setSelectedImageableComponent(planeA);
		
		// start off the simulation with the light source initialised
		imageOfA.setInitialiseToNull(false);
		
		// sets the amplitude matrix dimensions
		imageOfA.getLightSource().setAmplitudeMatrixColumns(X_AMPLITUDE_DIMENSION);
		imageOfA.getLightSource().setAmplitudeMatrixRows(Y_AMPLITUDE_DIMENSION);
		imageOfA.getLightSource().setPhysicalWidth(2e-2);	// 2cm
		// imageOfA.getLightSource().adjustPhysicalHeight(); // now done automatically in setPhysicalWidth
		
		addFirstComponent(imageOfA);
		
		addAfter(imageOfA, 0, lensInA, 0);
		addAfter(lensInA, 0, propagateA2S, 0);
		addAfter(propagateA2S, 0, planeS, 0);
		addAfter(planeS, 0, lensInS, 0);
		addAfter(lensInS, 0, apertureInS, 0);
		addAfter(apertureInS, 0, planeSApertured, 0);
		addAfter(planeSApertured, 0, propagateS2A, 0);
		addAfter(propagateS2A, 0, eitherOr, 0);
		addAfter(eitherOr, 0, planeA, 0);
		addAfter(planeA, 0, propagateA2Eyes, 0);
		addAfter(propagateA2Eyes, 0, planeEyes, 0);
		addAfter(planeEyes, 0, eyePupil, 0);
		addAfter(eyePupil, 0, propagateEyes2A, 0);
		addAfter(propagateEyes2A, 0, planeAFiltered, 0 );

	}
}