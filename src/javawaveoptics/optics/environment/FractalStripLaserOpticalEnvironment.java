package javawaveoptics.optics.environment;

import java.io.Serializable;

import javawaveoptics.optics.aperture.Slit;
import javawaveoptics.optics.component.*;
import javawaveoptics.ui.workbench.WorkbenchFactory;

/**
 * Default optical environment, for testing purposes.
 * 
 * @author Steven Miller
 * @author Johannes
 */
public class FractalStripLaserOpticalEnvironment extends AbstractOpticalEnvironment implements Serializable
{
	private static final long serialVersionUID = 6224104410878105469L;

	public FractalStripLaserOpticalEnvironment()
	{
		// Set up optical environment to use the extensive workbench for GUI applications
		super(WorkbenchFactory.TYPE_EXTENSIVE);

		double M_L = 2;
		double f1 = 0.5;
		double f2 = f1 * Math.sqrt(M_L);	// transverse magnification M_L = -(f2 / f1)^2 = -2
		
		// BPM parameters
		double BPM_STEPSIZE = 1e-3;
		int BPM_ABSORBING_BOUNDARY_WIDTH = 50;
		
		// Self-conjugate plane, S
		Plane selfConjugatePlane = new Plane();
		selfConjugatePlane.setName("S");
		
		// Aperture
		Aperture aperture = new Aperture(
				"Slit",
				new Slit(
						"Slit",
						20.8e-3,	// slitWidth, 
						0,	// rotationAngle
						0,	// xCentre
						0	// yCentre
					)
			);
//		aperture.setApertureType(Aperture.SLIT);
//		((Slit)(aperture.getSelectedAperture())).setSlitWidth(20.8e-3);

		// Image of plane S, plus initialisation
		ImageOfPlaneNonInitialising imageOfPlane = new ImageOfPlaneNonInitialising("(S)'");
		imageOfPlane.setSelectedImageableComponent(selfConjugatePlane);
		double width = 4e-2;	// optimised so that on-axis intensity has maximum in magnified self-conjugate plane
		int pixelsX = 4096, pixelsY = 1;
		LightSource lightSource = new LightSource(
				"Laser",	// name
				width,	// physical width
				width*pixelsY/pixelsX, // physical height
				632.8e-9,	// wavelength (HeNe)
				pixelsX,	// columns
				pixelsY,	// rows
				LightSource.BEAM_UNIFORM_PLANE_WAVE	// beamType
			);
		ImageOfPlane sPrime = new ImageOfPlane(
				"(S)'",	// name
				imageOfPlane,
				lightSource,
				false	// initialise to null
			);
		
		// Distance f1
		Distance distance1 = new Distance();
		distance1.setName("f1");
		distance1.setDistance(f1);
		distance1.setBPM(true);
		distance1.setStepSize(BPM_STEPSIZE);
		distance1.setWidthOfAbsorbingBoundary(BPM_ABSORBING_BOUNDARY_WIDTH);
		
		// First mirror
		Lens lens1 = new Lens("M1", f1);
		
		// Distance f1 + f2, i.e. entire length of resonator
		Distance distance2 = new Distance();
		distance2.setName("f1 + f2");
		distance2.setDistance(f1+f2);
		distance2.setBPM(true);
		distance2.setStepSize(BPM_STEPSIZE);
		distance2.setWidthOfAbsorbingBoundary(BPM_ABSORBING_BOUNDARY_WIDTH);
		
		// Second mirror
		Lens lens2 = new Lens("M2", f2);
		
		// Distance f2
		Distance distance3 = new Distance();
		distance3.setName("f2");
		distance3.setDistance(f2);
		distance3.setBPM(true);
		distance3.setStepSize(BPM_STEPSIZE);
		distance3.setWidthOfAbsorbingBoundary(BPM_ABSORBING_BOUNDARY_WIDTH);
		
		// Mirror 1 viewing plane		
		Plane M1View = new Plane("behind M1");
		
		// Mirror 1 viewing plane		
		Plane M2View = new Plane("behind M2");
		
		// Add components
		addFirstComponent(sPrime);
		
		addAfter(sPrime, 0, distance1, 0);
		addAfter(distance1, 0, lens1, 0);
		
		// uncomment when adding aperture to mirror 1 (otherwise comment out)
		// addAfter(lens1, 0, aperture, 0);
		// addAfter(aperture, 0, M1View, 0);
		
		// uncomment when adding aperture to mirror 2 (otherwise comment out)
		addAfter(lens1, 0, M1View, 0);
		
		addAfter(M1View, 0, distance2, 0);
		addAfter(distance2, 0, lens2, 0);
		
		// uncomment when adding aperture to mirror 2 (otherwise comment out)
		addAfter(lens2, 0, aperture, 0);
		addAfter(aperture, 0, M2View, 0);
		
		// uncomment when adding aperture to mirror 1 (otherwise comment out)
		// addAfter(lens2, 0, M2View, 0);
		
		addAfter(M2View, 0, distance3, 0);
		addAfter(distance3, 0, selfConjugatePlane, 0);
	}
}