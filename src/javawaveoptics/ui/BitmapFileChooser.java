package javawaveoptics.ui;

import java.io.File;
import java.io.Serializable;

import javawaveoptics.utility.BitmapFileFilter;

import javax.swing.JFileChooser;

/**
 * Custom file chooser for bitmap files.
 * 
 * @author Sean, Johannes
 */
public class BitmapFileChooser extends JFileChooser implements Serializable
{
	private static final long serialVersionUID = 2559411349883420442L;

	public BitmapFileChooser(String dialogTitle, boolean showBitmapPreview)
	{
		super();
		
		setDialogTitle(dialogTitle);
		
		// Set bitmap filter
		setFileFilter(new BitmapFileFilter());
		
		// Set up the preview box
		if(showBitmapPreview) setAccessory(new BitmapPreview(this));
	}
	
	public BitmapFileChooser(File file)
	{
		super(file);
		
		// Set bitmap filter
		setFileFilter(new BitmapFileFilter());
		
		// Set up the preview box
		setAccessory(new BitmapPreview(this));
	}
}
