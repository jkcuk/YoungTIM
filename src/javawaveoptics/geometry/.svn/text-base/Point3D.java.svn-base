package javawaveoptics.geometry;

import library.maths.MyMath;

public class Point3D
{
	double i;
	double j;
	double k;
	
	public Point3D(double i, double j, double k)
	{
		this.i = i;
		this.j = j;
		this.k = k;
	}
	
	public Point3D(Point3D point)
	{
		this(point.getI(), point.getJ(), point.getK());
	}
	
	public Point3D()
	{
		this(0, 0, 0);
	}
	
	public double getISquared()
	{
		return i * i;
	}
	
	public double getJSquared()
	{
		return j * j;
	}
	
	public double getKSquared()
	{
		return k * k;
	}

	public double getI()
	{
		return i;
	}

	public void setI(double i)
	{
		this.i = i;
	}

	public double getJ()
	{
		return j;
	}

	public void setJ(double j)
	{
		this.j = j;
	}
	
	public double getK()
	{
		return k;
	}

	public void setK(double k)
	{
		this.k = k;
	}
	
	public double getDistanceFrom(Point3D P)
	{
		return Math.sqrt(
				MyMath.sqr(P.getI()-getI()) +
				MyMath.sqr(P.getJ()-getJ()) +
				MyMath.sqr(P.getK()-getK())
			);
	}
}