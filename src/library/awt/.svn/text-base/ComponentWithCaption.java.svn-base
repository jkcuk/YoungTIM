package library.awt;

import java.awt.*;

// the component with a one-line caption underneath
public class ComponentWithCaption extends Panel
{
	private static final long serialVersionUID = -3163580107581595690L;

	public ComponentWithCaption(Component c, String caption)
	{
		super();
		
		setLayout(new BorderLayout());
		add("North", c);
		add("South", new Label(caption, Label.CENTER));
	}

	public ComponentWithCaption(Component c, Component l)
	{
		super();
		
		setLayout(new BorderLayout());
		add("North", c);
		add("South", l);
	}
}