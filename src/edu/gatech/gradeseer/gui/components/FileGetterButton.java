package edu.gatech.gradeseer.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileGetterButton extends JButton {
	private File lastFile;
	private FileFilter filter;
	private ArrayList<FileChangeListener> onChange;
	private boolean directoriesOnly;

	public FileGetterButton(String text, boolean onlyDirectories, File startFile) {
		super(text);
		lastFile = startFile;
		directoriesOnly = onlyDirectories;
		onChange = new ArrayList<FileChangeListener>();
		this.addActionListener(new FolderGetListener());
	}

	public FileGetterButton(String text, boolean onlyDirectories) {
		this(text, onlyDirectories, new File(""));
	}

	public FileGetterButton(String text) {
		this(text, false);
	}

	public void setFileFilter(FileFilter filter) {
		this.filter = filter;
	}

	public File getLastFile() {
		return lastFile;
	}

	public void setLastFile(File file) {
		lastFile = file;
	}

	public void addFileChangeListener(FileChangeListener newListener) {
		if (newListener != null)
			onChange.add(newListener);
	}

	public abstract static class FileChangeListener {
		public abstract void onFileChange(File newFile);
	}

	private class FolderGetListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(lastFile);
			if (filter != null)
				chooser.setFileFilter(filter);
			if (directoriesOnly)
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = chooser.showOpenDialog(FileGetterButton.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					lastFile = chooser.getSelectedFile();
					for (FileChangeListener listener : onChange) {
						listener.onFileChange(lastFile);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
