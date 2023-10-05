package javawaveoptics.optics.environment;

import java.io.Serializable;

import javawaveoptics.optics.aperture.Grating;
import javawaveoptics.optics.component.*;
import javawaveoptics.optics.lightsource.LaguerreGaussianBeam;
import javawaveoptics.ui.workbench.WorkbenchFactory;

/**
 * Dove-prism-interferometer optical environment.
 * 
 * @author Johannes
 */
public class ResonatorInterfaceOpticalEnvironment extends AbstractOpticalEnvironment implements Serializable
{
	private static final long serialVersionUID = -2483428236429712501L;

	public ResonatorInterfaceOpticalEnvironment()
	{
		// Set up optical environment to use the extensive workbench for GUI applications
		super(WorkbenchFactory.TYPE_EXTENSIVE);
		
		LightSource lightSource = new LightSource();
		lightSource.setBeamType(LightSource.BEAM_UNIFORM_PLANE_WAVE);
		// ((UniformPlaneWave)lightSource.getSelectedLightSource()).;
		
		Plane r = new Plane("Reflected");
		Mirror m1 = new Mirror("M1", 0.9);
		Plane behindM1Right2Left = new Plane("Behind M_1 <-");
		ImageOfPlane behindM1Right2LeftImage = new ImageOfPlane();
		behindM1Right2LeftImage.setSelectedImageableComponent(behindM1Right2Left);
		double arrayPeriod = lightSource.getPhysicalWidth() / 3.;
		double focalLength1 = 0.5;
		double focalLength2 = 0.5;
		LensletArray la1 = new LensletArray("LA1", arrayPeriod, focalLength1, 0, 0, 0);
		Distance f1 = new Distance("f_1", focalLength1);
		Plane fourierPlaneLeft2Right = new Plane("FT plane ->");
		Plane fourierPlaneLeft2RightAfterFilter = new Plane("FT plane ->");
		Plane fourierPlaneRight2Left = new Plane("FT plane <-");
		Aperture fourierFilter = new Aperture("Fourier filter", new Grating(
				"Grating that makes up Fourier filter", // name
				arrayPeriod,	// gratingPeriod
				0.1*arrayPeriod,	// slitWidth
				0,	// rotationAngle
				0,	// xCentre
				0,	// yCentre
				false,	// softEdges
				0,	// edgeWidth
				true	// showAlsoPerpendicularGrating
			));
		Distance f2 = new Distance("f_2", focalLength2);
		LensletArray la2 = new LensletArray("LA2", arrayPeriod, focalLength2, 0, 0, 0);
		Distance phaseShift = new Distance("LA2 -> M2", 400e-9);	// essentially a phase shift to make the resonator resonant
		Mirror m2 = new Mirror("M2", 0.999);
		Plane t = new Plane("Transmitted");

		addFirstComponent(lightSource);
		
		// mirror 1
		addAfter(lightSource, 0, m1, 0);
		addAfter(behindM1Right2LeftImage, 0, m1, 1);
		
		// light reflected from mirror 1
		addAfter(m1, 0, r, 0);
		
		// light transmitted through mirror 1
		addAfter(m1, 1, la1, 0);
		addAfter(la1, 0, f1, 0);
		addAfter(f1, 0, fourierPlaneLeft2Right, 0);
		addAfter(fourierPlaneLeft2Right, 0, fourierFilter, 0);
		addAfter(fourierFilter, 0, fourierPlaneLeft2RightAfterFilter, 0);
		addAfter(fourierPlaneLeft2RightAfterFilter, 0, f2, 0);
		addAfter(f2, 0, la2, 0);
		
		// mirror 2
		addAfter(la2, 0, phaseShift, 0);
		addAfter(phaseShift, 0, m2, 0);
		
		// light reflected from mirror 2
		CloneOfComponent c_la2 = new CloneOfComponent(la2);
		CloneOfComponent c_f2 = new CloneOfComponent(f2);
		CloneOfComponent c_fourierFilter = new CloneOfComponent(fourierFilter);
		CloneOfComponent c_f1 = new CloneOfComponent(f1);
		CloneOfComponent c_la1 = new CloneOfComponent(la1);
		
		addAfter(m2, 0, c_la2, 0);
		addAfter(c_la2, 0, c_f2, 0);
		addAfter(c_f2, 0, fourierPlaneRight2Left, 0);
		addAfter(fourierPlaneRight2Left, 0, c_fourierFilter, 0);
		addAfter(c_fourierFilter, 0, c_f1, 0);
		addAfter(c_f1, 0, c_la1, 0);
		addAfter(c_la1, 0, behindM1Right2Left, 0);
		
		// light transmitted through mirror 2
		addAfter(m2, 1, t, 0);
	}
}