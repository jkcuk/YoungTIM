package javawaveoptics.optics.component;

import java.util.ArrayList;
import java.util.BitSet;

public class OpticalComponentFactory
{
//	public enum StartComponents
//	{
//		LIGHT_SOURCE("Light source"),
//		IMAGE_OF_PLANE("Image of plane");
//		
//		private String description;
//		private StartComponents(String description) {this.description = description;}	
//		@Override
//		public String toString() {return description;}
//	}

	private static final String[] lightSources = {
			"Light source",
			"Image of plane"
	};

//	public enum MiddleComponents
//	{
//		APERTURE("Aperture"),
//		BEAM_EXPANDER("Beam expander"),
//		BEAM_ROTATOR("Beam rotator"),
//		BEAM_SPLITTER("Beam splitter"),
//		CYLINDRICAL_LENS("Cylindrical lens"),
//		CYLINDRICAL_LENS_MODE_CONVERTER("Cylindrical-lens mode converter"),
//		DISTANCE("Distance"),
//		DOVE_PRISM("Dove prism"),
//		DOVE_PRISM_ARRAY("Dove-prism array"),
//		EITHER_OR_COMPONENT("Either/or component"),
//		FOURIER_LENS("Fourier lens"),
//		HOLOGRAM("Hologram"),
//		HOLOGRAMMIFIER("Hologrammifier"),
//		HOLOGRAM_FROM_FILE("Hologram (from file)"),
//		LENS("Lens"),
//		NEUTRAL_DENSITY_FILTER("Neutral-density filter"),
//		PHASE_CONJUGATING_SURFACE("Phase-conjugating surface"),
//		PLANE("Plane"),
//		SPIRAL_PHASE_PLATE("Spiral phase plate"),
//		WEDGE("Wedge");
//		
//		private String description;
//		private MiddleComponents(String description) {this.description = description;}	
//		@Override
//		public String toString() {return description;}
//	}

	private static final String[] nonLightSources = {
			"Aperture",
			"Aperture stack",
			"Beam expander",
			"Beam rotator",
			"Beam splitter",
			"Clone of component",
			"Cylindrical lens",
			"Cylindrical-lens mode converter",
			"Cylindrical-lens spiral",
			"Distance",
			"Dove prism",
			"Dove-prism array",
			// "Either/or component",
			"Fourier lens",
			"Hologram",
			"Hologram (from bitmap)",
			"Hologrammifier",
			"Lens",
			"Lenslet array",
			"Mirror",
			"Neutral-density filter",
			"Phase-conjugating surface",
			"Plane",
			"Spiral phase plate",
			"Wedge"
	};
	
	public static AbstractOpticalComponent create(String name)
	{
		AbstractOpticalComponent opticalComponent = null;

		if(name.equals("Light source"))
		{
			opticalComponent = new LightSource();
		}
		else if(name.equals("Distance"))
		{
			opticalComponent = new Distance();
		}
		else if(name.equals("Aperture"))
		{
			opticalComponent = new Aperture();
		}
		else if(name.equals("Aperture stack"))
		{
			opticalComponent = new ApertureStack();
		}
		else if(name.equals("Lens"))
		{
			opticalComponent = new Lens();
		}
		else if(name.equals("Fourier lens"))
		{
			opticalComponent = new FourierLens();
		}
		else if(name.equals("Cylindrical lens"))
		{
			opticalComponent = new CylindricalLens();
		}
		else if(name.equals("Lenslet array"))
		{
			opticalComponent = new LensletArray();
		}
		else if(name.equals("Cylindrical-lens spiral"))
		{
			opticalComponent = new CylindricalLensSpiral();
		}
		else if(name.equals("Wedge"))
		{
			opticalComponent = new Wedge();
		}
		else if(name.equals("Beam splitter"))
		{
			opticalComponent = new BeamSplitter();
		}
		else if(name.equals("Mirror"))
		{
			opticalComponent = new Mirror();
		}
		else if(name.equals("Hologram"))
		{
			opticalComponent = new Hologram();
		}
		else if(name.equals("Hologrammifier"))
		{
			opticalComponent = new Hologrammifier();
		}
		else if(name.equals("Hologram (from bitmap)"))
		{
			opticalComponent = new HologramFromBitmap();
		}
		else if(name.equals("Spiral phase plate"))
		{
			opticalComponent = new SpiralPhasePlate();
		}
		else if(name.equals("Plane"))
		{
			opticalComponent = new Plane();
		}
		else if(name.equals("Image of plane"))
		{
			opticalComponent = new ImageOfPlane();
		}
		else if(name.equals("Beam rotator"))
		{
			opticalComponent = new BeamRotator();
		}
		else if(name.equals("Dove prism"))
		{
			opticalComponent = new DovePrism();
		}
		else if(name.equals("Dove-prism array"))
		{
			opticalComponent = new DovePrismArray();
		}
		else if(name.equals("Autostereogram resonator (experts only!)"))
		{
			opticalComponent = new AutostereogramResonator();
		}
//		else if(name.equals("Light source/Image of plane"))
//		{
//			opticalComponent = new ImageOfPlaneAutoInitialising();
//		}
		else if(name.equals("Beam expander"))
		{
			opticalComponent = new BeamExpander();
		}
		else if(name.equals("Cylindrical-lens mode converter"))
		{
			opticalComponent = new CylindricalLensModeConverter();
		}
		else if(name.equals("Neutral-density filter"))
		{
			opticalComponent = new NeutralDensityFilter();
		}
		else if(name.equals("Phase-conjugating surface"))
		{
			// opticalComponent = new EitherOrPhaseConjugateSurface();
			opticalComponent = new PhaseConjugator();
		}
		else if(name.equals("Either/or component"))
		{
			opticalComponent = new EitherOrComponent();
		}
		else if(name.equals("Clone of component"))
		{
			opticalComponent = new CloneOfComponent();
		}
		
		return opticalComponent;
	}
	
	public static ArrayList<String> getLightSourceComponents()
	{
		return getLightSourceComponents(new BitSet());
	}
	
	/**
	 * @return	a list of light-source component types
	 */
	public static ArrayList<String> getLightSourceComponents(BitSet bitSet)
	{
		ArrayList<String> availableComponents = new ArrayList<String>();
		
		int i = 0;
		
		for(String component : lightSources)
		{
			// Check if component is enabled
			// False means yes
			if(!bitSet.get(i))
			{
				availableComponents.add(component);
			}
			
			i++;
		}
		
		return availableComponents;
	}
	
	public static ArrayList<String> getNonLightSourceComponents()
	{
		return getNonLightSourceComponents(new BitSet());
	}
	
	/**
	 * @return	a list of non-light-source component types
	 */
	public static ArrayList<String> getNonLightSourceComponents(BitSet bitSet)
	{
		ArrayList<String> availableComponents = new ArrayList<String>();
		
		int i = 0;
		
		for(String component : nonLightSources)
		{
			// Check if component is enabled
			// False means yes
			if(!bitSet.get(i))
			{
				availableComponents.add(component);
			}
			
			i++;
		}
		
		return availableComponents;
	}
	
	/**
	 * Returns the number of disabled light sources in the specified bit set.
	 * 
	 * @param lightSourceComponentsBitSet
	 * 
	 * @return
	 */
	public static int getNumberOfDisabledLightSourceComponents(BitSet lightSourceComponentsBitSet)
	{
		return lightSourceComponentsBitSet.cardinality();
	}
	
	/**
	 * Returns the number of enabled light sources in the specified bit set.
	 * 
	 * @param lightSourceComponentsBitSet
	 * 
	 * @return
	 */
	public static int getNumberOfEnabledLightSourceComponents(BitSet lightSourceComponentsBitField)
	{
		return lightSources.length - lightSourceComponentsBitField.cardinality();
	}
	
	/**
	 * Returns the number of disabled non light sources in the specified bit set.
	 * 
	 * @param nonLightSourceComponentsBitSet
	 * 
	 * @return
	 */
	public static int getNumberOfDisabledNonLightSourceComponents(BitSet nonLightSourceComponentsBitSet)
	{
		return nonLightSourceComponentsBitSet.cardinality();
	}
	
	/**
	 * Returns the number of enabled non light sources in the specified bit set.
	 * 
	 * @param nonLightSourceComponentsBitSet
	 * 
	 * @return
	 */
	public static int getNumberOfEnabledNonLightSourceComponents(BitSet nonLightSourceComponentsBitField)
	{
		return nonLightSources.length - nonLightSourceComponentsBitField.cardinality();
	}
	
	public static int getNumberOfLightSources()
	{
		return lightSources.length;
	}
	
	public static int getNumberOfNonLightSources()
	{
		return nonLightSources.length;
	}

	public static String[] getLightSources()
	{
		return lightSources;
	}

	public static String[] getNonLightSources()
	{
		return nonLightSources;
	}
}
