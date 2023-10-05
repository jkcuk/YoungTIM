package javawaveoptics.optics.environment;

import java.io.Serializable;

import javawaveoptics.optics.component.*;
import javawaveoptics.ui.workbench.WorkbenchFactory;

/**
 * Autostereogram resonator optical environment.
 * 
 * @author Sean
 */
public class AutostereogramOpticalEnvironment extends AbstractOpticalEnvironment implements Serializable
{
	private static final long serialVersionUID = 6060186090924463526L;

	public AutostereogramOpticalEnvironment()
	{
		// Set up optical environment to use the extensive workbench for GUI applications
		super(WorkbenchFactory.TYPE_EXTENSIVE);
		
		LightSource lightSource = new LightSource();
		Plane plane1 = new Plane();
		AutostereogramResonator resonator = new AutostereogramResonator();
		Plane plane2 = new Plane();
		
		addFirstComponent(lightSource);
		
		addAfter(lightSource, 0, plane1, 0);
		addAfter(plane1, 0, resonator, 0);
		addAfter(resonator, 0, plane2, 0);
	}
}