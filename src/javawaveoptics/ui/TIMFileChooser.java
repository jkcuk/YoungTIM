package javawaveoptics.ui;

import java.io.Serializable;

import javawaveoptics.utility.TIMFileFilter;

import javax.swing.JFileChooser;

/**
 * Custom file chooser for YoungTIM files.
 * 
 * @author Sean
 */
public class TIMFileChooser extends JFileChooser implements Serializable
{
	private static final long serialVersionUID = 2559411349883420442L;

	public TIMFileChooser()
	{
		super();
		
		// Set bitmap filter
		setFileFilter(new TIMFileFilter());
	}
}
