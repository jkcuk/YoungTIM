package javawaveoptics.ui;

import javax.swing.JComboBox;


/**
 * Defines a component for editing units.
 * 
 * @author Johannes
 */
public class PowersOf2ComboBox extends JComboBox
{
	private static final long serialVersionUID = -8959069547387673210L;

	private static int[] powersOf2 = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536};
	
	public PowersOf2ComboBox()
	{
		super();
		
		for(int i = 0; i < 16; i++)
		{
			addItem(powersOf2[i]);
		}
		setSelectedItem("256");
	}
	
	/**
	 * Get the multiplication factor corresponding to the unit, e.g. 1e-3 for mm, 1e-9 for nm
	 * @return	the multiplication factor for this unit
	 */
	public int getValue()
	{
		return powersOf2[getSelectedIndex()];
	}
	
	public void setValue(int number)
	{
		double log2Number = Math.log(number) / Math.log(2.0);
		// int i = new Double(log2Number).intValue();
		// int i = (int)log2Number-1; // works if the list starts with "2"
		int i = (int)log2Number; // works if the list starts with "1"?
		if(i > powersOf2.length) i = powersOf2.length;

		setSelectedIndex(i);
	}
}
