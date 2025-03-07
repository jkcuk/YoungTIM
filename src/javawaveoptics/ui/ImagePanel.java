package javawaveoptics.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Creates a Swing component displaying an image of specified width and height, scaling the image if
 * necessary.
 * 
 * @author Johannes
 */
public class ImagePanel extends JPanel implements Serializable, ActionListener, ChangeListener, ImageSizeProblemListener
{
	private static final long serialVersionUID = -8562213495056419264L;
	
	private boolean isAspectRatioFixed;

	private transient ImagePanelSimple imagePanelSimple;
	private transient JScrollPane imagePanelSimpleScrollPane;
	private transient JPanel controlPanel;
	private transient JComponent plotSettingsPanel;
	// private transient PlusMinusButtons zoomButtons;
	private transient JSpinner zoomSpinner, zoomXSpinner, zoomYSpinner;
	private transient JButton fitButton, saveButton, settingsButton;
	// private transient JCheckBox showSettingsCheckBox;
	private boolean showZoomButtons, showFitButton, showSaveButton, showSettingsButton, settingsShowing = true;
	private transient OpticalComponentEditListener editListener;
	private String fileSaveName;
	private ZoomListener zoomListener;

	/**
	 * @param image
	 * @param aspectRatio
	 * @param zoomFactor
	 * @param showZoomButtons
	 * @param showSaveButton
	 * @param settingsShowing	true recommended, as the size of the panel is otherwise very big
	 * @param showSettingsButton
	 * @param settingsPanel
	 * @param fileSaveName
	 * @param editListener
	 */
	public ImagePanel(
			BufferedImage image,
			double zoomFactorX, double zoomFactorY,
			boolean isAspectRatioFixed,
			boolean showZoomButtons,
			boolean showFitButton,
			boolean showSaveButton,
			boolean settingsShowing, boolean showSettingsButton,
			JComponent settingsPanel,
			JComponent controlPanelLeft,
			JComponent controlPanelRight,
			String fileSaveName,
			OpticalComponentEditListener editListener
		)
	{
		super();
		setLayout(new BorderLayout());
		// setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		// setLayout(new FlowLayout());
		
		this.isAspectRatioFixed = isAspectRatioFixed;

		this.showZoomButtons = showZoomButtons;
		this.showFitButton = showFitButton;
		this.showSaveButton = showSaveButton;
		this.settingsShowing = settingsShowing;
		this.showSettingsButton = showSettingsButton;

		plotSettingsPanel = settingsPanel;
		// plotSettingsPanel.setBorder(UIBitsAndBobs.getTitledBorder("Plot settings"));
		add(plotSettingsPanel, BorderLayout.EAST);
		plotSettingsPanel.setVisible(settingsShowing);

		// leftPanel.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		// leftPanel.add(plotSettingsPanel);
		createControlPanel(controlPanelLeft, controlPanelRight);
		// controlPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		// controlPanel.setVisible(image != null);
//		if(zoomSpinner != null) zoomSpinner.setEnabled(image != null);
//		if(zoomXSpinner != null) zoomXSpinner.setEnabled(image != null);
//		if(zoomYSpinner != null) zoomYSpinner.setEnabled(image != null);
//		// if(zoomButtons != null) zoomButtons.setEnabled(image != null);
//		if(fitButton != null) fitButton.setEnabled(image != null);
//		if(saveButton != null) saveButton.setEnabled(image != null);
		// leftPanel.add(controlPanel);
//		JScrollPane controlPanelScrollPane = new JScrollPane(controlPanel);
//		controlPanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
//		controlPanelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		add(controlPanelScrollPane, BorderLayout.SOUTH);
		add(controlPanel, BorderLayout.SOUTH);

		imagePanelSimple = new ImagePanelSimple(image, zoomFactorX, zoomFactorY, this);
		imagePanelSimple.setAlignmentY(Component.CENTER_ALIGNMENT);
		imagePanelSimple.setAlignmentX(Component.CENTER_ALIGNMENT);
		imagePanelSimpleScrollPane = new JScrollPane(imagePanelSimple);
		imagePanelSimpleScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		imagePanelSimpleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// imagePanelSimpleScrollPane.getHorizontalScrollBar().addPropertyChangeListener(this);
		add(imagePanelSimpleScrollPane, BorderLayout.CENTER);
		
		this.fileSaveName = fileSaveName;
		
		this.editListener = editListener;
	}
	
	public ImagePanel()
	{
		this(null, 1.0, 1.0, true, true, true, true, false, true, null, null, null, "image", null);
	}
	
	/**
	 * @param controlPanelLeft	component to be added on left of control panel
	 * @param controlPanelRight	component to be added on right of control panel
	 */
	private void createControlPanel(JComponent controlPanelLeft, JComponent controlPanelRight)
	{
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		// controlPanel.setLayout(new BorderLayout());

//		// make the control-panel font smaller; doesn't work for some reason
//		Font f = controlPanel.getFont();
//		Float s = f.getSize2D();
//		s *= .6f; // a bit smaller
//		controlPanel.setFont(f.deriveFont(s));
		
		if(controlPanelLeft != null)
		{
			controlPanel.add(controlPanelLeft);
		}

		if(showZoomButtons)
		{
			if(isAspectRatioFixed)
			{
				zoomSpinner = UIBitsAndBobs.createZoomSpinner();
				zoomSpinner.setToolTipText("Zoom factor");
				zoomSpinner.addChangeListener(this);
				controlPanel.add(zoomSpinner);
			}
			else
			{
				zoomXSpinner = UIBitsAndBobs.createZoomSpinner();
				zoomXSpinner.setToolTipText("Horizontal zoom factor");
				zoomXSpinner.addChangeListener(this);
				controlPanel.add(zoomXSpinner);

				controlPanel.add(new JLabel("\u00D7"));

				zoomYSpinner = UIBitsAndBobs.createZoomSpinner();
				zoomYSpinner.setToolTipText("Vertical zoom factor");
				zoomYSpinner.addChangeListener(this);
				controlPanel.add(zoomYSpinner);
			}

//			zoomButtons = new PlusMinusButtons(" ", this);
//			controlPanel.add(zoomButtons);
		}
		
		if(showFitButton)
		{
			fitButton = new JButton("Fit");
			// fitButton.setFont(getControlPanelFont());
			fitButton.setToolTipText("Set zoom level to fit window size");
			// fitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			fitButton.setActionCommand("Fit");
			fitButton.addActionListener(this);
			controlPanel.add(fitButton);
		}
		
		// controlPanel.add(Box.createRigidArea(new Dimension(30,30)));
		
		if(showSaveButton)
		{
			saveButton = new JButton("Save");
			// saveButton.setFont(getControlPanelFont());
			saveButton.setToolTipText("Save as .bmp image");
			// saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			saveButton.setActionCommand("Save");
			saveButton.addActionListener(this);
			controlPanel.add(saveButton);
		}

		if(controlPanelRight != null)
		{
			controlPanel.add(controlPanelRight);
		}

		if(showSettingsButton)
		{
//			showSettingsCheckBox = new JCheckBox("Settings");
//			showSettingsCheckBox.setSelected(settingsShowing);
//			showSettingsCheckBox.setActionCommand("Settings");
//			showSettingsCheckBox.addActionListener(this);
//			controlPanel.add(showSettingsCheckBox);
			
			settingsButton = new JButton();
			// settingsButton.setFont(getControlPanelFont());
			settingsButton.setToolTipText("Show/hide additional settings");
			settingsButton.setText(settingsShowing?"Hide parameters":"Show parameters");
			// not sure why the following doesn't reduce the size of the button
//			settingsButton.setMargin(new java.awt.Insets(1, 2, 1, 2));
//			settingsButton.setMaximumSize(settingsButton.getPreferredSize());
			// settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			settingsButton.setActionCommand("Settings");
			settingsButton.addActionListener(this);
			controlPanel.add(settingsButton);
		}
	}
	
	public static Font getControlPanelFont1()
	{
		Font font = UIManager.getFont("Label.font");
		return font.deriveFont((float)(0.75*font.getSize()));
	}
			
		@Override
		public void stateChanged(ChangeEvent e)
		{
			// not very pretty: lots of stuff aimed at keeping the relative scrollbar position constant during zooming
			Point viewportTopLeftPosition = imagePanelSimpleScrollPane.getViewport().getViewPosition();
			Dimension viewportSize = imagePanelSimpleScrollPane.getViewport().getSize();
			int
				imageWidth = imagePanelSimple.getWidth(),
				imageHeight = imagePanelSimple.getHeight();
			if(imageWidth <= 0) imageWidth = viewportSize.width;
			if(imageHeight <= 0) imageHeight = viewportSize.height;
			
			double
				relativeXPosition = (viewportTopLeftPosition.getX() + 0.5*viewportSize.width) / imageWidth,
				relativeYPosition = (viewportTopLeftPosition.getY() + 0.5*viewportSize.height) / imageHeight;
			
			double
				zoomXFactorOld = imagePanelSimple.getZoomFactorX(),
				zoomYFactorOld = imagePanelSimple.getZoomFactorY();
			double zoomXFactor, zoomYFactor;
			
			if(isAspectRatioFixed)
			{
				zoomXFactor = zoomYFactor = ((Double)zoomSpinner.getValue()).doubleValue();
			}
			else
			{
				zoomXFactor = ((Double)zoomXSpinner.getValue()).doubleValue();
				zoomYFactor = ((Double)zoomYSpinner.getValue()).doubleValue();
			}
			
			if(zoomListener == null)
			{
				// if there is no zoomListener, let the imagePanelSimple deal with it
				imagePanelSimple.setZoomFactors(zoomXFactor, zoomYFactor);
			}
			else
			{
				// if there is a zoomListener, let the zoomListner deal with it
				zoomListener.setZoomFactors(zoomXFactor, zoomYFactor);

				// do we need this?
				imagePanelSimple.setZoomFactors(1., 1.);
			}

			// http://stackoverflow.com/questions/13155382/jscrollpane-zoom-relative-to-mouse-position
			imagePanelSimpleScrollPane.getViewport().setViewPosition(new Point(
					(int)(relativeXPosition * imageWidth * zoomXFactor / zoomXFactorOld - viewportSize.width * 0.5),
					(int)(relativeYPosition * imageHeight * zoomYFactor / zoomYFactorOld - viewportSize.height * 0.5)
				));
			
			if(editListener != null)
			{
				editListener.editMade();
			}
//			else
//			{
//				revalidate();
//			}
		}
	
	/**
	 * the file chooser; make sure the same one is re-used, so that the user has to navigate to the relevant directory only once
	 */
	private static final BitmapFileChooser fileChooser = new BitmapFileChooser("Save image as .bmp", false);

	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		
		if(command.equals("Save"))
		{
			if(imagePanelSimple.getImage() != null)
			{
				// fileChooser.addChoosableFileFilter(new BitmapFileFilter());
				// fileChooser.setDialogTitle("Save image as .bmp");
				// if the selected file is null, which is the case if the JFileChooser hasn't been used before, ...
				if(fileChooser.getSelectedFile() == null)
				{
					// ... set the file name to something sensible
					fileChooser.setSelectedFile(new File(fileSaveName + ".bmp"));					
				}
				
				int returnValue = fileChooser.showSaveDialog(null);
				
				if(returnValue == JFileChooser.APPROVE_OPTION)
				{
					try
					{
						File saveFile = fileChooser.getSelectedFile();
						
						ImageIO.write(imagePanelSimple.getImage(), "bmp", saveFile);
					}
					catch(IOException e)
					{
						System.err.println(e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
		else if(command.equals("Fit"))
		{
			if(imagePanelSimple.getImage() != null)
			{
				adjustZoomToFitImageSize(imagePanelSimple.getImage());
				imagePanelSimple.revalidate();
				imagePanelSimple.repaint();
			}
		}
		else if(command.equals("Settings"))
		{
			settingsShowing = !settingsShowing;
			settingsButton.setText(settingsShowing?"Hide parameters":"Show parameters");
			// settingsShowing = showSettingsCheckBox.isSelected();
			plotSettingsPanel.setVisible(settingsShowing);
			revalidate();
			repaint();	// TODO needed?
		}
	}
	
	public void adjustZoomToFitImageSize(BufferedImage image)
	{
		// calculate the factor required to scale the image so that it fills the pane horizontally and vertically...
		if(imagePanelSimpleScrollPane != null)
		{
			double
			xFactor = imagePanelSimpleScrollPane.getViewport().getSize().getWidth() / image.getWidth(),
			yFactor = imagePanelSimpleScrollPane.getViewport().getSize().getHeight() / image.getHeight(); // getWidth() * imagePanelSimple.getAspectRatio();
			//	System.out.println(imagePanelSimpleScrollPane.getSize().getWidth() + " "
			//			+ imagePanelSimple.getImage().getWidth() + " "
			//			+ imagePanelSimpleScrollPane.getSize().getHeight() + " "
			//			+ imagePanelSimple.getImage().getWidth() / imagePanelSimple.getAspectRatio() + " "
			//			+ imagePanelSimple.getImage().getHeight() + " "
			//			+ xFactor + " " + yFactor
			//		);

			if(isAspectRatioFixed)
			{
				// ... and use the smaller of the two
				double zoomFactor = Math.min(xFactor, yFactor);
				zoomSpinner.setValue(zoomFactor);
				if(zoomListener == null)
				{
					// if there is no zoomListener, let the imagePanelSimple deal with it
					imagePanelSimple.setZoomFactors(zoomFactor, zoomFactor);
				}
				else
				{
					// if there is a zoomListener, let the zoomListner deal with it
					// zoomListener.setZoomFactors(zoomFactor, zoomFactor);

					// do we need this?
					imagePanelSimple.setZoomFactors(1., 1.);
				}
			}
			else
			{
				zoomXSpinner.setValue(xFactor);
				zoomYSpinner.setValue(yFactor);
				if(zoomListener == null)
				{
					// if there is no zoomListener, let the imagePanelSimple deal with it
					imagePanelSimple.setZoomFactors(xFactor, yFactor);
				}
				else
				{
					// if there is a zoomListener, let the zoomListner deal with it
					// zoomListener.setZoomFactors(xFactor, yFactor);

					// do we need this?
					imagePanelSimple.setZoomFactors(1., 1.);
				}
			}
		}
	}

	
	public BufferedImage getImage()
	{
		return imagePanelSimple.getImage();
	}

	public void setImage(BufferedImage image)
	{
//		BufferedImage oldImage = imagePanelSimple.getImage();
//		
//		// if the image is set for the first time, adjust the zoom to fit
//		if((image != null) && (oldImage == null))
//		{
//			adjustZoomToFitImageSize(image);
//		}

		imagePanelSimple.setImageAndRepaint(image);
		revalidate();
		repaint();

		// controlPanel.setVisible(image != null);
//		if(zoomSpinner != null) zoomSpinner.setEnabled(image != null);
//		if(zoomXSpinner != null) zoomXSpinner.setEnabled(image != null);
//		if(zoomYSpinner != null) zoomYSpinner.setEnabled(image != null);
//		// if(zoomButtons != null) zoomButtons.setEnabled(image != null);
//		if(fitButton != null) fitButton.setEnabled(image != null);
//		if(saveButton != null) saveButton.setEnabled(image != null);
	}
	
	public JScrollPane getScrollPane()
	{
		return imagePanelSimpleScrollPane;
	}
	
	public Dimension getScrollPaneSize()
	{
		return imagePanelSimpleScrollPane.getSize();
	}

	public JPanel getControlPanel() {
		return controlPanel;
	}

	public void setControlPanel(JPanel controlPanel) {
		this.controlPanel = controlPanel;
	}


	@Override
	public void dealWithImageSizeProblem(boolean okay)
	{
		if(imagePanelSimple != null)
		{
			// System.out.println("Image too big? " + imagePanelSimple.isImageTooBig());
			
			if(okay)
			{
				Color backgroundColor = UIManager.getColor ( "TextField.background" );	// Panel.background
				if(zoomSpinner != null) ((JSpinner.DefaultEditor)zoomSpinner.getEditor()).getTextField().setBackground(backgroundColor);
				if(zoomXSpinner != null) ((JSpinner.DefaultEditor)zoomXSpinner.getEditor()).getTextField().setBackground(backgroundColor);
				if(zoomYSpinner != null) ((JSpinner.DefaultEditor)zoomYSpinner.getEditor()).getTextField().setBackground(backgroundColor);			
			}
			else
			{
				if(zoomSpinner != null) ((JSpinner.DefaultEditor)zoomSpinner.getEditor()).getTextField().setBackground(Color.red);
				if(zoomXSpinner != null) ((JSpinner.DefaultEditor)zoomXSpinner.getEditor()).getTextField().setBackground(Color.red);
				if(zoomYSpinner != null) ((JSpinner.DefaultEditor)zoomYSpinner.getEditor()).getTextField().setBackground(Color.red);
			}
		}		
	}
	
	public double getZoomFactorX() {
		return imagePanelSimple.getZoomFactorX();
	}

	public double getZoomFactorY() {
		return imagePanelSimple.getZoomFactorY();
	}
	
	public int getViewportWidth()
	{
		return imagePanelSimpleScrollPane.getViewport().getWidth();
	}

	public int getViewportHeight()
	{
		return imagePanelSimpleScrollPane.getViewport().getHeight();
	}

	
	public ZoomListener getZoomListener() {
		return zoomListener;
	}

	public void setZoomListener(ZoomListener zoomListener) {
		this.zoomListener = zoomListener;
	}
}
