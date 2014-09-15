package edu.gatech.gradeseer.gradingmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic model of an assignment.
 * 
 * @author Andrey Kurenkov [akurenkov3@gateche.edu]
 */
public class Assignment {
	private String name;
	private List<AssignmentProblem> problems;
	private List<AssignmentSubmission> submissions;
	private List<String> fileNames;
	private List<String> questionNames;
	private double maxGrade;

	/**
	 * @param name
	 * @param problems
	 * @param fileNames
	 *            The names/regexes for names that students should submit to this assignment.
	 */
	public Assignment(String name, List<AssignmentProblem> problems, List<String> fileNames) {
		this.name = name;
		this.problems = problems;
		this.maxGrade = 0;
		this.questionNames = new ArrayList<String>();
		for (AssignmentProblem problem : problems) {
			this.maxGrade += problem.getMaxGrade();
			this.questionNames.add(problem.getName());
		}
		this.fileNames = fileNames;
		this.submissions = new ArrayList<AssignmentSubmission>();
	}

	/**
	 * Default implementation of just summing up the grades stored.
	 * 
	 * @return
	 */
	public double computeFinalGrade(AssignmentSubmission submission) {
		double grade = 0;
		for (Double pGrade : submission.getGrades().values())
			grade += pGrade;
		return grade;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the problems
	 */
	public List<AssignmentProblem> getProblems() {
		return problems;
	}

	/**
	 * @return the maxGrade
	 */
	public double getMaxGrade() {
		return maxGrade;
	}

	/**
	 * 
	 * @param submission
	 */
	public void addStudentSubmission(AssignmentSubmission submission) {
		this.submissions.add(submission);
	}

	public List<AssignmentSubmission> getSubmissions() {
		return this.submissions;
	}

	public List<String> getQuestionNames() {
		return questionNames;
	}

	/**
	 * @return
	 */
	public List<String> getFileNames() {
		return fileNames;
	}
}
