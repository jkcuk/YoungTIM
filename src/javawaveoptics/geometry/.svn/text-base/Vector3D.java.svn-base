package javawaveoptics.geometry;

public class Vector3D
{
	Point3D point;
	
	public Vector3D(Point3D point)
	{
		this.point = point;
	}
	
	public Vector3D(Vector3D vector)
	{
		this.point = vector.getPoint();
	}
	
	public Vector3D()
	{
		this.point = new Point3D();
	}
	
	public void rotate(double azimuth, double zenith)
	{		
		double length = getLength();
		
		point = new Point3D(
				length * Math.sin(azimuth) * Math.cos(zenith),
				length * Math.sin(azimuth) * Math.sin(zenith),
				length * Math.cos(azimuth)
			);
	}

	public double getLength()
	{
		return Math.pow(point.getISquared() + point.getJSquared() + point.getKSquared(), 0.5);
	}
	
	/**
	 * Normalises the vector.
	 * @return	the normalised vector
	 */
	public Vector3D normalise()
	{
		double length = getLength();
		
		point.setI(point.getI()/length);
		point.setJ(point.getJ()/length);
		point.setK(point.getK()/length);
		
		return this;
	}
	
	static public Vector3D getNormalised(Vector3D v)
	{
		double length = v.getLength();
		
		return new Vector3D(new Point3D(v.getPoint().getI()/length, v.getPoint().getJ()/length, v.getPoint().getK()/length));
	}

	public Point3D getPoint()
	{
		return point;
	}

	public void setPoint(Point3D point)
	{
		this.point = point;
	}
}