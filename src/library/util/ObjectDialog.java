/*

classes for simple control over public variables in objects

public interface:
	public interface SelfExplainingObject

public classes:
	public class ObjectPanel extends Panel
	public class ObjectDialog extends Dialog

example:

	import java.applet.Applet;
	import johannes.util.*;

	public class TestClass
	implements SelfExplainingObject
	{
		public int i = 1;
		public double d = 123.456;
		public float f = 3.14;
		public String s = "Hello, world!";
		public boolean b = false;
		
		public String getExplanation()
		{
			return "Explanation.\nNewlines allowed.";
		}
	}
	
	public class TrivialApplet extends Applet
	{
		public void init()
		{
			TestClass t = new TestClass();
			Frame f = new Frame();
			ObjectDialog od = new ObjectDialog(f, "t", t);

			// free system resources associated with frame
			f.dispose();
			// free system resources associated with dialog
			od.dispose();
			
			System.out.println(
				"i = " + t.i + ", " +
				"d = " + t.d + ", " +
				"f = " + t.f + ", " +
				"s = " + t.s + ", " +
				"b = " + t.b
			);
		}		
	}

*/

package library.util;


import java.awt.*;
import java.awt.event.*;


public class ObjectDialog extends Dialog
implements ActionListener
{
	private static final long serialVersionUID = -1306918230823405314L;
	
	private ObjectPanel ocp;
	private Button OKButton, CancelButton, ApplyButton;
	private boolean OK;
	private ApplyButtonListener applyButtonListener;


	// creates an ObjectDialog without an Apply button
	public ObjectDialog(Frame parent, String name, Object o)
	{
		this(parent, name, o, 450, 280);
	}

	// creates an ObjectDialog without an Apply button
	public ObjectDialog(Frame parent, String name, Object o, int width, int height)
	{
		super(parent, name, true);
		
		setSize(width, height);
		// setResizable(false);
		setLayout(new BorderLayout());
		
		
		//
		// add panel that controls object
		//
				
		ocp = new ObjectPanel(o);
		add("Center", ocp);
		
		
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
		
		
		//
		// do the layout
		//
		
		validate();
		
		
		//
		// start the dialog
		//
		
		show();
	}
	
	// creates an ObjectDialog with an Apply button;
	public ObjectDialog(Frame parent, String name, Object o, ApplyButtonListener applyButtonListener)
	{
		super(parent, name, true);
		
		this.applyButtonListener = applyButtonListener;
		
		setSize(350, 220);
		// setResizable(false);
		setLayout(new BorderLayout());
		
		
		//
		// add panel that controls object
		//
		
		ocp = new ObjectPanel(o);
		add("Center", ocp);
		
		
		//
		// Apply, Cancel and OK buttons
		//

		Panel cancelOKPanel = new Panel();
		cancelOKPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add("South", cancelOKPanel);
		
		ApplyButton = new Button("Apply");
		cancelOKPanel.add(ApplyButton);
		ApplyButton.addActionListener(this);
		
		CancelButton = new Button("Cancel");
		cancelOKPanel.add(CancelButton);
		CancelButton.addActionListener(this);
		
		OKButton = new Button("OK");
		cancelOKPanel.add(OKButton);
		OKButton.addActionListener(this);
		
		
		//
		// do the layout
		//
		
		validate();
		
		
		//
		// start the dialog
		//
		
		show();
	}
	

	//
	// when the dialog has finished
	//
	
	// this function checks that the contents of TextFields
	// that should be numbers actually are numbers
	public void validateNumberFormat()
	throws NumberFormatException
	{
		ocp.validateNumberFormat();
	}
	
	// this function changes the variables in the object according to the changes made
	// in the dialog
	public void changeVariables()
	{
		ocp.changeObject();
	}
	
	// was the dialog ended by clicking OK?
	public boolean OK()
	{
		return OK;
	}
	
	
	//
	// ActionListener method
	//
	
	public void actionPerformed(ActionEvent ae)
	{
		Object eventSource = ae.getSource();
		
		// handle button clicks
		if(eventSource == OKButton)
		{
			try
			{
				validateNumberFormat();
				changeVariables();
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
		else if(eventSource == ApplyButton)
		{
			try
			{
				validateNumberFormat();
				changeVariables();
				applyButtonListener.applyButtonClicked();
			}
			catch(NumberFormatException e)
			{
				System.out.println(e);
			}		
		}
	}
}