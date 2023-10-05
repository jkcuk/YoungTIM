/*
*/

package library.plot;

import java.awt.event.*;
import java.io.*;


// allows notification when the window is resized
public class DensityPlotFrameComponentAdapter extends ComponentAdapter
implements Serializable
{
	private static final long serialVersionUID = -7427782061429234076L;
	
	DensityPlotFrame dpf;
	
	DensityPlotFrameComponentAdapter(DensityPlotFrame dpf)
	{
		this.dpf = dpf;
	}
	
	public void componentResized(ComponentEvent ce)
	{
		dpf.componentResized();
	}
}
