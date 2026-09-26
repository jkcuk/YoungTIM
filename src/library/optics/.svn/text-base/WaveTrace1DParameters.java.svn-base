/*	WaveTrace1DSettingsDialog.java

	Provides a dialog to alter the fundamental settings of the beam propagation program in 1D,
	the size of the amplitude matrix, the physical size represented by the matrix, and the
	wavelength of the modelled light.

	Example:
	
		import johannes.optics.*;
		
		WaveTrace1DParameters p;
		
		Frame f = new Frame();
		
		// create new WaveTrace1DParametersDialog
		WaveTrace1DParametersDialog wtpd = new WaveTrace1DParametersDialog(f);
		
		// communicate the current settings to the dialog
		wtpd.setParameters(p);
		
		// start dialog
		wtpd.show();
		
		// free system resources associated with frame
		f.dispose();
		
		// was the dialog ended by clicking on OK or Cancel?
		if(wtsd.OK())
		{
			// dialog was ended by clicking OK
			
			// change parameters
			p = wtpd.getParameters();
		}
		
		// free system resources associated with dialog
		wtsd.dispose();

		// create a new LightBeamCrossSection1D
		LightBeamCrossSection1D b = new LightBeamCrossSection1D(
			p.matrixSize, // width
			p.physicalSize, // physical width in meters
			p.lambda // wavelength
		);
*/

package library.optics;


import java.io.*;


public class WaveTrace1DParameters
implements Serializable
{
	private static final long serialVersionUID = 446674775120037324L;
	
	public int matrixSize;
	public double physicalSize, lambda;
}
