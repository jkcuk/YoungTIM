package library.awt;

import java.awt.*;
import java.awt.event.*;


public class MessageDialog extends Dialog
implements ActionListener
{
	private static final long serialVersionUID = -125400893192923677L;

	public MessageDialog(Frame parent, String title, String message)
	{
		super(parent, title, true);

		setSize(400, 120);
		setResizable(false);

		setLayout(new BorderLayout());

		TextArea ta = new TextArea(message);
		ta.setEditable(false);
		add("Center", ta);
		
		Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
		add("South", buttonPanel);
		
		Button OKButton = new Button("OK");
		buttonPanel.add(OKButton);
		OKButton.addActionListener(this);
		
		// validate();
		
		show();
	}


	//
	// ActionListener method
	//
	
	public void actionPerformed(ActionEvent ae)
	{
		setVisible(false);
		dispose();
	}
}