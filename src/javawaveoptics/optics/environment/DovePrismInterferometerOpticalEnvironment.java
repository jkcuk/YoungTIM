package javawaveoptics.optics.environment;

import java.io.Serializable;

import javawaveoptics.optics.component.*;
import javawaveoptics.optics.lightsource.LaguerreGaussianBeam;
import javawaveoptics.ui.workbench.WorkbenchFactory;

/**
 * Dove-prism-interferometer optical environment.
 * 
 * @author Johannes
 */
public class DovePrismInterferometerOpticalEnvironment extends AbstractOpticalEnvironment implements Serializable
{
	private static final long serialVersionUID = -1028771192603074437L;

	public DovePrismInterferometerOpticalEnvironment()
	{
		// Set up optical environment to use the extensive workbench for GUI applications
		super(WorkbenchFactory.TYPE_EXTENSIVE);
		
		LightSource lightSource = new LightSource();
		lightSource.setBeamType(LightSource.BEAM_LAGUERRE_GAUSSIAN);
		((LaguerreGaussianBeam)lightSource.getSelectedLightSource()).setL(1);

		BeamSplitter bs1 = new BeamSplitter("BS1");
		BeamSplitter bs2 = new BeamSplitter("BS2");
		DovePrism dp1 = new DovePrism("DP1", 90);
		DovePrism dp2 = new DovePrism("DP2", 0);
		Distance l1 = new Distance("L1", 1);
		Distance l2 = new Distance("L2", 1);
		Plane p = new Plane("P");
		ImageOfPlane imageOfP = new ImageOfPlane(p);
		Plane even = new Plane("Even");
		Plane odd = new Plane("Odd");

		addFirstComponent(lightSource);
		
		// the first beam splitter
		addAfter(lightSource, 0, bs1, 0);
		
		// top arm of the interferometer
		addAfter(bs1, 0, dp1, 0);
		addAfter(dp1, 0, l1, 0);
		
		// the second beam splitter
		addAfter(l1, 0, bs2, 0);
		
		// the bottom arm of the interferometer
		addAfter(bs1, 1, dp2, 0);
		addAfter(dp2, 0, l2, 0);
		addAfter(l2, 0, p, 0);
		addBefore(bs2, 1, imageOfP, 0);
		
		// the output arms
		addAfter(bs2, 0, even, 0);
		addAfter(bs2, 1, odd, 0);
	}
}