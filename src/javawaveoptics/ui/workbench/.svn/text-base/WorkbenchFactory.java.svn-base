package javawaveoptics.ui.workbench;

import javawaveoptics.optics.environment.AbstractOpticalEnvironment;

public class WorkbenchFactory
{
	public static final int
		TYPE_EXTENSIVE = 0,
		TYPE_LIMITED = 1;
	
	public static AbstractWorkbench createWorkbench(AbstractOpticalEnvironment opticalEnvironment)
	{
		int type = opticalEnvironment.getWorkbenchType();
		
		switch(type)
		{
			case 1:
				return new LimitedWorkbench(opticalEnvironment);
		
			case 0:
			default:
				return new ExtensiveWorkbench(opticalEnvironment);
		}
	}
}
