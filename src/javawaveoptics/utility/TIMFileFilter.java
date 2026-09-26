package javawaveoptics.utility;

import java.io.File;
import java.io.Serializable;

import javax.swing.filechooser.FileFilter;

/**
 * Custom file filter for YoungTIM files.
 * 
 * @author Sean
 */
public class TIMFileFilter extends FileFilter implements Serializable
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
	        
	        if(extension.equals(".tim"))
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
		return "Young TIM Files (*.tim)";
	}
}