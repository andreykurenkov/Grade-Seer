package edu.gatech.gradeseer.gui.core;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import edu.gatech.gradeseer.gradingmodel.StudentSet;
import edu.gatech.gradeseer.gui.components.ProcessTextArea;
import edu.gatech.gradeseer.gui.components.TabCloseComponent;
import edu.gatech.gradeseer.gui.components.TextStdIOPane;

public class InteractTabsPane extends JTabbedPane {
	private GradingPanel gradingPanel;
	private JFrame gradingFrame;

	public InteractTabsPane(StudentSet students) {
		gradingPanel = new GradingPanel(students);
		addTab("Grading", null, gradingPanel, "Grading Panel");
		addTab("Std IO", TextStdIOPane.getInstance());
	}

	public void addProcessTab(ProcessTextArea toAdd, String name) {
		addTab(name, toAdd);
		setTabComponentAt(getTabCount() - 1, new TabCloseComponent(this));
		setSelectedIndex(getTabCount() - 1);
	}

	public void toggleGradingSeperation() {
		if (gradingFrame == null) {
			gradingFrame = new JFrame("Grading Window");
			gradingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			gradingFrame.add(gradingPanel);
			gradingFrame.pack();
			gradingFrame.setVisible(true);
		} else {
			gradingFrame.setEnabled(false);
			this.insertTab("Grading", null, gradingPanel, "Grading Panel", 0);
			this.setSelectedIndex(0);
			gradingFrame = null;
		}
	}

	public void updateStudents(StudentSet newSet) {
		setComponentAt(0, new GradingPanel(newSet));
	}

	public void setSaveAndLoadDir(File newDir) {
		gradingPanel.setSaveDir(newDir);
		gradingPanel.setLoadDir(newDir);
		gradingPanel.setSaveLoadName(newDir.getName() + " Grades");
	}
}
