/* WaveTrace1D Version 1.0
   adapted from WaveTrace


version history:

	1.0	adapted for 1-dimensional beam cross sections from WaveTrace.java version 1.1


public classes:

  public class DiscretePhaseList1D implements DoubleList1D

  public class DiscreteIntensityList implements DoubleList1D

  public class LightBeamCrossSection1D implements ComplexList1D


example:

*/

package library.optics;

import java.io.*;

import library.list.*;


/////////////////////////////////////////////////////////////////////////////////////
// define implementations of the interface ComplexList1D that make //
// life a little bit easier when dealing with 1-dimensional light fields           //
/////////////////////////////////////////////////////////////////////////////////////

// This class effectively "wraps around" a LightBeamCrossSection1D.  When asked to
// return the "value" at index <i>, using the method "getElement(i)", returns its phase
// as a double-precision real number rather than its (complex) amplitude.
// This class only stores a REFERENCE to the original LightBeamCrossSection1D.  If the
// LightBeamCrossSection1D changes after the DiscretePhaseList1D class has been
// constructed, the phase returned by DiscretePhaseList1D will change accordingly.
public class DiscretePhaseList1D implements DoubleList1D, Serializable
{
	private static final long serialVersionUID = -1209815400069601984L;
	
	public LightBeamCrossSection1D amplitudeList;
	
	public DiscretePhaseList1D(LightBeamCrossSection1D amplitudeList)
	{
		this.amplitudeList = amplitudeList;
	}
	
	public int getSize()
	{
		return amplitudeList.getSize();
	}
	
	public double getElement(int i)
	{
		return amplitudeList.getPhase(i);
	}
	
	public static DoubleArray1D getPhaseArray1D(LightBeamCrossSection1D amplitudeList)
	{
		DoubleArray1D a = new DoubleArray1D(amplitudeList.getSize());
		
		for(int i=0; i<a.getSize(); i++)
			a.setElement(i, amplitudeList.getPhase(i));
		
		return a;
	}
}
