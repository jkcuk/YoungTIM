package library.util;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Ending listener to close program
 * 
 * @author Sean
 *
 */
public class EndingListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		System.exit(0);
	}
}
