package library.plot;


import java.awt.*;
import java.awt.event.*;

class PlotControlsDialog extends Dialog
implements ActionListener, ItemListener
{
	private static final long serialVersionUID = -7620885411315579063L;
	
	// the window components
	private Checkbox
		xRangeAllCheckbox, yRangeAllCheckbox, doubleBufferingCheckbox, plotGridCheckbox,
		yLogPlotCheckbox, useXRangeCheckbox;
	private TextField xRangeMinField, xRangeMaxField, yRangeMinField, yRangeMaxField;
	private Button OKButton, CancelButton, ApplyButton;
	private Choice controlsGroupChoice;
	private Panel controlsGroupPanel;
	private CardLayout controlsGroupCardLayout;
	private PlotCanvas canvas;
	
	public PlotControlsDialog(PlotCanvas canvas, Frame parent)
	{
		super(parent, "Plot controls", true);
		
		this.canvas = canvas;
		
		setSize(260, 260);
		setResizable(false);
		
		// arrange everything very nicely
		setLayout(new BorderLayout());
		
		
		////////////////////////////////////
		// choice: what shall be changed? //
		////////////////////////////////////
		
		controlsGroupChoice = new Choice();
		controlsGroupChoice.add("plot range");
		controlsGroupChoice.add("other options");
		controlsGroupChoice.addItemListener(this);
		Panel choicePanel = new Panel();
		choicePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		choicePanel.add(controlsGroupChoice);
		add("North", choicePanel);
		
		
		//////////////
		// controls //
		//////////////
				
		// the plot range panel
		
		Panel plotRangePanel = new Panel(new GridLayout(2,1));
		
		// horizontal range
		Panel xRangePanel = new Panel(new FlowLayout(FlowLayout.CENTER));
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
		Panel yRangePanel = new Panel(new FlowLayout(FlowLayout.CENTER));
		yRangePanel.add(new Label("vertical range:"));
		yRangeAllCheckbox = new Checkbox("show all");
		yRangeAllCheckbox.addItemListener(this);
		yRangePanel.add(yRangeAllCheckbox);
		yLogPlotCheckbox = new Checkbox("log plot");
		yLogPlotCheckbox.addItemListener(this);
		yRangePanel.add(yLogPlotCheckbox);
		Panel yRangeMinMaxPanel = new Panel();
		yRangeMinField = new TextField(12);
		yRangeMaxField = new TextField(12);
		yRangeMinMaxPanel.add(yRangeMinField);
		yRangeMinMaxPanel.add(new Label("to"));
		yRangeMinMaxPanel.add(yRangeMaxField);
		yRangePanel.add(yRangeMinMaxPanel);
		plotRangePanel.add(yRangePanel);
		
		// the other options panel
		
		Panel otherOptionsPanel = new Panel();
		doubleBufferingCheckbox = new Checkbox("double buffering");
		otherOptionsPanel.add(doubleBufferingCheckbox);
		plotGridCheckbox = new Checkbox("show grid");
		otherOptionsPanel.add(plotGridCheckbox);
		
		// put all these different panels into one CardLayout panel
		
		controlsGroupPanel = new Panel();
		controlsGroupCardLayout = new CardLayout();
		controlsGroupPanel.setLayout(controlsGroupCardLayout);
		controlsGroupPanel.add("plot range", plotRangePanel);
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
	
	public void setYLogPlot(boolean yLogPlot)
	{
		yLogPlotCheckbox.setState(yLogPlot);
	}
	
	public boolean getYLogPlot()
	{
		return yLogPlotCheckbox.getState();
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
	
	public void setYRange(double yRangeMin, double yRangeMax)
	{
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
	
	public void setDoubleBuffering(boolean doubleBuffering)
	{
		doubleBufferingCheckbox.setState(doubleBuffering);
	}

	public boolean getDoubleBuffering()
	{
		return doubleBufferingCheckbox.getState();
	}
	
	public void setPlotGrid(boolean plotGrid)
	{
		plotGridCheckbox.setState(plotGrid);
	}
	
	public boolean getPlotGrid()
	{
		return plotGridCheckbox.getState();
	}
	
	private void validateNumberFormat()
	throws NumberFormatException
	{
		// see whether all the text field contents are numbers;
		// if not, a NumberFormatException will be thrown
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
		Double.valueOf(yRangeMinField.getText());
		Double.valueOf(yRangeMaxField.getText());
	}
	
	private void setParameters(PlotCanvas c)
	{
		// set the parameters
		c.setShowIRangeAll(getShowXRangeAll());
		c.setShowYRangeAll(getShowYRangeAll());
		c.setYLogPlot(getYLogPlot());
		if(getUseXRange()) c.setXRange(getXRangeMin(), getXRangeMax());
		else c.setIRange(getIRangeMin(), getIRangeMax());
		c.setYRange(getYRangeMin(), getYRangeMax());
		c.setDoubleBuffering(getDoubleBuffering());
		c.setPlotGrid(getPlotGrid());
	}

	///////////////////////////
	// ActionListener method //
	///////////////////////////
	
	public void actionPerformed(ActionEvent ae)
	{
		Object eventSource = ae.getSource();
		
		if(eventSource == OKButton)
		{
			try
			{
				validateNumberFormat();
				setParameters(canvas);
				canvas.repaint();

				setVisible(false);
			}
			catch(NumberFormatException e)
			{
				System.out.println(e);
			}
		}
		else if(eventSource == CancelButton)
		{
			setVisible(false);
		}
		else if(eventSource == ApplyButton)
		{
			try
			{
				validateNumberFormat();
				setParameters(canvas);
				canvas.repaint();
				
				// update the x range and y range
				// if(getUseXRange()) setXRange(canvas.getXRangeMin(), canvas.getXRangeMax());
				// else setIRange(canvas.getIRangeMin(), canvas.getIRangeMax());
				// setYRange(canvas.getYRangeMin(), canvas.getYRangeMax());
			}
			catch(NumberFormatException e)
			{
				System.out.println(e);
			}
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
		else if(ie.getSource() == controlsGroupChoice)
		{
			// switch to a different "card" in the card layout
			controlsGroupCardLayout.show(controlsGroupPanel, controlsGroupChoice.getSelectedItem());
		}
	}
}