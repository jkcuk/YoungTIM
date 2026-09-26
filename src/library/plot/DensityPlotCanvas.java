/*
*/

package library.plot;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import library.awt.*;
import library.list.*;


// create a subclass of Canvas
public class DensityPlotCanvas extends Canvas
implements MouseListener
{
	private static final long serialVersionUID = 4287718187996391726L;
	
	// This must be transient to prevent the plot data being saved during
	// serialization
	private transient DoubleList2D d; // holds the list
	
	private double brightness, contrast;
	private boolean showIRangeAll, showJRangeAll, resizable = false, plotGrid = false;
	private int iRangeMin = 0, iRangeMax = 0, jRangeMin = 0, jRangeMax = 0;
	private double
		xMeshRangeMin = 0, xMeshRangeMax = 1, // horizontal mesh range
		yMeshRangeMin = 0, yMeshRangeMax = 1, // vertical mesh range
		xRangeMin, xRangeMax, yRangeMin, yRangeMax;
	private boolean useXRange, useYRange;
	private transient Image scaledImage;	// holds the density plot
	private Frame frame;
	private boolean storeListAsReference = true;
	private DensityPlotCanvPopupMenu popupMenu;
	
	public DensityPlotCanvas(DoubleList2D d,
		int width, int height, boolean storeListAsReference)
	{
		super();	// create an instance of a Canvas
		
		// set the size of the Canvas
		super.setSize(width, height);
		
		setBackground(Color.lightGray);
		
		//
		brightness = 0.5;
		contrast = 1.0;
		
		useXRange = false;
		useYRange = false;

		showIRangeAll = true;
		showJRangeAll = true;
		
		plotGrid = false;
		
		// calculate and set image
		setList(d, storeListAsReference);
		this.storeListAsReference = storeListAsReference;
		
		// add a mouse listener that brings up a controls window when the mouse
		// is clicked inside the DensityPlotCanvas
		addMouseListener(this);
		
		popupMenu = new DensityPlotCanvPopupMenu(this);
		add(popupMenu);
	}
	
	public DensityPlotCanvas(DoubleList2D d,
		int width, int height, boolean storeListAsReference,
		double xMeshRangeMin, double xMeshRangeMax,
		double yMeshRangeMin, double yMeshRangeMax )
	{
		super();	// create an instance of a Canvas
		
		// set the size of the Canvas
		super.setSize(width, height);
		
		setBackground(Color.lightGray);
		
		useXRange = true;
		useYRange = true;
		
		this.xMeshRangeMin = xMeshRangeMin;
		this.xMeshRangeMax = xMeshRangeMax;
		this.yMeshRangeMin = yMeshRangeMin;
		this.yMeshRangeMax = yMeshRangeMax;

		//
		brightness = 0.5;
		contrast = 1.0;
		
		showIRangeAll = true;
		showJRangeAll = true;
		
		plotGrid = false;
		
		// calculate and set image
		setList(d, storeListAsReference);
		this.storeListAsReference = storeListAsReference;
		
		// add a mouse listener that brings up a controls window when the mouse
		// is clicked inside the DensityPlotCanvas
		addMouseListener(this);
		
		popupMenu = new DensityPlotCanvPopupMenu(this);
		add(popupMenu);
	}
	

	public synchronized void setList(
		DoubleList2D d,
		boolean storeListAsReference)
	{
		if(d != null)	// make sure a list is defined
		{
			if(storeListAsReference) this.d = d;
			else
			{
				// free the memory used up by d while calculating new array
				this.d = null;
			
				// store the list information
				this.d = new DoubleArray2D(d); // store as floats rather than doubles
			}
		
			calculateImage();
		}
		else
		{
			this.d = null;
			scaledImage = null;
		}
		
		repaint();
	}
	
	public void setList(DoubleList2D d)
	{
		setList(d, storeListAsReference);
	}
	
	
	public void addPopupMenuItem(MenuItem menuItem)
	{
		popupMenu.add(new MenuItem("-"));
		popupMenu.add(menuItem);
	}


	// v is the RELATIVE value of the data point; it ranges between 0 (at the mimimum)
	// and 1 (at the maximum)
	private double value2intensity(double v)
	{
		double i = brightness + contrast * (v-0.5);
		if(i > 1.0) i = 1.0;
		else if(i < 0.0) i = 0.0;
		
		return i;
	}
	
	/* calculate the full-size image, using the methods
	   getSize() and getElement(int i, int j) supplied by the
	   implementation of the DoubleList2D interface
	*/
	public synchronized void calculateImage()
	{
		int i, j;
		double min, max, f;
		Dimension size = getSize();
		
		
		calculateIJRange();		
				
		// find maximum and minimum of data
		min = max = d.getElement(0, 0);
		for(j=jRangeMin; j<=jRangeMax; j++) {
			for(i=iRangeMin; i<=iRangeMax; i++) {
				f = d.getElement(i,j);
				if(f < min) min = f;
				if(f > max) max = f;
			}
		}
		
		// free the memory used by the scaled image
		scaledImage = null;
		
		// create image
		int pixels[] = new int[(iRangeMax - iRangeMin + 1) * (jRangeMax - jRangeMin + 1)];
		int k = 0;
		int v;
		for(j=jRangeMin; j<=jRangeMax; j++) {
			for(i=iRangeMin; i<=iRangeMax; i++) {
				f = (d.getElement(i,j)-min)/(max-min);
				v = (int)(255*value2intensity(f));
				pixels[k++] =
					// saturation
					(255 << 24) |
					// red component
					v << 16 |
					// green component
					v << 8 |
					// blue component
					v;
			}
		}
		scaledImage = 
			createImage(
				new MemoryImageSource(
					iRangeMax - iRangeMin + 1, 
					jRangeMax - jRangeMin + 1, 
					pixels, 0,
					iRangeMax - iRangeMin + 1)
			).getScaledInstance(size.width, size.height, Image.SCALE_DEFAULT);
	}
	

	public void calculateIJRange()
	{
		// set iRange and jRange
		
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

		if(useYRange)
		{
			jRangeMin = y2j(getYRangeMin());
			jRangeMax = y2j(getYRangeMax());
		}
		else
		{
			jRangeMin = getJRangeMin();
			jRangeMax = getJRangeMax();
		}
	}


	//
	// the i range is the range of indices being plotted horizontally
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
		if(showIRangeAll) return d.getSize().width-1;
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

        // do the x and y ranges actually do anything?
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
	// the j range is the range of indices being plotted vertically
	//
	
	public void setShowJRangeAll(boolean showJRangeAll)
	{
		this.showJRangeAll = showJRangeAll;
	}
	
	public boolean getShowJRangeAll()
	{
		return showJRangeAll;
	}
	
	public void setJRange(int jRangeMin, int jRangeMax)
	{
		useYRange = false;
		this.jRangeMin = jRangeMin;
		this.jRangeMax = jRangeMax;
	}
	
	public int getJRangeMin()
	{
		if(showJRangeAll) return 0;
		else return jRangeMin;	
	}
	
	public int getJRangeMax()
	{
		if(showJRangeAll) return d.getSize().height-1;
		else return jRangeMax;	
	}
	
	
	//
	// the y mesh range is the range of real values that correspond to the
	// entire size of the list
	//

	public void setUseYRange(boolean useYRange)
	{
		this.useYRange = useYRange;
	}
	
	public boolean getUseYRange()
	{
		return useYRange;
	}

	// mesh range that corresponds to the entire list
	public void setYMeshRange(double yMeshRangeMin, double yMeshRangeMax)
	{
		useYRange = true;
		this.yMeshRangeMin = yMeshRangeMin;
		this.yMeshRangeMax = yMeshRangeMax;
	}
	
	public double getYMeshRangeMin()
	{
		return yMeshRangeMin;
	}
	
	public double getYMeshRangeMax()
	{
		return yMeshRangeMax;
	}
	
	public void setYRange(double yRangeMin, double yRangeMax)
	{
		useYRange = true;
		this.yRangeMin = yRangeMin;
		this.yRangeMax = yRangeMax;
	}
	
	public double getYRangeMin()
	{
		if(showJRangeAll) return yMeshRangeMin;
		else return yRangeMin;
	}
	
	public double getYRangeMax()
	{
		if(showJRangeAll) return yMeshRangeMax;
		else return yRangeMax;
	}
	

        //
        // methods for finding the x and y values corresponding to
        // given array indices i and j (and vice versa)
        //
        
	// convert a horizontal index into the corresponding x value
	public double i2x(int i)
	{
		return xMeshRangeMin + (xMeshRangeMax - xMeshRangeMin) * i / (d.getSize().width-1.0);
	}
	
	public int x2i(double x)
	{
		return Math.round(
			(float)
			(	(d.getSize().width-1.0) * (x - xMeshRangeMin) 
				/ (xMeshRangeMax - xMeshRangeMin)
			)
		);
	}
	
	// convert a vertical index into the corresponding y value
	public double j2y(int j)
	{
		return yMeshRangeMin + (yMeshRangeMax - yMeshRangeMin) * j / (d.getSize().height-1.0);
	}
	
	public int y2j(double y)
	{
		return Math.round(
			(float)
			(	(d.getSize().height-1.0) * (y - yMeshRangeMin) 
				/ (yMeshRangeMax - yMeshRangeMin)
			)
		);
	}
	
	
	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		
		if(d != null) calculateImage();
	}

	// brightness ranges between 0 and 1
	public void setBrightness(double brightness)
	{
		this.brightness = brightness;
	}
	
	public double getBrightness()
	{
		return brightness;
	}

	public void setContrast(double contrast)
	{
		this.contrast = contrast;
	}
	
	public double getContrast()
	{
		return contrast;
	}
	
	public void setPlotGrid(boolean plotGrid)
	{
		this.plotGrid = plotGrid;
	}
	
	public boolean getPlotGrid()
	{
		return plotGrid;
	}
	
	public void setResizable(boolean resizable)
	{
		this.resizable = resizable;
	}
	
	public void setFrame(Frame frame)
	{
		this.frame = frame;
	}

 	public DoubleList2D getList()
	{
		return d;
	}
	
	
	public void saveRawData()
	{
		Frame frame = new Frame();
		FileDialog fd = new FileDialog(frame, "", FileDialog.SAVE);
			
		// save
		try
		{
			fd.setFile(
				"" + (getIRangeMax() - getIRangeMin() + 1) + "x" +
				(getJRangeMax() - getJRangeMin() + 1) );
				
			fd.setVisible(true);
			
			if(fd.getFile() != null)
			{
				//
				// find maximum and minimum of data
				//
			
				int i, j;
		
				double min, max, f;
		
				calculateIJRange();
		
				min = max = d.getElement(0, 0);
				for(j=jRangeMin; j<=jRangeMax; j++) {
					for(i=iRangeMin; i<=iRangeMax; i++) {
						f = d.getElement(i,j);
						if(f < min) min = f;
						if(f > max) max = f;
					}
				}

				// open file
				FileOutputStream fos =
					new FileOutputStream(fd.getDirectory() + fd.getFile());
		
				// allocate memory for row data
				byte row[] = new byte[iRangeMax - iRangeMin + 1];

				// go through all the rows
				for(j=jRangeMin; j<=jRangeMax; j++)
				{
					// put the data for this row into the array row[]...
					for(i=iRangeMin; i<=iRangeMax; i++)
						row[i-iRangeMin] =
							(byte)(255*value2intensity((d.getElement(i,j)-min)/(max-min)));
			
					// ... and write it to the file
					fos.write(row);
				}
				
				fos.close();
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
                    frame.dispose();
		}
	}

	// converts a byte into a two-digit (with leading '0's) hexadecimal number
	private String toHexString(int b)
	{
		return "" + Integer.toHexString(b >> 4) + Integer.toHexString(b & 15);
	}

	public void saveAsEPS()
	{
		Frame frame = new Frame();
		FileDialog fd = new FileDialog(frame, "", FileDialog.SAVE);
			
		// save
		try
		{
			int
				width = iRangeMax - iRangeMin + 1,
				height = jRangeMax - jRangeMin + 1;
				
			fd.setFile(
				"" + width + "x" +
				height + ".eps" );
				
			fd.setVisible(true);
			
			if(fd.getFile() != null)
			{
				//
				// find maximum and minimum of data
				//
			
				int i, j;
		
				double min, max, f;
		
				calculateIJRange();
		
				min = max = d.getElement(0, 0);
				for(j=jRangeMin; j<=jRangeMax; j++) {
					for(i=iRangeMin; i<=iRangeMax; i++) {
						f = d.getElement(i,j);
						if(f < min) min = f;
						if(f > max) max = f;
					}
				}

				// open file
				FileOutputStream fos =
					new FileOutputStream(fd.getDirectory() + fd.getFile());
				PrintWriter pw = new PrintWriter(fos);
				
				pw.print(
					"%!PS-Adobe-3.0 EPSF-3.0\n" +
					"%ImageData: " +
					width + " " +
					height +
					" 8 1 0 " + width + 
					" 1 \"image\"\n" +	// bits/pixel, fitness knows what the rest means
					"image\n"
				);
				
				pw.flush();
				
				// allocate memory for row data
				byte row[] = new byte[width];

				// go through all the rows
				for(j=jRangeMin; j<=jRangeMax; j++)
				{
					// put the data for this row into the array row[]...
					for(i=iRangeMin; i<=iRangeMax; i++)
						row[i-iRangeMin] =
							(byte)(255*value2intensity((d.getElement(i,j)-min)/(max-min)));
			
					// ... and write it to the file
					fos.write(row);
				}

		
				// go through all the rows
				// for(j=jRangeMin; j<=jRangeMax; j++)
					// go through all the columns...
				// 	for(i=iRangeMin; i<=iRangeMax; i++)
				//		// ... and write the intensity to the file
				//		fw.write(toHexString((int)(255*value2intensity((d.getElement(i,j)-min)/(max-min)))));
				
				fos.close();
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
                    frame.dispose();
		}
	}


	public void showCopyInNewFrame()
	{
		DensityPlotFrame pf =
			new DensityPlotFrame(null, "copy of density plot",
			getSize().width, getSize().height, true);
				
		// when the DensityPlotFrame's close box is clicked, close it
		// and free all its system resources
		pf.addWindowListener(new WindowDisposingAdapter(pf));
		
		// set the parameters
		pf.c.setXMeshRange(getXMeshRangeMin(), getXMeshRangeMax());
		pf.c.setYMeshRange(getYMeshRangeMin(), getYMeshRangeMax());
		pf.c.setBrightness(getBrightness());
		pf.c.setContrast(getContrast());
		pf.c.setShowIRangeAll(getShowIRangeAll());
		pf.c.setShowJRangeAll(getShowJRangeAll());
		if(getUseXRange()) pf.c.setXRange(getXRangeMin(), getXRangeMax());
		else pf.c.setIRange(getIRangeMin(), getIRangeMax());
		if(getUseYRange()) pf.c.setYRange(getYRangeMin(), getYRangeMax());
		else pf.c.setJRange(getJRangeMin(), getJRangeMax());
		pf.c.setPlotGrid(getPlotGrid());

		pf.c.setResizable(true);
	
		pf.c.setList(getList(), true);
		
		pf.setVisible(true);
	}


	public void settingsDialog()
	{
		DensityPlotControlsDialog dpcd;
		Frame f = new Frame();
		
		dpcd = new DensityPlotControlsDialog(this, f, false);
		dpcd.setBrightness(brightness);
		dpcd.setContrast(contrast);
		dpcd.setShowXRangeAll(showIRangeAll);
		dpcd.setShowYRangeAll(showJRangeAll);
		if(useXRange) dpcd.setXRange(getXRangeMin(), getXRangeMax());
		else dpcd.setIRange(getIRangeMin(), getIRangeMax());
		if(useYRange) dpcd.setYRange(getYRangeMin(), getYRangeMax());
		else dpcd.setJRange(getJRangeMin(), getJRangeMax());
		dpcd.setDensityPlotSize(getSize().width, getSize().height);
		dpcd.setPlotGrid(plotGrid);
		dpcd.show();
		
		// free system resources associated with dialog and frame
		dpcd.dispose();
		f.dispose();
	}
	
	public void update(Graphics g)
	{
		if(d != null)
			paint(g);	// avoid filling with background colour before painting
		else
			super.update(g);	// fill with background colour, then paint
	}


        //
        // methods for converting the mesh-range coordinates x and y into the
        // coordinates used by Canvas
        //
        
        public int x2componentX(double x)
        {
            return (int)((double)(x - i2x(iRangeMin)) * (double)getWidth() /
                         (double)(i2x(iRangeMax) - i2x(iRangeMin)));
        }

        public double componentX2x(int cX)
        {
            return (cX*(double)(i2x(iRangeMax) - i2x(iRangeMin))/(double)getWidth()
                    + i2x(iRangeMin));
        }

        public int y2componentY(double y)
        {
            return (int)((double)(y - j2y(jRangeMin)) * (double)getHeight() /
                         (double)(j2y(jRangeMax) - j2y(jRangeMin)));
        }

        public double componentY2y(int cY)
        {
            return (cY*(double)(j2y(jRangeMax) - j2y(jRangeMin))/(double)getHeight()
                    + j2y(jRangeMin));
        }

        
        public void paint(Graphics g)
	{
            super.paint(g);
            
		if(d != null)
		{
			calculateIJRange();
			
			// a list is present, but is an image there as well?
			if(scaledImage == null)
				// no, create an image
				calculateImage();
				
			// draw the image
			g.drawImage(scaledImage, 0, 0, this);


			///////////////////////////////////
			// plot a grid in the foreground //
			///////////////////////////////////
			
			if(plotGrid)
			{
				g.setColor(Color.green);
				
				double xMin, xMax, yMin, yMax;

				Dimension size = getSize();

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
				
				if(useYRange)
				{
					yMin = j2y(jRangeMin);
					yMax = j2y(jRangeMax);
				}
				else
				{
					yMin = jRangeMin;
					yMax = jRangeMax;
				}
				
				ScaleTicks
					stx = new ScaleTicks(xMin, xMax, size.width),
					sty = new ScaleTicks(yMin, yMax, size.height);
				
				int h, v, i;
				
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
					g.drawString(""+(float)tick, 2, v-2);
					tick += sty.getTickSeparation();
					if(Math.abs(tick) < Math.abs(1e-10*sty.getTickSeparation())) tick = 0.0;
				}
			
				g.setColor(Color.black);
			}
		
		
		}
		else
		{
			// no list present
			g.drawString("- no plot data present -", 0, 10);
		}
	}
	
	////////////////////////////////////////
	// mouse listener interface functions //
	////////////////////////////////////////
	
	public void mouseClicked(MouseEvent me) 
	{
	}
	
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
