package javawaveoptics.ui.workbench;

import java.awt.event.ActionListener;
import java.io.Serializable;

import javawaveoptics.optics.component.AbstractLightSourceComponent;
import javawaveoptics.optics.component.AbstractOpticalComponent;
import javawaveoptics.optics.component.ConvertableComponent;
import javawaveoptics.optics.component.Plane;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Defines a workbench optical component popup menu, allowing the user to remove the optical
 * component to which this popup menu is attached. As such this class's constructor must be
 * supplied with a reference to an action listener which will facilitate the removal of the
 * optical component.
 * 
 * @author Sean
 */
public class ExtensiveWorkbenchOpticalComponentPopupMenu extends JPopupMenu implements Serializable
{
	private static final long serialVersionUID = -7857526985771193216L;
	
	public ExtensiveWorkbenchOpticalComponentPopupMenu(
			ActionListener popupMenuActionListener,
			AbstractOpticalComponent opticalComponent
		)
	{
		JMenuItem removeMenuItem = new JMenuItem("Remove");
		removeMenuItem.addActionListener(popupMenuActionListener);
		removeMenuItem.setActionCommand("Remove");
		add(removeMenuItem);

		// TODO uncomment
		// at the same time, uncomment public abstract AbstractOpticalComponent clone();

		if(!(opticalComponent instanceof AbstractLightSourceComponent))
		{
			JMenuItem duplicateMenuItem = new JMenuItem("Duplicate");
			duplicateMenuItem.addActionListener(popupMenuActionListener);
			duplicateMenuItem.setActionCommand("Duplicate");
			add(duplicateMenuItem);
		}

		JMenuItem enabledMenuItem = new JCheckBoxMenuItem("Enabled");
		enabledMenuItem.setSelected(true);
		enabledMenuItem.addActionListener(popupMenuActionListener);
		enabledMenuItem.setActionCommand("Enabled");
		add(enabledMenuItem);

		if(opticalComponent instanceof Plane)
		{
			JMenuItem clearDataMenuItem = new JMenuItem("Clear data");
			clearDataMenuItem.addActionListener(popupMenuActionListener);
			clearDataMenuItem.setActionCommand("Clear data");
			add(clearDataMenuItem);
		}
		
		if(opticalComponent instanceof ConvertableComponent)
		{
			JMenuItem convertMenuItem = new JMenuItem(((ConvertableComponent)opticalComponent).getConvertMenuItemText());
			convertMenuItem.addActionListener(popupMenuActionListener);
			convertMenuItem.setActionCommand("Convert");
			add(convertMenuItem);
		}
		
		// TODO add shift-to-right menu item (provided optical-train position < 
		
		// TODO add shift-to-left menu item (provided opticalTrainPosition > 0)
		
//		if((opticalComponent instanceof LightSource) || (opticalComponent instanceof ImageOfPlane))
//		{
//			JMenuItem replaceWithLightSourceOrImagePlaneMenuItem = new JMenuItem("Replace with light source/image");
//			replaceWithLightSourceOrImagePlaneMenuItem.addActionListener(popupMenuActionListener);
//			replaceWithLightSourceOrImagePlaneMenuItem.setActionCommand("Replace with light source/image");
//			add(replaceWithLightSourceOrImagePlaneMenuItem);
//		}
	}
}
