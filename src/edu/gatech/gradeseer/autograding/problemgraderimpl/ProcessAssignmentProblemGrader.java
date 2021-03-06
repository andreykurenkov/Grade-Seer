package edu.gatech.gradeseer.autograding.problemgraderimpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * 
 * Date modified: Sep 15, 2014
 * 
 * @author Andrey Kurenkov
 */
public abstract class ProcessAssignmentProblemGrader extends DirectoryAssignmentProblemGrader {
	protected double timeLimit;
	protected boolean outputToFile;

	/**
	 * 
	 * @param dir
	 */
	public ProcessAssignmentProblemGrader(File dir, double timeLimit, boolean outputToFile) {
		super(dir);
		this.timeLimit = timeLimit;
		this.outputToFile = outputToFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.gatech.gradeseer.autograding.extensions.DirectoryAssignmentProblemGrader#gradeCopiedFiles(edu.gatech.gradeseer
	 * .gradingmodel.Assignment, edu.gatech.gradeseer.gradingmodel.AssignmentProblem,
	 * edu.gatech.gradeseer.gradingmodel.StudentSubmission, java.util.List)
	 */
	@Override
	public double gradeCopiedFiles(Assignment assignment, AssignmentProblem problem, AssignmentSubmission submission,
			List<File> files) {
		if (files.size() < assignment.getFileNames().size()) {
			if (submission.getComments().size() < 1)
				submission.addToComments("Did not submit enough files (only " + submission.getSubmissionFiles().size()
						+ "), skipping.");
			return 0;
		}
		ProcessBuilder builder = this.getProcessBuilder(assignment, problem, submission, files);
		builder.directory(super.gradingDirectory);
		File logOut = new File(submission.getSubmissionDirectory(), "out" + problem.getName() + ".txt");
		if (outputToFile) {
			builder.redirectError(logOut);
			builder.redirectOutput(logOut);
		}
		StringBuilder processOut = new StringBuilder();
		long millStart = System.currentTimeMillis();
		double timeElapsed = 0; // seconds

		Process process = null;
		BufferedReader reader = null;
		int charc = 0;
		boolean done = false;
		try {
			process = builder.start();
			if (!outputToFile)
				reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		} catch (IOException e) {
			System.out.println("oh no");
			e.printStackTrace();
			done = true;
		}
		while (timeElapsed < timeLimit && !done) {
			try {
				while (!outputToFile && (charc = reader.read()) != -1) {
					processOut.append(Character.toChars(charc));
					// TODO: check if correct
				}
				process.exitValue();
				done = true;
			} catch (Exception e) {
				done = false;
			}
			timeElapsed = (System.currentTimeMillis() - millStart) / 1000.0;
		}
		process.destroy();

		if (timeElapsed >= timeLimit) {
			submission.addCommentForProblem(problem, "Violated time restriction for question " + problem.getName()
					+ " with time " + timeElapsed + "- autograder did not finish");
			return 0;
		}
		try {
			if (outputToFile) {
				reader = new BufferedReader(new FileReader(logOut));
				while ((charc = reader.read()) != -1) {
					// TODO: check if correct
					processOut.append(Character.toChars(charc));
				}

			} else {
				reader.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return gradeProcessOutput(assignment, problem, submission, files, processOut.toString());
	}

	public abstract ProcessBuilder getProcessBuilder(Assignment assignment, AssignmentProblem problem,
			AssignmentSubmission submission, List<File> files);

	public abstract double gradeProcessOutput(Assignment assignment, AssignmentProblem problem,
			AssignmentSubmission submission, List<File> files, String out);

}
