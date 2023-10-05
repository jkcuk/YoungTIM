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

			// free system resources associated with dialog
			od.dispose();
                        // free system resources associated with frame
                        f.dispose();
			
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
import java.lang.reflect.*;
import java.util.*;


public class ObjectPanel extends Panel
{
	private static final long serialVersionUID = -7796772111925123368L;
	
	private Object o;
	private Hashtable variableComponents;
	

	/////////////////
	// constructor //
	/////////////////
	
	public ObjectPanel(Object o)
	{
		super();
		
		//
		// store a reference to the object
		//
				
		this.o = o;
		
		
		//
		// add an explanation, if available
		//
		
		if(o instanceof SelfExplainingObject)
		{
			TextArea t = new TextArea(((SelfExplainingObject)o).getExplanation(), 2, 45);
			t.setEditable(false);
			add(t);
		}
		
		//
		// add control fields for all the variables
		//
		
		variableComponents = new Hashtable();
		
		Field[] fields = o.getClass().getFields();
		
		Panel inScrollPanePanel = new Panel(new GridLayout((int)Math.ceil(fields.length/2.0),2));
		
		for(int i=0; i<fields.length; i++)
		try
		{
			Field f = fields[i];
			Panel variablePanel = new Panel();
			Object fObject = f.get(o);
			
			// depending on the type of the field...
			if(	(fObject instanceof Integer) ||
				(fObject instanceof Float) ||
				(fObject instanceof Double) )
			{
				// ... it's a number
				variablePanel.add(new Label(f.getName() + "="));
				TextField t = new TextField(6);
				variablePanel.add(t);
				variableComponents.put(f, t); // store the field
				t.setText(fObject.toString());
				t.selectAll();
			}
			else if(fObject instanceof Boolean)
			{
				Checkbox c = new Checkbox(f.getName());
				variablePanel.add(c);
				variableComponents.put(f, c);
				c.setState(f.getBoolean(o));
			}
			else if(fObject instanceof String)
			{
				variablePanel.add(new Label(f.getName() + "="));
				TextField t = new TextField(20);
				variablePanel.add(t);
				variableComponents.put(f, t); // store the field
				t.setText((String)fObject);
				t.selectAll();
			}
			else if(f.getType().isArray())
			{
				variablePanel.add(new Label(f.getName() + "=<Array>"));
			}
			else
			{
				variablePanel.add(new Label(f.getName() + "="));
				// variablePanel.add(new Label(f.get(o).toString()));
				variablePanel.add(new Label(" <" + f.getType() + ">"));
			}
			
			inScrollPanePanel.add(variablePanel);
			// add(variablePanel);
		}
		catch(IllegalAccessException iae)
		{
			iae.printStackTrace();
		}
		
		ScrollPane sp = new ScrollPane();
		sp.add(inScrollPanePanel);
		sp.setSize(new Dimension(350, 150));
		// sp.doLayout();
		add(sp);
	}


	// this function checks that the contents of TextFields
	// that should be numbers actually are numbers
	public void validateNumberFormat()
	throws NumberFormatException
	{
		// get all the variable names for which a control component has been installed
		Enumeration keys = variableComponents.keys();
		
		while(keys.hasMoreElements())
		try
		{
			Field f = (Field)keys.nextElement();
			
			// depending on the type of the field...
			if(f.get(o) instanceof Integer)
			{
				Integer.parseInt(((TextField)variableComponents.get(f)).getText());
			}
			else if(f.get(o) instanceof Float)
			{
				Float.valueOf(((TextField)variableComponents.get(f)).getText());
			}
			else if(f.get(o) instanceof Double)
			{
				Double.valueOf(((TextField)variableComponents.get(f)).getText());
			}
		}
		catch(IllegalAccessException iae)
		{
			iae.printStackTrace();
		}
	}
	
	// this function changes the variables in the object according to the changes made
	// in the dialog
	public void changeObject()
	{
		// get all the variable names for which a control component has been installed
		Enumeration keys = variableComponents.keys();
		
		while(keys.hasMoreElements())
		try
		{
			Field f = (Field)keys.nextElement();
			
			// depending on the type of the field...
			if(f.get(o) instanceof Integer)
				f.setInt(o, Integer.parseInt(((TextField)variableComponents.get(f)).getText()));
			else if(f.get(o) instanceof Float)
				f.setFloat(o, Float.valueOf(((TextField)variableComponents.get(f)).getText()).floatValue());
			else if(f.get(o) instanceof Double)
				f.setDouble(o, Double.valueOf(((TextField)variableComponents.get(f)).getText()).doubleValue());
			else if(f.get(o) instanceof Boolean)
				f.setBoolean(o, ((Checkbox)variableComponents.get(f)).getState());
			else if(f.get(o) instanceof String)
				f.set(o, ((TextField)variableComponents.get(f)).getText());
		}
		catch(IllegalAccessException iae)
		{
			iae.printStackTrace();
		}
	}
	
	public Object getObject()
	{
		return o;
	}
}