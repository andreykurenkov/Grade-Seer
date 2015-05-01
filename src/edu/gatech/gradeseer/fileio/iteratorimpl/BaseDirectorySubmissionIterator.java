package edu.gatech.gradeseer.fileio.iteratorimpl;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * 
 * 
 * Date: Sep 14, 2014
 * 
 * @author Andrey Kurenkov
 */
public abstract class BaseDirectorySubmissionIterator implements CloneableSubmissionIterator {
	protected File baseDir;
	protected File[] dirFiles;
	protected int atFile;

	/**
	 * 
	 * 
	 * @param dir
	 *            The directory to look for student submissions
	 * @param studentDirRegex
	 *            The naming format for student submissions folders
	 */
	public BaseDirectorySubmissionIterator(File dir) {
		this(dir, true);
	}

	/**
	 * 
	 * 
	 * @param dir
	 *            The directory to look for student submissions
	 * @param sort
	 *            Whether to sort the list of students
	 * @param studentDirRegex
	 *            The naming format for student submissions folders
	 */
	public BaseDirectorySubmissionIterator(File dir, boolean sort) {
		this.baseDir = dir;
		dirFiles = dir.listFiles();
		if (sort) {
			Arrays.sort(dirFiles, new Comparator<File>() {

				@Override
				public int compare(File f1, File f2) {
					return f1.getName().compareTo(f2.getName());
				}

			});
		}
		atFile = 0;
	}

	/**
	 * Checks if a folder in this directory is a student submission directory.
	 * 
	 * @param File
	 *            file
	 * @return
	 */
	protected abstract boolean isStudentDir(File file);

	/**
	 * 
	 * @param File
	 *            File dir
	 * @return
	 */
	protected abstract AssignmentSubmission getStudentSubmission(File dir);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		while (atFile < dirFiles.length && !isStudentDir(dirFiles[atFile])) {
			atFile++;
		}
		return atFile < dirFiles.length && isStudentDir(dirFiles[atFile]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public AssignmentSubmission next() {
		if (!hasNext())
			throw new NoSuchElementException("No more student submissions.");
		while (atFile < dirFiles.length && !isStudentDir(dirFiles[atFile])) {
			atFile++;
		}
		return getStudentSubmission(dirFiles[atFile++]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Removal of student submissions not supported.");
	}

}
