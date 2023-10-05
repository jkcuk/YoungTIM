package javawaveoptics.ui;

import java.text.Format;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author johannes
 * 
 * A JFormattedTextField that fires PropertyChangeEvents whenever anything changes.
 * 
 * This is necessary here as the standard JFormattedTextField sometimes does not send a PropertyChangeEvent even though
 * the user has made an edit.  This happens, for example, when the user makes an edit without pressing "Enter" or "Tab", but
 * instead leaves the edited text field by creating a new optical component.
 */
public class MyFormattedTextField extends JFormattedTextField implements DocumentListener {
	private static final long serialVersionUID = -2807604079392862079L;

	public MyFormattedTextField() {
		super();
		
		// see https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
		getDocument().addDocumentListener(this);
	}
	
	public MyFormattedTextField(Format format) {
		super(format);

		// see https://stackoverflow.com/questions/3953208/value-change-listener-to-jtextfield
		getDocument().addDocumentListener(this);
	}

	// DocumentListener methods
	public void changedUpdate(DocumentEvent e) {
		try {
			commitEdit();
		} catch (ParseException e1) {
			// System.out.println("Parse exception");
		}
	}

	public void removeUpdate(DocumentEvent e) {
		try {
			commitEdit();
		} catch (ParseException e1) {
			// System.out.println("Parse exception");
		}
	}

	public void insertUpdate(DocumentEvent e) {
		try {
			commitEdit();
		} catch (ParseException e1) {
			// System.out.println("Parse exception");
		}
	}

}
