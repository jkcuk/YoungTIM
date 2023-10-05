package javawaveoptics.ui;

import javax.swing.JComboBox;


/**
 * Defines a component for editing units.
 * 
 * @author Johannes
 */
public class LengthUnitsComboBox extends JComboBox
{
	private static final long serialVersionUID = -6322252140398577913L;

	private static String[] unitsStrings = { "fm",  "pm",  "nm", "\u00B5m", "mm", "cm", "m", "km" };
	private static double[] unitsFactors = { 1e-15, 1e-12, 1e-9, 1e-6,      1e-3, 1e-2, 1,   1e3  };

	public LengthUnitsComboBox()
	{
		super(unitsStrings);
		setSelectedItem("m");
		setMaximumSize(getPreferredSize());
	}
	
	/**
	 * Get the multiplication factor corresponding to the unit, e.g. 1e-3 for mm, 1e-9 for nm
	 * @return	the multiplication factor for this unit
	 */
	public double getMultiplicationFactor()
	{
		return unitsFactors[getSelectedIndex()];
	}
	
	public String getUnitString()
	{
		return unitsStrings[getSelectedIndex()];
	}

	/**
	 * Set to a suitable unit for a given value
	 * @param lengthInMetres	length in metres, e.g. 2e-3 = 2 mm
	 * @return	the numerical value of the given length in metres (in our example 2), in the suitable unit (in our example mm)
	 */
	public double setSuitableUnitForLengthInMetres(double lengthInMetres)
	{
		int i;
		
		if(lengthInMetres == 0.0)
		{
			setSelectedItem("m");
			i = getSelectedIndex();
		}
		else
		{
			for(i=0; (i<unitsFactors.length-1) && (Math.abs(lengthInMetres) >= unitsFactors[i+1]); i++);
			setSelectedIndex(i);
		}

		return lengthInMetres / unitsFactors[i];
	}
	
	public static String length2NiceString(double lengthInMetres)
	{
		int index;
		
		if(lengthInMetres == 0.0)
		{
			for(index=0; (index<unitsStrings.length) && !unitsStrings[index].equals("m"); index++);
		}
		else
		{
			for(index=0; (index<unitsFactors.length-1) && (Math.abs(lengthInMetres) >= unitsFactors[index+1]); index++);
		}

		return String.format("%3.2f", lengthInMetres / unitsFactors[index]) + " " + unitsStrings[index];
	}
}
