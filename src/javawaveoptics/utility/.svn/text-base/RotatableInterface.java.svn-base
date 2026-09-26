package javawaveoptics.utility;

/**
 * Defines an optical component which is to be allowed to be rotated. A class implementing this interface
 * must implement functions to get and set the angle (in radians) at which the component should be rotated
 * with respect to the optical axis.
 * 
 * The simulation code identifies classes which implement this interface and, before passing light through
 * the optical component represented by that class, will 'rotate' the object so as to simulate the passage
 * of light through the component as if it were actually rotated.
 * 
 * @author Sean
 */
public interface RotatableInterface
{
	/**
	 * Returns the current angle to which the component should be rotated.
	 * 
	 * @return	Angle (in radians)
	 */
	public double getRotateAngle();
	
	/**
	 * Sets the angle to which the component should be rotated.
	 * 
	 * @param angle						Angle (in radians)
	 * @throws NumberFormatException
	 */
	public void setRotateAngle(double angle) throws NumberFormatException;
}
