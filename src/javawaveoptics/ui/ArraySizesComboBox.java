package javawaveoptics.ui;

import javax.swing.JComboBox;


/**
 * Defines a component for editing units.
 * 
 * @author Johannes
 */
public class ArraySizesComboBox extends JComboBox<String>
{
	private static final long serialVersionUID = -3477742679410885870L;

	private static int[] powersOf2 = {8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536};
	
	public ArraySizesComboBox()
	{
		super();
		
		for(int i = 0; i < powersOf2.length; i++)
		{
			addItem(powersOf2[i] + " \u2a09 " + powersOf2[i]);
		}
		setSelectedItem("256 \u2a09 256");
	}
	
	public int getValue()
	{
		return powersOf2[getSelectedIndex()];
	}
	
	public void setValue(int number)
	{
		double log2Number = Math.log(number/powersOf2[0]) / Math.log(2.0);
		// int i = new Double(log2Number).intValue();
		int i = (int)log2Number;
		if(i > powersOf2.length) i = powersOf2.length;

		setSelectedIndex(i);
	}
}
