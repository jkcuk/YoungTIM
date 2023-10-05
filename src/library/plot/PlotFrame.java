/*

Classes for plotting 1-dimensional discrete lists of real numbers.

The list itself has to be described by an implementation of the
interface DoubleList1D.


public classes:

  public class PlotCanvas extends Canvas

  public class PlotFrame extends Frame


example:

	import johannes.plot.*;
	import johannes.util.*;
	
	// a class that describes the list y(i) = i
	// in 20 points
	class SimpleList implements DoubleList1D
	{
		public int getSize()
		{
			return 20;
		}

		public float getElement(int i)
		{
			return (float)i;
		}
	}
	
	// show a plot in a window of size 100 x 100
	PlotFrame pf = new PlotFrame(new SimpleList(), "z = x + i y", 100, 100);
	
	// closes PlotFrame and frees system resources when the close box is clicked
	pf.addWindowListener(new WindowDisposingAdapter());
*/



package library.plot;


import java.awt.*;
import java.awt.event.*;
import java.io.*;

import library.list.*;


// Create a subclass of Frame
public class PlotFrame extends Frame
implements Serializable
{
	private static final long serialVersionUID = 7012190888081769172L;
	
	public PlotCanvas c;
	
	public PlotFrame(DoubleList1D d, String title, int width, int height,
		boolean storeDataAsReference)
	{
		super(title);	// create an instance of a frame
		
		super.setSize(width, height);
		
		// initialise the component adapter, so that the window can be
		// resized
		PlotFrameComponentAdapter ca =
			new PlotFrameComponentAdapter(this);
		addComponentListener(ca);

		// add Canvas with density plot
		c = new PlotCanvas(d, width, height, storeDataAsReference);
		add(c);
	}

	public PlotFrame(DoubleList1D d, String title, int width, int height,
		boolean storeDataAsReference,
		double xMeshRangeMin, double xMeshRangeMax)
	{
		super(title);	// create an instance of a frame
		
		super.setSize(width, height);
		
		// initialise the component adapter, so that the window can be
		// resized
		PlotFrameComponentAdapter ca =
			new PlotFrameComponentAdapter(this);
		addComponentListener(ca);

		// add Canvas with density plot
		c = new PlotCanvas
		(d, width, height, storeDataAsReference, xMeshRangeMin, xMeshRangeMax);
		add(c);
	}

	
	public void componentResized()
	{
		Dimension size = getSize();
		
		c.setSize(size.width, size.height);
	}
} // PlotFrame


// allows notification when the window is resized
class PlotFrameComponentAdapter extends ComponentAdapter
implements Serializable
{
	PlotFrame pf;
	
	PlotFrameComponentAdapter(PlotFrame pf)
	{
		this.pf = pf;
	}
	
	public void componentResized(ComponentEvent ce)
	{
		pf.componentResized();
	}
}
