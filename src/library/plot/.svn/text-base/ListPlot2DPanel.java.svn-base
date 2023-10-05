package library.plot;


import java.awt.*;
import java.awt.event.*;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import library.awt.*;
import library.list.*;
import library.maths.*;


// create a subclass of Canvas
/**
 * Classes for plotting 1-dimensional discrete lists of real numbers.
 * 
 * The list itself has to be described by an implementation of the
 * interface DoubleList1D.
 * 
 * example:
 * import johannes.plot.*;
 * import johannes.util.*;
 * 
 * // a class that describes the list y(i) = i in 20 points
 * class SimpleList implements DoubleList1D
 * {
 * 	public int getSize()
 * 	{
 * 		return 20;
 * 	}
 * 
 * 	public float getElement(int i)
 * 	{
 * 		return (float)i;
 * 	}
 * }
 * 
 * // show a plot in a panel of size 100 x 100
 * ListPlot2DPanel panel = new ListPlot2DPanel(new SimpleList(), "z = x + i y", 100, 100);
 * 
 * @author johannes
 *
 */
public class ListPlot2DPanel extends JPanel
implements MouseListener
{
	private static final long serialVersionUID = -7631753203142402239L;
	
	private DoubleList1D d;	// holds the plot information
	private boolean showIRangeAll, showYRangeAll;
	private int iRangeMin = 0, iRangeMax = 0; // range of horizontal index
	private double xMeshRangeMin = 0, xMeshRangeMax = 1; // horizontal mesh range
	private double xRangeMin, xRangeMax;
	private double yRangeMin = 0, yRangeMax = 1;
	private boolean
		useXRange, // use xRangeMin and xRangeMax rather than iRangeMin and iRangeMax
		storeDataAsReference = true, plotGrid = true,
		yLogPlot = false, doubleBuffering;
	private ListPlot2DPanelPopupMenu popupMenu;
	
	
	//////////////////
	// constructors //
	//////////////////
	
	public ListPlot2DPanel(DoubleList1D d, int width, int height,
		boolean storeDataAsReference)
	{
		super();	// create an instance of a Canvas
		
		// set the size of the Canvas
		super.setSize(width, height);
		
		setBackground(Color.lightGray);

		useXRange = false;

		showIRangeAll = true;
		showYRangeAll = true;
		
		doubleBuffering = false;
		
		// calculate and set image
		setList(d, storeDataAsReference);
		this.storeDataAsReference = storeDataAsReference;

		// add a mouse listener that brings up a controls window when the mouse
		// is clicked inside the DensityPlotCanvas
		addMouseListener(this);
		
		popupMenu = new ListPlot2DPanelPopupMenu(this);
		setComponentPopupMenu(popupMenu);
	}
	
	public ListPlot2DPanel(DoubleList1D d, int width, int height,
		boolean storeDataAsReference, double xMeshRangeMin, double xMeshRangeMax)
	{
		super();	// create an instance of a Canvas
		
		// set the size of the Canvas
		super.setSize(width, height);
		
		setBackground(Color.lightGray);

		useXRange = true;
		
		this.xMeshRangeMin = xMeshRangeMin;
		this.xMeshRangeMax = xMeshRangeMax;

		showIRangeAll = true;
		showYRangeAll = true;
		
		doubleBuffering = false;
		
		// calculate and set image
		setList(d, storeDataAsReference);
		this.storeDataAsReference = storeDataAsReference;

		// add a mouse listener that brings up a controls window when the mouse
		// is clicked inside the DensityPlotCanvas
		addMouseListener(this);

		popupMenu = new ListPlot2DPanelPopupMenu(this);
		add(popupMenu);
	}
	
	
	public void addPopupMenuItem(JMenuItem menuItem)
	{
		popupMenu.add(new JMenuItem("-"));
		popupMenu.add(menuItem);
	}
	

	// this method not just sets the list initially;
	// it can be invoked at any point if the list data change
	public void setList(DoubleList1D d,
		boolean storeDataAsReference)
	{
		if(d != null)
		{
			if(storeDataAsReference)
				this.d = d;
			else
				this.d = new DoubleArray1D(d);
		}
		else
			this.d = null;
		
		repaint();
	}

	public void setList(DoubleList1D d)
	{
		setList(d, storeDataAsReference);
	}
	
	public DoubleList1D getList()
	{
		return d;
	}
	
	
	//
	// the i range is the range of indices being plotted
	//
	
	public void setShowIRangeAll(boolean showIRangeAll)
	{
		this.showIRangeAll = showIRangeAll;
	}
	
	public boolean getShowIRangeAll()
	{
		return showIRangeAll;
	}
	
	public void setIRange(int iRangeMin, int iRangeMax)
	{
		useXRange = false;
		this.iRangeMin = iRangeMin;
		this.iRangeMax = iRangeMax;
	}
	
	public int getIRangeMin()
	{
		if(showIRangeAll) return 0;
		else return iRangeMin;	
	}
	
	public int getIRangeMax()
	{
		if(showIRangeAll) return d.getSize()-1;
		else return iRangeMax;	
	}
	
	
	//
	// the x mesh range is the range of real values that correspond to the
	// entire size of the list
	//
	
	public void setUseXRange(boolean useXRange)
	{
		this.useXRange = useXRange;
	}
	
	public boolean getUseXRange()
	{
		return useXRange;
	}

	// mesh range that corresponds to the entire list
	public void setXMeshRange(double xMeshRangeMin, double xMeshRangeMax)
	{
		useXRange = true;
		this.xMeshRangeMin = xMeshRangeMin;
		this.xMeshRangeMax = xMeshRangeMax;
	}
	
	public double getXMeshRangeMin()
	{
		return xMeshRangeMin;
	}
	
	public double getXMeshRangeMax()
	{
		return xMeshRangeMax;
	}
	
	public void setXRange(double xRangeMin, double xRangeMax)
	{
		useXRange = true;
		this.xRangeMin = xRangeMin;
		this.xRangeMax = xRangeMax;
	}
	
	public double getXRangeMin()
	{
		if(showIRangeAll) return xMeshRangeMin;
		else return xRangeMin;
	}
	
	public double getXRangeMax()
	{
		if(showIRangeAll) return xMeshRangeMax;
		else return xRangeMax;
	}
	
	
	//
	// y range
	//
	
	public void setYLogPlot(boolean yLogPlot)
	{
		this.yLogPlot = yLogPlot;
	}
	
	public boolean getYLogPlot()
	{
		return yLogPlot;
	}
	
	public void setShowYRangeAll(boolean showYRangeAll)
	{
		this.showYRangeAll = showYRangeAll;
	}

	public boolean getShowYRangeAll()
	{
		return showYRangeAll;
	}
	
	public void setYRange(double yRangeMin, double yRangeMax)
	{
		this.yRangeMin = yRangeMin;
		this.yRangeMax = yRangeMax;
	}
	
	public double getYRangeMin()
	{
		return yRangeMin;	
	}
	
	public double getYRangeMax()
	{
		return yRangeMax;	
	}
	
	
	//
	// other parameters
	//
	
	public void setDoubleBuffering(boolean doubleBuffering)
	{
		this.doubleBuffering = doubleBuffering;
	}
	
	public boolean getDoubleBuffering()
	{
		return doubleBuffering;
	}
	
	public void setPlotGrid(boolean plotGrid)
	{
		this.plotGrid = plotGrid;
	}
	
	public boolean getPlotGrid()
	{
		return plotGrid;
	}
	
	
	//
	// conversion i <-> x
	//
	
	// convert a horizontal index into the corresponding x value
	private double i2x(int i)
	{
		return xMeshRangeMin + (xMeshRangeMax - xMeshRangeMin) * i / (d.getSize()-1.0);
	}
	
	private int x2i(double x)
	{
		return Math.round(
			(float)
			(	(d.getSize()-1.0) * (x - xMeshRangeMin) 
				/ (xMeshRangeMax - xMeshRangeMin)
			)
		);
	}
	
	
	public void calculateIRange()
	{
		// calculate x range
		if(useXRange)
		{
			iRangeMin = x2i(getXRangeMin());
			iRangeMax = x2i(getXRangeMax());
		}
		else
		{
			iRangeMin = getIRangeMin();
			iRangeMax = getIRangeMax();
		}	
	}
	
	
	// calculate the full-size image, using the methods
	// getSize() and getElement(int i) supplied by the
	// implementation of the DiscreteRealList interface
	private void plot(Graphics g)
	{
		int h, v, hOld = 0, vOld = 0; // these variables hold horizontal/vertical positions
		double f, yMin, yMax, xMin, xMax;
		int i;
		Dimension size = getSize();
		
		// variables for double buffering
		Image buffer = null;
		Graphics screenGraphics = null;
		
		//////////////////////////////////////////////////////////////////
		// the following three lines and the last line of code for this //
		// method are for double buffering                              //
		//////////////////////////////////////////////////////////////////
		
		if(doubleBuffering)
		{
			buffer = createImage(size.width, size.height);
			screenGraphics = g;
			g = buffer.getGraphics();
		}
		
		calculateIRange();

		if(useXRange)
		{
			xMin = i2x(iRangeMin);
			xMax = i2x(iRangeMax);
		}
		else
		{
			xMin = iRangeMin;
			xMax = iRangeMax;
		}
		
		// find maximum and minimum of data
		if(showYRangeAll)
		{
			if(yLogPlot)
			{
				// for log plot take only values greater than zero into account
				
				// start off with first value greater than zero
				for(i=iRangeMin; (i<=iRangeMax) && (d.getElement(i)<=0); i++);
				
				if(i<=iRangeMax) yRangeMin = yRangeMax = d.getElement(i);
				
				// see whether the remaining values are greater or less
				for(; i<=iRangeMax; i++)
					if( (f=d.getElement(i)) > 0 )
					{
						if(f < yRangeMin) yRangeMin = f;
						else if(f > yRangeMax) yRangeMax = f;
					}
			}
			else
			{
				// find maximum and minimum of values
				
				// start off with first value
				yRangeMin = yRangeMax = d.getElement(iRangeMin);
				for(i=iRangeMin; i<=iRangeMax; i++) {
					f = d.getElement(i);
					if(f < yRangeMin) yRangeMin = f;
					else if(f > yRangeMax) yRangeMax = f;
				}
			
				// if not too much effort, plot from zero
				if((yRangeMin > 0) && (yRangeMin < 0.2*yRangeMax))
					yRangeMin = 0;
			}
		}

		if(yLogPlot)
		{
			yMin = MyMath.log10(yRangeMin);
			yMax = MyMath.log10(yRangeMax);
		}
		else
		{
			yMin = yRangeMin;
			yMax = yRangeMax;
		}

		///////////////////////////////////
		// plot a grid in the background //
		///////////////////////////////////
		
		if(plotGrid)
		{
			g.setColor(Color.green);
			String sy;
			
			if(yLogPlot) sy = "1E";
			else sy = "";
			
			ScaleTicks
				stx = new ScaleTicks(xMin, xMax, size.width),
				sty = new ScaleTicks(yMin, yMax, size.height);
			
			// the x grid
			double tick = stx.getFirstTick();
			for(i=0; i<stx.getNumberOfTicks(); i++)
			{
				h = (int)((double)(tick - xMin) * (double)size.width /
					(double)(xMax - xMin));
				g.drawLine(h, 0, h, size.height-1);
				g.drawString(""+(float)tick, h+2, size.height-1-2);
				tick += stx.getTickSeparation();
				if(Math.abs(tick) < Math.abs(1e-10*stx.getTickSeparation())) tick = 0.0;
			}
			
			// the y grid
			tick = sty.getFirstTick();
			for(i=0; i<sty.getNumberOfTicks(); i++)
			{
				v = (int)((size.height-1) * (yMax-tick)/(yMax - yMin));
				g.drawLine(0, v, size.width-1, v);
				g.drawString(sy+(float)tick, 2, v-2);
				tick += sty.getTickSeparation();
				if(Math.abs(tick) < Math.abs(1e-10*sty.getTickSeparation())) tick = 0.0;
			}
		
			g.setColor(Color.black);
		}
		
		
		////////////////////
		// plot the curve //
		////////////////////
		
		if(yLogPlot)
		{
			f = d.getElement(iRangeMin);
			if(f > 0) f = MyMath.log10(f);
			else f = yMin;
			
			hOld = 0;
			vOld = (int)((size.height-1) * (yMax-f)/(yMax - yMin));

			// create image
			for(i=iRangeMin+1; i<=iRangeMax; i++)
			{
				f = d.getElement(i);
				if(f > 0) f = MyMath.log10(f);
				else f = yMin;

				h = (int)((double)(i - iRangeMin) *
					(double)size.width / (double)(iRangeMax - iRangeMin));
				v = (int)((size.height-1) * (yMax-f)/(yMax - yMin));
			
				g.drawLine(hOld, vOld, h, v);
				
				hOld = h;
				vOld = v;
			}
		}
		else
		{
			f = d.getElement(iRangeMin);
			
			h = (int)((double)0 * (double)size.width / (double)(iRangeMax - iRangeMin));
			v = (int)((size.height-1) * (yMax-f)/(yMax - yMin));
			
			// create image
			for(i=iRangeMin+1; i<=iRangeMax; i++) {
				f = d.getElement(i);
				
				hOld = h;
				vOld = v;
				h = (int)((double)(i - iRangeMin) * (double)size.width / (double)(iRangeMax - iRangeMin));
				v = (int)((size.height-1) * (yMax-f)/(yMax - yMin));
				
				g.drawLine(hOld, vOld, h, v);
			}
		}
		
		//////////////////////////////////////////////////////////////////////////
		// for double buffering: copy the image from the buffer onto the screen //
		//////////////////////////////////////////////////////////////////////////
		
		if(doubleBuffering) screenGraphics.drawImage(buffer, 0, 0, null);
	}
	
	public void update(Graphics g)
	{
		if(d != null)
			paint(g);	// avoid filling with background colour before painting
		else
			super.update(g);	// fill with background colour, then paint
								// (that's what update does)
	}
	
	public void paint(Graphics g)
	{
            super.paint(g);

            if(d != null)
            {
                plot(g);	// draw image
            }
            else
            {
                g.drawString("- no plot data present -", 0, 10);
            }
	}
	
	
	//
	// do the settings dialog
	//
	
	public void settingsDialog()
	{
		// is a list present?
		if(d != null)
		{
//			// yes, there is a list present
//			Frame f = new Frame();
//			PlotControlsDialog pcd = new PlotControlsDialog(this, f);
//			pcd.setShowXRangeAll(showIRangeAll);
//			pcd.setShowYRangeAll(showYRangeAll);
//			pcd.setYLogPlot(yLogPlot);
//			if(useXRange) pcd.setXRange(getXRangeMin(), getXRangeMax());
//			else pcd.setIRange(getIRangeMin(), getIRangeMax());
//			pcd.setYRange(yRangeMin, yRangeMax);
//			pcd.setDoubleBuffering(doubleBuffering);
//			pcd.setPlotGrid(plotGrid);
//			pcd.show();
//			
//			// free system resources associated with dialog
//			pcd.dispose();
//
//			// free system resources associated with frame
//			f.dispose();
		}
	}


	public void showCopyInNewFrame()
	{
		PlotFrame pf =
			new PlotFrame(null, "copy of plot",
				getSize().width, getSize().height, true);
		
		// when the close box is clicked, close the PlotFrame and free
		// the corresponding system resources
		pf.addWindowListener(new WindowDisposingAdapter(pf));
	
		// set the parameters of the PlotFrame's PlotCanvas
		pf.c.setXMeshRange(getXMeshRangeMin(), getXMeshRangeMax());
		
		// set the parameters
		pf.c.setShowIRangeAll(getShowIRangeAll());
		pf.c.setShowYRangeAll(getShowYRangeAll());
		pf.c.setYLogPlot(getYLogPlot());
		if(getUseXRange()) pf.c.setXRange(getXRangeMin(), getXRangeMax());
		else pf.c.setIRange(getIRangeMin(), getIRangeMax());
		pf.c.setYRange(getYRangeMin(), getYRangeMax());
		pf.c.setDoubleBuffering(getDoubleBuffering());
		pf.c.setPlotGrid(getPlotGrid());
		
		pf.c.setList(getList(), true);
		
		pf.setVisible(true);
	}
	
	
	public void saveDataInTextFormat()
	{
		Frame f = new Frame();
		FileDialog fd = new FileDialog(f, "", FileDialog.SAVE);
			
		// save
		try
		{
			fd.setVisible(true);
			
			if(fd.getFile() != null)
			{
				// get the range of indices corresponding to the x range
				calculateIRange();
					
				DoubleList1DClass.writeToFile(
					fd.getDirectory() + fd.getFile(), d, getIRangeMin(), getIRangeMax());
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception during saving: " + e);
			// System.exit(0);
		}
		finally
		{
                    // free system resources associated with frame and dialog
                    fd.dispose();
                    f.dispose();
		}
	}
	
	
	////////////////////////////////////////
	// mouse listener interface functions //
	////////////////////////////////////////
	
	public void mouseClicked(MouseEvent me) 
	{}
	
	public void mouseEntered(MouseEvent me)
	{}
	
	public void mouseExited(MouseEvent me)
	{}

	public void mousePressed(MouseEvent me)
	{
		popupMenu.show(this, me.getX(), me.getY());
	}

	public void mouseReleased(MouseEvent me)
	{} 
}