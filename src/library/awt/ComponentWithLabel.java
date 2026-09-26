package library.awt;

import java.awt.*;

// the component with a label on the left
public class ComponentWithLabel extends Panel
{
	private static final long serialVersionUID = 143860045837698508L;

	public ComponentWithLabel(Component c, String label)
	{
		super();
		
		add(new Label(label, Label.LEFT));
		add(c);
	}

	public ComponentWithLabel(Component c, Component l)
	{
		super();
		
		add(l);
		add(c);
	}
}