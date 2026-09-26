package library.opticalSystem;


import java.awt.*;
import java.awt.event.*;
import java.util.*;

import library.util.*;


public class OpticalElement1DDialog extends Dialog
implements ActionListener, ItemListener
{
	private static final long serialVersionUID = 491296335413397884L;
	
	private OpticalElement1D o;
	private Hashtable elementObjectPanels;
	
	// the window components
	private Choice typeChoice;
	private Panel typePanel;
	private CardLayout typeCardLayout;
	private Button OKButton, CancelButton;
	
	private boolean OK = false;


	public OpticalElement1DDialog(Frame parent, OpticalElement1D o, Vector elementTypes)
	{
		super(parent, "Optical element", true);
		
		setSize(350, 290);
		// setResizable(false);
		
		// arrange everything very nicely
		setLayout(new BorderLayout());

		
		this.o = o;
		
		//
		// type
		//
		
		Panel typeChoicePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		typeChoicePanel.add(new Label("Type:"));
		typeChoice = new Choice();
		typeChoice.addItemListener(this);
		typeChoicePanel.add(typeChoice);
		add("North", typeChoicePanel);
		
		typePanel = new Panel();
		typeCardLayout = new CardLayout();
		typePanel.setLayout(typeCardLayout);
		add("Center", typePanel);
		
		Class oClass = o.getClass();

		// for storing the ObjectPanels corresponding to all possible element types
		elementObjectPanels = new Hashtable();
		
		// go through all the element types
		for(int i=0; i<elementTypes.size(); i++)
		{
			OpticalElement1D et = ((OpticalElement1D)elementTypes.elementAt(i)).copy();
			
			// add type name to type choice
			typeChoice.add(et.getTypeName());
			
			ObjectPanel op;
			// make an ObjectPanel for this type, ...
			// is this the type of the object that is being altered?
			if(oClass.isInstance(et))
				// yes, so make an ObjectPanel for the object, so that all the variables
				// are set correctly
				op = new ObjectPanel(o);
			else
			{
				// let all the possible types have the same name and z coordinate
				et.name = o.name;
				et.z = o.z;
				
				op = new ObjectPanel(et);
			}
			
			// ... store in the hashtable under its type name, ...
			elementObjectPanels.put(et.getTypeName(), op);
			
			// ... and add it to the card layout
			typePanel.add(et.getTypeName(), op);
		}
		
		typeChoice.select(o.getTypeName());
		typeCardLayout.show(typePanel, typeChoice.getSelectedItem());
		
		
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
	
	////////////////////////////////////
	// methods to set / retrieve data //
	////////////////////////////////////
	
	public OpticalElement1D getChangedElement()
	{
		ObjectPanel eop = (ObjectPanel)elementObjectPanels.get(typeChoice.getSelectedItem());
		eop.changeObject();
		
		return (OpticalElement1D)eop.getObject();
	}
	
	
	// allows the caller to establish whether the OK or Cancel button was clicked
	public boolean OK()
	{
		return OK;
	}

	
	private void validateNumberFormat()
	throws NumberFormatException
	{
		// see whether all the text field contents are numbers;
		// if not, a NumberFormatException will be thrown

		// validate number format of the type-specific fields...
		((ObjectPanel)elementObjectPanels.get(typeChoice.getSelectedItem())).validateNumberFormat();
	}
	
	///////////////////////////
	// ActionListener method //
	///////////////////////////
	
	public void actionPerformed(ActionEvent ae)
	{
		Object eventSource = ae.getSource();
		
		if(eventSource == OKButton)
		{
			try
			{
				validateNumberFormat();
				OK = true;
				setVisible(false);
			}
			catch(NumberFormatException e)
			{
				System.out.println(e);
			}
		}
		else if(eventSource == CancelButton)
		{
			OK = false;
			setVisible(false);
		}
	}
	
	/////////////////////////
	// ItemListener method //
	/////////////////////////
	
	public void itemStateChanged(ItemEvent ie)
	{
		if(ie.getSource() == typeChoice)
		{
			// switch to a different "card" in the card layout
			typeCardLayout.show(typePanel, typeChoice.getSelectedItem());
		}
	}
}