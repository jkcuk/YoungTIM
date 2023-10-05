package library.plot;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

//the little popup menu
class ListPlot2DPanelPopupMenu extends JPopupMenu
implements ActionListener
{
	private static final long serialVersionUID = -6474699684305375580L;

	ListPlot2DPanel panel;
	
	private MenuItem
//		exportMenuItem,
//		openInFrameMenuItem,
		settingsMenuItem;
	
	public ListPlot2DPanelPopupMenu(ListPlot2DPanel panel)
	{
		super();
		
		this.panel = panel;
		
		JMenuItem settingsMenuItem = new JMenuItem("Settings...");		
		settingsMenuItem.addActionListener(this);	
		add(settingsMenuItem);
		
		add(new JMenuItem("-"));
		
//		openInFrameMenuItem = new MenuItem("Show copy in new window");
//		openInFrameMenuItem.addActionListener(this);
//		add(openInFrameMenuItem);
//		add(new MenuItem("-"));
//		exportMenuItem = new MenuItem("Export plot data...");
//		exportMenuItem.addActionListener(this);
//		add(exportMenuItem);
	}


	//
	// ActionListener method
	//
	
	public void actionPerformed(ActionEvent ae)
	{
//		if(ae.getSource() == exportMenuItem)
//		{
//			plotCanvas.saveDataInTextFormat();
//		}
//		if(ae.getSource() == openInFrameMenuItem)
//		{
//			plotCanvas.showCopyInNewFrame();
//		}
		if(ae.getSource() == settingsMenuItem)
		{
			panel.settingsDialog();
		}
	}
}