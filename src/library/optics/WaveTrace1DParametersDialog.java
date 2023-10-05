/*	WaveTrace1DSettingsDialog.java

	Provides a dialog to alter the fundamental settings of the beam propagation program in 1D,
	the size of the amplitude matrix, the physical size represented by the matrix, and the
	wavelength of the modelled light.

	Example:
	
		import johannes.optics.*;
		
		WaveTrace1DParameters p;
		
		Frame f = new Frame();
		
		// create new WaveTrace1DParametersDialog
		WaveTrace1DParametersDialog wtpd = new WaveTrace1DParametersDialog(f);
		
		// communicate the current settings to the dialog
		wtpd.setParameters(p);
		
		// start dialog
		wtpd.show();
		
		// free system resources associated with frame
		f.dispose();
		
		// was the dialog ended by clicking on OK or Cancel?
		if(wtsd.OK())
		{
			// dialog was ended by clicking OK
			
			// change parameters
			p = wtpd.getParameters();
		}
		
		// free system resources associated with dialog
		wtsd.dispose();

		// create a new LightBeamCrossSection1D
		LightBeamCrossSection1D b = new LightBeamCrossSection1D(
			p.matrixSize, // width
			p.physicalSize, // physical width in meters
			p.lambda // wavelength
		);
*/

package library.optics;


import java.awt.*;
import java.awt.event.*;


public class WaveTrace1DParametersDialog extends Dialog
implements ActionListener
{
	private static final long serialVersionUID = -3223821545667694094L;
	
	//
	// the window components
	//

	private TextField
		matrixSizeTextField,
		physicalSizeTextField,
		lambdaTextField;

	private Button OKButton, CancelButton;
	
	
	//
	// variable to indicate how dialog was exited
	//
	
	private boolean OK;
	
	
	//
	// constructor
	//
	
	public WaveTrace1DParametersDialog(Frame parent)
	{
		// create a modal dialog
		super(parent, "1D beam propagation parameters", true);
		
		setSize(300, 130);
		setResizable(false);
		
		setLayout(new BorderLayout());
		
		Panel centrePanel = new Panel(new GridLayout(3,1));
		
		Panel widthPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		widthPanel.add(new Label("physical width ="));
		widthPanel.add(physicalSizeTextField = new TextField(7));
		widthPanel.add(new Label("mm,"));
		centrePanel.add(widthPanel);
		
		Panel matrixSizePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		matrixSizePanel.add(new Label("represented by matrix of size ="));
		matrixSizePanel.add(matrixSizeTextField = new TextField(5));
		matrixSizePanel.add(new Label(","));
		centrePanel.add(matrixSizePanel);
		
		Panel lambdaPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		lambdaPanel.add(new Label("light wavelength ="));
		lambdaPanel.add(lambdaTextField = new TextField(7));
		lambdaPanel.add(new Label("nm"));
		centrePanel.add(lambdaPanel);
		
		add("Center", centrePanel);


		///////////////////////////
		// cancel and OK buttons //
		///////////////////////////

		Panel cancelOKPanel = new Panel();
		cancelOKPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add("South", cancelOKPanel);
		
		CancelButton = new Button("Cancel");
		cancelOKPanel.add(CancelButton);
		CancelButton.addActionListener(this);
		
		OKButton = new Button("OK");
		cancelOKPanel.add(OKButton);
		OKButton.addActionListener(this);
		
		// do the layout
		validate();
	}


	//
	// methods for accessing data
	//
	
	public void setParameters(WaveTrace1DParameters p)
	{
		matrixSizeTextField.setText("" + p.matrixSize);
		physicalSizeTextField.setText("" + 1000*p.physicalSize);
		lambdaTextField.setText("" + 1e9*p.lambda);
	}
	
	public WaveTrace1DParameters getParameters()
	{
		WaveTrace1DParameters p = new WaveTrace1DParameters();
		
		p.matrixSize = Integer.parseInt(matrixSizeTextField.getText());
		p.physicalSize = Double.valueOf(physicalSizeTextField.getText()).doubleValue() / 1000;
		p.lambda = Double.valueOf(lambdaTextField.getText()).doubleValue() / 1e9;
		
		return p;
	}
	
	public boolean OK()
	{
		return OK;
	}
	

	private void validateNumberFormat()
	throws NumberFormatException
	{
		// see whether all the text field contents are numbers
		Integer.parseInt(matrixSizeTextField.getText());
		Double.valueOf(physicalSizeTextField.getText());
		Double.valueOf(lambdaTextField.getText());
	}
	

	//
	// ActionListener method
	//
	
	public void actionPerformed(ActionEvent ae)
	{
		Object eventSource = ae.getSource();
		
		try
		{
			if(eventSource == OKButton)
			{
				validateNumberFormat();
				OK = true;
				setVisible(false);
			}
			else if(eventSource == CancelButton)
			{
				OK = false;
				setVisible(false);
			}
			else repaint();
		}
		catch(NumberFormatException e)
		{
			System.out.println(e);
		}
	}
}