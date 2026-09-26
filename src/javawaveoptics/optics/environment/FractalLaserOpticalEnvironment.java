package javawaveoptics.optics.environment;

import java.io.Serializable;

import javawaveoptics.optics.aperture.PolygonalAperture;
import javawaveoptics.optics.component.*;
import javawaveoptics.ui.workbench.WorkbenchFactory;

/**
 * Fractal laser optical environment.
 * 
 * @author Johannes
 */
public class FractalLaserOpticalEnvironment extends AbstractOpticalEnvironment implements Serializable
{
	private static final long serialVersionUID = 1682861758440827726L;
	
	public enum FractalLaserType
	{
		/**
		 * M=-2, 6-sided aperture
		 */
		M2A6("M=-2, 6-sided aperture"),
		/**
		 * M=-2, 7-sided aperture
		 */
		M2A7("M=-2, 7-sided aperture"),
		/**
		 * M=-3, 6-sided aperture
		 */
		M3A6("M=-3, 6-sided aperture");
		
		private String description;
		private FractalLaserType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}


	public FractalLaserOpticalEnvironment(FractalLaserType fractalLaserType)
	{
		// Set up optical environment to use the extensive workbench for GUI applications
		super(WorkbenchFactory.TYPE_EXTENSIVE);
		
		// set the parameters that depend on the choice of laser type
		int APERTURE_1_SIDES;	// number of sides of the polygonal aperture
		double M;	// transverse magnification (*-1)
		double R1, R2;

		switch(fractalLaserType)
		{
		case M2A7:
			APERTURE_1_SIDES = 7;
			M = 2;
			R2 = 165.07e-3;
			R1 = M*R2;	// radius of curvature of mirror 1
			break;
		case M3A6:
			APERTURE_1_SIDES = 6;
			M = 3;
			R2 = 148.889e-3;	// 165.07e-3,
			R1 = M*R2;	// radius of curvature of mirror 1
			break;
		case M2A6:
		default:
			APERTURE_1_SIDES = 6;
			M = 2;
			R2 = 148.889e-3;	// 165.07e-3,
			R1 = M*R2;	// radius of curvature of mirror 1
		}
		
		// parameters
		int
			AMPLITUDE_MATRIX_ELEMENTS_X = 1024,	// the amplitude matrix is represented by an array of AMPLITUDE_MATRIX_ELEMENTS_X by ...
			AMPLITUDE_MATRIX_ELEMENTS_Y = 1024,	// ... AMPLITUDE_MATRIX_ELEMENTS_Y complex numbers
			BPM_ABSORBING_BOUNDARY_WIDTH = 100;	// in pixels
		double
			AMPLITUDE_MATRIX_REPRESENTED_WIDTH = 10e-3,	// the width the amplitude matrix represents
			// radius of curvature of mirror 2
			// if chosen too small (e.g. 140e-3), phase gradient introduced by lens at the edge of the beam is beyond Nyquist limit
			// This can be seen by the FT of the field in the plane S_m reaching the edge, or by additional spots appearing in plane S_m after 1 round trip for exposure compensation +10;
			// fine-tuned so that there is a 3D intensity maximum at the centre of plane S_M
			APERTURE_1_RADIUS = 2.4e-3,	// 2.4e-3,	// radius of aperture of mirror 1
			APERTURE_1_ANGLE = 0,
			BPM_STEPSIZE = 20e-3;
		
		// calculate the focal lengths of the mirrors
		double
			f1 = R1/2,
			f2 = R2/2;
		
		boolean
			simpleLayout = true;	// if false, output coupler etc. are added

		if(simpleLayout)
		{
			// keep the number of planes to a minimum to reduce memory usage;
			// remove unnecessary components such as beam splitters (i.e. output couplers)
			// create all the components, ...
			ImageOfPlane imageOfSM = new ImageOfPlane();
			Distance f2Right = new Distance("f_2 (S_M to M_2)", f2);
			Lens m2 = new Lens("M_2", f2);
			// either uncomment this...
			Distance f2Left = new Distance("f_2 (M_2 to S_m)", f2);
			Plane sm = new Plane("S_m");	// the de-magnified self-conjugate plane
			Distance f1Left = new Distance("f_1 (S_m to M_1)", f1);
			// ... or this
			// Distance lLeft = new Distance("f_1 + f_2 (M_2 to M_1)", f1+f2);
			// ... until here; don't forget to comment out/uncomment the corresponding bits below
			
			// the aperture of mirror 1
			PolygonalAperture pa = new PolygonalAperture(
					"Polygonal aperture",
					APERTURE_1_SIDES,	// numberOfSides
					APERTURE_1_RADIUS,	// radius
					APERTURE_1_ANGLE,	// rotationAngle
					0,	// xCentre
					0	// yCentre
				);
			Aperture a1 = new Aperture("aperture of M_1", pa);
			Lens m1 = new Lens("M_1", f1);
			Distance f1Right = new Distance("f_1 (M_1 to S_M)", f1);
			Plane sM = new Plane("S_M");	// the magnified self-conjugate plane
			
			// ... set them up properly, ...
			
			// make image's object the plane S_M
			imageOfSM.setSelectedImageableComponent(sM);
			
			// start off the simulation with the light source initialised
			imageOfSM.setInitialiseToNull(false);
			
			// sets the amplitude matrix dimensions
			imageOfSM.getLightSource().setAmplitudeMatrixColumns(AMPLITUDE_MATRIX_ELEMENTS_X);
			imageOfSM.getLightSource().setAmplitudeMatrixRows(AMPLITUDE_MATRIX_ELEMENTS_Y);
			imageOfSM.getLightSource().setPhysicalWidth(AMPLITUDE_MATRIX_REPRESENTED_WIDTH);	// 2cm
			imageOfSM.getLightSource().setBeamType(LightSource.BEAM_UNIFORM_PLANE_WAVE);
			// imageOfA.getLightSource().adjustPhysicalHeight(); // now done automatically in setPhysicalWidth
			
			// make all propagation steps use the beam-propagation method
			f2Right.setBPM(true);
			f2Right.setStepSize(BPM_STEPSIZE);
			f2Right.setWidthOfAbsorbingBoundary(BPM_ABSORBING_BOUNDARY_WIDTH);

			// depending on what has been uncommented above, uncomment either this, ...
			f1Left.setBPM(true);
			f1Left.setStepSize(BPM_STEPSIZE);
			f1Left.setWidthOfAbsorbingBoundary(BPM_ABSORBING_BOUNDARY_WIDTH);
			f2Left.setBPM(true);
			f2Left.setStepSize(BPM_STEPSIZE);
			f2Left.setWidthOfAbsorbingBoundary(BPM_ABSORBING_BOUNDARY_WIDTH);
			// ... or this, ...
			// lLeft.setBPM(true);
			// lLeft.setStepSize(BPM_STEPSIZE);
			// lLeft.setWidthOfAbsorbingBoundary(BPM_ABSORBING_BOUNDARY_WIDTH);
			// ... until here
			f1Right.setBPM(true);
			f1Right.setStepSize(BPM_STEPSIZE);
			f1Right.setWidthOfAbsorbingBoundary(BPM_ABSORBING_BOUNDARY_WIDTH);
			
			// ... and wire them together
			
			addFirstComponent(imageOfSM);
			
			addAfter(imageOfSM, 0, f2Right, 0);
			addAfter(f2Right, 0, m2, 0);
			// depending on what has been uncommented above, uncomment either this, ...
			addAfter(m2, 0, f2Left, 0);
			addAfter(f2Left, 0, sm, 0);
			addAfter(sm, 0, f1Left, 0);
			addAfter(f1Left, 0, a1, 0);
			// ... or this, ...
			// addAfter(m2, 0, lLeft, 0);
			// addAfter(lLeft, 0, a1, 0);
			// ... until here
			addAfter(a1, 0, m1, 0);
			addAfter(m1, 0, f1Right, 0);
			addAfter(f1Right, 0, sM, 0);
		}
		else // not simple layout
		{
			// create all the components, ...
			ImageOfPlane imageOfSM = new ImageOfPlane();
			Distance f2Right = new Distance("f_2 (S_M to M_2)", f2);
			BeamSplitter bs2 = new BeamSplitter("output coupler 2");
			Lens m2 = new Lens("M_2", f2);
			Distance f2Left = new Distance("f_2 (M_2 to S_m)", f2);
			// Plane sm = new Plane("S_m");
			Distance f1Left = new Distance("f_1 (S_m to M_1)", f1);
			
			// the aperture of mirror 1
			PolygonalAperture pa = new PolygonalAperture(
					"Polygonal aperture",
					APERTURE_1_SIDES,	// numberOfSides
					APERTURE_1_RADIUS,	// radius
					APERTURE_1_ANGLE,	// rotationAngle
					0,	// xCentre
					0	// yCentre
				);
			Aperture a1 = new Aperture("aperture of M_1", pa);
			BeamSplitter bs1 = new BeamSplitter("output coupler 1");
			Lens m1 = new Lens("M_1", f1);
			Distance f1Right = new Distance("f_1 (M_1 to S_M)", f1);
			Plane sM = new Plane("S_M");	// the magnifying self-conjugate plane

			// ... set them up properly, ...

			// make image's object the plane S_M
			imageOfSM.setSelectedImageableComponent(sM);

			// start off the simulation with the light source initialised
			imageOfSM.setInitialiseToNull(false);

			// sets the amplitude matrix dimensions
			imageOfSM.getLightSource().setAmplitudeMatrixColumns(AMPLITUDE_MATRIX_ELEMENTS_X);
			imageOfSM.getLightSource().setAmplitudeMatrixRows(AMPLITUDE_MATRIX_ELEMENTS_Y);
			imageOfSM.getLightSource().setPhysicalWidth(AMPLITUDE_MATRIX_REPRESENTED_WIDTH);	// 2cm
			// imageOfA.getLightSource().adjustPhysicalHeight(); // now done automatically in setPhysicalWidth

			// ... and wire them up

			addFirstComponent(imageOfSM);

			addAfter(imageOfSM, 0, f2Right, 0);
			addAfter(f2Right, 0, bs2, 0);
			addAfter(bs2, 0, m2, 0);
			addAfter(m2, 0, f2Left, 0);
			addAfter(f2Left, 0, f1Left, 0);
			addAfter(f1Left, 0, a1, 0);
			addAfter(a1, 0, bs1, 0);
			addAfter(bs1, 0, m1, 0);
			addAfter(m1, 0, f1Right, 0);
			addAfter(f1Right, 0, sM, 0);
		}
	}
}