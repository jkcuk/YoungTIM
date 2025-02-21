package javawaveoptics.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;


/**
 * A counter that can be increased, decreased, and reset
 * 
 * @author Johannes
 */
public class Counter extends JPanel implements PropertyChangeListener, ActionListener
{
	private static final long serialVersionUID = -7800597554089362533L;

	// the number being counted
	private int value = 0;
	
	private transient JFormattedTextField valueTextField;
	private transient JButton incrementButton, decrementButton, resetButton;
	
	private PropertyChangeListener propertyChangeListener;
	
	public Counter(int initialValue, boolean showIncrementButton, boolean showDecrementButton, boolean showResetButton)
	{
		super();
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		value = initialValue;
		
		valueTextField = UIBitsAndBobs.makeIntFormattedTextField(this);
		valueTextField.setValue(Integer.valueOf(value));
		valueTextField.setEditable(false);
		add(valueTextField);
		
		if(showIncrementButton)
		{
			incrementButton = new JButton("+");
			int buttonHeight = incrementButton.getPreferredSize().height;
			Dimension buttonSize = new Dimension(buttonHeight, buttonHeight);
			incrementButton.setMinimumSize(buttonSize);
			incrementButton.setMaximumSize(buttonSize);
			incrementButton.setPreferredSize(buttonSize);
			incrementButton.setSize(buttonSize);
			//		plusButton.setMaximumSize(plusButton.getPreferredSize());
			incrementButton.setAlignmentY(Component.CENTER_ALIGNMENT);
			incrementButton.addActionListener(this);
			add(incrementButton);
		}

		if(showDecrementButton)
		{
			decrementButton = new JButton("-");
			int buttonHeight = decrementButton.getPreferredSize().height;
			Dimension buttonSize = new Dimension(buttonHeight, buttonHeight);
			decrementButton.setMinimumSize(buttonSize);
			decrementButton.setMaximumSize(buttonSize);
			decrementButton.setPreferredSize(buttonSize);
			decrementButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			decrementButton.setSize(buttonSize);
			// minusButton.setMaximumSize(minusButton.getPreferredSize());
			decrementButton.setAlignmentY(Component.CENTER_ALIGNMENT);
			decrementButton.addActionListener(this);
			add(decrementButton);
		}
		
		if(showResetButton)
		{
			resetButton = new JButton("Reset");
			resetButton.setAlignmentY(Component.CENTER_ALIGNMENT);
			resetButton.addActionListener(this);
			add(resetButton);
		}
	}

	public int setValue(int newValue)
	{
		int oldValue = value;
		
		value = newValue;
		
		// show the new value in the text field...
		valueTextField.setValue(Integer.valueOf(value));
		
		// ... and let the propertyChangeListener (if any) know
		if(propertyChangeListener != null)
		{
			propertyChangeListener.propertyChange(new PropertyChangeEvent(this, "value", oldValue, value));
		}

		return value;
	}
	
	public int increment()
	{
		return setValue(value+1);
	}

	public int decrement()
	{
		return setValue(value-1);
	}
	
	public int reset()
	{
		return setValue(0);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		if(source == incrementButton)
		{
			increment();
		}
		else if(source == decrementButton)
		{
			decrement();
		}
		else if(source == resetButton)
		{
			reset();
		}
	}

	@Override
	public void setEnabled(boolean enable)
	{
		if(incrementButton != null) incrementButton.setEnabled(enable);
		if(decrementButton != null) decrementButton.setEnabled(enable);
		if(resetButton != null) resetButton.setEnabled(enable);
	}

	public int getValue() {
		return value;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		// should never be called, as the text field is not editable
	}
}
