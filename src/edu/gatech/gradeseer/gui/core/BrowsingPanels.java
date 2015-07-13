package edu.gatech.gradeseer.gui.core;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import edu.gatech.gradeseer.fileio.SmartFile;
import edu.gatech.gradeseer.gradingmodel.Configuration;
import edu.gatech.gradeseer.gradingmodel.StudentSet;
import edu.gatech.gradeseer.gui.components.BackResetNextButtons.ListArrowButtons;
import edu.gatech.gradeseer.gui.components.FileGetterButton;
import edu.gatech.gradeseer.gui.components.FileGetterButton.FileChangeListener;
import edu.gatech.gradeseer.gui.helpers.ConstraintsHelper;
import edu.gatech.gradeseer.gui.helpers.NewUserDisplay;

/**
 * Container for all the left-hand side top-level panels. Stores a toolbar and two JLists that enable browsing of students
 * and the files they have submitted in the current grading folder.
 * 
 * @author Andrey Kurenkov
 */
public class BrowsingPanels extends JPanel {
	private FilesBrowser filesBrowser;
	private StudentsBrowser studentsBrowser;
	private JToolBar browsingToolbar;
	private JLabel folderLabel;
	private FileGetterButton gradingFolderSetter;

	/**
	 * Constructor that sets up the panel given configuration (students+grading file) and the abiliy to affect the
	 * display/input side of the program
	 * 
	 * @param config
	 * @param gradingPanels
	 */
	public BrowsingPanels(final Configuration config, final GradingPanels gradingPanels) {
		super(new BorderLayout());
		filesBrowser = new FilesBrowser(gradingPanels);
		studentsBrowser = new StudentsBrowser(config.getStudentSet(), config.getGradingDir(), filesBrowser);
		folderLabel = new JLabel("Current: " + config.getGradingDir().getName());
		gradingFolderSetter = new FileGetterButton("Set grading directory", true);
		gradingFolderSetter.setLastFile(config.getGradingDir());
		FileChangeListener changeListener = new FileChangeListener() {

			@Override
			public void onFileChange(File chosen) {
				config.setGradingDir(chosen);
				gradingPanels.getInteractTabs().setSaveAndLoadDir(chosen);
				studentsBrowser.setCurrentDir(chosen);
				folderLabel.setText("Current: " + chosen.getName());
			}

		};
		gradingFolderSetter.addFileChangeListener(changeListener);
		gradingFolderSetter.setToolTipText(folderLabel.getText());
		browsingToolbar = new JToolBar();
		browsingToolbar.setFloatable(false);
		browsingToolbar.add(gradingFolderSetter);
		browsingToolbar.add(folderLabel);

		ListArrowButtons studentsBrowserButtons = new ListArrowButtons(true, true, studentsBrowser);
		studentsBrowserButtons.setResetText("Configure");
		studentsBrowserButtons.addResetActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NewUserDisplay.getStudentsFile(null, config);
				StudentSet newSet = config.getStudentSet();
				if (newSet != null) {
					studentsBrowser.setListData(newSet.getSet());
					gradingPanels.getInteractTabs().updateStudents(newSet);
				}
			}
		});

		ListArrowButtons fileBrowserButtons = new ListArrowButtons(true, true, filesBrowser);
		fileBrowserButtons.setResetText("Read");
		fileBrowserButtons.addResetActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ArrayList<SmartFile> toView = filesBrowser.getSelectedFiles();
				gradingPanels.getDisplayTabs().addTextPanes(toView);
			}

		});

		JPanel browsingPanels = new JPanel(new GridBagLayout());
		browsingPanels.add(new JScrollPane(studentsBrowser), ConstraintsHelper.makeConstraints(0, 0,
				GridBagConstraints.BOTH, 10, 10, GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE,
				GridBagConstraints.WEST));
		browsingPanels.add(new JSeparator(JSeparator.VERTICAL), ConstraintsHelper.makeConstraints(1, 0,
				GridBagConstraints.BOTH, 0.1, 0.1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER,
				GridBagConstraints.WEST));
		browsingPanels.add(studentsBrowserButtons, ConstraintsHelper.makeConstraints(0, 1, GridBagConstraints.BOTH, 0.1,
				0.1, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, GridBagConstraints.WEST));
		browsingPanels.add(new JScrollPane(filesBrowser), ConstraintsHelper.makeConstraints(2, 0, GridBagConstraints.BOTH,
				10, 10, GridBagConstraints.REMAINDER, GridBagConstraints.RELATIVE, GridBagConstraints.WEST));
		browsingPanels.add(fileBrowserButtons, ConstraintsHelper.makeConstraints(2, 1, GridBagConstraints.BOTH, 0.1, 0.1,
				GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, GridBagConstraints.WEST));

		this.add(browsingToolbar, BorderLayout.NORTH);
		this.add(browsingPanels, BorderLayout.CENTER);
	}

	/**
	 * Update student list content and behavior given newConfig.
	 * 
	 * @param newConfig
	 *            new Configuration
	 */
	public void reloadToConfig(Configuration newConfig) {
		gradingFolderSetter.setLastFile(newConfig.getGradingDir());
		folderLabel.setText("Current " + newConfig.getGradingDir().getName());
		studentsBrowser.setListData(newConfig.getStudentSet().getSet());
		studentsBrowser.setCurrentDir(newConfig.getGradingDir());
	}
}
