package javawaveoptics.optics.plot;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JLabel;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.ui.OpticalComponentEditListener;
import javawaveoptics.ui.ImagePanel;
import javawaveoptics.ui.ZoomListener;

/**
 * Abstract class representing a two dimensional visualisation of a light beam
 * cross section. This class defines the methods which a child class must implement
 * in order to be compatible with the program, such as a method which returns an
 * AWT component (e.g. Component, JPanel, etc.) with the plot visualisation.
 *  
 * @author Sean
 * @author Johannes
 */
public abstract class AbstractPlot implements Serializable, ComponentListener
{
	private static final long serialVersionUID = -8470727812832095926L;
	
	public enum AreaPlotType
	{
		INTENSITY("intensity"),
		LOG_INTENSITY("log(intensity)"),
		PHASE_COLOUR("phase (colour)"),
		PHASE_GRAYSCALE("phase (grayscale)"),
		PHASE_AND_INTENSITY("phase & intensity"),
		REAL_PART("real part"),
		IMAGINARY_PART("imaginary part");
		
		private String description;
		private AreaPlotType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	public static final double maxLogIntensityDecades = 10;

	public enum LinePlotType
	{
		INTENSITY("intensity"),
		PHASE("phase"),
		REAL_PART("real part"),
		IMAGINARY_PART("imaginary part");
		
		private String description;
		private LinePlotType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	// can then do things like new JComboBox(Mood.values());
	
	public enum Coordinate {
		X("x"), Y("y"), Z("z");
		
		private String description;
		private Coordinate(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
	};

	public enum TransverseCoordinate {
		X("x"), Y("y");
		
		private String description;
		private TransverseCoordinate(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
	};
	
	/*
	 * Fields
	 */
		
	// The plot image
	protected BufferedImage plotImage;
		
	// Plot name
	protected String name = "Abstract plot";
	
	// Name to give saved bitmaps
	protected String fileSaveName = "Cross section";
	
	protected transient ImagePanel plotImagePanel;
	
	/*
	 * GUI edit controls
	 */
	
	// Edit panel change listener. This listener should be fired whenever there is an edit made to any plot
	// parameter.
	private OpticalComponentEditListener editPanelListener;
	
	/**
	 * Sets up a plot of the supplied beam
	 * 
	 * @param name
	 */
	public AbstractPlot(String name)
	{
		this.name = name;
	}
	
	/**
	 * Override if necessary
	 * @return
	 */
	public boolean isAspectRatioFixed()
	{
		return true;
	}
	
	/**
	 * This method can assume that the beam is not null.
	 * @param beam
	 * @return	the sized image representing the beam
	 */
	public abstract BufferedImage getPlotImage(BeamCrossSection beam);
	
	/**
	 * Override for "unusual" plot types, e.g. Fourier Transform
	 * @param beam
	 * @return
	 */
	public double getAspectRatio(BeamCrossSection beam)
	{
		return beam.getPhysicalWidth() / beam.getPhysicalHeight();
	}
	
	/**
	 * override in order to suppress the zoom buttons
	 * @return
	 */
	public boolean getShowZoomButtons()
	{
		return true;
	}

	/**
	 * override in order to suppress the fit button
	 * @return
	 */
	public boolean getShowFitButton()
	{
		return true;
	}

	/**
	 * override in order to suppress the save buttons
	 * @return
	 */
	public boolean getShowSaveButton()
	{
		return true;
	}

	/**
	 * override in order to suppress the settings button
	 * @return
	 */
	public boolean getShowSettingsButton()
	{
		return true;
	}
	
	/**
	 * Override if calculation of plot image needs to know ImagePanel's zoom factors
	 * @return	true if the plot needs to know the ImagePanel's zoom factors when calculating the plot image
	 */
	public boolean isZoomFactorSensitive()
	{
		return false;
	}

	/**
	 * Method to return an ImagePanel containing the plot image in question.
	 * 
	 * @return
	 */
	public ImagePanel getPlotImagePanel(BeamCrossSection beam)
	{
		if(beam != null)
		{
			plotImage = getPlotImage(beam);
			
			if(plotImagePanel == null)
			{
				plotImagePanel = new ImagePanel(plotImage, 1.0, 1.0, isAspectRatioFixed(), getShowZoomButtons(), getShowFitButton(), getShowSaveButton(), false, getShowSettingsButton(), getSettingsPanel(), getControlPanelLeft(), getControlPanelRight(), getFileSaveName(), editPanelListener);
				if(this instanceof ZoomListener)
				{
					plotImagePanel.setZoomListener((ZoomListener)this);
				}
				plotImagePanel.getScrollPane().addComponentListener(this);
			}
			else
			{
				plotImagePanel.setImage(plotImage);
			}
		}
		else
		{			
			if(plotImagePanel == null)
			{
				plotImagePanel = new ImagePanel(null, 1.0, 1.0, isAspectRatioFixed(), getShowZoomButtons(), getShowFitButton(), getShowSaveButton(), false, getShowSettingsButton(), getSettingsPanel(), getControlPanelLeft(), getControlPanelRight(), getFileSaveName(), editPanelListener);
				if(this instanceof ZoomListener)
				{
					plotImagePanel.setZoomListener((ZoomListener)this);
				}
				plotImagePanel.getScrollPane().addComponentListener(this);
			}
			else
			{
				plotImagePanel.setImage(null);
			}
		}
		
		return plotImagePanel;
	}
	
	/**
	 * Method to return an AWT component containing the plot's edit controls.
	 * Override if necessary.
	 * 
	 * @return
	 */
	public JComponent getSettingsPanel()
	{
		return new JLabel("No additional controls");
	}
	
	/**
	 * Override to add components to control panel
	 * @return
	 */
	public JComponent getControlPanelLeft()
	{
		return null;
	}

	/**
	 * Override to add components to control panel
	 * @return
	 */
	public JComponent getControlPanelRight()
	{
		return null;
	}

	/**
	 * Fires an edit event on the edit panel listener.
	 */
	protected void fireEditEvent()
	{
		if(editPanelListener != null)
		{
			editPanelListener.editMade();
		}
	}
	
	/**
	 * Returns a user-friendly string representation of the plot.
	 */
	public String toString()
	{
		return name;
	}
	
	/*
	 * Getters and setters
	 */
	
	public String getFileSaveName()
	{
		return fileSaveName;
	}

	public void setFileSaveName(String fileSaveName)
	{
		this.fileSaveName = fileSaveName;
	}
	
	public OpticalComponentEditListener getEditPanelListener()
	{
		return editPanelListener;
	}

	public void setEditPanelListener(OpticalComponentEditListener editPanelListener)
	{
		this.editPanelListener = editPanelListener;
	}
	
	// ComponentListener methods
	
	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	/* (non-Javadoc)
	 * Override if necessary.
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent arg0) {}

	@Override
	public void componentShown(ComponentEvent arg0) {}
}
