package library.opticalSystem.laser;


import java.awt.*;

import library.opticalSystem.*;


public class Laser1DControlsWindow extends Frame
{
	private static final long serialVersionUID = 5718443728651756727L;

	private Laser1DInitialiser standardInitialiser;
		
	private Laser1D laser;

	
	//
	// graph components
	//
	
	private transient TextField
		roundTripNumberTextField,
		propagationDirectionTextField,
		powerInBeamTextField,
		currentElementTextField;


	public Laser1DControlsWindow(Laser1DInitialiser standardInitialiser)
	{
		super();
		
		this.standardInitialiser = standardInitialiser;
		
		addWindowListener(new Laser1DControlsWindowClosingAdapter());
		setSize(900, 100);
		// setResizable(false);
		
		setMenuBar(new Laser1DMenuBar(this));
		
		
		Panel statusPanel = new Panel();
		add(statusPanel);

		
		statusPanel.add(new Label("round trip #"));
		roundTripNumberTextField = new TextField(5);
		roundTripNumberTextField.setEditable(false);
		statusPanel.add(roundTripNumberTextField);
		
		statusPanel.add(new Label(", in front of element"));
		currentElementTextField = new TextField(25);
		currentElementTextField.setEditable(false);
		statusPanel.add(currentElementTextField);
		
		statusPanel.add(new Label(", propagation direction:"));
		propagationDirectionTextField = new TextField(5);
		propagationDirectionTextField.setEditable(false);
		statusPanel.add(propagationDirectionTextField);
		
		statusPanel.add(new Label(", power in beam:"));
		powerInBeamTextField = new TextField(12);
		powerInBeamTextField.setEditable(false);
		statusPanel.add(powerInBeamTextField);
		

		// do the layout...
		validate();
		
		// ...and fill the fields and show the window
		updateStatus();
	}
	
	public Laser1D getLaser()
	{
		return laser;
	}
	
	private void disposeOfOldLaser()
	{
		if(laser != null)
		{
			laser.closeAllWindows();
		}
	}
	
	public void newLaser()
	{
		disposeOfOldLaser();
		laser = new Laser1D(standardInitialiser, this);
	}
	
	public void open(String filename)
	{
		try
		{
			disposeOfOldLaser();
			laser = Laser1D.open(filename, this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void updateStatus()
	{
		if(laser != null)
		{
			roundTripNumberTextField.setText( "" + laser.getRoundTripCounter() );
			try
			{
				currentElementTextField.setText( laser.getCurrentElement().shortToString() );
			}
			catch(EndOfOpticalSystemError e)
			{
				currentElementTextField.setText("- end of optical system -");
			}
			propagationDirectionTextField.setText(
				(laser.getForwardDirection())?"( --> )":"( <-- )" );
			powerInBeamTextField.setText( "" + (float)laser.getPowerInBeam() );
			setTitle( laser.getStatusString() + laser.getFilename() );
		}
		else
		{
			roundTripNumberTextField.setText( "***" );
			currentElementTextField.setText( "************" );
			propagationDirectionTextField.setText( "***" );
			powerInBeamTextField.setText( "*******" );
			setTitle( "no laser data present" );
		}
		
		if(!isShowing()) setVisible(true);
	}
}
