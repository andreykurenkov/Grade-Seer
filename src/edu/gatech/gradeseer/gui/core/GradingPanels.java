package edu.gatech.gradeseer.gui.core;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import edu.gatech.gradeseer.gradingmodel.Configuration;

/**
 * Container for all the right-hand side top-level panels. Contains a JSplitPane with two Tabbed panes and a Toolbar, with
 * all functionality implemented in their respective classes.
 * 
 * @author Andrey Kurenkov
 */
public class GradingPanels extends JPanel {
	private DisplayTabsPane displayTabs;
	private InteractTabsPane interactTabs;
	private JSplitPane splitPane;
	private GradingToolbar toolbar;

	/**
	 * Builds the display/interaction half of the program according to the given Configuration.
	 * 
	 * @param config
	 *            Configuration holding students and the file set for grading
	 */
	public GradingPanels(Configuration config) {
		super(new BorderLayout());
		displayTabs = new DisplayTabsPane();
		interactTabs = new InteractTabsPane(config.getStudentSet());
		interactTabs.setSaveAndLoadDir(config.getGradingDir());

		toolbar = new GradingToolbar(displayTabs, interactTabs);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(displayTabs);
		splitPane.setRightComponent(interactTabs);
		this.add(toolbar, BorderLayout.NORTH);
		this.add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(330);
	}

	/**
	 * Changes the component displayed in the current teb of displayTabs and updates the Toolbar appropriately.
	 * 
	 * @param component
	 *            the new current JComponent
	 */
	public void setCurrentDisplayTab(JComponent component) {
		displayTabs.setCurrent(component);
		toolbar.updateButtonState();
	}

	/**
	 * @return the displayTabs
	 */
	public DisplayTabsPane getDisplayTabs() {
		return displayTabs;
	}

	/**
	 * @return the interactTabs
	 */
	public InteractTabsPane getInteractTabs() {
		return interactTabs;
	}

	/**
	 * Updates the tabs to the settings of the given configuration.
	 * 
	 * @param newConfig
	 */
	public void reloadToConfig(Configuration newConfig) {
		interactTabs.updateStudents(newConfig.getStudentSet());
		interactTabs.setSaveAndLoadDir(newConfig.getGradingDir());
	}

}
