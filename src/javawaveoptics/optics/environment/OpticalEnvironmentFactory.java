package javawaveoptics.optics.environment;

import javawaveoptics.optics.environment.FractalLaserOpticalEnvironment.FractalLaserType;

public class OpticalEnvironmentFactory
{
	public enum OpticalEnvironmentType
	{
		DOUBLE_SLIT("Double-slit experiment"),
		LIMITED("Limited optical environment"),
		AUTOSTEREOGRAM_RESONATOR("Autostereogram resonator"),
		KATIE("Katie's optical environment"),
		DOVE_PRISM_INTERFEROMETER("Dove-prism interferometer"),
		FRACTAL_LASER_1D("Fractal strip resonator, M=-2"),
		FRACTAL_LASER_M2A6("Fractal resonator, M=-2, 6-sided aperture"),
		FRACTAL_LASER_M2A7("Fractal resonator, M=-2, 7-sided aperture"),
		FRACTAL_LASER_M3A6("Fractal resonator, M=-3, 6-sided aperture"),
		RESONATOR_INTERFACE("Resonator interface");
		
		private String description;
		private OpticalEnvironmentType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	
	/**
	 * Creates and returns an optical environment based on the specified type number. The type number
	 * corresponds to one of the numbers defined above, which is used to decide which optical environment
	 * to load.
	 * 
	 * @param type		The type number, as defined above
	 * @return			The corresponding optical environment
	 */
	public static AbstractOpticalEnvironment createOpticalEnvironment(OpticalEnvironmentType type)
	{
		switch(type)
		{
			case LIMITED:
				return new LimitedExampleOpticalEnvironment();
			
			case AUTOSTEREOGRAM_RESONATOR:
				return new AutostereogramOpticalEnvironment();
				
			case KATIE:
				return new KatiesOpticalEnvironment();
				
			case DOVE_PRISM_INTERFEROMETER:
				return new DovePrismInterferometerOpticalEnvironment();
				
			case FRACTAL_LASER_M2A6:
				return new FractalLaserOpticalEnvironment(FractalLaserType.M2A6);

			case FRACTAL_LASER_M2A7:
				return new FractalLaserOpticalEnvironment(FractalLaserType.M2A7);

			case FRACTAL_LASER_M3A6:
				return new FractalLaserOpticalEnvironment(FractalLaserType.M3A6);

			case FRACTAL_LASER_1D:
				return new FractalStripLaserOpticalEnvironment();
			
			case RESONATOR_INTERFACE:
				return new ResonatorInterfaceOpticalEnvironment();

			case DOUBLE_SLIT:
			default:
				// return new DoubleSlitOpticalEnvironmentRestricted();
				return new DoubleSlitOpticalEnvironment();
		}
	}
}
