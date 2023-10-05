package javawaveoptics.optics.component;

import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;


/**
 * Defines a phase-conjugating component.
 * 
 * @author Adam, Johannes
 */

public class PhaseConjugator extends AbstractSimpleOpticalComponent implements SimplePixelWiseOpticalComponentInterface, Serializable
{
	private static final long serialVersionUID = 8504681810947387503L;

	/*
	 * Fields
	 */
		
	/*
	 * GUI variables
	 */

	public PhaseConjugator(String name)
	{			
		super(name);		
	}

	public PhaseConjugator() {
		
		this("Phase-conjugator");
	}
	
	@Override
	public String getComponentTypeName() 
	{
		return "Phase-conjugator";
	}
	
	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		inputBeam.phaseConjugate();

		return inputBeam;
	}
	
	@Override
	public BeamCrossSection changePixelInInputBeam(int i, int j, BeamCrossSection inputBeam)
	{
		inputBeam.phaseConjugateElement(i, j);
		
		return inputBeam;
	}
	
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();
	}
}
