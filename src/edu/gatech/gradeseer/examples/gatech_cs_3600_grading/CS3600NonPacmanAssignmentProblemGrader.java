package edu.gatech.gradeseer.examples.gatech_cs_3600_grading;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import edu.gatech.gradeseer.autograding.problemgraderimpl.ProcessAssignmentProblemGrader;
import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * 
 * Date modified: Sep 15, 2014
 * 
 * @author Andrey Kurenkov
 */
public class CS3600NonPacmanAssignmentProblemGrader extends ProcessAssignmentProblemGrader {

	/**
	 * 
	 * @param dir
	 * @param timeLimit
	 * @param outputToFile
	 */
	public CS3600NonPacmanAssignmentProblemGrader(File dir, double timeLimit, boolean outputToFile) {
		super(dir, timeLimit, outputToFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.gatech.gradeseer.autograding.extensions.ProcessAssignmentProblemGrader#getProcessBuilder(edu.gatech.gradeseer.
	 * gradingmodel.Assignment, edu.gatech.gradeseer.gradingmodel.AssignmentProblem,
	 * edu.gatech.gradeseer.gradingmodel.StudentSubmission, java.util.List)
	 */
	@Override
	public ProcessBuilder getProcessBuilder(Assignment assignment, AssignmentProblem problem,
			AssignmentSubmission submission, List<File> files) {
		return new ProcessBuilder("python", "autograder.py", "-q", problem.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.gatech.gradeseer.autograding.extensions.ProcessAssignmentProblemGrader#gradeProcessOutput(edu.gatech.gradeseer
	 * .gradingmodel.Assignment, edu.gatech.gradeseer.gradingmodel.AssignmentProblem,
	 * edu.gatech.gradeseer.gradingmodel.StudentSubmission, java.util.List, java.lang.String)
	 */
	@Override
	public double gradeProcessOutput(Assignment assignment, AssignmentProblem problem, AssignmentSubmission submission,
			List<File> files, String out) {
		if (submission.getSubmissionFiles().size() < assignment.getFileNames().size()) {
			if (submission.getComments().size() < 1)
				submission.addToComments("Did not submit enough files (only " + submission.getSubmissionFiles().size()
						+ "), skipping.");
			return 0;
		}
		double score = -1;
		Scanner scan = new Scanner(out);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if (line.startsWith("Warning")) {
				submission.addCommentForProblem(problem, line);
			}
			if (line.startsWith("All tests passed")) {
				score = problem.getMaxGrade();
			} else if (line.startsWith("Not all tests passed")) {
				score = 0;
			}
		}// while
		scan.close();
		if (score == -1) {
			System.err.println("Error in autograder while grading " + problem.getName());
		}
		return score;
	}
}
