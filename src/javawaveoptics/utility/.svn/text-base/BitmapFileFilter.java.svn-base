package javawaveoptics.utility;

import java.io.File;
import java.io.Serializable;

import javax.swing.filechooser.FileFilter;

/**
 * Custom file filter for bitmap files.
 * 
 * @author Sean
 */
public class BitmapFileFilter extends FileFilter implements Serializable
{
	private static final long serialVersionUID = -151051192229731905L;

	@Override
	public boolean accept(File file)
	{
		// Accept directories
		if(file.isDirectory())
		{
			return true;
		}
		else
		{
	        String extension = "";
	        String fileName = file.getName();
	        
	        int index = fileName.lastIndexOf('.');
	        
	        // index will be > -1 if a full stop has been found at some point
	        if(index > -1)
	        {
	        	extension = fileName.substring(index).toLowerCase();
	        }
	        
	        if(extension.equals(".bmp"))
	        {
	        	return true;
	        }
	        else
	        {
	        	return false;
	        }
		}
	}

	@Override
	public String getDescription()
	{
		return "Bitmap Files (*.bmp)";
	}
}