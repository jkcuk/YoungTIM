package javawaveoptics.geometry;

/**
 * Defines a generic 3D surface.
 * 
 * @author Sean
 */
public abstract class AbstractSurface3D
{
	/**
	 * Returns the z value for a given x and y value
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public abstract double getZ(double x, double y);
}
