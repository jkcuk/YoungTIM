package javawaveoptics.optics.aperture;

public enum ApertureType {
	ABSORBING_BOUNDARY("Absorbing boundary"),
	CIRCULAR_APERTURE("Circular aperture"),
	DOUBLE_SLIT("Double slit"),
	GAUSSIAN_APERTURE("Gaussian aperture"),
	GRATING("Grating"),
	POLYGONAL_APERTURE("Polygonal aperture"),
	SLIT("Slit");

	private String description;
	
	private ApertureType(String description)
	{
		this.description = description;
	}
	
	public static AbstractAperture getAperture(ApertureType apertureType)
	{
		switch(apertureType)
		{
		case ABSORBING_BOUNDARY:
			return new AbsorbingBoundary();
		case CIRCULAR_APERTURE:
			return new AnnularAperture();
		case DOUBLE_SLIT:
			return new DoubleSlit();
		case GAUSSIAN_APERTURE:
			return new GaussianAperture();
		case GRATING:
			return new Grating();
		case POLYGONAL_APERTURE:
			return new PolygonalAperture();
		case SLIT:
		default:
			return new Slit();
		}
	}
	
	public static ApertureType getApertureType(AbstractAperture aperture)
	{
		if(aperture instanceof AbsorbingBoundary) return ABSORBING_BOUNDARY;
		else if(aperture instanceof AnnularAperture) return CIRCULAR_APERTURE;
		else if(aperture instanceof DoubleSlit) return DOUBLE_SLIT;
		else if(aperture instanceof GaussianAperture) return GAUSSIAN_APERTURE;
		else if(aperture instanceof Grating) return GRATING;
		else if(aperture instanceof PolygonalAperture) return POLYGONAL_APERTURE;
		else if(aperture instanceof Slit) return SLIT;
		
		// if nothing has been returned
		return null;
	}

	@Override
	public String toString() {return description;}
}
