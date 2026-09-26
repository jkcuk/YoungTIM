package javawaveoptics.optics.environment;

import javawaveoptics.optics.aperture.AnnularAperture;
import javawaveoptics.optics.component.*;
import javawaveoptics.ui.workbench.WorkbenchFactory;

public class LimitedExampleOpticalEnvironment extends AbstractOpticalEnvironment
{
	private static final long serialVersionUID = -1311373233635344400L;
	
	private LightSource lightSource = new LightSource();
	private AnnularAperture circularAperture;
	private Distance propagate = new Distance("propagation distance", 0);
	private Plane plane = new Plane();
	
	public LimitedExampleOpticalEnvironment()
	{
		// Set up optical environment to use the limited workbench for GUI applications
		super(WorkbenchFactory.TYPE_LIMITED);

		// make the light source uniform
		lightSource.setBeamType(LightSource.BEAM_UNIFORM_PLANE_WAVE);
		// lightSource.setSelectedLightSource(new UniformPlaneWave());
		
		Aperture aperture = new Aperture(
				"Aperture",
				new AnnularAperture()
			);
//		aperture.setApertureType(Aperture.CIRCULAR_APERTURE);
//		circularAperture = (AnnularAperture)aperture.getSelectedAperture();

		addFirstComponent(lightSource);
		addAfter(lightSource, 0, aperture, 0);
		addAfter(aperture, 0, propagate, 0);
		addAfter(propagate, 0, plane, 0);
	}

	public LightSource getLightSource() {
		return lightSource;
	}

	public void setLightSource(LightSource lightSource) {
		this.lightSource = lightSource;
	}

	public AnnularAperture getCircularAperture() {
		return circularAperture;
	}

	public Distance getPropagate() {
		return propagate;
	}

	public void setPropagate(Distance propagate) {
		this.propagate = propagate;
	}

	public Plane getPlane() {
		return plane;
	}

	public void setPlane(Plane plane) {
		this.plane = plane;
	}
}
