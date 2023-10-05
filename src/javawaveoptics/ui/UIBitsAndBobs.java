package javawaveoptics.ui;


import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLEditorKit;

/**
 * A few bits and bobs related to the GUI
 */
public class UIBitsAndBobs
{
	public static JFormattedTextField makeDoubleFormattedTextField()
	{
		JFormattedTextField formattedTextField;
		
		// see http://www.exampledepot.com/egs/java.text/FormatNumExp.html
		formattedTextField = new MyFormattedTextField(NumberFormat.getNumberInstance());
		formattedTextField.setColumns(6);
		// formattedTextField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		
		return formattedTextField;
	}

	public static JFormattedTextField makeDoubleFormattedTextField(PropertyChangeListener propertyChangeListener)
	{
		JFormattedTextField formattedTextField = makeDoubleFormattedTextField();
		formattedTextField.addPropertyChangeListener("value", propertyChangeListener);
		// formattedTextField.setFocusLostBehavior(JFormattedTextField.COMMIT);
		
		return formattedTextField;
	}

	public static JFormattedTextField makeEngineeringFormattedTextField(PropertyChangeListener propertyChangeListener)
	{
		JFormattedTextField formattedTextField;
		
		// see http://www.exampledepot.com/egs/java.text/FormatNumExp.html
		formattedTextField = new MyFormattedTextField(new DecimalFormat("###E0"));	// NumberFormat.getNumberInstance());
		formattedTextField.setColumns(6);
		formattedTextField.addPropertyChangeListener("value", propertyChangeListener);
		
		return formattedTextField;
	}

	public static JFormattedTextField makeIntFormattedTextField(PropertyChangeListener propertyChangeListener)
	{
		JFormattedTextField formattedTextField;
		
		formattedTextField = new MyFormattedTextField();
		formattedTextField.setColumns(6);
		if(propertyChangeListener != null) formattedTextField.addPropertyChangeListener("value", propertyChangeListener);
		
		return formattedTextField;
	}
	
	public static JEditorPane makeHTMLLabel(String text)
	{
		JEditorPane editorPane = new JEditorPane(new HTMLEditorKit().getContentType(), text);
	    editorPane.setEditable(false);
	    
	    // make sure the background colour is that used in the rest of the program
	    editorPane.setBackground(UIManager.getColor ( "Panel.background" ));
	    
	    // make sure the font is that used in the rest of the program
	    // see http://explodingpixels.wordpress.com/2008/10/28/make-jeditorpane-use-the-system-font/
        Font font = UIManager.getFont("Label.font");
        String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }";
        ((javax.swing.text.html.HTMLDocument)editorPane.getDocument()).getStyleSheet().addRule(bodyRule);
        
	    editorPane.setMaximumSize(editorPane.getPreferredSize());
	    return editorPane;
	}
	
	public static JCPanel makeRow(Component field1, Component field2, boolean restrictMaximumSize)
	{
		JCPanel panel = new JCPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(field1); 
		panel.add(field2);

		if(restrictMaximumSize)
		{
			// set the maximum size so that the BoxLayout doesn't resize it
			panel.setMaximumSize(panel.getPreferredSize());
		}

		return panel;
	}


	/**
	 * Creates a combined text label and interactive component.
	 */
	public static JCPanel makeRow(String text, Component field, boolean restrictMaximumSize)
	{
		JCPanel panel = new JCPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(makeHTMLLabel(text)); 
		panel.add(field);
			
		if(restrictMaximumSize)
		{
			// set the maximum size so that the BoxLayout doesn't resize it
			panel.setMaximumSize(panel.getPreferredSize());
		}

		return panel;
	}

	/**
	 * Creates a combined text label, interactive component, and another text label.
	 */
	public static JCPanel makeRow(String text1, Component field, String text2, boolean restrictMaximumSize)
	{
		JCPanel panel = new JCPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(makeHTMLLabel(text1)); 
		panel.add(field);
		panel.add(makeHTMLLabel(text2));

		if(restrictMaximumSize)
		{
			// set the maximum size so that the BoxLayout doesn't resize it
			panel.setMaximumSize(panel.getPreferredSize());
		}

		return panel;
	}

	/**
	 * Creates a combination of text and fields
	 */
	public static JCPanel makeRow(String text, Component field1, Component field2, boolean restrictMaximumSize)
	{
		JCPanel panel = new JCPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(makeHTMLLabel(text)); 
		panel.add(field1);
		panel.add(field2);

		if(restrictMaximumSize)
		{
			// set the maximum size so that the BoxLayout doesn't resize it
			panel.setMaximumSize(panel.getPreferredSize());
		}

		return panel;
	}

	public static JCPanel makeRow(String text1, Component field1, Component field2, String text2, boolean restrictMaximumSize)
	{
		JCPanel panel = new JCPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(makeHTMLLabel(text1)); 
		panel.add(field1);
		panel.add(field2);
		panel.add(makeHTMLLabel(text2));

		if(restrictMaximumSize)
		{
			// set the maximum size so that the BoxLayout doesn't resize it
			panel.setMaximumSize(panel.getPreferredSize());
		}

		return panel;
	}

	public static JCPanel makeRow(String text1, Component field1, String text2, Component field2, boolean restrictMaximumSize)
	{
		JCPanel panel = new JCPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(makeHTMLLabel(text1)); 
		panel.add(field1);
		panel.add(makeHTMLLabel(text2));
		panel.add(field2);

		if(restrictMaximumSize)
		{
			// set the maximum size so that the BoxLayout doesn't resize it
			panel.setMaximumSize(panel.getPreferredSize());
		}

		return panel;
	}

	public static JCPanel makeRow(Component field1, String text1, Component field2, boolean restrictMaximumSize)
	{
		JCPanel panel = new JCPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(field1);
		panel.add(makeHTMLLabel(text1));
		panel.add(field2);

		if(restrictMaximumSize)
		{
			// set the maximum size so that the BoxLayout doesn't resize it
			panel.setMaximumSize(panel.getPreferredSize());
		}

		return panel;
	}

	/**
	 * Creates a combined text label, interactive component, another text label, another interactive component, and another text label.
	 * Can be used to make 2D vectors, e.g. "centre = ([text1], [text2])
	 */
	public static JCPanel makeRow(String text1, Component field1, String text2, Component field2, String text3, boolean restrictMaximumSize)
	{
		JCPanel panel = new JCPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(makeHTMLLabel(text1)); 
		panel.add(field1);
		panel.add(makeHTMLLabel(text2));
		panel.add(field2);
		panel.add(makeHTMLLabel(text3));

		if(restrictMaximumSize)
		{
			// set the maximum size so that the BoxLayout doesn't resize it
			panel.setMaximumSize(panel.getPreferredSize());
		}

		return panel;
	}

	/**
	 * Creates a combined text label, interactive component, another text label, another interactive component, and another text label.
	 * Can be used to make 2D vectors, e.g. "centre = ([text1], [text2])
	 */
	public static JCPanel makeRow(String text1, Component field1, String text2, Component field2, String text3, Component field3, boolean restrictMaximumSize)
	{
		JCPanel panel = new JCPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		panel.add(makeHTMLLabel(text1)); 
		panel.add(field1);
		panel.add(makeHTMLLabel(text2));
		panel.add(field2);
		panel.add(makeHTMLLabel(text3));
		panel.add(field3);

		if(restrictMaximumSize)
		{
			// set the maximum size so that the BoxLayout doesn't resize it
			panel.setMaximumSize(panel.getPreferredSize());
		}

		return panel;
	}

	/**
	 * @param title
	 * @return	a titled border which can be added to a component
	 */
	public static TitledBorder getTitledBorder(String title)
	{
		TitledBorder border;
		
		border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				title
			);
		border.setTitleJustification(TitledBorder.CENTER);
		
		return border;
	}
	
	public static JSpinner createZoomSpinner()
	{
        SpinnerNumberModel zoomSpinnerModel = new SpinnerNumberModel(1.0, 0.1, 20, .1);
		JSpinner zoomSpinner = new JSpinner(zoomSpinnerModel);

        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(zoomSpinner, "\u00D70.0");
		editor.getTextField().setColumns(3);
		// editor.getTextField().setFont(getControlPanelFont());  
		zoomSpinner.setEditor(editor);
		
		zoomSpinner.setMaximumSize(zoomSpinner.getPreferredSize());

		return zoomSpinner;
	}
	
	/**
	 * @param exposureCompensationValue
	 * @return	an exposure-compensation spinner
	 */
	public static JSpinner createExposureCompensationSpinner(double exposureCompensationValue)
	{
        SpinnerNumberModel exposureCompensationSpinnerModel = new SpinnerNumberModel(exposureCompensationValue, 0, 40, 0.5);
		JSpinner exposureCompensationSpinner = new JSpinner(exposureCompensationSpinnerModel);
		// ((JSpinner.NumberEditor)(exposureCompensationSpinner.getEditor())).getTextField().setFont(ImagePanel.getControlPanelFont());
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(exposureCompensationSpinner, "+0.0");
		// editor.getTextField().setText("+0");
		editor.getTextField().setColumns(3);
		exposureCompensationSpinner.setEditor(editor);
		exposureCompensationSpinner.setToolTipText("Exposure compensation");
		exposureCompensationSpinner.setMaximumSize(exposureCompensationSpinner.getPreferredSize());

		return exposureCompensationSpinner;
	}
}


