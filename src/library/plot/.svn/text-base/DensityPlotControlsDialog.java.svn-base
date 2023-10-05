package library.plot;

import java.awt.*;
import java.awt.event.*;

import library.awt.*;

class DensityPlotControlsDialog extends Dialog
implements ActionListener, FocusListener, ItemListener, BrightnessContrastControlListener
{
	private static final long serialVersionUID = -6400465377618081393L;
	
	// the window components
	private BrightnessContrastControl
		brightnessContrastControl;
	private Checkbox xRangeAllCheckbox, yRangeAllCheckbox, plotGridCheckbox,
		useXRangeCheckbox, useYRangeCheckbox;
	private TextField
		brightnessField, contrastField,
		xRangeMinField, xRangeMaxField, yRangeMinField, yRangeMaxField, widthField, heightField;
	private Button
		makeOwnSizeButton, 
		OKButton, CancelButton, ApplyButton;
	private Choice controlsGroupChoice;
	private Panel controlsGroupPanel;
	private CardLayout controlsGroupCardLayout;
	private boolean resizable, resize = false;
	private DensityPlotCanvas canvas;
	private Frame frame;
	
	public DensityPlotControlsDialog(
		DensityPlotCanvas canvas, Frame parent, boolean resizable)
	{
		super(parent, "Density plot controls", true);
		
		this.frame = parent;
		this.canvas = canvas;
		// determines whether the size-related items are present
		this.resizable = resizable;
		
		setSize(260, 280);
		setResizable(false);
		
		// arrange everything very nicely
		setLayout(new BorderLayout());
		
		
		////////////////////////////////////
		// choice: what shall be changed? //
		////////////////////////////////////
		
		controlsGroupChoice = new Choice();
		controlsGroupChoice.add("brightness & contrast");
		controlsGroupChoice.add("plot range");
		if(resizable) controlsGroupChoice.add("size");
		controlsGroupChoice.add("other options");
		controlsGroupChoice.addItemListener(this);
		Panel choicePanel = new Panel();
		choicePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		choicePanel.add(controlsGroupChoice);
		add("North", choicePanel);
		
		
		//////////////
		// controls //
		//////////////
		
		// the brightness & contrast control panel
		
		Panel brightnessContrastPanel = new Panel();
		brightnessContrastControl = new BrightnessContrastControl(100, 100);
		brightnessContrastPanel.add(brightnessContrastControl);
		brightnessField = new TextField(8);
		brightnessField.addActionListener(this);
		brightnessField.addFocusListener(this);
		brightnessContrastPanel.add(new ComponentWithLabel(brightnessField, "brightness:"));
		contrastField = new TextField(8);
		contrastField.addActionListener(this);
		contrastField.addFocusListener(this);
		brightnessContrastPanel.add(new ComponentWithLabel(contrastField, "contrast:"));
		brightnessContrastControl.addBrightnessContrastControlListener(this);

		// the plot range panel
		
		Panel plotRangePanel = new Panel(new GridLayout(2,1));
		
		// horizontal range
		Panel xRangePanel = new Panel();
		xRangePanel.add(new Label("horizontal range:"));
		xRangeAllCheckbox = new Checkbox("show all");
		xRangeAllCheckbox.addItemListener(this);
		xRangePanel.add(xRangeAllCheckbox);
		useXRangeCheckbox = new Checkbox("use mesh range");
		useXRangeCheckbox.addItemListener(this);
		xRangePanel.add(useXRangeCheckbox);
		Panel xRangeMinMaxPanel = new Panel();
		xRangeMinField = new TextField(12);
		xRangeMaxField = new TextField(12);
		// xRangeMinMaxPanel.add(new Label("min:"));
		xRangeMinMaxPanel.add(xRangeMinField);
		xRangeMinMaxPanel.add(new Label("to"));
		xRangeMinMaxPanel.add(xRangeMaxField);
		xRangePanel.add(xRangeMinMaxPanel);
		plotRangePanel.add(xRangePanel);
		
		// vertical range
		Panel yRangePanel = new Panel();
		yRangePanel.add(new Label("vertical range:"));
		yRangeAllCheckbox = new Checkbox("show all");
		yRangeAllCheckbox.addItemListener(this);
		yRangePanel.add(yRangeAllCheckbox);
		useYRangeCheckbox = new Checkbox("use mesh range");
		useYRangeCheckbox.addItemListener(this);
		yRangePanel.add(useYRangeCheckbox);
		Panel yRangeMinMaxPanel = new Panel();
		yRangeMinField = new TextField(12);
		yRangeMaxField = new TextField(12);
		yRangeMinMaxPanel.add(yRangeMinField);
		yRangeMinMaxPanel.add(new Label("to"));
		yRangeMinMaxPanel.add(yRangeMaxField);
		yRangePanel.add(yRangeMinMaxPanel);
		plotRangePanel.add(yRangePanel);
		
		// size panel
		
		Panel sizePanel = new Panel();
		if(resizable)
		{
			widthField = new TextField(6);
			sizePanel.add(new ComponentWithLabel(widthField, "width:"));
			heightField = new TextField(6);
			sizePanel.add(new ComponentWithLabel(heightField, "height:"));
			makeOwnSizeButton = new Button("1 pixel/point");
			makeOwnSizeButton.addActionListener(this);
			sizePanel.add(makeOwnSizeButton);
		}

		// the other options panel
		
		Panel otherOptionsPanel = new Panel();
		plotGridCheckbox = new Checkbox("show grid");
		otherOptionsPanel.add(plotGridCheckbox);
		
		// put all these different panels into one CardLayout panel
		
		controlsGroupPanel = new Panel();
		controlsGroupCardLayout = new CardLayout();
		controlsGroupPanel.setLayout(controlsGroupCardLayout);
		controlsGroupPanel.add("brightness & contrast", brightnessContrastPanel);
		controlsGroupPanel.add("plot range", plotRangePanel);
		if(resizable) controlsGroupPanel.add("size", sizePanel);
		controlsGroupPanel.add("other options", otherOptionsPanel);
		add("Center", controlsGroupPanel);
		
		
		///////////////////////////
		// cancel and OK buttons //
		///////////////////////////

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
		
		// do the layout
		validate();
	}
	
	public void setBrightness(double brightness)
	{
		brightnessContrastControl.setBrightness(brightness);
	}
	
	public double getBrightness()
	{
		return Double.valueOf(brightnessField.getText()).doubleValue();
		// return brightnessContrastControl.getBrightness();
	}

	public void setContrast(double contrast)
	{
		brightnessContrastControl.setContrast(contrast);
	}
	
	public double getContrast()
	{
		return Double.valueOf(contrastField.getText()).doubleValue();
		// return brightnessContrastControl.getContrast();
	}
	
	public void setShowXRangeAll(boolean showXRangeAll)
	{
		xRangeAllCheckbox.setState(showXRangeAll);
		
		xRangeMinField.setEnabled(!showXRangeAll);
		xRangeMaxField.setEnabled(!showXRangeAll);
	}
	
	public boolean getShowXRangeAll()
	{
		return xRangeAllCheckbox.getState();
	}

	public void setShowYRangeAll(boolean showYRangeAll)
	{
		yRangeAllCheckbox.setState(showYRangeAll);
		
		yRangeMinField.setEnabled(!showYRangeAll);
		yRangeMaxField.setEnabled(!showYRangeAll);
	}

	public boolean getShowYRangeAll()
	{
		return yRangeAllCheckbox.getState();
	}
	
	public boolean getUseXRange()
	{
		return useXRangeCheckbox.getState();
	}
	
	public void setXRange(double xRangeMin, double xRangeMax)
	{
		useXRangeCheckbox.setState(true);
		xRangeMinField.setText("" + (float)xRangeMin);
		xRangeMinField.selectAll();
		xRangeMaxField.setText("" + (float)xRangeMax);
		xRangeMaxField.selectAll();
	}
	
	public double getXRangeMin()
	{
		return Double.valueOf(xRangeMinField.getText()).doubleValue();	
	}
	
	public double getXRangeMax()
	{
		return Double.valueOf(xRangeMaxField.getText()).doubleValue();	
	}
	
	public void setIRange(int iRangeMin, int iRangeMax)
	{
		useXRangeCheckbox.setState(false);
		xRangeMinField.setText("" + iRangeMin);
		xRangeMinField.selectAll();
		xRangeMaxField.setText("" + iRangeMax);
		xRangeMaxField.selectAll();
	}
	
	public int getIRangeMin()
	{
		return Integer.parseInt(xRangeMinField.getText());
	}
	
	public int getIRangeMax()
	{
		return Integer.parseInt(xRangeMaxField.getText());	
	}
	

	public boolean getUseYRange()
	{
		return useYRangeCheckbox.getState();
	}
	
	public void setYRange(double yRangeMin, double yRangeMax)
	{
		useYRangeCheckbox.setState(true);
		yRangeMinField.setText("" + (float)yRangeMin);
		yRangeMinField.selectAll();
		yRangeMaxField.setText("" + (float)yRangeMax);
		yRangeMaxField.selectAll();
	}
	
	public double getYRangeMin()
	{
		return Double.valueOf(yRangeMinField.getText()).doubleValue();	
	}
	
	public double getYRangeMax()
	{
		return Double.valueOf(yRangeMaxField.getText()).doubleValue();	
	}
	
	public void setJRange(int jRangeMin, int jRangeMax)
	{
		useYRangeCheckbox.setState(false);
		yRangeMinField.setText("" + jRangeMin);
		yRangeMinField.selectAll();
		yRangeMaxField.setText("" + jRangeMax);
		yRangeMaxField.selectAll();
	}
	
	public int getJRangeMin()
	{
		return Integer.parseInt(yRangeMinField.getText());
	}
	
	public int getJRangeMax()
	{
		return Integer.parseInt(yRangeMaxField.getText());	
	}
	


	public void setPlotGrid(boolean plotGrid)
	{
		plotGridCheckbox.setState(plotGrid);
	}
	
	public boolean getPlotGrid()
	{
		return plotGridCheckbox.getState();
	}
	
	public void setDensityPlotSize(int width, int height)
	{
		if(resizable)
		{
			widthField.setText("" + width);
			widthField.selectAll();
			heightField.setText("" + height);
			heightField.selectAll();
		}
	}
	
	public int getDensityPlotWidth()
	{
		return Integer.parseInt(widthField.getText());
	}
	
	public int getDensityPlotHeight()
	{
		return Integer.parseInt(heightField.getText());
	}
	
	public boolean resize()
	{
		return resizable && resize;
	}
	
	private void validateNumberFormat()
	throws NumberFormatException
	{
		// see whether all the text field contents are numbers
		Double.valueOf(brightnessField.getText());
		Double.valueOf(contrastField.getText());
		if(getUseXRange())
		{
			Double.valueOf(xRangeMinField.getText());
			Double.valueOf(xRangeMaxField.getText());
		}
		else
		{
			Integer.parseInt(xRangeMinField.getText());
			Integer.parseInt(xRangeMaxField.getText());
		}
		if(getUseYRange())
		{
			Double.valueOf(yRangeMinField.getText());
			Double.valueOf(yRangeMaxField.getText());
		}
		else
		{
			Integer.parseInt(yRangeMinField.getText());
			Integer.parseInt(yRangeMaxField.getText());
		}
		if(resizable)
		{
			Integer.parseInt(widthField.getText());
			Integer.parseInt(heightField.getText());
		}
	}
	
	private void setParameters(DensityPlotCanvas c)
	{
		// set the parameters
		c.setBrightness(getBrightness());
		c.setContrast(getContrast());
		c.setShowIRangeAll(getShowXRangeAll());
		c.setShowJRangeAll(getShowYRangeAll());
		if(getUseXRange()) c.setXRange(getXRangeMin(), getXRangeMax());
		else c.setIRange(getIRangeMin(), getIRangeMax());
		if(getUseYRange()) c.setYRange(getYRangeMin(), getYRangeMax());
		else c.setJRange(getJRangeMin(), getJRangeMax());
		c.setPlotGrid(getPlotGrid());
	}
	
	private void setSizeParameters(DensityPlotFrame f)
	{
		f.setSize(getWidth(), getHeight());
		f.c.setSize(getWidth(), getHeight());
	}

	///////////////////////////
	// ActionListener method //
	///////////////////////////
	
	public void actionPerformed(ActionEvent ae)
	{
		Object eventSource = ae.getSource();
		
		try
		{
			if(eventSource == OKButton)
			{
				validateNumberFormat();
				setParameters(canvas);
				if(resizable) setSizeParameters((DensityPlotFrame)frame);
				canvas.calculateImage();
				canvas.repaint();
				setVisible(false);
			}
			else if(eventSource == CancelButton)
			{
				setVisible(false);
			}
			else if(eventSource == ApplyButton)
			{
				validateNumberFormat();
				setParameters(canvas);
				if(resizable) setSizeParameters((DensityPlotFrame)frame);
				canvas.calculateImage();
				canvas.repaint();
			}
			else if(eventSource == makeOwnSizeButton)
			{
				validateNumberFormat();
				setParameters(canvas);
				canvas.calculateIJRange();
				setDensityPlotSize(
					canvas.getIRangeMax()-canvas.getIRangeMin()+1,
					canvas.getJRangeMax()-canvas.getJRangeMin()+1 );			
			}
			else if(eventSource == brightnessField)
			{
				brightnessContrastControl.setBrightness(Double.valueOf(brightnessField.getText()).doubleValue());
				brightnessContrastControl.repaint();
			}
			else if(eventSource == contrastField)
			{
				brightnessContrastControl.setContrast(Double.valueOf(contrastField.getText()).doubleValue());
				brightnessContrastControl.repaint();
			}
			else repaint();
		}
		catch(NumberFormatException e)
		{
			System.out.println(e);
		}
	}
	
	///////////////////////////
	// FocusListener methods //
	///////////////////////////
	

	public void focusGained(FocusEvent fe)
	{
	}

	public void focusLost(FocusEvent fe)
	{
		Object eventSource = fe.getSource();

		try
		{
			if(eventSource == brightnessField)
			{
				brightnessContrastControl.setBrightness(Double.valueOf(brightnessField.getText()).doubleValue());
				brightnessContrastControl.repaint();
			}
			else if(eventSource == contrastField)
			{
				brightnessContrastControl.setContrast(Double.valueOf(contrastField.getText()).doubleValue());
				brightnessContrastControl.repaint();
			}
		}
		catch(NumberFormatException e)
		{
			System.out.println(e);
		}
	}
	
	
	/////////////////////////
	// ItemListener method //
	/////////////////////////
	
	public void itemStateChanged(ItemEvent ie)
	{
		if(ie.getSource() == xRangeAllCheckbox)
		{
			xRangeMinField.setEnabled(!xRangeAllCheckbox.getState());
			xRangeMaxField.setEnabled(!xRangeAllCheckbox.getState());
		}
		else if(ie.getSource() == yRangeAllCheckbox)
		{
			yRangeMinField.setEnabled(!yRangeAllCheckbox.getState());
			yRangeMaxField.setEnabled(!yRangeAllCheckbox.getState());
		}
		else if(ie.getSource() == useXRangeCheckbox)
		{
			if(useXRangeCheckbox.getState())
			{
				// use x range checked
				xRangeMinField.setText("" + (float)canvas.getXRangeMin());
				xRangeMaxField.setText("" + (float)canvas.getXRangeMax());
			}
			else
			{
				// use i range
				xRangeMinField.setText("" + canvas.getIRangeMin());
				xRangeMaxField.setText("" + canvas.getIRangeMax());
			}
		}
		else if(ie.getSource() == useYRangeCheckbox)
		{
			if(useYRangeCheckbox.getState())
			{
				// use y range checked
				yRangeMinField.setText("" + (float)canvas.getYRangeMin());
				yRangeMaxField.setText("" + (float)canvas.getYRangeMax());
			}
			else
			{
				// use j range
				yRangeMinField.setText("" + canvas.getJRangeMin());
				yRangeMaxField.setText("" + canvas.getJRangeMax());
			}
		}
		else if(ie.getSource() == controlsGroupChoice)
		{
			// switch to a different "card" in the card layout
			controlsGroupCardLayout.show(controlsGroupPanel, controlsGroupChoice.getSelectedItem());
		}
	}
	
	//////////////////////////////////////////////
	// BrightnessContrastControlListener method //
	//////////////////////////////////////////////
	
	public void brightnessContrastChanged(double brightness, double contrast)
	{
		brightnessField.setText(""+(float)brightness);
		contrastField.setText(""+(float)contrast);
	}
}