package javawaveoptics.geometry;

/**
 * Represents a hemisphere.
 * It's the half of the sphere of radius <radius>, centred at <centre>, that's given by z >= z_<centre>.
 * 
 * @author Sean, Johannes
 */
public class Hemisphere3D extends AbstractSurface3D
{
	private double radius, radiusSquared;
	private Point3D centre;
	
	public Hemisphere3D(double radius, Point3D centre)
	{
		setRadius(radius);
		setCentre(centre);
	}
	
	@Override
	public double getZ(double x, double y)
	{
		double xRelative = x - centre.getI();
		double yRelative = y - centre.getJ();
		
		double square = radiusSquared - xRelative * xRelative - yRelative * yRelative;
		
		if(square >= 0)
		{
			// if the (x,y) coordinates lie on the hemisphere, return the position on the hemisphere
			return centre.getK() + Math.sqrt(square);
		}
		else
		{
			// if the (x,y) coordinates don't actually lie on the hemisphere, return the plane z=z_<centre>
			return centre.getK();
		}
	}

	public double getRadius()
	{
		return radius;
	}

	public void setRadius(double radius)
	{
		this.radius = radius;
		radiusSquared = radius * radius;
	}

	public Point3D getCentre() {
		return centre;
	}

	public void setCentre(Point3D centre) {
		this.centre = centre;
	}
}
