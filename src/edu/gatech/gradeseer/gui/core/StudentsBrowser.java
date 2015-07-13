package edu.gatech.gradeseer.gui.core;

import java.io.File;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.gatech.gradeseer.fileio.SmartFile;
import edu.gatech.gradeseer.gradingmodel.Student;
import edu.gatech.gradeseer.gradingmodel.StudentSet;

public class StudentsBrowser extends JList<Student> {
	private FilesBrowser filesBrowser;
	private Student selectedStudent;
	private File currentDir;

	public StudentsBrowser(StudentSet set, File dir, FilesBrowser filesBrowser) {
		super();
		this.filesBrowser = filesBrowser;
		currentDir = dir;
		setListData(set.getSet());
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (getSelectedIndex() == -1) {
					StudentsBrowser.this.filesBrowser.setListData(new SmartFile[0]);
				} else {
					updateFileBrowser();
				}
			}
		});

	}

	private void updateFileBrowser() {
		selectedStudent = getSelectedValue();
		File folder = selectedStudent.findFolder(currentDir);

		if (folder == null) {
			JOptionPane.showMessageDialog(this, "No folder for student " + selectedStudent + "\nIn grading folder "
					+ currentDir.getAbsolutePath() + "\nGrading folder must be unzipped HW folder.", "Error Message",
					JOptionPane.ERROR_MESSAGE);
		} else {
			filesBrowser.setCurrentStudentFolder(folder);
			filesBrowser.updateFiles();
		}
	}

	public void setCurrentDir(File dir) {
		this.currentDir = dir;
	}

	public Student getStudent() {
		return selectedStudent;
	}

}
