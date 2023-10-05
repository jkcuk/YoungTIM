package library.opticalSystem;



import library.optics.*;


/////////////////////
// an aperture ... //
/////////////////////

public class AsymmetricSlitAperture1D extends BasicOpticalElement1D
{
	private static final long serialVersionUID = 1568729942099070469L;
	
	//
	// variables
	//
	
	public double
		xMin, xMax; // x range of opening	
	
	//
	// constructor(s)
	//
	
	public AsymmetricSlitAperture1D()
	{
		name = "new asymmetric slit aperture";
		z = 0;
		xMin = -0.5;
                xMax = 0.5;
	}
	
	public AsymmetricSlitAperture1D(String name, double z, double x1, double x2)
	{
		this.name = name;
		this.z = 1000*z;
		this.xMin = 1000*Math.min(x1, x2); // store data in mm
		this.xMax = 1000*Math.max(x1, x2);
	}

        public AsymmetricSlitAperture1D(AsymmetricSlitAperture1D o)
        {
            name = o.name;
            z = o.z;
            xMin = o.xMin;
            xMax = o.xMax;
        }
	
	
	//
	// OpticalElement1D methods
	//
	
	public String getTypeName() { return "asymmetric slit aperture"; }
	
	public String toString()
	{
		return
			"asymmetric slit aperture " + super.toString() + ", xMin=" +
			(float)xMin + "mm, xMax=" + (float)xMax + "mm";
	}

	public String getExplanation()
	{
            return
            "Asymmetric slit aperture, (hard) edges at\n" +
            "xMin and xMax.\n" +
            super.getExplanation();
	}

	//
	// BasicOpticalElement1D methods
	//
	
	public void act(LightBeamCrossSection1D b)
	{
            b.passThroughSlitAperture(1e-3 * xMin, 1e-3 * xMax);
	}
}
