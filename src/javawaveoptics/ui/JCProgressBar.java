package javawaveoptics.ui;

import javax.swing.JProgressBar;


/**
 * A slightly modified JProgressBar.
 * 
 * @author Johannes
 */
public class JCProgressBar extends JProgressBar
{
	private static final long serialVersionUID = -6274321419539417133L;

	public JCProgressBar(int min, int max)
	{
		super(min, max);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setValue(int n)
	{
		super.setValue(n);
		
		setToolTipText((int)(100*getPercentComplete()+0.5)+"% complete (completed step "+n+" out of "+getMaximum()+")");
	}
}
