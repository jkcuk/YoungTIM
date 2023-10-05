package javawaveoptics.ui;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A JPanel that applies its tool-tip text to all its sub-components (well, those sub-components that are JComponents)
 * @author johannes
 */
public class JCPanel extends JPanel
{
	private static final long serialVersionUID = -3265016046463812000L;
	
	public JCPanel()
	{
		super();
		
		// super.setToolTipText("alter me!");
	}

	@Override
	public void setToolTipText(String text)
	{
		super.setToolTipText(text);
		
		// get all the components in the panel...
		Component[] components = getComponents();
		
		// ... and go through them all;
		for (int i = 0; i < components.length; i++) 
		{
			Component component = components[i]; 
			
			// for those that are JComponents, ...
			if (component instanceof JComponent)
			{
				// ... set the tool-tip text to that of the JCPanel
				((JComponent)component).setToolTipText(text);
			}
		}
	}
	
	@Override
	public Component add(Component component)
	{
		super.add(component);
		
		// if the tool-tip text is set, ...
		if(getToolTipText() != null)
		{
			// ..., and if the new component is a JComponent, ...
			if(component instanceof JComponent)
			{
				// ... set the tool-tip text to that of the JCPanel
				((JComponent)component).setToolTipText(getToolTipText());
			}
		}
		
		return component;
	}
}
