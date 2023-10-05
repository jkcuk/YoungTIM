package library.opticalSystem;


import java.awt.*;
import java.awt.event.*;

import library.awt.*;
import library.list.*;
import library.optics.*;
import library.plot.*;
import library.util.*;



//
// a plot plane
//

public class PlotPlane1D extends SpecialOpticalElement1D
implements ActionListener, ApplyButtonListener, BeamInitialisationElement1D
{
	private static final long serialVersionUID = 2761711586357913583L;
	
	public boolean forwards, backwards, phase, FT;
	public double offset;
	
	private LightBeamCrossSection1D beam;
	private boolean forwardDirection; // false: beam travels backwards
	private transient PlotFrame plotFrame;
	private transient MenuItem
		plotPlaneParametersMenuItem, saveBeamDataMenuItem;
	
	//
	// constructors
	//
	
	public PlotPlane1D()
	{
		name = "new plot plane";
		z = 0;
		offset = 0;
		forwards = true;
		backwards = true;
		phase = false;
		FT = false;
	}
	
	public PlotPlane1D(
		String name, double z, double offset, boolean forwards, boolean backwards,
		boolean phase, boolean FT)
	{
		this.name = name;
		this.z = 1000*z; // store in mm
		this.offset = offset;
		this.forwards = forwards;
		this.backwards = backwards;
		this.phase = phase;
		this.FT = FT;
	}
	
	public PlotPlane1D(PlotPlane1D o)
	{
		name = o.name;
		z = o.z;
		offset = o.offset;
		forwards = o.forwards;
		backwards = o.backwards;
		phase = o.phase;
		FT = o.FT;
	}
	
	
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "plot plane"; }
		
	public String toString()
	{
		return "plot plane " + super.toString();
	}

	public String getExplanation()
	{
		return 
			"A plot plane in an optical system.\n" +
			"<offset> specifies the distance in mm\n" +
			"by which the beam is propagated before\n" +
			"it is plotted.\n" +
			"The same plane in an optical resonator\n" +
			"counts as two different planes in its\n" +
			" unfolded lens-guide equivalent.\n" +
			"The variables \"forwards\" and \"backwards\"\n" +
			"control whether a plane corresponds to\n" +
			"a beam moving forwards, or backwards,\n" +
			"or both.\n" +
			"<phase> determines whether the phase is\n" +
			"plotted instead of the intensity.\n" +
			"<FT> determines whether the phase or\n" +
			"amplitude of the Fourier transform of the\n" +
			"amplitude distribution rather than the\n" +
			"amplitude distribution itself is plotted.\n" +
			super.getExplanation();
	}
        
	
	//
	// SpecialOpticalElement1D methods
	//
	
	public void act(LightBeamInOpticalSystem1D beamAndSystem)
	{
		// is this plane to be plotted for the direction the beam is travelling in?
		if( (beamAndSystem.forwardDirection?forwards:backwards) )
		{
			// yes, plot!

			// store a copy of the light beam, ...
			beam = new LightBeamCrossSection1D(beamAndSystem.b);
			
			forwardDirection = beamAndSystem.forwardDirection;
				
			// ... and plot the beam
			plot();
		}
	}
	
	public boolean propagateToNextElement()
	{
		return true;
	}


	public void plot()
	{
		if(plotFrame == null)
		{
			// create new PlotFrame...
			plotFrame =
				new PlotFrame(
					null, "", 200, 200, true, 
					-1000*beam.getPhysicalWidth()/2,
					+1000*beam.getPhysicalWidth()/2
				);
			
			// add a "PlotPlane1D parameters..." menu point to the popup menu
			plotPlaneParametersMenuItem = new MenuItem("PlotPlane1D parameters...");
			plotPlaneParametersMenuItem.addActionListener(this);
			plotFrame.c.addPopupMenuItem(plotPlaneParametersMenuItem);
			
			// add a "Export Light Beam Data..." menu point to the popup menu
			saveBeamDataMenuItem = new MenuItem("Export Light Beam Data...");
			saveBeamDataMenuItem.addActionListener(this);
			plotFrame.c.addPopupMenuItem(saveBeamDataMenuItem);
			
			plotFrame.addWindowListener(new WindowInvisibleAdapter(plotFrame));
		}
		
		// ... decide on a title for the window...
		String plotFrameTitle =
			(phase?"phase(":"I(") +
			(FT?"FT(":"") +
			shortToString() +
			((offset != 0.0)?(((offset>0.0)?"+":"")+(float)offset+"mm"):"") +
			(FT?")":"") +
			")" +
			(forwardDirection?" (-->)":" (<--)");
		
		plotFrame.setTitle(plotFrameTitle);

		LightBeamCrossSection1D b1;

		if(offset != 0.0)
		{
			// make a copy of the beam...
			b1 = new LightBeamCrossSection1D(beam);
			
			// ... and propagate it to the position specified by <offset>, ...
			b1.propagate(1e-3*offset);
		}
		else
			// just point b1 to the beam
			b1 = beam;
			
		// set the list
		DoubleArray1D list;
		
		// is it a Fourier plot?
		if(FT)
		{
			// yes, it's a Fourier plot
			if(phase)
				// set the list to be the phase of the Fourier transform
				list = ComplexArray1D.FT(b1, +1).getArg();
			else
				// set the list to be the power spectrum (abs^2 of FT)
				list = ComplexArray1D.FT(b1, +1).getAbsSqr();
		}
		else
		{
			// no, it's a real-space plot
			if(phase)
				// set the list to be the phase of the amplitude cross-section
				list =
					new DoubleArray1D(new DiscretePhaseList1D(b1));
			else
				// set the list to the "real-space" intensity distribution
				list =
					new DoubleArray1D(new DiscreteIntensityList1D(b1));
		}
		
		// plot the list
		plotFrame.c.setList(list, true); // store as reference
		
		// in case the plot frame is not visible...
		if(!plotFrame.isShowing())
			// ... show it
			plotFrame.setVisible(true);
	}
	
	public void closePlotFrame()
	{
		if(plotFrame != null)
		{
			plotFrame.dispose();
			plotFrame = null;
		}
	}


	//
	// ActionListener method
	//
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == plotPlaneParametersMenuItem)
		{
			Frame f = new Frame();
			ObjectDialog objectDialog = new ObjectDialog(f, name, this, this);
			
			// was the dialog OKed?
			if(objectDialog.OK())
				// yes, plot what the user wants to see
				plot();
			
			// free system resources associated with dialog and frame
			objectDialog.dispose();
			f.dispose();
		}
		else if(ae.getSource() == saveBeamDataMenuItem)
		{
			beam.saveDataInTextFormat();
		}
	}
	
	
	//
	// ApplyButtonListener method
	//
	
	public void applyButtonClicked()
	{
		plot();
	}
}