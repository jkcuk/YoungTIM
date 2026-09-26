/*
*/

package library.plot;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import library.list.*;


// Create a subclass of Frame
public class DensityPlotFrame extends Frame
implements Serializable
{
	private static final long serialVersionUID = 7453230125623793210L;
	
	public DensityPlotCanvas c;
	
	public DensityPlotFrame(
		DoubleList2D d, String title, int width, int height,
		boolean storeListAsReference)
	{
		super(title);	// create an instance of a frame
		
		super.setSize(width, height);
		
		// initialise the component adapter, so that the window can be
		// resized
		DensityPlotFrameComponentAdapter ca =
			new DensityPlotFrameComponentAdapter(this);
		addComponentListener(ca);

		// add Canvas with density plot
		c = new DensityPlotCanvas(d, width, height, storeListAsReference);
		c.setResizable(true);
		c.setFrame(this);
		add(c);
	}
	
	public DensityPlotFrame(
		DoubleList2D d, String title, int width, int height,
		boolean storeListAsReference,
		double xMeshRangeMin, double xMeshRangeMax,
		double yMeshRangeMin, double yMeshRangeMax)
	{
		super(title);	// create an instance of a frame
		
		super.setSize(width, height);
		
		// initialise the component adapter, so that the window can be
		// resized
		DensityPlotFrameComponentAdapter ca =
			new DensityPlotFrameComponentAdapter(this);
		addComponentListener(ca);

		// add Canvas with density plot
		c = new DensityPlotCanvas(
			d, width, height, storeListAsReference,
			xMeshRangeMin, xMeshRangeMax, yMeshRangeMin, yMeshRangeMax );
		c.setResizable(true);
		c.setFrame(this);
		add(c);
	}


	public void componentResized()
	{
		Dimension size = getSize();
		
		c.setSize(size.width, size.height);
	}
} // DensityPlotFrame


// the little popup menu
class DensityPlotCanvPopupMenu extends PopupMenu
implements ActionListener
{
	DensityPlotCanvas densityPlotCanvas;
	private MenuItem
		saveAsEPSMenuItem,
		exportMenuItem,
		openInFrameMenuItem,
		settingsMenuItem;
	
	public DensityPlotCanvPopupMenu(DensityPlotCanvas densityPlotCanvas)
	{
		super();
		
		this.densityPlotCanvas = densityPlotCanvas;
		
		settingsMenuItem = new MenuItem("Settings...");
		settingsMenuItem.addActionListener(this);
		add(settingsMenuItem);
		add(new MenuItem("-"));
		openInFrameMenuItem = new MenuItem("Show Copy in New Window");
		openInFrameMenuItem.addActionListener(this);
		add(openInFrameMenuItem);
		add(new MenuItem("-"));
		exportMenuItem = new MenuItem("Export Plot Data...");
		exportMenuItem.addActionListener(this);
		add(exportMenuItem);
		add(new MenuItem("-"));
		saveAsEPSMenuItem = new MenuItem("Save As EPS...");
		saveAsEPSMenuItem.addActionListener(this);
		add(saveAsEPSMenuItem);
	}


	//
	// ActionListener method
	//
	
	public synchronized void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == exportMenuItem)
		{
			densityPlotCanvas.saveRawData();
		}
		if(ae.getSource() == openInFrameMenuItem)
		{
			densityPlotCanvas.showCopyInNewFrame();
		}
		if(ae.getSource() == settingsMenuItem)
		{
			densityPlotCanvas.settingsDialog();
		}
		if(ae.getSource() == saveAsEPSMenuItem)
		{
			densityPlotCanvas.saveAsEPS();
		}
	}
}