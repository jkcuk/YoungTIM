package javawaveoptics.ui;

import java.util.EventListener;

/**
 * Listener for when changes are made to an optical environment, such as adding or removing a component.
 * 
 * @author Sean
 */
public interface OpticalEnvironmentChangeListener extends EventListener
{
	/**
	 * Notifies the class implementing this listener that an change has been made and an appropriate
	 * action should be taken.
	 */
	public void changeMade(OpticalEnvironmentChangeEvent event);
}
