package library.opticalSystem.laser;


import java.io.*;
import java.util.*;

import library.opticalSystem.*;
import library.optics.*;


public class Laser1DInitialiser
implements ElementTypesProvider, Serializable
{
	private static final long serialVersionUID = 2312038800235378092L;

	public String getFileName()
	{
		return "untitled (a stable resonator)";
	}
	
	public WaveTrace1DParameters getWaveTraceParameters()
	{
		WaveTrace1DParameters p = new WaveTrace1DParameters();
		
		p.matrixSize = 8192;
		p.physicalSize = 20e-3;
		p.lambda = 633e-9;
		
		return p;
	}

	// a stable resonator with a Gaussian lowest-loss eigenmode
	public OpticalSystem1D getInitialResonator()
	{
		// create a new OpticalSystem1D
		OpticalSystem1D resonator = new OpticalSystem1D();
		
		// add some random components to the optical system
		resonator.add(new ResonatorBeginning1D("left mirror", 0));
		resonator.add(new PlotPlane1D("P1", 0, 0, true, false, false, false));
		resonator.add(new Lens1D("L1", 0, 100e-3));
		resonator.add(new SlitAperture1D("A1", 0, 0.5e-3, 0));
		resonator.add(new UnfoldedResonatorEnd1D("right mirror", 100e-3));
		
		return resonator;
	}
	
	public Vector getElementTypes()
	{
		// collect all possible types of optical element in the Vector elementTypes
		// for registration with the OpticalSystem1DDialog
		Vector elementTypes = new Vector();
		elementTypes.addElement(new PlotPlane1D());
		elementTypes.addElement(new Lens1D());
		elementTypes.addElement(new SlitAperture1D());
                elementTypes.addElement(new AsymmetricSlitAperture1D());
		elementTypes.addElement(new SideBySideLensSuperposition1D());
		elementTypes.addElement(new SideBySideLenses1D());
		elementTypes.addElement(new ImperfectSideBySideLenses1D());
		elementTypes.addElement(new GaussianGainProfile1D());
		elementTypes.addElement(new Hologram1D());
		elementTypes.addElement(new ResonatorBeginning1D());
		elementTypes.addElement(new UnfoldedResonatorEnd1D());
		elementTypes.addElement(new FoldedResonatorEnd1D());
		
		return elementTypes;
	}
}