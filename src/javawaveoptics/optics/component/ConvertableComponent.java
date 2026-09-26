package javawaveoptics.optics.component;

import javawaveoptics.ui.workbench.ExtensiveWorkbenchOpticalComponent;

/**
 * A component that can be converted into some other, equivalent, component or combination of components.
 */
public interface ConvertableComponent {
	/**
	 * @return the text of the menu item for converting the component
	 */
	public String getConvertMenuItemText();
	
	
	/**
	 * perform the conversion!
	 */
	public void convert(ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent);
}
