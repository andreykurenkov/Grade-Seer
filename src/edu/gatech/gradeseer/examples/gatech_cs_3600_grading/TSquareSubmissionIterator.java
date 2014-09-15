package edu.gatech.gradeseer.examples.gatech_cs_3600_grading;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import edu.gatech.gradeseer.fileio.FileIOUtil;
import edu.gatech.gradeseer.fileio.extensions.BaseDirectorySubmissionIterator;
import edu.gatech.gradeseer.fileio.extensions.CloneableSubmissionIterator;
import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;
import edu.gatech.gradeseer.gradingmodel.Student;

/**
 * Iterator to go through the TSquare formatted folders of student submissions.
 * 
 * TODO: add supported to matching student folders to students in StudentSet
 * 
 * Date modified: Sep 14, 2014
 * 
 * @author Andrey Kurenkov
 */
public class TSquareSubmissionIterator extends BaseDirectorySubmissionIterator {
	private String[] instructorNames;
	private Assignment assignment;

	/**
	 * 
	 * @param dir
	 */
	public TSquareSubmissionIterator(File dir, String[] instructorNames, Assignment assignment) {
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
		return true;
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
		// TODO: sort of hacky
		AssignmentSubmission submission = new AssignmentSubmission(new Student(studentName, null));
		File[] compressedFiles = filesDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return FileIOUtil.isCompressed(file);
			}

		});
		for (File file : compressedFiles) {
			try {
				FileIOUtil.decompress(file, file.getParentFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
			file.delete();
		}
		for (File file : filesDir.listFiles()) {
			submission.addFile(file);
		}

		/**
		 * try {// logic to unpack the unzipped files into what we need. Yeah, I was lazy. for (File file :
		 * filesDir.listFiles()) { if (checkCodeFiles(file)) { studentFiles.add(file); } else if (file.isDirectory()) {
		 * badSubmit = true; for (File innerFile : file.listFiles()) { if (checkCodeFiles(innerFile)) {
		 * studentFiles.add(innerFile); } else if (innerFile.isDirectory()) {// some people... for (File anotherFile :
		 * innerFile.listFiles()) { if (checkCodeFiles(anotherFile)) { studentFiles.add(anotherFile); }// if contains }// for
		 * anotherfile }// if innerfile is dir } } } } catch (Exception e) { e.printStackTrace();
		 * println("\tBad submission - python files not found"); continue; }
		 */
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
		return new TSquareSubmissionIterator(super.baseDir, instructorNames, assignment);
	}
}
