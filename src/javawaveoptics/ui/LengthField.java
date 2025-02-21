package javawaveoptics.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;


/**
 * Defines a component for editing lengths.
 * 
 * @author Johannes
 */
public class LengthField extends JCPanel implements PropertyChangeListener, ActionListener, ChangeListener //, FocusListener
{
	private static final long serialVersionUID = -6094767353973244920L;

	private transient PropertyChangeListener propertyChangeListener;
	
	private transient JSpinner valueSpinner;	// the number part
	// private transient JFormattedTextField valueTextField;	// the number part
	private transient LengthUnitsComboBox unitsComboBox;	// the units part
	
	public LengthField(PropertyChangeListener propertyChangeListener)
	{
		super();
		setLayout(new FlowLayout());
		
//		valueTextField = UIBitsAndBobs.makeDoubleFormattedTextField(this);
//		add(valueTextField);
        SpinnerNumberModel valueSpinnerModel = new SpinnerNumberModel(0.0, -1000, 1e12, 1); // TODO: is 1e12 high enough for all intents and purposes? This means the maximum is 1e12km
		valueSpinner = new JSpinner(valueSpinnerModel);
		((JSpinner.DefaultEditor)valueSpinner.getEditor()).getTextField().setColumns(6);
		valueSpinner.setMaximumSize(valueSpinner.getPreferredSize());
//		valueSpinner.getEditor().addFocusListener(this);
//		valueSpinner.getEditor().addKeyListener
//	      (new KeyAdapter() {
//	         public void keyPressed(KeyEvent e) {
//	           int key = e.getKeyCode();
//	           if (key == KeyEvent.VK_ENTER) {
//	        	   informPropertyChangeListener();
//	              }
//	           }
//	         }
//	      );
		valueSpinner.addChangeListener(this);
		
		// try to make sure the value change *always* gets registered, even if the JSpinner is left to create a new component
		// see https://stackoverflow.com/questions/3949382/jspinner-value-change-events
		JComponent comp = valueSpinner.getEditor();
	    JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
	    DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
	    formatter.setCommitsOnValidEdit(true);
	    
		add(valueSpinner);
		
		unitsComboBox = new LengthUnitsComboBox();
		unitsComboBox.addActionListener(this);
		add(unitsComboBox);

		// valueSpinner.setValue(0.0);
		// valueTextField.setValue(new Double(0));
		
		this.propertyChangeListener = propertyChangeListener;
	}	

	public void setLengthInMetres(double lengthInMetres)
	{
		double value = unitsComboBox.setSuitableUnitForLengthInMetres(lengthInMetres);
		valueSpinner.setValue(value);
		// valueTextField.setValue(new Double(value));
	}
	
	public double getLengthInMetres()
	{
		// System.out.println("LengthField::getLengthInMetres: valueSpinner.getValue() = " + valueSpinner.getValue());
		double value = ((Number)valueSpinner.getValue()).doubleValue();
		// double value = ((Number)valueTextField.getValue()).doubleValue();
		return  value * unitsComboBox.getMultiplicationFactor();
	}
	
	public String getLengthString()
	{
		return valueSpinner.getValue().toString() + unitsComboBox.getUnitString();
		// return valueTextField.getText() + unitsComboBox.getUnitString();
	}
	
	public JSpinner getValueSpinner() {
		return valueSpinner;
	}

	public void setValueSpinner(JSpinner valueSpinner) {
		this.valueSpinner = valueSpinner;
	}

	public PropertyChangeListener getPropertyChangeListener() {
		return propertyChangeListener;
	}

	public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
		this.propertyChangeListener = propertyChangeListener;
	}

	private void informPropertyChangeListener()
	{
		// System.out.println("inform propertyChangeListener that length="+getLengthInMetres());

		if(propertyChangeListener != null)
		{
			propertyChangeListener.propertyChange(new PropertyChangeEvent(this, "value", 0, getLengthInMetres()));
		}		
	}

	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		// System.out.println("Property change; length="+getLengthInMetres());
		informPropertyChangeListener();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// System.out.println("Action performed; length="+getLengthInMetres());
		informPropertyChangeListener();
		// propertyChangeListener.propertyChange(new PropertyChangeEvent(this, "value", 0, getLengthInMetres()));
	}
	
//	@Override
//	public void focusGained(FocusEvent e)
//	{}
//
//	@Override
//	public void focusLost(FocusEvent e)
//	{
//		informPropertyChangeListener();
//	}
	
	@Override
	public void stateChanged(ChangeEvent e)
	{
		// System.out.println("State changed; length="+getLengthInMetres());
		informPropertyChangeListener();
	}
}
