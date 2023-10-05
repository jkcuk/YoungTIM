package javawaveoptics.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Just a pair of buttons, one saying "+", the other "-", used for controlling zooming etc.
 * 
 * @author Johannes
 */
public class PlusMinusButtons extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -7882293557112600340L;

	// gets incremented or decremented by pressing plus or minus buttons
	private int number = 0;
	
	private transient JButton plusButton, minusButton;
	private transient JLabel nameLabel;
	
	private transient PropertyChangeListener propertyChangeListener;
	
	public PlusMinusButtons(String name, PropertyChangeListener propertyChangeListener)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		// setLayout(new FlowLayout());
		
		plusButton = new JButton("+");
		int buttonHeight = plusButton.getPreferredSize().height;
		Dimension buttonSize = new Dimension(buttonHeight, buttonHeight);
		plusButton.setMinimumSize(buttonSize);
		plusButton.setMaximumSize(buttonSize);
		plusButton.setPreferredSize(buttonSize);
		plusButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		plusButton.setSize(buttonSize);
//		plusButton.setMaximumSize(plusButton.getPreferredSize());
		plusButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		plusButton.addActionListener(this);
		add(plusButton);

		if(!name.isEmpty())
		{
			nameLabel = new JLabel(name);
			nameLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
			add(nameLabel);
		}
		else
		{
			nameLabel = null;
		}
		
		minusButton = new JButton("-");
		minusButton.setMinimumSize(buttonSize);
		minusButton.setMaximumSize(buttonSize);
		minusButton.setPreferredSize(buttonSize);
		minusButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		minusButton.setSize(buttonSize);
		// minusButton.setMaximumSize(minusButton.getPreferredSize());
		minusButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		minusButton.addActionListener(this);
		add(minusButton);
		
		this.propertyChangeListener = propertyChangeListener;
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		int oldNumber = number;
		
		if(source == plusButton)
		{
			number = 1;
		}
		if(source == minusButton)
		{
			number = -1;
		}
		
		if(propertyChangeListener != null)
		{
			propertyChangeListener.propertyChange(new PropertyChangeEvent(this, "value", oldNumber, number));
		}
	}

	@Override
	public void setEnabled(boolean enable)
	{
		plusButton.setEnabled(enable);
		minusButton.setEnabled(enable);
		if(nameLabel != null) nameLabel.setEnabled(enable);
	}

	public int getNumber() {
		return number;
	}


	public void setNumber(int number) {
		this.number = number;
	}
}
