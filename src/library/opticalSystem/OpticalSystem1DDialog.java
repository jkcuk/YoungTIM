package library.opticalSystem;


import java.awt.*;
import java.awt.event.*;


class OpticalSystem1DControl extends Panel
implements ActionListener
{
	private static final long serialVersionUID = -3750292721018958777L;
	
	//
	// variables
	//

	private OpticalSystem1D
		system;
	
	// graphics components
	
	private List
		elementList;
	
	private Button
		addButton,
		editButton,
		makeLastButton,
		removeButton;
	
	private ElementTypesProvider
		elementTypesProvider;
	

	//
	// constructor
	//
	
	public OpticalSystem1DControl
	(int rows, OpticalSystem1D system, String name, ElementTypesProvider elementTypesProvider)
	{
		super();
		
		// store a reference to the OpticalSystem1D
		this.system = system;
		
		this.elementTypesProvider = elementTypesProvider;
		
		setLayout(new BorderLayout());
		
		add("North", new Label("Optical elements in \"" + name + "\":"));
		
		elementList = new List(rows);
		buildElementList();
		add("Center", elementList);
		
		Panel buttonsPanel = new Panel();
		addButton = new Button("new");
		addButton.addActionListener(this);
		buttonsPanel.add(addButton);
		editButton = new Button("edit");
		editButton.addActionListener(this);
		buttonsPanel.add(editButton);
		makeLastButton = new Button("last of elements with same z");
		makeLastButton.addActionListener(this);
		buttonsPanel.add(makeLastButton);
		removeButton = new Button("remove");
		removeButton.addActionListener(this);
		buttonsPanel.add(removeButton);
		add("South", buttonsPanel);
	}
	
	
	private void buildElementList()
	{
		// clear the element list
		elementList.removeAll();
		
		for(int i=0; i<system.getSize(); i++)
			elementList.add(system.getElementAt(i).toString());
	}


	//
	// ActionListener method
	//
	
	public void actionPerformed(ActionEvent ae)
	{
            Object eventSource = ae.getSource();

            if(eventSource == addButton)
            {
                // create a new standard optical element, ...
                OpticalElement1D n = new Plane1D("new element", 0, true, false);

                Frame f = new Frame();

                // ... let the user alter it, ...
                OpticalElement1DDialog oed =
                    new OpticalElement1DDialog(f, n, elementTypesProvider.getElementTypes());
                oed.show();

                // free system resources associated with frame
                f.dispose();

                // ... and, if it was OKed, ...
                if(oed.OK())
                {
                    // ... add it to the system
                    system.add(oed.getChangedElement());

                    // rebuild the entries in the List displayed in the dialog
                    buildElementList();
                }

                // free system resources associated with dialog
                oed.dispose();
            }
            else if(eventSource == editButton)
            {
                int index = elementList.getSelectedIndex();

                if(index != -1)
                {
                    // an element has been selected

                    OpticalElement1D o = system.getElementAt(index);

                    Frame f = new Frame();

                    OpticalElement1DDialog oed =
                        new OpticalElement1DDialog(f, o, elementTypesProvider.getElementTypes());
                    oed.show();

                    if(oed.OK())
                    {
                        // OK button was clicked, and the element has been changed

                        // get the z coordinate of the unchanged element
                        double oldZ = o.z;

                        // change the element and get the new element
                        OpticalElement1D n = oed.getChangedElement();

                        system.replaceAt(index, n);

                        // has the z coordinate of the edited element changed?
                        if(n.z != oldZ)
                        {
                            // yes, the z coordinate has changed

                            // remove the element...
                            system.removeAt(index);

                            // ... and add it again, at the right place
                            system.add(n);
                        }

                        // rebuild the entries in the List displayed in the dialog
                        buildElementList();
                    }

                    // free system resources associated with dialog
                    oed.dispose();

                    // free system resources associated with frame
                    f.dispose();
                }
            }
            else if(eventSource == makeLastButton)
            {
                int index = elementList.getSelectedIndex();

                if(index != -1)
                {
                    // an element has been selected

                    OpticalElement1D o = system.getElementAt(index);

                    // to make thie element the last of those with the same z,
                    // take it out of the system, ...
                    system.removeAt(index);

                    // ... and add it again
                    system.add(o);

                    // rebuild the entries in the List displayed in the dialog
                    buildElementList();
                }
            }
            else if(eventSource == removeButton)
            {
                int index = elementList.getSelectedIndex();

                // is an element selected?
                if(index != -1)
                    // yes, remove it
                    system.removeAt(index);

                buildElementList();
            }
        }
}


public class OpticalSystem1DDialog extends Dialog
implements ActionListener
{
	private Button OKButton;
	

	public OpticalSystem1DDialog(
		Frame parent, OpticalSystem1D system, String name,
		ElementTypesProvider elementTypesProvider)
	{
		super(parent, "Optical system configuration", true);
		
		setSize(450, 300);
		setResizable(false);
		
		// arrange everything very nicely
		setLayout(new BorderLayout());

		
		//
		// the system configuration control
		//
		
		add("Center", new OpticalSystem1DControl(10, system, name, elementTypesProvider));
		
		
		//
		// OK button
		//

		Panel OKPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
		OKButton = new Button("OK");
		OKButton.addActionListener(this);
		OKPanel.add(OKButton);
		add("South", OKPanel);
		
		// do the layout
		validate();
	}
	

	///////////////////////////
	// ActionListener method //
	///////////////////////////
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == OKButton) setVisible(false);
	}
}