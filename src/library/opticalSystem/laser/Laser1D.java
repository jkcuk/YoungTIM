package library.opticalSystem.laser;


import java.io.*;

import library.awt.*;
import library.list.*;
import library.opticalSystem.*;
import library.optics.*;
import library.plot.*;
import library.util.*;


public class Laser1D implements Serializable
{
	private static final long serialVersionUID = -5941979542110055292L;
	
	//
	// parameters
	//
	
	public int
		matrixSize; // size of the amplitude matrix
	public double
		physicalSize,	// width (in meters) represented by amplitude matrix;
		lambda; // wavelength (in meters) of the light in meters
	
	
	public String filename;
	
	private Laser1DInitialiser
		init;

	
	//
	// data arrays that hold the amplitude cross-section through the eigenmode
	// and the optical system
	//
	
	private LightBeamInOpticalSystem1D
		beamAndResonator;
	
	
	private transient String statusString = "";
		
	private transient Laser1DControlsWindow controlsWindow;


	public Laser1D(Laser1DInitialiser init, Laser1DControlsWindow controlsWindow)
	{
		this.init = init;
		this.controlsWindow = controlsWindow;
		
		filename = init.getFileName();

		beamAndResonator = new LightBeamInOpticalSystem1D();

		//
		// create a LightBeamCrossSection1D
		//
		
		// provide sensible starting values for the WaveTrace parameters, ...
		WaveTrace1DParameters waveTraceParameters = init.getWaveTraceParameters();
		matrixSize = waveTraceParameters.matrixSize;
		physicalSize = waveTraceParameters.physicalSize;
		lambda = waveTraceParameters.lambda;
		// ... and customise them
		customiseWaveTraceParameters();

		// make a light beam according to these parameters...
		beamAndResonator.b = new LightBeamCrossSection1D(
			matrixSize, // width
			physicalSize, // physical width in meters
			lambda // wavelength
		);
		// ...and set the amplitude to 1 everywhere
		for(int i=0; i<matrixSize; i++)
			beamAndResonator.b.setAmplitude(i, 1.0);


		//
		// create the resonator
		//
			
		// provide a starting point for the resonator...
		beamAndResonator.system = init.getInitialResonator();
		// ... and customise it
		customiseResonator();
			

		//
		// other initialisations
		//
			
		customiseBeam();
			

		//
		// update the controls&status window
		//
			
		updateStatus();
	}
	
	public void setControlsWindow(Laser1DControlsWindow controlsWindow)
	{
		this.controlsWindow = controlsWindow;
	}
	
	
	public void showAllWindows()
	{
		// show all the PlotFrames that correspond to PlotPlane1Ds in the resonator
		for(int i=0; i<beamAndResonator.system.getSize(); i++)
		{
			OpticalElement1D e = beamAndResonator.system.getElementAt(i);
			
			if(e instanceof PlotPlane1D) ((PlotPlane1D)e).plot();
		}
		
		// close the DensityPlotFrame that displays the intensity cross section
		// over one round trip
		if(roundTripIntensityArray != null)
		{
			plotRoundTripIntensityArray();
		}
	}
	
	
	public void closeAllWindows()
	{
		// close all the PlotFrames that correspond to PlotPlane1Ds in the resonator
		for(int i=0; i<beamAndResonator.system.getSize(); i++)
		{
			OpticalElement1D e = beamAndResonator.system.getElementAt(i);
			
			if(e instanceof PlotPlane1D) ((PlotPlane1D)e).closePlotFrame();
		}
		
		// close the DensityPlotFrame that displays the intensity cross section
		// over one round trip
		if(roundTripGraphFrame != null)
		{
			roundTripGraphFrame.dispose();
			roundTripGraphFrame = null;
		}
	}
	
	
	boolean customiseWaveTraceParameters()
	{
		//
		// get initial settings for the beam propagation program
		//
		
		boolean OK;
		
		WaveTrace1DParametersDialog wtpd = new WaveTrace1DParametersDialog(controlsWindow);
		
		WaveTrace1DParameters p = new WaveTrace1DParameters();
		p.matrixSize = matrixSize;
		p.physicalSize = physicalSize;
		p.lambda = lambda;
		wtpd.setParameters(p);
		wtpd.show();
		OK = wtpd.OK();
		if(OK)
		{
			p = wtpd.getParameters();
			matrixSize = p.matrixSize;
			physicalSize = p.physicalSize;
			lambda = p.lambda;
		}
		
		// free system resources associated with dialog
		wtpd.dispose();
		
		return OK;
	}
	
	
	public void initialiseWaveTrace()
	{
		if(customiseWaveTraceParameters())
		{
			// the customiseWaveTraceParameters dialog was ended by clicking OK
			
			// make a light beam according to these parameters...
			beamAndResonator.b = new LightBeamCrossSection1D(
				matrixSize, // width
				physicalSize, // physical width in meters
				lambda // wavelength
			);
			// ...and set the amplitude to 1 everywhere
			for(int i=0; i<matrixSize; i++)
				beamAndResonator.b.setAmplitude(i, 1.0);
		}
	}
	
	
	void customiseResonator()
	{
		// show the control that allows system configuration
		OpticalSystem1DDialog osd =
			new OpticalSystem1DDialog(
				controlsWindow,
				beamAndResonator.system,
				"Laser1D",
				init
			);
		osd.show();

		// free system resources associated with dialog
		osd.dispose();

		// show the beam (e.g. in case the plot has become a Fourier plot)
		updatePlot();
	}
	
	
	//
	// beam initialisation
	//
	
	private transient Beam1DInitialisationDialog bid;
	
	void customiseBeam()
	throws OutOfMemoryError
	{
		beamAndResonator.roundTripCounter = 0;
	
		// initialise the beam
		
		// has the Beam1DInitialisationDialog been used before?
		if(bid == null)
		{
			// no, create a new BeamInitialisationDialog
			bid = new Beam1DInitialisationDialog(controlsWindow, beamAndResonator);
		}
		
		// show (and therefore start) the (old or new) BeamInitialisationDialog
		bid.show();
		
		// show the new beam
		updatePlot();

		// propagate to the beginning of the resonator
		while(!(beamAndResonator.currentElement() instanceof ResonatorBeginning1D))
		{
                    beamAndResonator.toNextElement();
                    updateStatus();
		}
	}
	
	public void setStatusString(String statusString)
	{
		this.statusString = statusString;
		updateStatus();
	}
		
	public void updateStatus()
	{
            controlsWindow.updateStatus();
	}
	
	public int getRoundTripCounter()
	{
		return beamAndResonator.roundTripCounter;
	}
	
	public OpticalElement1D getCurrentElement()
	throws EndOfOpticalSystemError
	{
		return beamAndResonator.currentElement();
	}

	public boolean getForwardDirection()
	{
		return beamAndResonator.forwardDirection;
	}
	
	public double getPowerInBeam()
	{		
		return beamAndResonator.b.getPowerInBeam();
	}
	
	public String getFilename()
	{
		return filename;
	}
	
	public String getStatusString()
	{
		return statusString;
	}


	public void passThroughResonator()
	{
		int startIndex = beamAndResonator.currentElementIndex;
		boolean startDirection = beamAndResonator.forwardDirection;
		
		do
		{
			beamAndResonator.toNextElement();
			updateStatus();
		} while (
			(beamAndResonator.currentElementIndex != startIndex) ||
			(beamAndResonator.forwardDirection != startDirection) );
	}
	
	public void calculateEigenmode()
	{
		// parameters
		int rtMax = 50;
		double maxDeviation = 0.001; // 0.1 % deviation
		
		// store the original intensity list
		DoubleArray1D
			oldIntensityList,
			intensityList =
				new DoubleArray1D(new DiscreteIntensityList1D(beamAndResonator.b));
		
		boolean intensityListsDifferent;
		int rt;
	
		for(
			intensityListsDifferent = true, rt = 0;
			intensityListsDifferent && (rt < rtMax);
			rt++)
		{
			oldIntensityList = intensityList;
			
			passThroughResonator();
			
			intensityList =
				new DoubleArray1D(new DiscreteIntensityList1D(beamAndResonator.b));
			
			// is the new intensity list sufficiently similar to the
			// old one?
			
			double
				max = intensityList.getMax(),
				maxRatio = max / oldIntensityList.getMax();
			
			int i;
			
			for(
				i=0;
				(i<intensityList.getSize()) &&
				( Math.abs(
					intensityList.getElement(i) -
					maxRatio * oldIntensityList.getElement(i) )
				< maxDeviation * max);
				i++ );
			
			intensityListsDifferent = (i < intensityList.getSize());
		}
		
		if(intensityListsDifferent)
			new MessageDialog(
				controlsWindow,
				"Warning",
				"No convergence to the lowest-loss eigenmode after " + rtMax + "round trips."
			); 
			// System.out.println(
			//	"Warning: No convergence to the lowest-loss eigenmode after " + rtMax +
			//	"round trips.");
	}
	
	public class RoundTripGraphParameters
	implements SelfExplainingObject, Serializable
	{
		public int columns, rows;
		public boolean normalise;
		
		public RoundTripGraphParameters(int columns, int rows, boolean normalise)
		{
			this.columns = columns;
			this.rows = rows;
			this.normalise = normalise;
		}
		
		// SelfExplainingObject method
		public String getExplanation()
		{
			//  " <--        maximum string width         --> "
			return 
				"columns is the number of beam cross sections\n" +
				"that will be calculated over one round trip;\n" +
				"rows is the number of points stored from\n" +
				"each cross section." +
				"If normalise is checked, the power in the\n" +
				"beam will be normalised before the intensity\n" +
				"list is calculates; this is\n" +
				"recommended.";
		}
	}
	
	private transient RoundTripGraphParameters roundTripGraphParameters;
	private transient DensityPlotFrame roundTripGraphFrame;
	private DoubleArray2D roundTripIntensityArray;
	private double resonatorLength;
						
	public void calculateIntensityListOverOneRoundTrip()
	{
		// have the parameters been changed before?
		if(roundTripGraphParameters == null)
		{
			// no, create a new RoundTripGraphParameters object
			roundTripGraphParameters = new RoundTripGraphParameters(128, 1024, true);
		}
			
		// run a dialog for changing the parameters
		ObjectDialog od =
			new ObjectDialog(
				controlsWindow, "round trip graph parameters", roundTripGraphParameters );
			
		try
		{
			// was the dialog ended by clicking OK?
			if(od.OK())
			{
				// yes; go ahead
				
				// normalisation, if requested
				if(roundTripGraphParameters.normalise)
					beamAndResonator.b.normalisePowerInBeam();
			
				// set the parameters
				int roundTripGraphColumns = roundTripGraphParameters.columns;
				int roundTripGraphRows = roundTripGraphParameters.rows;

				// variables
				int i;
				
				roundTripIntensityArray = null;
				
				// create a new 2D array to hold the intensity information
				roundTripIntensityArray =
					new DoubleArray2D(roundTripGraphColumns, roundTripGraphRows);
				
				
				//
				// first, propagate to the beginning of the resonator
				//
				
				while(!(beamAndResonator.currentElement() instanceof ResonatorBeginning1D))
				{
					beamAndResonator.toNextElement();
					updateStatus();
				}


				//
				// calculate length of un-folded (equivalent of) resonator
				//
				
				// find the end of the resonator
				for(
					i=0;
					(i<beamAndResonator.system.getSize()) &&
					!(beamAndResonator.system.getElementAt(i) instanceof
						UnfoldedResonatorEnd1D) &&
					!(beamAndResonator.system.getElementAt(i) instanceof
						FoldedResonatorEnd1D);
					i++
				);
				
				if(beamAndResonator.system.getElementAt(i) instanceof UnfoldedResonatorEnd1D)
				{
					// found an UnfoldedResonatorEnd1D
					resonatorLength =
						beamAndResonator.system.getElementAt(i).getZ() -
						beamAndResonator.getZ();
				}
				else
				if(beamAndResonator.system.getElementAt(i) instanceof FoldedResonatorEnd1D)
				{
					// found an FoldedResonatorEnd1D
					resonatorLength =
						2*( beamAndResonator.system.getElementAt(i).getZ() -
							beamAndResonator.getZ() );
				}
				else throw new Error("Resonator has no end.");
				
				// "cumulative z coordinate",
				// i.e. cumulative distance travelled since beginning of resonator
				double zC = 0;
				int column = 0; // number of column (0...roundTripGraphColumns-1)
				
				// make the initial beam cross section the first column of the plot
				roundTripIntensityArray.setColumn(
					column, 
					reducedSizeIntensityArray(roundTripGraphRows, beamAndResonator.b)
				);
				
				// go through all the elements in the resonator...
				do
				{
					OpticalElement1D e = beamAndResonator.currentElement();
				
					// do action of current element
					beamAndResonator.performActionOfCurrentElement();
					
					// check whether it's necessary to propagate to the next element
					if(
						!(e instanceof SpecialOpticalElement1D) ||
						( (e instanceof SpecialOpticalElement1D) &&
						  // if it's a special optical element, propagate to the next element
						  // only if the propagateToNextElement() method returns true
						  ((SpecialOpticalElement1D)e).propagateToNextElement() )
					)
					{
						// yes
					
						// store the z component of the current element for the moment
						double zStored = beamAndResonator.getZ();
		
						// make the next element the current element
						// (without the beam having been propagated there yet!)
						beamAndResonator.nextElement();
		
						// calculate cumulative z coordinate of the next element
						double zCNext = zC + Math.abs(beamAndResonator.getZ() - zStored);
						
						// calculate cumulative z coordinate of plane corresponding to
						// the next column
						double zCOfNextColumn =
							resonatorLength * (column+1.0) / (roundTripGraphColumns-1.0);
						
						// while the plane corresponding to the next column is still
						// on this side of the next element...
						while( zCOfNextColumn <= zCNext )
						{
							// propagate to the plane corresponding to the next column...
							beamAndResonator.b.propagate( zCOfNextColumn - zC );
							
							// ... and reflect this in the "cumulative z" coordinate
							zC = zCOfNextColumn;
		
							// the beam is in the next column now
							column++;
							
							// set the column in the graph
							roundTripIntensityArray.setColumn(
								column, 
								reducedSizeIntensityArray(roundTripGraphRows, beamAndResonator.b)
							);
							
							// calculate the cumulative z coordinate of the plane corresponding
							// to the next column
							zCOfNextColumn =
								resonatorLength * (column+1.0) / (roundTripGraphColumns-1.0);
						}
		
						// propagate to the plane of the next element
						if(zC < zCNext)
						{
							beamAndResonator.b.propagate(zCNext - zC);
							zC = zCNext;
						}
					}
						
					// show how far the beam has travelled
					updateStatus();
				} 
				// ... until back at the beginning
				while(!(beamAndResonator.currentElement() instanceof ResonatorBeginning1D));

				plotRoundTripIntensityArray();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// free system resources associated with dialog
			od.dispose();
		}
	}
	
	public void plotRoundTripIntensityArray()
	{
		// if there is no window yet to plot the round trip graph in...
		if(roundTripGraphFrame == null)
		{
			// ... open a new one
			roundTripGraphFrame = new DensityPlotFrame(
				null, "", 600, 400, true);
			
			roundTripGraphFrame.addWindowListener(
				new WindowInvisibleAdapter(roundTripGraphFrame));
		}
	
		roundTripGraphFrame.setTitle(
			"round trip #" + beamAndResonator.roundTripCounter +
			" through " + filename);
		roundTripGraphFrame.c.setList(roundTripIntensityArray, true);
		roundTripGraphFrame.c.setXMeshRange(0, 1e3*resonatorLength);
		roundTripGraphFrame.c.setYMeshRange(-1000*physicalSize/2, 1000*physicalSize/2);
		
		// in case the window is not visible...
		if(!roundTripGraphFrame.isShowing())
			// ... show it
			roundTripGraphFrame.setVisible(true);
	}


	private DoubleArray1D reducedSizeIntensityArray(int size, LightBeamCrossSection1D b)
	{
		DoubleArray1D array = new DoubleArray1D(size);
		
		// an intensity list class "wrapped" around b
		DiscreteIntensityList1D intensityList =
			new DiscreteIntensityList1D(b);
		
		double p = b.getPhysicalWidth();
		
		for(int i=0; i<size; i++)
		{
			array.setElement(i,
				intensityList.getElement(
					(int)Math.round(b.getIndex(-p/2.0 + p * i/(size-1.0)))
				));
		}
		
		return array;
	}
	

	public void updatePlot()
	{
		OpticalElement1D e = beamAndResonator.currentElement();
		
		// is the current element a plot plane?
		if(e instanceof PlotPlane1D)
		{
			// yes; plot the beam
			// init.plots.plot(beamAndResonator);	
			((SpecialOpticalElement1D)e).act(beamAndResonator);
		}
	}
	
	public synchronized void save(String filename)
	throws IOException
	{
		this.filename = filename;
		updateStatus();
		
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.flush();
		oos.close();
	}
	
	public static synchronized Laser1D open(
		String filename, Laser1DControlsWindow controlsWindow )
	throws IOException, ClassNotFoundException
	{
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Laser1D laser = (Laser1D)ois.readObject();
		ois.close();
		
		// any remaining initialisation necessary?
		laser.filename = filename;
		laser.setControlsWindow(controlsWindow);
		laser.showAllWindows();
		laser.updateStatus();
		
		return laser;
	}


	void normalisePowerInBeam()
	{
		beamAndResonator.b.normalisePowerInBeam();
	}
}
