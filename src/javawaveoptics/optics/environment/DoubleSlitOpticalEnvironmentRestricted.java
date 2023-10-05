package javawaveoptics.optics.environment;

import java.io.Serializable;

import javawaveoptics.optics.aperture.ApertureType;
import javawaveoptics.optics.component.*;
import javawaveoptics.ui.workbench.WorkbenchFactory;

/**
 * Double-slit optical environment.
 * This is normally the default optical environment.
 * 
 * @author Sean
 * @author Johannes
 */
public class DoubleSlitOpticalEnvironmentRestricted extends AbstractOpticalEnvironment implements Serializable
{
	private static final long serialVersionUID = 6060186090924463526L;

	public DoubleSlitOpticalEnvironmentRestricted()
	{
		// Set up optical environment to use the extensive workbench for GUI applications
		super(WorkbenchFactory.TYPE_EXTENSIVE);
		
		// Disable all light sources and components initially
		disableAllLightSources();
		disableAllNonLightSources();
		
		LightSource lightSource = new LightSource();
		lightSource.setName("Laser beam");
		enableLightSource("Light source");
		
		Aperture aperture = new Aperture("Double slit", ApertureType.getAperture(ApertureType.DOUBLE_SLIT));
		enableNonLightSource("Aperture");
//		((DoubleSlit)aperture.getSelectedAperture()).setSlitSeparation(1e-3);
//		((DoubleSlit)aperture.getSelectedAperture()).setSlitWidth(250e-6);

		double f = 0.6;	// with this focal length, the Fourier transform represented by the amplitude matrix fits within the represented physical size
		Lens lens = new Lens("Far-field lens", f);
		enableNonLightSource("Lens");
		
		Distance focalDistance = new Distance();
		focalDistance.setName("Focal distance");
		focalDistance.setDistance(f);
		enableNonLightSource("Distance");
		
		Plane farField = new Plane();
		farField.setName("Far field");
		enableNonLightSource("Plane");
		
		addFirstComponent(lightSource);
		
		addAfter(lightSource, 0, aperture, 0);
		addAfter(aperture, 0, lens, 0);
		addAfter(lens, 0, focalDistance, 0);
		addAfter(focalDistance, 0, farField, 0);
		
		/*
		BeamSplitter beamSplitter = new BeamSplitter();
		Propagate propagate = new Propagate();
		BeamSplitter beamSplitter2 = new BeamSplitter("Beam Splitter 2");
		
		addAfter(startComponent, 0, aperture, 0);
		addAfter(aperture, 0, beamSplitter, 0);
		addAfter(beamSplitter, 1, propagate, 0);
		addAfter(propagate, 0, beamSplitter2, 0);
		*/
	}
}