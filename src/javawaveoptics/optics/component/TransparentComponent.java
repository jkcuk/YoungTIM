package javawaveoptics.optics.component;

import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;


/**
 * Defines a component that does nothing.
 * 
 * @author Johannes
 */

public class TransparentComponent extends AbstractSimpleOpticalComponent implements SimplePixelWiseOpticalComponentInterface, Serializable
{
	private static final long serialVersionUID = -6437689917973276132L;

	/*
	 * Fields
	 */
		
	/*
	 * GUI variables
	 */

	public TransparentComponent(String name)
	{			
		super(name);		
	}

	public TransparentComponent() {
		
		this("Transparent component");
	}
	
	@Override
	public String getComponentTypeName() 
	{
		return "Transparent component";
	}
	
	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{
		// do nothing
		return inputBeam;
	}
	
	@Override
	public BeamCrossSection changePixelInInputBeam(int i, int j, BeamCrossSection inputBeam)
	{
		// do nothing
		return inputBeam;
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();
	}
}
