package javawaveoptics.optics.component;

import javawaveoptics.optics.BeamCrossSection;

/**
 * Interface that defines a simple optical component that can manipulate a single pixel at a time.
 * 
 * Like a simple optical component, this can take only one light beam as input, and produce only one light beam as output,
 * in contrast to other optical components which may require any number of inputs and outputs. This class
 * removes the need for ArrayList objects to be passed in or out, as we only require one input and one
 * output.
 * 
 * @author Johannes
 */
public interface SimplePixelWiseOpticalComponentInterface
{
	/**
	 * Abstract class that takes an input beam and changes pixel (i,j) in it.
	 * 
	 * @param	i	the horizontal pixel index
	 * @param 	j	the vertical pixel index
	 * @param inputBeam		The input beam
	 * @return				The output beam
	 */
	public BeamCrossSection changePixelInInputBeam(int i, int j, BeamCrossSection inputBeam);
}