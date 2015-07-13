package edu.gatech.gradeseer.gui.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.gatech.gradeseer.fileio.SmartFile;
import edu.gatech.gradeseer.fileio.smartfiles.JavaSmartFile;
import edu.gatech.gradeseer.gui.components.CodeTextPane;

public class FilesBrowser extends JList<SmartFile> {
	private GradingPanels grading;

	private File currentStudentFolder, currentFilesFolder;
	private ArrayList<SmartFile> currentFiles, selectedFiles;

	private final String exten = "Submission attachment(s)";

	public FilesBrowser(GradingPanels gradingPanes) {
		super();
		this.grading = gradingPanes;
		currentFiles = new ArrayList<SmartFile>();
		selectedFiles = new ArrayList<SmartFile>();
		addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selectedFiles = new ArrayList<SmartFile>(getSelectedValuesList());
			}
		});

		addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				SmartFile firstReadable = null;
				for (SmartFile cur : selectedFiles) {
					if (cur.isReadable()) {
						firstReadable = cur;
						break;
					}
				}
				if (firstReadable != null) {
					JTextPane text = null;
					if (firstReadable.isJavaFile()) {
						JavaSmartFile file;
						try {
							file = new JavaSmartFile(firstReadable);
							text = CodeTextPane.getJavaTextPane(file);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						text = new JTextPane();
						text.setEditable(false);
						text.setText(firstReadable.read());
					}
					if (text != null)
						grading.setCurrentDisplayTab(new JScrollPane(text));
				}
			}
		});
	}

	/**
	 * @return the currentStudentFolder
	 */
	public File getCurrentStudentFolder() {
		return currentStudentFolder;
	}

	/**
	 * @param currentStudentFolder
	 *            the currentStudentFolder to set
	 */
	public void setCurrentStudentFolder(File currentStudentFolder) {
		this.currentStudentFolder = currentStudentFolder;
	}

	/**
	 * @return the files
	 */
	public ArrayList<SmartFile> getCurrentFiles() {
		return currentFiles;
	}

	/**
	 * @return the files
	 */
	public ArrayList<SmartFile> getSelectedFiles() {
		return selectedFiles;
	}

	public void updateFiles() {
		currentFilesFolder = new File(currentStudentFolder.getAbsolutePath() + "/" + exten);
		if (!currentFilesFolder.exists()) {
			currentFilesFolder = null;
		}
		if (currentFilesFolder == null || currentFilesFolder.listFiles() == null)
			JOptionPane.showMessageDialog(this, "Not a valid directory!\n" + "Current dir must be unzipped HW folder."
					+ "\n(Choose folder such as Homework 1,\n"
					+ "which is unzipped from a bulk download\n and which has student folders inside it).",
					"Directory error", JOptionPane.ERROR_MESSAGE);
		else {
			SmartFile[] smart = SmartFile.makeSmarter(currentFilesFolder.listFiles());
			currentFiles = new ArrayList<SmartFile>();
			for (SmartFile file : smart)
				if (file.isReadable())
					currentFiles.add(file);
			smart = currentFiles.toArray(new SmartFile[1]);
			Arrays.sort(smart);
			setListData(smart);
		}
	}
}
