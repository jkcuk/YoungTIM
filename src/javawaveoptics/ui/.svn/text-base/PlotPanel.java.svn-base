package javawaveoptics.ui;

import java.awt.BorderLayout;
import java.io.Serializable;

import javawaveoptics.optics.BeamCrossSection;
import javawaveoptics.optics.plot.AbstractPlot;

import javax.swing.JPanel;

/**
 * @author Sean mainly
 * A Swing component that holds the ImagePanel that corresponds to a given AbstractPlot.
 * This component is aware both of the AbstractPlot and of the ImagePanel, and so can act as
 * a two-way intermediary between the two.
 */
public class PlotPanel extends JPanel implements Serializable
{
	private static final long serialVersionUID = 428324362394041739L;

	private AbstractPlot plot;
	private transient ImagePanel imagePanel;
	
	public PlotPanel(AbstractPlot plot, BeamCrossSection beam)
	{
		super();
		
		// make this a BorderLayout so that all the available space goes to the centre component
		setLayout(new BorderLayout());
		
		this.plot = plot;
		imagePanel = plot.getPlotImagePanel(beam);
		add(imagePanel, BorderLayout.CENTER);
	}
	
	public PlotPanel(AbstractPlot plot)
	{
		this(plot, null);
	}
	
	public void update(BeamCrossSection beam)
	{
		imagePanel = plot.getPlotImagePanel(beam);
		add(imagePanel, BorderLayout.CENTER);
	}
	
	public String toString()
	{
		return plot.toString();
	}

	public AbstractPlot getPlot()
	{
		return plot;
	}

	public void setPlot(AbstractPlot plot)
	{
		this.plot = plot;
	}
}
