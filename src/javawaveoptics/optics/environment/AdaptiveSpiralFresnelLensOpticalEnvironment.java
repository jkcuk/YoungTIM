package javawaveoptics.optics.environment;

import java.io.Serializable;

import javawaveoptics.optics.component.*;
import javawaveoptics.optics.component.CylindricalLensSpiral.CylindricalLensSpiralType;
import javawaveoptics.optics.component.CylindricalLensSpiral.WindingBoundaryPlacementType;
import javawaveoptics.optics.lightsource.GaussianBeam;
import javawaveoptics.ui.workbench.WorkbenchFactory;
import library.maths.MyMath;

/**
 * Adaptive spiral-Fresnel lens optical environment.
 * 
 * @author Johannes
 */
public class AdaptiveSpiralFresnelLensOpticalEnvironment extends AbstractOpticalEnvironment implements Serializable
{
	private static final long serialVersionUID = 60810104927541279L;

	public AdaptiveSpiralFresnelLensOpticalEnvironment(CylindricalLensSpiralType cylindricalLensSpiralType)
	{
		// Set up optical environment to use the extensive workbench for GUI applications
		super(WorkbenchFactory.TYPE_EXTENSIVE);
		
		// System.out.println("spiral type = " + cylindricalLensSpiralType);
				
		LightSource lightSource = new LightSource(
				"Light source",
				1e-2, 1e-2,	// 1cm x 1cm
				632.8e-9,	// He-Ne
				1024, 1024,	// amplitude-matrix size
				LightSource.BEAM_GAUSSIAN
			);
		((GaussianBeam)lightSource.getSelectedLightSource()).setWaist(3e-3);
		
		// calculate b such that, at radial distance r0, the winding width is (approx.) w0
		double r0 = 0.5e-2;
		double w0 = 1e-3;
		double b = SpiralAdaptiveFresnelLens.r0w02b(cylindricalLensSpiralType, r0, w0);
		
		// calculate f1 such that, for deltaPhi = deltaPhi0, the focal length is focalLength0
		double focalLength0 = 1;
		double deltaPhi0 = MyMath.deg2rad(30);
		double f1 = SpiralAdaptiveFresnelLens.focalLenth0DeltaPhi0b2f1(focalLength0, deltaPhi0, b);
		
		SpiralAdaptiveFresnelLens adaptiveFresnelLens = new SpiralAdaptiveFresnelLens(
				"Adaptive lens",	// name
				cylindricalLensSpiralType, 
				b,	// b
				f1,	// f1
				deltaPhi0,	// deltaPhi
				0.0,	// deltaZ
				deltaPhi0,	// deltaPhi0
				focalLength0,	// focalLength0
				r0,	// r0
				w0,	// w0
				WindingBoundaryPlacementType.HALF_WAY,	// windingBoundaryPlacement
				true,	// alvarezLohmannWindingFocussing
				true,	// azimuthalPhaseRampComponensation
				true,	// showComponent1
				true	// showComponent2
			);
		
//		CylindricalLensSpiral cls1;
//		CylindricalLensSpiral cls2;
//		switch(cylindricalLensSpiralType)
//		{
//		case ARCHIMEDEAN:
//			// System.out.println("Archimedean");
//
//			cls1 = new CylindricalLensSpiral(
//					"Component 1", 
//					CylindricalLensSpiralType.ARCHIMEDEAN, 
//					1e-5,	// f1 
//					1e-4, 	// b
//					0,	// rotation angle
//					WindingBoundaryPlacementType.HALF_WAY, 
//					true	// AL winding focussing
//				);
//	
//			cls2 = new CylindricalLensSpiral(
//					"Component 2", 
//					CylindricalLensSpiralType.ARCHIMEDEAN, 
//					-1e-5,	// f1 
//					1e-4,	// b
//					MyMath.deg2rad(-20), 	// rotation angle
//					WindingBoundaryPlacementType.HALF_WAY, 
//					true	// AL winding focussing
//				);
//			break;
//		case FERMAT:
//
//			cls1 = new CylindricalLensSpiral(
//					"Component 1", 
//					CylindricalLensSpiralType.FERMAT, 
//					1e-7,	// f1 
//					1e-6, 	// b
//					0,	// rotation angle
//					WindingBoundaryPlacementType.ROTATED_SPIRAL, 
//					true	// AL winding focussing
//				);
//	
//			cls2 = new CylindricalLensSpiral(
//					"Component 2", 
//					CylindricalLensSpiralType.FERMAT, 
//					-1e-7,	// f1 
//					1e-6,	// b
//					MyMath.deg2rad(-20), 	// rotation angle
//					WindingBoundaryPlacementType.ROTATED_SPIRAL, 
//					true	// AL winding focussing
//				);
//			break;
//			
//		case HYPERBOLIC:
//			// System.out.println("Hyperbolic");
//
//			cls1 = new CylindricalLensSpiral(
//					"Component 1", 
//					CylindricalLensSpiralType.HYPERBOLIC, 
//					1,	// f1 
//					10, 	// b
//					0,	// rotation angle
//					WindingBoundaryPlacementType.HALF_WAY, 
//					true	// AL winding focussing
//				);
//	
//			cls2 = new CylindricalLensSpiral(
//					"Component 2", 
//					CylindricalLensSpiralType.HYPERBOLIC, 
//					-1,	// f1 
//					10,	// b
//					MyMath.deg2rad(20), 	// rotation angle
//					WindingBoundaryPlacementType.HALF_WAY, 
//					true	// AL winding focussing
//				);
//			break;
//		case LOGARITHMIC:
//		default:
//			// System.out.println("Logarithmic");
//
//			cls1 = new CylindricalLensSpiral(
//					"Component 1", 
//					CylindricalLensSpiralType.LOGARITHMIC, 
//					1e-2,	// f1 
//					0.1, 	// b
//					0,	// rotation angle
//					WindingBoundaryPlacementType.HALF_WAY, 
//					true	// AL winding focussing
//				);
//	
//			cls2 = new CylindricalLensSpiral(
//					"Component 2", 
//					CylindricalLensSpiralType.LOGARITHMIC, 
//					-1e-2,	// f1 
//					0.1,	// b
//					MyMath.deg2rad(20), 	// rotation angle
//					WindingBoundaryPlacementType.HALF_WAY, 
//					true	// AL winding focussing
//				);
//		}
		
		Plane planeBehind = new Plane();
		planeBehind.setName("Plane");
		
		addFirstComponent(lightSource);
		addAfter(lightSource, 0, adaptiveFresnelLens, 0);
		addAfter(adaptiveFresnelLens, 0, planeBehind, 0);
		
	}
}