package javawaveoptics.ui;

import java.io.Serializable;

import javawaveoptics.optics.environment.AbstractOpticalEnvironment;

import javax.swing.JPanel;

/**
 * Defines a generic user interface class, not necessarily graphical. The basic requirements
 * of a user interface are that there exists an optical environment.
 * 
 * @author Sean
 */
public abstract class AbstractUI extends JPanel implements Serializable
{
	private static final long serialVersionUID = -528649088433971082L;

	// The optical environment in use
	protected AbstractOpticalEnvironment opticalEnvironment;
	
	public AbstractUI(AbstractOpticalEnvironment opticalEnvironment)
	{
		// Set the environment
		this.opticalEnvironment = opticalEnvironment;
	}
	
	public AbstractOpticalEnvironment getOpticalEnvironment()
	{
		return opticalEnvironment;
	}

	public void setOpticalEnvironment(AbstractOpticalEnvironment environment)
	{
		this.opticalEnvironment = environment;
	}
}
