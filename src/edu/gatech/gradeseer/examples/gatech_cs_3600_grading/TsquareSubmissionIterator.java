package edu.gatech.gradeseer.examples.gatech_cs_3600_grading;

import java.io.File;

import edu.gatech.gradeseer.fileio.FileIOUtil;
import edu.gatech.gradeseer.fileio.filefilters.FileNameFilter;
import edu.gatech.gradeseer.fileio.iteratorimpl.BaseDirectorySubmissionIterator;
import edu.gatech.gradeseer.fileio.iteratorimpl.CloneableSubmissionIterator;
import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;
import edu.gatech.gradeseer.gradingmodel.Student;

/**
 * Iterator to go through the TSquare formatted folders of student submissions.
 * 
 * TODO: add supported to matching student folders to students in StudentSet TODO: could probably extract some logic from
 * here to a superclass (ie using FileIOUtil to find files from assignment).
 * 
 * Date modified: Sep 14, 2014
 * 
 * @author Andrey Kurenkov
 */
public class TsquareSubmissionIterator extends BaseDirectorySubmissionIterator {
	private String[] instructorNames;
	private Assignment assignment;

	/**
	 * 
	 * @param dir
	 */
	public TsquareSubmissionIterator(File dir, String[] instructorNames, Assignment assignment) {
		super(dir);
		this.instructorNames = instructorNames;
		this.assignment = assignment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.gatech.gradeseer.fileio.extensions.BaseDirectorySubmissionIterator#isStudentDir(java.io.File)
	 */
	@Override
	protected boolean isStudentDir(File file) {
		if (file.isDirectory()) { // student name directory
			int nameDelim = file.getName().indexOf("(");
			if (nameDelim == -1)
				return false;
			String studentName = file.getName().substring(0, nameDelim);
			for (String instrName : instructorNames)
				if (studentName.contains(instrName))
					return false;

			File filesDir = new File(file.getAbsolutePath(), "Submission attachment(s)");
			if (!filesDir.exists())
				return false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.gatech.gradeseer.fileio.extensions.BaseDirectorySubmissionIterator#getStudentSubmission(java.io.File)
	 */
	@Override
	protected AssignmentSubmission getStudentSubmission(File dir) {
		int nameDelim = dir.getName().indexOf("(");
		String studentName = dir.getName().substring(0, nameDelim);
		File filesDir = new File(dir.getAbsolutePath(), "Submission attachment(s)");
		AssignmentSubmission submission = new AssignmentSubmission(new Student(studentName, null));
		submission.setSubmissionFiles(FileIOUtil.recursiveFileFind(filesDir, new FileNameFilter(assignment.getFileNames()),
				true, true));
		submission.setSubmissionDirectory(dir);
		return submission;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.gatech.gradeseer.fileio.extensions.CloneableSubmissionIterator#makeClone()
	 */
	@Override
	public CloneableSubmissionIterator makeClone() {
		return new TsquareSubmissionIterator(super.baseDir, instructorNames, assignment);
	}
}
