package javawaveoptics.utility;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javawaveoptics.optics.BeamCrossSection;
import library.maths.MyMath;

public class ImageUtilities
{
	/**
	 * Loads the specified image into a data array of length 2 * width * height, with
	 * each row being placed side by side in ascending order, and with each pixel being
	 * represented by two consecutive numbers: the real and imaginary parts, respectively.
	 * 
	 * The image itself is analysed for its intensity and phase by calculating the
	 * real and imaginary parts.
	 * The brightness of each image pixel is interpreted as intensity, its hue as phase.

	 * 
	 * @param image							The image to load
	 * @return								A double precision floating point data array
	 * @throws IllegalArgumentException		The specified image must be valid otherwise this exception is thrown
	 */
	public static double[] image2BeamTypeArray(BufferedImage image) throws IllegalArgumentException
	{
		if(image != null)
		{
			int width = image.getWidth();
			int height = image.getHeight();
			
			double[] data = new double[2 * width * height];
			
			// Set the real parts (intensity)
			for(int x = 0; x < width; x++)
			{
				for(int y = 0; y < height; y++)
				{
					// Fetch the RGB components of the pixel
					Color colour = new Color(image.getRGB(x, y));
					
					// Calculate phase angle and amplitude
					float[] hsb = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
					double phaseAngle = 2 * Math.PI * (hsb[0] - 0.5);
					double amplitude = Math.sqrt(hsb[2]);
					
					// Set real part
					data[2 * ((height-1-y) * width + x)] = amplitude * Math.cos(phaseAngle);
					
					// Set imaginary part
					data[2 * ((height-1-y) * width + x) + 1] = amplitude * Math.sin(phaseAngle);
				}
			}
			
			return data;
		}
		else
		{
			// The specified image is null OR it is an invalid image format			
			throw new IllegalArgumentException("The specified BufferedImage is null.");
		}
	}

	/**
	 * The brightness of each image pixel (and not the square root of the brightness!) is interpreted as
	 * the multiplication factor, its hue gets converted to phase.
	 * @param image
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static double[] image2HologramTypeArray(BufferedImage image) throws IllegalArgumentException
	{
		if(image != null)
		{
			int width = image.getWidth();
			int height = image.getHeight();
			
			double[] data = new double[2 * width * height];
			
			// Set the real parts (intensity)
			for(int x = 0; x < width; x++)
			{
				for(int y = 0; y < height; y++)
				{
					// Fetch the RGB components of the pixel
					Color colour = new Color(image.getRGB(x, y));
					
					// Calculate phase angle and amplitude
					float[] hsb = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
					double phaseAngle = 2 * Math.PI * (hsb[0] - 0.5);
					double amplitude = hsb[2];
					
					// Set real part
					data[2 * ((height-1-y) * width + x)] = amplitude * Math.cos(phaseAngle);
					
					// Set imaginary part
					data[2 * ((height-1-y) * width + x) + 1] = amplitude * Math.sin(phaseAngle);
				}
			}
			
			return data;
		}
		else
		{
			// The specified image is null OR it is an invalid image format			
			throw new IllegalArgumentException("The specified BufferedImage is null.");
		}
	}

	/**
	 * bilinear interpolation --- see http://en.wikipedia.org/wiki/Bilinear_interpolation
	 * @param i
	 * @param j
	 * @param i1
	 * @param i2
	 * @param j1
	 * @param j2
	 * @param q11
	 * @param q12
	 * @param q21
	 * @param q22
	 * @return
	 */
	public static double interpolate(double i, double j, int i1, int i2, int j1, int j2, double q11, double q12, double q21, double q22)
	{		
		return 
			q11 * (i2 - i) * (j2 - j) +
			q21 * (i - i1) * (j2 - j) +
			q12 * (i2 - i) * (j - j1) +
			q22 * (i - i1) * (j - j1);
	}
	
	/**
	 * The range of indices iMin ... iMax and jMin ... jMax describe a rectangle in the beams 1 and 2, which are supposed to be of the same size.
	 * This method tests a number of things, specifically
	 * that the dimensions of beams 1 and 2 are the same;
	 * that 0 <= iMin <= iMax < beam width;
	 * that 0 <= iMin <= iMax < beam width;
	 * that 0 <= jMin <= jMax < beam height;
	 * and that 0 <= jMin <= jMax < beam height.
	 * It throws a RuntimeException if anything is wrong.
	 * @param beam1
	 * @param beam2
	 * @param iMin
	 * @param iMax
	 * @param jMin
	 * @param jMax
	 */
	private static void checkIndexBounds(BeamCrossSection beam1, BeamCrossSection beam2, int iMin, int iMax, int jMin, int jMax)
	{
		if((beam1.getWidth() != beam2.getWidth()) || (beam1.getHeight() != beam2.getHeight()))
		{
			throw new RuntimeException("Dimensions of beam 1 are different from those of beam 2");
		}
		
		if((iMin < 0) || (iMax < 0) || (jMin < 0) || (jMax < 0))
		{
			throw new RuntimeException("One of the pixel index bounds is < 0");
		}

		int width = beam1.getWidth();
		int height = beam1.getHeight();
		
		if((iMin >= width) || (iMax >= width) || (jMin >= height) || (jMax >= height))
		{
			throw new RuntimeException("One of the pixel index bounds is >= width or height");
		}
		
		if((iMin > iMax) || (jMin > jMax))
		{
			throw new RuntimeException("One of the pixel index lower bounds is greater than the corresponding upper bound");
		}
	}

	
	/**
	 * Calculate the Euclidean distance between the intensity the two beams.
	 * Each beam is interpreted as a vector, with the intensities of the pixels being the elements.
	 * The Euclidean distance is then simply
	 * 
	 * 		sum((I_1(x,y) - I_2(x,y))^2),
	 * 
	 * where the sum is over pixels (i,j) with iMin <= i <= iMax and jMin <= j <= jMax,
	 * and where I_1 and I_2 are respectively the intensities of beams 1 and 2.
	 * The two beams are assumed to have the same <i>width</i> and <i>height</i>.
	 * See also:  https://en.wikipedia.org/wiki/Similarity_measure
	 * @param beam1
	 * @param beam2
	 * @param iMin	0 <= iMin <= iMax < beam width
	 * @param iMax	0 <= iMin <= iMax < beam width
	 * @param jMin	0 <= jMin <= jMax < beam height
	 * @param jMax	0 <= jMin <= jMax < beam height
	 * @return	the Euclidean distance between the intensity vectors
	 */
	public static double calculateEuclideanDistance(BeamCrossSection beam1, BeamCrossSection beam2, int iMin, int iMax, int jMin, int jMax)
	{
		checkIndexBounds(beam1, beam2, iMin, iMax, jMin, jMax);
		
		double sumIntensityDifferenceSquared = 0;
		
		for(int j = jMin; j <= jMax; j++)
		{
			for(int i = iMin; i <= iMax; i++)
			{
				sumIntensityDifferenceSquared += MyMath.sqr(beam1.getIntensity(i, j) - beam2.getIntensity(i, j));
			}
		}
		
		return Math.sqrt(sumIntensityDifferenceSquared);
	}
	
	/**
	 * Calculate the normalised squared Euclidean distance between the intensity vectors of the two beams.
	 * Each beam is interpreted as a vector, with the intensities of the pixels being the elements.
	 * The normalised squared Euclidean distance [1] is defined as
	 * 
	 * 		0.5 (|(I1-I1Mean)-(I2-I2Mean)|^2) / (|I1-I1Mean|^2 + |I2-I2Mean|^2),
	 * 
	 * where |x|^2=sum(x_(i,j)^2), where the sum is over pixels (i,j) with iMin <= i <= iMax and jMin <= j <= jMax,
	 * and where I_1 and I_2 are respectively the intensities of beams 1 and 2.
	 * The two beams are assumed to have the same <i>width</i> and <i>height</i>.
	 * See also:  http://reference.wolfram.com/language/ref/NormalizedSquaredEuclideanDistance.html
	 * @param beam1
	 * @param beam2
	 * @param iMin	0 <= iMin <= iMax < beam width
	 * @param iMax	0 <= iMin <= iMax < beam width
	 * @param jMin	0 <= jMin <= jMax < beam height
	 * @param jMax	0 <= jMin <= jMax < beam height
	 * @return	the normalised squared Euclidean distance between the intensity vectors
	 */
	public static double calculateNormalisedSquaredEuclideanDistance(BeamCrossSection beam1, BeamCrossSection beam2, int iMin, int iMax, int jMin, int jMax)
	{
		checkIndexBounds(beam1, beam2, iMin, iMax, jMin, jMax);
		
		// first calculate the mean intensity in beams 1 and 2
		double beam1Intensity = 0;
		double beam2Intensity = 0;

		for(int j = jMin; j <= jMax; j++)
		{
			for(int i = iMin; i <= iMax; i++)
			{
				beam1Intensity += beam1.getIntensity(i, j);
				beam2Intensity += beam2.getIntensity(i, j);
			}
		}
		
		double beam1MeanIntensity = beam1Intensity / ((jMax-jMin+1)*(iMax-iMin+1));
		double beam2MeanIntensity = beam2Intensity / ((jMax-jMin+1)*(iMax-iMin+1));

		// now calculate the
		double normDifferenceSquared = 0;
		double norm1Squared = 0;
		double norm2Squared = 0;

		for(int j = jMin; j <= jMax; j++)
		{
			for(int i = iMin; i <= iMax; i++)
			{
				normDifferenceSquared += MyMath.sqr(
						(beam1.getIntensity(i, j)-beam1MeanIntensity) -
						(beam2.getIntensity(i, j)-beam2MeanIntensity)
					);
				norm1Squared += MyMath.sqr(beam1.getIntensity(i, j)-beam1MeanIntensity);
				norm2Squared += MyMath.sqr(beam2.getIntensity(i, j)-beam2MeanIntensity);
			}
		}
		
		return 0.5*normDifferenceSquared/(norm1Squared+norm2Squared);
	}

	/**
	 * Calculate the Image Euclidean Distance (IMED).
	 * According to Eqn (6) in [1], this can be calculated according to the formula
	 * 	d_{IME}^2 = 1/(2 \pi \sigma^2) \sum_{i,j=1}^{M,N} \exp(-|P_i-P_j|^2/(2 \sigma^2)) (x_i - y_i) (x_j - y_j).
	 * Note that this is not normalised.
	 * [1] L. Wang, Y. Zhang, and J. Feng, "On the Euclidean Distance of Images", IEEE Transactions on Pattern Analysis and Machine Intelligence (2005)
	 * @param beam1
	 * @param beam2
	 * @param sigma
	 * @param iMin	0 <= iMin <= iMax < beam width
	 * @param iMax	0 <= iMin <= iMax < beam width
	 * @param jMin	0 <= jMin <= jMax < beam height
	 * @param jMax	0 <= jMin <= jMax < beam height
	 * @return	IMED
	 */
	public static double calculateIMED(BeamCrossSection beam1, BeamCrossSection beam2, double sigma, int iMin, int iMax, int jMin, int jMax)
	{
		checkIndexBounds(beam1, beam2, iMin, iMax, jMin, jMax);
		
		double sum = 0;
		double twoSigmaSquared = 2.*sigma*sigma;
		
		for(int j1 = jMin; j1 <= jMax; j1++)
		{
			for(int i1 = iMin; i1 <= iMax; i1++)
			{
				for(int j2 = jMin; j2 <= jMax; j2++)
				{
					for(int i2 = iMin; i2 <= iMax; i2++)
					{
						sum += Math.exp(-(MyMath.sqr(i1-i2)+MyMath.sqr(j1-j2))/twoSigmaSquared)
								* (beam1.getIntensity(i1, j1) - beam2.getIntensity(i1, j1))
								* (beam1.getIntensity(i2, j2) - beam2.getIntensity(i2, j2));
					}
				}
			}
		}

		return Math.sqrt(sum / twoSigmaSquared / Math.PI);
	}
	

}
