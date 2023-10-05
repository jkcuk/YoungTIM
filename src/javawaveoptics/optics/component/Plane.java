package javawaveoptics.optics.component;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.optics.plot.AbstractPlot;
import javawaveoptics.optics.plot.AnaglyphPlot;
import javawaveoptics.optics.plot.FourierTransformPlot;
import javawaveoptics.optics.plot.LinePlot;
import javawaveoptics.optics.plot.SelfSimilarityPlot;
import javawaveoptics.optics.plot.XYPlanePlot;
import javawaveoptics.optics.plot.ZPlanePlot;
import javawaveoptics.ui.LengthUnitsComboBox;
import javawaveoptics.ui.OpticalComponentEditListener;
import javawaveoptics.ui.PlotPanel;
import javawaveoptics.utility.ImageableInterface;

/**
 * Defines a plane on which the light beam cross section can be viewed in stasis. The user may choose
 * a type of plot to use to view the cross section, such as an intensity plot.
 * 
 * @author Sean
 * @author Johannes
 */
public class Plane extends AbstractSimpleOpticalComponent implements Serializable, ImageableInterface
{
	private static final long serialVersionUID = -3976970126370268095L;
	
	/**
	 * A plane object that stands for no plane --- handy for providing the "None" option in the object combo box
	 * in the ImageOfPlaneNonInitialising class's edit panel
	 */
	public static final Plane NO_PLANE = new Plane("  --- None ---  ");
	
	/*
	 * Fields
	 */
	
	// Local copy of the beam
	private BeamCrossSection beamCopy = null;

	/*
	 * GUI edit controls
	 */
	
	// List of panels containing the plots in use
	private transient ArrayList<PlotPanel> plotPanels;
	
	// Plot tabbed pane
	private transient JTabbedPane plotTabbedPane;
	
	/**
	 * Constructor.
	 * 
	 * @param name		The name to give this plot
	 */
	public Plane(String name)
	{
		super(name);
		addScrollBarsToEditPanel = false;
	}
	
	/**
	 * Empty constructor. Gives the plot a default name.
	 */
	public Plane()
	{
		this("Plane");
	}
	
	@Override
	public String getComponentTypeName()
	{
		return "Plane";
	}

	@Override
	public BeamCrossSection fromInputBeamCalculateOutputBeam(BeamCrossSection inputBeam)
	{		
		if(inputBeam != null)
		{
			// Take a copy of the beam
			beamCopy = new BeamCrossSection(inputBeam);
		}
		else
		{
			beamCopy = null;
		}
		
		plotBeam();
		
		// Return the original beam unchanged
		return inputBeam;
	}
	
	/**
	 * Returns a copy of the beam for use in ImageOfPlane
	 * 
	 * @return
	 */
	@Override
	public BeamCrossSection getCopyOfBeam()
	{
		// Check if copy of beam is null, and if so, return null (instead of a Beam object, which breaks
		// things!)
		if(beamCopy == null)
		{
			return null;
		}
		else
		{
			return new BeamCrossSection(beamCopy);
		}
	}
	
	@Override
	public boolean isCopyOfBeamPresent()
	{
		return (beamCopy != null);
	}

	
	@Override
	protected void createEditPanel()
	{
		super.createEditPanel();
		
		/*
		 * Add our own listener to the edit name box (for use in saving plot files)
		 */
		

		/*
		 * Plot type tabbed pane
		 */
		
		// plotTabbedPane.setPreferredSize(new Dimension(500, 500));
		
		editPanel.add(plotTabbedPane);
	}
	
	@Override
	protected void initialiseWidgets()
	{
		super.initialiseWidgets();
		
		// Plot type dropdown menu
		plotTabbedPane = new JTabbedPane();
		
		// Allow scrolling of tabs when there are many of them
		plotTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		// Populate plot tabbed pane
		populatePlotTabbedPane();
		
		// Set plot file save names to be the name of this plane
		setPlotFileSaveNames(name);
	}
	
	@Override
	public void readWidgets()
	{
		super.readWidgets();
	}
	
	private void populatePlotTabbedPane()
	{
		plotPanels = new ArrayList<PlotPanel>();
		
		plotPanels.add(setUpPlotAndGetNewPlotPanel(new XYPlanePlot()));
		plotPanels.add(setUpPlotAndGetNewPlotPanel(new ZPlanePlot()));
		plotPanels.add(setUpPlotAndGetNewPlotPanel(new LinePlot()));
		plotPanels.add(setUpPlotAndGetNewPlotPanel(new SelfSimilarityPlot()));
		plotPanels.add(setUpPlotAndGetNewPlotPanel(new FourierTransformPlot()));
		plotPanels.add(setUpPlotAndGetNewPlotPanel(new AnaglyphPlot()));
		
		// Add the tabs
		for(PlotPanel plotPanel : plotPanels)
		{
			plotTabbedPane.addTab(plotPanel.toString(), plotPanel);
		}
		
		// Add beam details tab
		plotTabbedPane.addTab("Details", null);
		
		// Fill the tabs with plots
		plotBeam();
	}
	
	private PlotPanel setUpPlotAndGetNewPlotPanel(AbstractPlot plot)
	{
		PlotPanel plotPanel = new PlotPanel(plot);
		plot.setEditPanelListener(new PlotEditPanelListener(plotPanel));
		
		return plotPanel;
	}
	
	private JPanel getDetailsPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
					
		if(beamCopy == null)
		{
			panel.add(new JLabel("\u2014 no data \u2014")); // shown if nothing else is painted over it
		}
		else
		{
			JLabel amplitudeDimensionsLabel = new JLabel(
					"Amplitude matrix dimensions: " +
					beamCopy.getWidth() + " \u2a09 " +
					beamCopy.getHeight()
				);
			panel.add(amplitudeDimensionsLabel);
			
			JLabel physicalSizeLabel = new JLabel(
					"Represented physical size: " + 
					LengthUnitsComboBox.length2NiceString(beamCopy.getPhysicalWidth()) + " \u2a09 " +
					LengthUnitsComboBox.length2NiceString(beamCopy.getPhysicalHeight())
				);
			panel.add(physicalSizeLabel);
			
			JLabel wavelengthLabel = new JLabel(
					"Wavelength: " + LengthUnitsComboBox.length2NiceString(beamCopy.getWavelength())
				);
			panel.add(wavelengthLabel);
						
			JLabel powerLabel = new JLabel("Power: " + String.format("%.2g", beamCopy.getPower()) + " a.u.");
			panel.add(powerLabel);

			JLabel dataSizeLabel = new JLabel(
					"Data size: " +
					Double.toString((beamCopy.getData().length * 8) / (1024 * 1024)) + " MB"
				);
			panel.add(dataSizeLabel);
		}

		return panel;
	}
	
	private void plotBeam()
	{
		if(plotPanels != null)
		{
			for(int i = 0; i < plotPanels.size(); i++)
			{
				plotPanels.get(i).update(beamCopy);
			}
		
			// Create beam details scroll panel
			JScrollPane plotScrollPane = new JScrollPane(getDetailsPanel());
		
			// Set the beam details panel
			plotTabbedPane.setComponentAt(plotPanels.size(), plotScrollPane);
		}
	}
	
	/**
	 * Returns a stand alone tabbed pane with the plots, independent from the traditional edit panel.
	 * 
	 * @return	JTabbedPane containing plots
	 */
	public JTabbedPane getStandalonePlotTabbedPane()
	{
		if(plotTabbedPane == null)
		{
			// The plot tabbed pane hasn't been initialised yet - initialise it
			initialiseWidgets();
		}
		
		return plotTabbedPane;
	}
	
	public void clearData()
	{
		// reset everything by letting the plane deal with a "null" input
		fromInputBeamCalculateOutputBeam(null);
	}
	
	public class PlotEditPanelListener implements Serializable, OpticalComponentEditListener
	{
		private static final long serialVersionUID = 3565889779916243137L;
		
		// The plot panel associated with this listener
		private PlotPanel plotPanel;
		
		public PlotEditPanelListener(PlotPanel plotPanel)
		{
			this.plotPanel = plotPanel;
		}
		
		@Override
		public void editMade()
		{
			plotPanel.update(beamCopy);
			
			plotTabbedPane.revalidate();
			plotTabbedPane.repaint();
		}

		@Override
		public void redraw()
		{
			// TODO Auto-generated method stub
		}
	}
			
	@Override
	public void setName(String name)
	{
		super.setName(name);
		
		// Update file save names
		setPlotFileSaveNames(name);
	}

	private void setPlotFileSaveNames(String fileName)
	{
		if(plotPanels != null)
		{
			for(int i = 0; i < plotPanels.size(); i++)
			{
				plotPanels.get(i).getPlot().setFileSaveName(fileName);

			}
		}
	}
}