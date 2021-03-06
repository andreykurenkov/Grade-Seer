package edu.gatech.gradeseer.gradingmodel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Contains information about individual student submissions. This gets filled in with additional information about the files
 * of this student when files are being iterated through, and later holds the grades this student got.
 * 
 * 
 * @author Andrey Kurenkov [akurenkov3@gatech.edu]
 */
public class AssignmentSubmission {
	private Student student;
	private List<String> comments;
	private HashMap<AssignmentProblem, List<String>> problemComments;
	private HashMap<AssignmentProblem, Double> grades;
	private double total;
	private HashMap<String, File> submissionFiles;
	private File submissionDirectory;

	public AssignmentSubmission(Student student) {
		this.student = student;
		this.submissionFiles = new HashMap<String, File>();
		this.grades = new HashMap<AssignmentProblem, Double>();
		this.problemComments = new HashMap<AssignmentProblem, List<String>>();
		this.comments = new ArrayList<String>();
	}

	public Student getStudent() {
		return student;
	}

	public HashMap<AssignmentProblem, Double> getGrades() {
		return grades;
	}

	public void addGrade(AssignmentProblem question, double points) {
		total += points;
		grades.put(question, points);
	}

	public double getTotal() {
		return total;
	}

	public void addToComments(String add) {
		this.comments.add(add);
	}

	public List<String> getComments() {
		return comments;
	}

	public void addCommentForProblem(AssignmentProblem problem, String comment) {
		if (!problemComments.containsKey(problem))
			problemComments.put(problem, new ArrayList<String>());
		this.problemComments.get(problem).add(comment);
	}

	public List<String> getProblemComments(AssignmentProblem problem) {
		if (!problemComments.containsKey(problem))
			return new ArrayList<String>();
		return this.problemComments.get(problem);
	}

	public void addFile(File toAdd) {
		if (toAdd != null)
			this.submissionFiles.put(toAdd.getName(), toAdd);
	}

	public void setSubmissionFiles(HashMap<String, File> setTo) {
		submissionFiles = setTo;
	}

	public HashMap<String, File> getSubmissionFiles() {
		return submissionFiles;
	}

	public File getSubmissionDirectory() {
		return submissionDirectory;
	}

	public void setSubmissionDirectory(File submissionDirectory) {
		this.submissionDirectory = submissionDirectory;
	}

	/**
	 * @param string
	 * @return
	 */
	public File getSubmissionFile(String name) {
		if (!submissionFiles.containsKey(name))
			return null;
		return submissionFiles.get(name);
	}

	public void setSubmissionFiles(List<File> fileList) {
		HashMap<String, File> files = new HashMap<String, File>();
		for (File file : fileList) {
			files.put(file.getName(), file);
		}
		setSubmissionFiles(files);
	}
}
