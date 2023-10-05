package library.opticalSystem;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import library.awt.*;
import library.list.*;
import library.maths.*;
import library.optics.*;
import library.util.*;


public class Beam1DInitialisationDialog extends Dialog
implements ActionListener, ItemListener, Serializable
{
	private static final long serialVersionUID = -7797673447331492453L;
	
	//
	// the window components
	//
	
	private Choice planeChoice;
	private Button OKButton, CancelButton;
	
	
	// the File/Functional form card layout
	private Choice fileOrFunctionChoice;
	private CardLayout fileOrFunctionCardLayout;
	private Panel fileOrFunctionPanel;
	
	//
	// file card components
	//
	
	private TextField filenameTextField;
	private Button chooseFileButton;
	
	//
	// functional form card components
	//
	
	// components related to the card layout
	private Choice realOrFTChoice;
	private CardLayout realOrFTCardLayout;
	private Panel realOrFTPanel;
	
	// other components
	private Choice peakShapeChoice;
	private Checkbox symmetriseFTCheckbox;
	private TextField
		peakPositionTextField, peakWidthTextField;
	private Label
		peakPositionUnitLabel, peakWidthUnitLabel;
		
	private Frame parent;
	
	// the optical system and the light beam cross-section
	private LightBeamInOpticalSystem1D beamAndSystem;
	
	
	public Beam1DInitialisationDialog
	(Frame parent, LightBeamInOpticalSystem1D beamAndSystem)
	{
		// create a modal dialog
		super(parent, "Beam initialisation", true);
		
		this.parent = parent;
		
		this.beamAndSystem = beamAndSystem;
		
		setSize(450, 200);
		setResizable(false);
		
		// arrange everything very nicely
		setLayout(new BorderLayout());
		
		Panel northPanel = new Panel(new BorderLayout());
		add("North", northPanel);
		
		planeChoice = new Choice();
		// add all the elements of type "plane" in the system
		addAllBeamInitialisationElements();
		northPanel.add("North", new ComponentWithLabel(planeChoice, "plane:"));
		
		fileOrFunctionChoice = new Choice();
		fileOrFunctionChoice.addItemListener(this);
		fileOrFunctionChoice.add("functional form");
		fileOrFunctionChoice.add("read from file");
		northPanel.add("South", new ComponentWithLabel(fileOrFunctionChoice, "initialisation method:"));
		
		fileOrFunctionCardLayout = new CardLayout();
		fileOrFunctionPanel = new Panel(fileOrFunctionCardLayout);
		add("Center", fileOrFunctionPanel);


		// the functional form panel
		
		Panel functionalFormPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		fileOrFunctionPanel.add("functional form", functionalFormPanel);
		
		
		peakShapeChoice = new Choice();
		peakShapeChoice.add("Top-hat");
		peakShapeChoice.add("Gaussian");
		functionalFormPanel.add(peakShapeChoice);
		
		functionalFormPanel.add(new Label("amplitude peak of width"));
		
		peakWidthTextField = new TextField(7);
		peakWidthTextField.setText("" + (float)beamAndSystem.b.getPhysicalWidth() * 1000);
		functionalFormPanel.add(peakWidthTextField);
		peakWidthUnitLabel = new Label("mm        ");
		functionalFormPanel.add(peakWidthUnitLabel);
		
		functionalFormPanel.add(new Label("centred at position"));
		
		peakPositionTextField = new TextField(7);
		peakPositionTextField.setText("0.0");
		functionalFormPanel.add(peakPositionTextField);
		peakPositionUnitLabel = new Label("mm        ");
		functionalFormPanel.add(peakPositionUnitLabel);
		
		functionalFormPanel.add(new Label("in"));
		
		realOrFTChoice = new Choice();
		realOrFTChoice.addItemListener(this);
		
		// place realOrFTChoice in the dialog
		functionalFormPanel.add(realOrFTChoice);
		
		functionalFormPanel.add(new Label("."));

		
		//
		// define the CardLayout panel that switches between real and Fourier space
		//
		
		realOrFTPanel = new Panel();
		realOrFTCardLayout = new CardLayout();
		realOrFTPanel.setLayout(realOrFTCardLayout);
		functionalFormPanel.add(realOrFTPanel);


		//
		// real space
		//
		
		Panel realSpacePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		
		realSpacePanel.add(new Label("The phase structure is flat."));
		
		// add realSpacePanel to realOrFTChoice and realOrFTPanel
		realOrFTChoice.add("real space");
		realOrFTPanel.add("real space", realSpacePanel);
		

		//
		// Fourier space
		//
		
		Panel fourierSpacePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		
		symmetriseFTCheckbox = new Checkbox("Symmetrise.");
		symmetriseFTCheckbox.setState(true);
		fourierSpacePanel.add(symmetriseFTCheckbox);

		fourierSpacePanel.add(new Label("The phase structure is flat."));
		
		// add realSpacePanel to realOrFTChoice and realOrFTPanel
		realOrFTChoice.add("Fourier space");
		realOrFTPanel.add("Fourier space", fourierSpacePanel);
		
		
		// the file panel
		
		Panel filePanel = new Panel();
		fileOrFunctionPanel.add("read from file", filePanel);
		
		filenameTextField = new TextField(40);
		filePanel.add(new ComponentWithLabel(filenameTextField, "Filename:"));
		chooseFileButton = new Button("Choose file...");
		chooseFileButton.addActionListener(this);
		filePanel.add(chooseFileButton);


		//
		// cancel and OK buttons
		//

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
	
	private void addAllBeamInitialisationElements()
	{
		// remove all the "old" beamInitialisationElements...
		planeChoice.removeAll();
		
		// ... and add all the current ones
		for(int i=0; i<beamAndSystem.system.getSize(); i++)
		{
			OpticalElement1D e = beamAndSystem.system.getElementAt(i);
			if(e instanceof BeamInitialisationElement1D)
				planeChoice.add(e.shortToString());
		}
	}
	
	public void show()
	{
		addAllBeamInitialisationElements();
		super.show();
	}
	
	private void doInitialisation()
	throws NumberFormatException
	{
		int i;
		LightBeamCrossSection1D b = beamAndSystem.b;
		
		if(fileOrFunctionChoice.getSelectedItem().equals("functional form"))
		{
			double p, w;
			
			// calculate p and w, the central index and the width in elements of the peak
			if(realOrFTChoice.getSelectedItem().equals("Fourier space"))
			{
				p = Double.valueOf(peakPositionTextField.getText()).doubleValue();
				w = Double.valueOf(peakWidthTextField.getText()).doubleValue();
			}
			else
			{
				p = b.getIndex(
					1e-3 * Double.valueOf(peakPositionTextField.getText()).doubleValue() );
				w = (b.getSize()-1) / b.getPhysicalWidth() * 
					(1e-3 * Double.valueOf(peakWidthTextField.getText()).doubleValue());
			}

			if(peakShapeChoice.getSelectedItem().equals("Gaussian"))
			{
				for(i=0; i<b.getSize(); i++)
					b.setAmplitude(
						i,
						new Complex(
							Math.exp(-MyMath.sqr((i-p)/w) / 2),
							0
						)
					);
			}
			else if(peakShapeChoice.getSelectedItem().equals("Top-hat"))
			{
				for(i=0; i<b.getSize(); i++)
				{
					double iAbs = Math.abs(i - p);
					b.setAmplitude(i, new Complex((iAbs<w/2)?1:0, 0));
				}
			}
	
			if(realOrFTChoice.getSelectedItem().equals("Fourier space"))
			{
				// symmetrise
				if(symmetriseFTCheckbox.getState())
					for(i=1; i<(b.getSize()>>1); i++)
						b.setAmplitude(b.getSize()-i, b.getAmplitude(i));
	
				// transform into the corresponding real-space distribution
				b.FT(-1);
			}
		}
		else if(fileOrFunctionChoice.getSelectedItem().equals("read from file"))
		{
			try
			{
				ComplexArray1D a = new ComplexArray1D(filenameTextField.getText());
			
				if(a.getSize() != b.getSize())
					throw(new SizeMismatchError("file length doesn't match beam's width"));
			
				for(i=0; i<b.getSize(); i++)
					b.setAmplitude(i, a.getElement(i));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
			
		// find the selected plane
		for(i=0;
			(i<beamAndSystem.system.getSize()) &&
			!( beamAndSystem.system.getElementAt(i).shortToString().equals(
				planeChoice.getSelectedItem() ));
			i++
		);
		
		// interpret the initialised beam as belonging to the selected plane
		beamAndSystem.currentElementIndex = i;
	}
	
	///////////////////////////
	// ActionListener method //
	///////////////////////////
	
	public void actionPerformed(ActionEvent ae)
	{
		Object eventSource = ae.getSource();
		
		try
		{
			if(eventSource == OKButton)
			{
				doInitialisation();
				setVisible(false);
			}
			else if(eventSource == CancelButton)
			{
				setVisible(false);
			}
			else if(eventSource == chooseFileButton)
			{
				FileDialog fd = new FileDialog(parent, "", FileDialog.LOAD);
			
				fd.setVisible(true);
				
				if(fd.getFile() != null)
				{
					String filename = fd.getDirectory() + fd.getFile();
					filenameTextField.setText(filename);
					filenameTextField.setCaretPosition(filename.length());
				}

				// free system resources associated with the dialog
				fd.dispose();
			}
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/////////////////////////
	// ItemListener method //
	/////////////////////////
	
	public void itemStateChanged(ItemEvent ie)
	{
		if(ie.getSource() == fileOrFunctionChoice)
			// switch to a different "card" in the card layout
			fileOrFunctionCardLayout.show(
				fileOrFunctionPanel, fileOrFunctionChoice.getSelectedItem());
		else if(ie.getSource() == realOrFTChoice)
		{
			// switch to a different "card" in the card layout
			realOrFTCardLayout.show(
				realOrFTPanel, realOrFTChoice.getSelectedItem());
			if(realOrFTChoice.getSelectedItem().equals("real space"))
			{
				peakPositionUnitLabel.setText("mm");
				peakWidthUnitLabel.setText("mm");
			}
			else if(realOrFTChoice.getSelectedItem().equals("Fourier space"))
			{
				peakPositionUnitLabel.setText("(bin #)");
				peakWidthUnitLabel.setText("bins");
			}
		}
	}
}