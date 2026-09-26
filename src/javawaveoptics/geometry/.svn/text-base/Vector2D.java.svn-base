package javawaveoptics.geometry;

public class Vector2D
{
	Point2D point;
	
	public Vector2D(Point2D point)
	{
		this.point = point;
	}
	
	public Vector2D(Vector2D vector)
	{
		this.point = vector.getPoint();
	}
	
	public Vector2D()
	{
		this.point = new Point2D();
	}
	
	public void rotate(double angle)
	{
		rotate(Math.cos(angle), Math.sin(angle));
	}
	
	public void rotate(double cosAngle, double sinAngle)
	{
		point = new Point2D(point.getI() * cosAngle - point.getJ() * sinAngle, point.getI() * sinAngle + point.getJ() * cosAngle);
	}

	public double getLength()
	{
		return Math.pow(point.getISquared() + point.getJSquared(), 0.5);
	}

	public Point2D getPoint()
	{
		return point;
	}

	public void setPoint(Point2D point)
	{
		this.point = point;
	}
}