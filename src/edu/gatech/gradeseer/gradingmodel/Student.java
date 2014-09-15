package edu.gatech.gradeseer.gradingmodel;

import java.io.File;
import java.io.FileFilter;

/**
 * Simple container for individual student data.
 * 
 * @author Andrey Kurenkov
 * 
 */
public class Student implements Comparable<Student> {
	private String name;
	private String id;
	private File currentFolder;// NOTE: StudentSubmission now holds this info

	/**
	 * Basic constructor to set vars
	 * 
	 * @param name
	 *            student name (last, first)
	 * @param id
	 *            student gtid
	 */
	public Student(String name, String id) {
		this.name = name;
		this.id = id;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Name getter
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * ID getter
	 * 
	 * @return id
	 */
	public String getID() {
		return id;
	}

	/**
	 * Checks if a folder in the file system has been assigned to the student (happens when grading to remember where to save
	 * comments).
	 * 
	 * @return true if folder not null
	 */
	public boolean folderKnown() {
		return currentFolder != null;
	}

	/**
	 * Setter for student folder on filesystem where to save comments
	 * 
	 * @param to
	 *            the new folder
	 */
	public void setFolder(File to) {
		this.currentFolder = to;
	}

	/**
	 * Finds the studen's folder within a directory(should be an unzipped homework directory) returns it (does not set it to
	 * current folder)
	 * 
	 * @param dir
	 *            directory to look within
	 * @return a folder that was found or null.
	 */
	public File findFolder(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return null;
		File[] folderMatch = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().toLowerCase().contains(getName().toLowerCase());
			}

		});
		if (folderMatch.length > 0) {
			File folder = folderMatch[0];
			this.currentFolder = folder;
			return folder;
		}
		return null;
	}

	/**
	 * Folder getter
	 * 
	 * @return current folder inside grading folder
	 */
	public File getFolder() {
		return currentFolder;
	}

	@Override
	public int compareTo(Student compare) {
		return this.name.compareTo(compare.getName());
	}

}