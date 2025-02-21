package javawaveoptics.ui.workbench;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javawaveoptics.optics.ComponentInput;
import javawaveoptics.optics.ComponentOutput;
import javawaveoptics.optics.component.AbstractOpticalComponent;
import javawaveoptics.optics.component.ConvertableComponent;
import javawaveoptics.optics.component.OpticalComponentFactory;
import javawaveoptics.optics.component.Plane;
import javawaveoptics.optics.environment.AbstractOpticalEnvironment;
import javawaveoptics.ui.ClickableInterface;
import javawaveoptics.ui.OpticalComponentEditListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JScrollPane;

public class ExtensiveWorkbench extends AbstractWorkbench
{
	private static final long serialVersionUID = 8370328664789193532L;
	
	/*
	 * Fields
	 */
	
	// Optical train
	protected ArrayList<AbstractOpticalComponent> opticalComponentTrain = new ArrayList<AbstractOpticalComponent>();
	
	// Index of currently selected optical train component
	protected int selectedOpticalComponentOpticalTrainIndex = 0;
	
	// The current x-size of the selected component on the scroll pane
	protected int selectedOpticalComponentXDistance;
	
	/*
	 * GUI components
	 */
	
	// Workbench panel and its corresponding scroll pane
	private JPanel componentWorkbenchPanel;	// the panel showing the optical train ...
	private JScrollPane componentWorkbenchScrollPane;	// ... and the scroll pane it's in
	
	// Edit panel
	private JPanel componentEditPanel;
	
	// List of current workbench components
	private ArrayList<ExtensiveWorkbenchOpticalComponent> workbenchComponents = new ArrayList<ExtensiveWorkbenchOpticalComponent>();
	
	/**
	 * Creates an extensive workbench from an optical environment.
	 * 
	 * @param opticalEnvironment
	 */
	public ExtensiveWorkbench(AbstractOpticalEnvironment opticalEnvironment)
	{
		super(opticalEnvironment);
		
		// Create empty workbench and edit panels
		componentWorkbenchPanel = new JPanel();
		componentEditPanel = new JPanel();
		componentEditPanel.setMinimumSize(new Dimension(400, 200));
		
		// Set the layout manager for the edit panel. We have chosen BorderLayout to make
		// use of its handy feature whereby it expands the central (default) component
		// to fill all of the available space. This is great because the edit panel then
		// takes up all the room it should do, without ugly empty areas on the side.
		componentEditPanel.setLayout(new BorderLayout());
		
		// Create the scroll pane for the workbench
		componentWorkbenchScrollPane = new JScrollPane(componentWorkbenchPanel);
		
		// Scroll panes by default have borders, but we don't want one here so we are
		// getting rid of it by setting the border to be an EmptyBorder (zero pixels).
		componentWorkbenchScrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		// Set minimum size for scroll pane
		//componentWorkbenchScrollPane.setMinimumSize(new Dimension(750, 200));
		//componentWorkbenchScrollPane.setMaximumSize(new Dimension(750, 200));
		
		// Make scroll pane's scroll bars always visible (prevents issues with layout)
		// componentWorkbenchScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		// componentWorkbenchScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		
		// Component workbench container layout engine
		GroupLayout workbenchLayout = new GroupLayout(this);
		ParallelGroup editAndWorkbenchContainerLayoutHorizontalGroup = workbenchLayout.createParallelGroup();
		SequentialGroup editAndWorkbenchContainerLayoutVerticalGroup = workbenchLayout.createSequentialGroup();
		workbenchLayout.setHorizontalGroup(editAndWorkbenchContainerLayoutHorizontalGroup);
		workbenchLayout.setVerticalGroup(editAndWorkbenchContainerLayoutVerticalGroup);
		
		// Set workbench layout
		setLayout(workbenchLayout);
		
		// Add component workbench (with accompanying scroll pane) and edit panel to workbench
		editAndWorkbenchContainerLayoutHorizontalGroup.addComponent(componentWorkbenchScrollPane);
		editAndWorkbenchContainerLayoutVerticalGroup.addComponent(componentWorkbenchScrollPane);
		editAndWorkbenchContainerLayoutHorizontalGroup.addComponent(componentEditPanel);
		editAndWorkbenchContainerLayoutVerticalGroup.addComponent(componentEditPanel);
		
		// Finally, create the optical train and draw the workbench
		createOpticalTrainAndWorkbenchComponents();
		drawWorkbench();
	}
	
	/**
	 * Draws the workbench on the GUI using the optical train in its current state.
	 * 
	 * This method adds flow arrows in between optical components, and, where no
	 * optical components exist, adds a flow arrow to allow the user to add new
	 * optical components.
	 */
	//@Override
	public void drawWorkbench()
	{		
		// Clear the workbench of any current workbench components
		componentWorkbenchPanel.removeAll();
		
		/*
		 * Workbench layout
		 *
		 * Note: this should be kept here inside drawWorkbench() and not
		 * moved outside into a class field. Redefining the layout engine
		 * every time drawWorkbench() is called is useful for removing
		 * bizarre effects attributed to possible bugs in
		 * GroupLayout.ComponentSpring. What can happen is that the layout
		 * horizontal and vertical groups aren't fully flushed when
		 * componentWorkbench.removeAll() is called, resulting in artifacts
		 * from the previous layouts remaining in the new groups causing
		 * a repeating pattern of the optical train along the workbench.
		 * 
		 * @author Sean
		 */
		GroupLayout workbenchLayout = new GroupLayout(componentWorkbenchPanel);
		
		// Set layout manager for workbench
		componentWorkbenchPanel.setLayout(workbenchLayout);
		SequentialGroup workbenchHorizontalGroup = workbenchLayout.createSequentialGroup();
		ParallelGroup workbenchVerticalGroup = workbenchLayout.createParallelGroup();
		workbenchLayout.setHorizontalGroup(workbenchHorizontalGroup);
		workbenchLayout.setVerticalGroup(workbenchVerticalGroup);
		
		// Check that there are workbench components
		if(workbenchComponents.size() > 0)
		{
			// Reset the current length of the visualisation (used for automatically scrolling to selected components)
			int scrollPaneXSize = 0;
			
			// Set the optical environment's start component to the first visible
			// component in the workbench. This is required to avoid strange effects
			// when loading a saved TIM file.
			opticalEnvironment.setStartComponent(workbenchComponents.get(0).getOpticalComponent());
			
			if(workbenchComponents.get(0).getOpticalComponent().getComponentInputs().length > 0)
			{
				// The first component in the train has input(s), so add a flow line to allow the user
				// to add stuff before it
				
				ExtensiveWorkbenchFlowArrow flowArrow = createWorkbenchFlowArrow(0);
				
				workbenchHorizontalGroup.addComponent(flowArrow);
				workbenchVerticalGroup.addComponent(flowArrow);
				
				scrollPaneXSize += flowArrow.getPreferredSize().width;	// getIcon().getIconWidth();
			}
			
			// Loop through the workbench components, adding them to the workbench
			for(int x = 0; x < workbenchComponents.size(); x++)
			{
				ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent = workbenchComponents.get(x);
				
				workbenchHorizontalGroup.addComponent(workbenchOpticalComponent);
				workbenchVerticalGroup.addComponent(workbenchOpticalComponent);
				
				// Add a flow arrow
				ExtensiveWorkbenchFlowArrow flowArrow = createWorkbenchFlowArrow(x + 1);
				
				workbenchHorizontalGroup.addComponent(flowArrow);
				workbenchVerticalGroup.addComponent(flowArrow);
				
				// Update x size (AFTER setting the selected component position above, because we want to
				// save the start position of the component, not the position immediately after it
				scrollPaneXSize += workbenchOpticalComponent.getPreferredSize().width;	// getIcon().getIconWidth();
				scrollPaneXSize += flowArrow.getPreferredSize().width;	// getIcon().getIconWidth();
			}
		}
		else
		{
			// The optical train is empty. Draw a flow arrow to allow the user to add new components.
			
			ExtensiveWorkbenchFlowArrow flowArrow = createWorkbenchFlowArrow(0);
			
			workbenchHorizontalGroup.addComponent(flowArrow);
			workbenchVerticalGroup.addComponent(flowArrow);
		}
		
		// This will get the operating system specific preferred height and then set
		// the minimum and maximum sizes based on this preferred height. This means
		// that whenever the window is resized, the workbench panel stays the same size --
		// its most natural size for the operating system -- at all times.
		//
		// Here we also add in the preferred size of the scroll bar to account for the
		// scroll bar's height being added to the preferred size of the underlying panel
		// (otherwise the scroll bar overlaps with the content in an ugly fashion!)
		int componentWorkbenchScrollPanePreferredHeight = componentWorkbenchScrollPane.getPreferredSize().height + componentWorkbenchScrollPane.getHorizontalScrollBar().getPreferredSize().height;
		
		// System.out.println("Preferred height: " + componentWorkbenchScrollPanePreferredHeight);
		
		componentWorkbenchScrollPane.setMinimumSize(new Dimension(100, componentWorkbenchScrollPanePreferredHeight));
		componentWorkbenchScrollPane.setMaximumSize(new Dimension(10000, componentWorkbenchScrollPanePreferredHeight));
		
		// Redraw everything to reflect changes
		componentWorkbenchScrollPane.revalidate();
		componentWorkbenchScrollPane.repaint();
	}
	
	/**
	 * Creates the optical train list from the components in the environment.
	 * The components specify their visible inputs and outputs - these are what
	 * this method uses to build the train.
	 * 
	 * This method must be supplied with a start component. It is from this
	 * component that the optical train is built - using this component's visible
	 * input and output to create the train.
	 * 
	 * If no start component exists (i.e. the optical environment is empty), the
	 * start component can be specified as null and the method will make the optical
	 * train empty.
	 * 
	 * @param startComponent		The optical component to start from
	 */
	public void createOpticalTrainAndWorkbenchComponents()
	{
		opticalComponentTrain.clear();
		workbenchComponents.clear();
		
		AbstractOpticalComponent startComponent = opticalEnvironment.getStartComponent();
		
		if(startComponent != null)
		{
			opticalComponentTrain.add(startComponent);
			workbenchComponents.add(createWorkbenchOpticalComponent(startComponent));
			
			addInputOpticalTrainAndWorkbenchComponents(startComponent);
			addOutputOpticalTrainAndWorkbenchComponents(startComponent);
		}
	}
	
	/**
	 * Updates the visible inputs of the specified workbench optical component.
	 * 
	 * @param workbenchOpticalComponent
	 */
	public void updateWorkbenchOpticalComponentVisibleInputs(ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent)
	{
		AbstractOpticalComponent opticalComponent = workbenchOpticalComponent.getOpticalComponent();
		
		ArrayList<AbstractOpticalComponent> opticalComponentsToRemove = new ArrayList<AbstractOpticalComponent>(opticalComponentTrain.subList(0, opticalComponentTrain.indexOf(opticalComponent)));
		opticalComponentTrain.removeAll(opticalComponentsToRemove);
		
		ArrayList<ExtensiveWorkbenchOpticalComponent> workbenchComponentsToRemove = new ArrayList<ExtensiveWorkbenchOpticalComponent>(workbenchComponents.subList(0, workbenchComponents.indexOf(workbenchOpticalComponent)));
		workbenchComponents.removeAll(workbenchComponentsToRemove);
		
		// Rebuild train to the left
		addInputOpticalTrainAndWorkbenchComponents(opticalComponent);
	}
	
	/**
	 * Updates the visible outputs of the specified workbench optical component.
	 * 
	 * @param workbenchOpticalComponent
	 */
	public void updateWorkbenchOpticalComponentVisibleOutputs(ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent)
	{
		AbstractOpticalComponent opticalComponent = workbenchOpticalComponent.getOpticalComponent();
		
		ArrayList<AbstractOpticalComponent> opticalComponentsToRetain = new ArrayList<AbstractOpticalComponent>(opticalComponentTrain.subList(0, opticalComponentTrain.indexOf(opticalComponent) + 1));
		opticalComponentTrain.retainAll(opticalComponentsToRetain);
		
		ArrayList<ExtensiveWorkbenchOpticalComponent> workbenchComponentsToRetain = new ArrayList<ExtensiveWorkbenchOpticalComponent>(workbenchComponents.subList(0, workbenchComponents.indexOf(workbenchOpticalComponent) + 1));
		workbenchComponents.retainAll(workbenchComponentsToRetain);
		
		// Rebuild train to the left
		addOutputOpticalTrainAndWorkbenchComponents(opticalComponent);
	}
	
	/**
	 * Recursive method to add visible inputs of optical components to the optical
	 * train and workbench. Using this method will add the input of the specified
	 * component to the start of the optical train and workbench, then that
	 * component's own input is added, and so on, until there is no longer an input
	 * to add (i.e. a light source is reached).
	 * 
	 * @param opticalComponent		The component whose input you wish to add to
	 * 								the optical train and workbench
	 */
	private void addInputOpticalTrainAndWorkbenchComponents(AbstractOpticalComponent opticalComponent)
	{
		int opticalTrainInputIndex = opticalComponent.getOpticalTrainInputIndex();
		
		ComponentOutput inputBeam;

		if(opticalTrainInputIndex < opticalComponent.getComponentInputs().length)
		{
			try
			{
				inputBeam = opticalComponent.getComponentInputs()[opticalTrainInputIndex];

				if(inputBeam != null)
				{
					AbstractOpticalComponent inputOpticalComponent = inputBeam.getComponent();

					// Add input component to start of list
					opticalComponentTrain.add(0, inputOpticalComponent);
					workbenchComponents.add(0, createWorkbenchOpticalComponent(inputOpticalComponent));

					addInputOpticalTrainAndWorkbenchComponents(inputOpticalComponent);
				}
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();

				// No input exists on the optical train
				// Do nothing
			}
		}
	}
	
	/**
	 * Recursive method to add visible outputs of optical components to the optical
	 * train and workbench. Using this method will add the output of the specified
	 * component to the end of the optical train and workbench, then that
	 * component's own output is added, and so on, until there is no longer an
	 * output to add (i.e. a light terminal is reached).
	 * 
	 * @param opticalComponent		The component whose input you wish to add to
	 * 								the optical train and workbench
	 */
	private void addOutputOpticalTrainAndWorkbenchComponents(AbstractOpticalComponent opticalComponent)
	{
		int opticalTrainOutputIndex = opticalComponent.getOpticalTrainOutputIndex();
		
		if(opticalTrainOutputIndex < opticalComponent.getComponentOutputs().length)
		{
			try
			{
				ComponentInput outputBeam = opticalComponent.getComponentOutputs()[opticalTrainOutputIndex];
				
				if(outputBeam != null)
				{
					AbstractOpticalComponent outputOpticalComponent = outputBeam.getComponent();
					
					// Add input component to start of list
					opticalComponentTrain.add(outputOpticalComponent);
					workbenchComponents.add(createWorkbenchOpticalComponent(outputOpticalComponent));
					
					addOutputOpticalTrainAndWorkbenchComponents(outputOpticalComponent);
				}
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();

				// No output exists on the optical train
				// Do nothing
			}
		}
	}
	
	/**
	 * Deselects all workbench components on the workbench and selects the
	 * specified workbench component.
	 * 
	 * @param workbenchOpticalComponent		Workbench component to select
	 */
	private void selectWorkbenchOpticalComponent(ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent)
	{
		// Deselect all workbench components initially
		deselectWorkbenchOpticalComponents();
		
		// Select the specified workbench component
		workbenchOpticalComponent.setSelected(true);
		
		// Set the edit panel listener for the specified workbench component
		workbenchOpticalComponent.getOpticalComponent().setEditPanelListener(new ComponentEditPanelListener(workbenchOpticalComponent));

		// Show the edit panel for the specified workbench component
		componentEditPanel.removeAll();
		componentEditPanel.add(workbenchOpticalComponent.getOpticalComponent().getEditPanel());
		
		// Needed to make edit panel contents visible...
		componentEditPanel.revalidate();
		componentEditPanel.repaint();
	}
	
	/**
	 * Deselects all workbench components present in the workbench component list.
	 */
	private void deselectWorkbenchOpticalComponents()
	{
		for(ExtensiveWorkbenchOpticalComponent workbenchComponent : workbenchComponents)
		{
			workbenchComponent.setSelected(false);
		}
	}
	
	/**
	 * Creates a workbench component from an optical component. Creates and adds
	 * a popup menu and a mouse listener to the workbench component.
	 * 
	 * @param opticalComponent		The optical component the workbench component
	 * 								is to be created from
	 * @return						New workbench component corresponding to the
	 * 								specified optical component
	 */
	private ExtensiveWorkbenchOpticalComponent createWorkbenchOpticalComponent(AbstractOpticalComponent opticalComponent)
	{
		// Create the workbench component
		ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent = new ExtensiveWorkbenchOpticalComponent(this, opticalComponent);
		
		// Create a popup menu for the workbench component, creating a listener for
		// it too
		ExtensiveWorkbenchOpticalComponentPopupMenu workbenchOpticalComponentPopupMenu = new ExtensiveWorkbenchOpticalComponentPopupMenu(new WorkbenchOpticalComponentPopupMenuActionListener(workbenchOpticalComponent), opticalComponent);
		
		// Add the popup menu to the workbench component
		workbenchOpticalComponent.setComponentPopupMenu(workbenchOpticalComponentPopupMenu);
		// getComponentImagePanel().
		
		// Set the mouse listener; adding it to the componentImagePanel makes sure that the event coordinates are relative to the image
		// However, unless the popup menu is also added to the componentImagePanel, that no longer works
		workbenchOpticalComponent.addMouseListener(new WorkbenchOpticalComponentMouseListener(workbenchOpticalComponent));
		// getComponentImagePanel().
		
		return workbenchOpticalComponent;
	}
	
	/**
	 * Creates a workbench flow arrow component, complete with popup menu.
	 * 
	 * @param opticalTrainPosition	The optical train index this component would
	 * 								be if it were an optical component (i.e. the
	 * 								left handed optical component's index + 1)
	 * @return						New flow arrow workbench component
	 */
	private ExtensiveWorkbenchFlowArrow createWorkbenchFlowArrow(int opticalTrainPosition)
	{
		ExtensiveWorkbenchFlowArrow workbenchFlowArrow = new ExtensiveWorkbenchFlowArrow(opticalTrainPosition);
				
		ExtensiveWorkbenchFlowArrowPopupMenu popupMenu = new ExtensiveWorkbenchFlowArrowPopupMenu(opticalTrainPosition == 0, opticalEnvironment.getAvailableLightSourceComponentsBitField(), opticalEnvironment.getAvailableNonLightSourceComponentsBitField(), new WorkbenchFlowArrowPopupMenuActionListener(workbenchFlowArrow));
		
		workbenchFlowArrow.setComponentPopupMenu(popupMenu);
		
		return workbenchFlowArrow;
	}
	
	@Override
	public AbstractOpticalComponent getStartComponent()
	{
		if(opticalComponentTrain.size() > 0)
		{
			return opticalComponentTrain.get(0);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * @return the workbenchComponents
	 */
	public ArrayList<ExtensiveWorkbenchOpticalComponent> getWorkbenchComponents() {
		return workbenchComponents;
	}

	public int getOpticalTrainIndexOf(AbstractOpticalComponent component)
	{
		return opticalComponentTrain.indexOf(component);
	}

	public int getOpticalTrainIndexOf(ExtensiveWorkbenchOpticalComponent extensiveWorkbenchOpticalComponent)
	{
		return workbenchComponents.indexOf(extensiveWorkbenchOpticalComponent);
	}

	public ExtensiveWorkbenchOpticalComponent insertComponent(int opticalTrainIndex, AbstractOpticalComponent component)
	{
		AbstractOpticalComponent previousComponent;
		AbstractOpticalComponent nextComponent;
		
		try
		{
			previousComponent = opticalComponentTrain.get(opticalTrainIndex - 1);
		}
		catch(IndexOutOfBoundsException e)
		{
			// There is nothing before the position we're inserting into
			previousComponent = null;
		}
		
		try
		{
			nextComponent = opticalComponentTrain.get(opticalTrainIndex);
		}
		catch(IndexOutOfBoundsException e)
		{
			// There is nothing after the position we're inserting into
			nextComponent = null;
		}
		
		if(previousComponent != null)
		{
			opticalEnvironment.addAfter(previousComponent, previousComponent.getOpticalTrainOutputIndex(), component, 0);
		}
		else if(nextComponent != null)
		{
			opticalEnvironment.addBefore(nextComponent, nextComponent.getOpticalTrainInputIndex(), component, 0);
		}
		else
		{
			// The environment is empty. Set the start component.						
			opticalEnvironment.addFirstComponent(component);
		}
		
		// Add new optical component to optical train
		opticalComponentTrain.add(opticalTrainIndex, component);

		// Add new workbench component to list of workbench components
		ExtensiveWorkbenchOpticalComponent newExtensiveWorkbenchOpticalComponent = createWorkbenchOpticalComponent(component);
		workbenchComponents.add(opticalTrainIndex, newExtensiveWorkbenchOpticalComponent);
		
		return newExtensiveWorkbenchOpticalComponent;
	}
	
	public void selectAndShowComponent(ExtensiveWorkbenchOpticalComponent extensiveWorkbenchOpticalComponent)
	{
		// now select the new optical component
		selectWorkbenchOpticalComponent(extensiveWorkbenchOpticalComponent);
		
		// Redraw the workbench
		drawWorkbench();
		
		// Set the scroll pane viewport so that the selected component is always at least at
		// the left hand side of the scroll pane
		componentWorkbenchScrollPane.getViewport().setViewPosition(new Point(selectedOpticalComponentXDistance, 0));
	}
	
	/*************************************************************************
	 * GUI listener subclasses                                               *
	 *                                                                       *
	 * These classes are defined here because they need to access fields and *
	 * methods present in this class.                                        *
	 *************************************************************************/
	
	/**
	 * Workbench optical component popup menu action listener subclass.
	 * 
	 * Detects click events from workbench optical component popup menus, such as
	 * 'Remove', and performs relevant actions based on the event.
	 * 
	 * @author Sean
	 */
	public class WorkbenchOpticalComponentPopupMenuActionListener implements ActionListener
	{
		ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent;
		
		public WorkbenchOpticalComponentPopupMenuActionListener(ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent)
		{
			this.workbenchOpticalComponent = workbenchOpticalComponent;
		}
		
		public void actionPerformed(ActionEvent event)
		{
			String command = event.getActionCommand();

			// Check for remove command
			if(command.equals("Remove"))
			{
				// Remove component
				opticalEnvironment.remove(workbenchOpticalComponent.getOpticalComponent());
				
				// Clear and redraw the edit panel
				// componentEditPanel.removeAll();
				
				// Remove the component from the optical train and workbench
				opticalComponentTrain.remove(workbenchOpticalComponent.getOpticalComponent());
				workbenchComponents.remove(workbenchOpticalComponent);
				
				// Redraw the workbench
				drawWorkbench();
				
				// Clear the edit panel, re-layout the empty edit panel and repaint it
				componentEditPanel.removeAll();
				componentEditPanel.revalidate();
				componentEditPanel.repaint();
			}
			else if(command.equals("Enabled"))
			{
				// Toggle disabled switch
				workbenchOpticalComponent.getOpticalComponent().setComponentEnabled(((JCheckBoxMenuItem)(event.getSource())).getState());
				workbenchOpticalComponent.updateImage();
			}
			else if(command.equals("Clear data"))
			{
				((Plane)(workbenchOpticalComponent.getOpticalComponent())).clearData();
			}
			else if(command.equals("Convert"))
			{
				((ConvertableComponent)workbenchOpticalComponent.getOpticalComponent()).convert(workbenchOpticalComponent);
			}
//			else if(command.equals("Replace with light source/image"))
//			{
//				LightSource lightSource = null;
//				ImageOfPlaneNonInitialising imageOfPlane = null;
//				
//				if(workbenchOpticalComponent.getOpticalComponent() instanceof LightSource)
//				{
//					lightSource = (LightSource)workbenchOpticalComponent.getOpticalComponent();
//				}
//				else if(workbenchOpticalComponent.getOpticalComponent() instanceof ImageOfPlaneNonInitialising)
//				{
//					imageOfPlane = (ImageOfPlaneNonInitialising)workbenchOpticalComponent.getOpticalComponent();
//				}
//	
//				if(lightSource == null) lightSource = new LightSource();
//				if(imageOfPlane == null) imageOfPlane = new ImageOfPlaneNonInitialising();
//
//				ImageOfPlane lightSourceOrImage = new ImageOfPlane(
//						"Light source / image",
//						lightSource,
//						imageOfPlane
//				);
//				
//				// TODO now link this in!
//
//			}
		}
	}
	
	/**
	 * Workbench flow arrow popup menu action listener subclass.
	 * 
	 * Detects click events from workbench flow arrow popup menus, such as
	 * 'Insert', and performs relevant actions based on the event.
	 * 
	 * @author Sean
	 */
	public class WorkbenchFlowArrowPopupMenuActionListener implements ActionListener
	{
		private ExtensiveWorkbenchFlowArrow flowArrow;
		
		WorkbenchFlowArrowPopupMenuActionListener(ExtensiveWorkbenchFlowArrow flowArrow)
		{
			this.flowArrow = flowArrow;
		}
		
		@Override
		public void actionPerformed(ActionEvent event)
		{
			String command = event.getActionCommand();
			
			ArrayList<String> availableComponents = OpticalComponentFactory.getLightSourceComponents();
			availableComponents.addAll(OpticalComponentFactory.getNonLightSourceComponents());

			
			for(String componentName : availableComponents)
			{
				if(command.equals("Insert " + componentName))
				{
					selectAndShowComponent(
							insertComponent(
									flowArrow.getOpticalTrainPosition(),	// index
									OpticalComponentFactory.create(componentName)	// component
									)
							);

//					AbstractOpticalComponent newComponent = OpticalComponentFactory.create(componentName);
//					
//					int flowArrowPosition = flowArrow.getOpticalTrainPosition();
//					
//					AbstractOpticalComponent previousComponent;
//					AbstractOpticalComponent nextComponent;
//					
//					try
//					{
//						previousComponent = opticalComponentTrain.get(flowArrowPosition - 1);
//					}
//					catch(IndexOutOfBoundsException e)
//					{
//						// There is nothing before the position we're inserting into
//						previousComponent = null;
//					}
//					
//					try
//					{
//						nextComponent = opticalComponentTrain.get(flowArrowPosition);
//					}
//					catch(IndexOutOfBoundsException e)
//					{
//						// There is nothing after the position we're inserting into
//						nextComponent = null;
//					}
//					
//					if(previousComponent != null)
//					{
//						opticalEnvironment.addAfter(previousComponent, previousComponent.getOpticalTrainOutputIndex(), newComponent, 0);
//					}
//					else if(nextComponent != null)
//					{
//						opticalEnvironment.addBefore(nextComponent, nextComponent.getOpticalTrainInputIndex(), newComponent, 0);
//					}
//					else
//					{
//						// The environment is empty. Set the start component.						
//						opticalEnvironment.addFirstComponent(newComponent);
//					}
//					
//					// Add new optical component to optical train
//					opticalComponentTrain.add(flowArrow.getOpticalTrainPosition(), newComponent);
//
//					// Add new workbench component to list of workbench components
//					ExtensiveWorkbenchOpticalComponent newExtensiveWorkbenchOpticalComponent = createWorkbenchOpticalComponent(newComponent);
//					workbenchComponents.add(flowArrow.getOpticalTrainPosition(), newExtensiveWorkbenchOpticalComponent);
//					
//					// now select the new optical component
//					selectWorkbenchOpticalComponent(newExtensiveWorkbenchOpticalComponent);
//					
//					// Redraw the workbench
//					drawWorkbench();
//					
//					// Set the scroll pane viewport so that the selected component is always at least at
//					// the left hand side of the scroll pane
//					componentWorkbenchScrollPane.getViewport().setViewPosition(new Point(selectedOpticalComponentXDistance, 0));
				}
			}
		}
	}
	
	/**
	 * Workbench optical component mouse listener subclass.
	 * 
	 * Detects mouse events from workbench optical components, and performs
	 * relevant actions based on the event.
	 * 
	 * @author Sean
	 */
	public class WorkbenchOpticalComponentMouseListener implements MouseListener
	{
		private ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent;
		
		public WorkbenchOpticalComponentMouseListener(ExtensiveWorkbenchOpticalComponent component)
		{
			this.workbenchOpticalComponent = component;
		}

		@Override
		public void mouseClicked(MouseEvent event)
		{

		}

		@Override
		public void mouseEntered(MouseEvent event)
		{

		}

		@Override
		public void mouseExited(MouseEvent event)
		{

		}

		@Override
		public void mousePressed(MouseEvent event)
		{

		}

		@Override
		public void mouseReleased(MouseEvent event)
		{
			// Select this workbench optical component
			selectWorkbenchOpticalComponent(workbenchOpticalComponent);
			
			AbstractOpticalComponent opticalComponent = workbenchOpticalComponent.getOpticalComponent();
			
			if(opticalComponent instanceof ClickableInterface)
			{
				// This optical component has some sort of click event handling - run it
				
				// First of all, take a copy of the input and output indices (in case they change)
				int inputIndex = opticalComponent.getOpticalTrainInputIndex();
				int outputIndex = opticalComponent.getOpticalTrainOutputIndex();
				
				// Have the optical component deal with click events
				ClickableInterface clickable = (ClickableInterface) opticalComponent;
				// convert mouse event into componentImagePanel's coordinate system, as this is how it will be interpreted
				MouseEvent e = SwingUtilities.convertMouseEvent(workbenchOpticalComponent, event, workbenchOpticalComponent.getComponentImagePanel());
				clickable.dealWithImageMouseEvent(e);
				
				// Check if the input index has changed
				if(opticalComponent.getOpticalTrainInputIndex() != inputIndex)
				{
					// The input has changed - redraw the workbench inputs
					updateWorkbenchOpticalComponentVisibleInputs(workbenchOpticalComponent);
				}
				
				// Check if the output index has changed
				if(opticalComponent.getOpticalTrainOutputIndex() != outputIndex)
				{
					// The output has changed - redraw the workbench outputs
					updateWorkbenchOpticalComponentVisibleOutputs(workbenchOpticalComponent);
				}

				// Redraw workbench
				drawWorkbench();
				
				// Update icon (in case it has changed)
				workbenchOpticalComponent.updateImage();
			}
			
			// Show the edit panel for the workbench we're interested in
			componentEditPanel.removeAll();
			componentEditPanel.add(workbenchOpticalComponent.getOpticalComponent().getEditPanel());
			
			// Needed to make edit panel contents visible...
			componentEditPanel.revalidate();
			componentEditPanel.repaint();
		}
	}
	
	/**
	 * Component edit panel listener subclass.
	 * 
	 * Updates the formatted name displayed on the workbench component when the
	 * editMade() method is fired by a component which implements an edit panel.
	 * 
	 * GUI components such as edit panels, combo boxes, etc. can fire this listener
	 * whenever they are changed (e.g. text entered/removed, list item selected).
	 * As the GUI component's value may have some part to play in making the formatted
	 * name for the component (i.e. the name text field is used in the formatted
	 * name), this is useful for letting the GUI know that it should update the
	 * formatted name displayed on the workbench for the component in question.
	 * 
	 * @author Sean
	 */
	public class ComponentEditPanelListener implements OpticalComponentEditListener
	{
		private ExtensiveWorkbenchOpticalComponent workbenchOpticalComponent;
		
		private String formattedName = "";
		
		public ComponentEditPanelListener(ExtensiveWorkbenchOpticalComponent workbenchComponent)
		{
			this.workbenchOpticalComponent = workbenchComponent;
		}
		
		@Override
		public void editMade()
		{
			String newName = workbenchOpticalComponent.getOpticalComponent().getFormattedName();
			
			if(!formattedName.equals(newName))
			{
				// The formatted name has changed, so update the workbench component's caption.
				workbenchOpticalComponent.setName(newName);
			
				// Update our records
				formattedName = newName;
				
				// Repaint whole workbench to reflect changes (repainting just the component doesn't seem to work)
				// This doesn't appear to be needed
				//drawWorkbench();
			}
		}
		
		@Override
		public void redraw()
		{
			// Update visible inputs and outputs
			updateWorkbenchOpticalComponentVisibleInputs(workbenchOpticalComponent);
			updateWorkbenchOpticalComponentVisibleOutputs(workbenchOpticalComponent);
			
			// Redraw the workbench
			drawWorkbench();
		}
	}

	public ArrayList<AbstractOpticalComponent> getOpticalComponentTrain()
	{
		return opticalComponentTrain;
	}

	public void setOpticalComponentTrain(ArrayList<AbstractOpticalComponent> opticalComponentTrain)
	{
		this.opticalComponentTrain = opticalComponentTrain;
	}
	
	@Override
	public boolean showLoadAndSaveButtons()
	{
		return true;
	}
	
	@Override
	public boolean showResearchButtons()
	{
		return true;
	}
}
