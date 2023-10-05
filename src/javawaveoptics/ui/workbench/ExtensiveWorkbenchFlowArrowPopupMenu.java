package javawaveoptics.ui.workbench;

import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.BitSet;

import javawaveoptics.optics.component.OpticalComponentFactory;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Defines a workbench flow arrow popup menu, allowing the user to add a new optical component
 * in place of the flow arrow this popup menu is attached to. Due to this, the constructor
 * requires a reference to an action listener to send click events to, usually part of the
 * graphical user interface.
 * 
 * @author Sean
 */
public class ExtensiveWorkbenchFlowArrowPopupMenu extends JPopupMenu implements Serializable
{
	private static final long serialVersionUID = -1406868558637882447L;
	
	public ExtensiveWorkbenchFlowArrowPopupMenu(boolean lightSourcesAllowed, BitSet availableLightSourceComponentsBitField, BitSet availableNonLightSourceComponentsBitField, ActionListener popupMenuActionListener)
	{		
//		JMenuItem insertLabel = new JMenuItem("Insert...");
//		insertLabel.setEnabled(false);	// grey it out
//		add(insertLabel);

		if(OpticalComponentFactory.getNumberOfEnabledLightSourceComponents(availableLightSourceComponentsBitField) > 0)
		{
			JMenuItem sourcesLabel = new JMenuItem("Insert beam sources (start of optical train only)");
			sourcesLabel.setEnabled(false);	// grey it out
			add(sourcesLabel);
			
			for(String componentName : OpticalComponentFactory.getLightSourceComponents(availableLightSourceComponentsBitField))
			{
				JMenuItem insertMenuItem = new JMenuItem("  " + componentName);
				
				insertMenuItem.addActionListener(popupMenuActionListener);
				insertMenuItem.setEnabled(lightSourcesAllowed);
				insertMenuItem.setActionCommand("Insert " + componentName);
			
				add(insertMenuItem);
			}	
		}

		if(OpticalComponentFactory.getNumberOfEnabledNonLightSourceComponents(availableNonLightSourceComponentsBitField) > 0)
		{
			JMenuItem opticalComponentsLabel = new JMenuItem("Insert optical components");
			opticalComponentsLabel.setEnabled(false);	// grey it out
			add(opticalComponentsLabel);
			
			for(String componentName : OpticalComponentFactory.getNonLightSourceComponents(availableNonLightSourceComponentsBitField))
			{
				JMenuItem insertMenuItem = new JMenuItem("  " + componentName);
				
				insertMenuItem.addActionListener(popupMenuActionListener);
				insertMenuItem.setActionCommand("Insert " + componentName);
			
				add(insertMenuItem);
			}
		}
	}
}
