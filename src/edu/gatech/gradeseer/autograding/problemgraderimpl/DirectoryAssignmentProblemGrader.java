package edu.gatech.gradeseer.autograding.problemgraderimpl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.gatech.gradeseer.autograding.AssignmentProblemGrader;
import edu.gatech.gradeseer.fileio.FileIOUtil;
import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * Copies relevant files to grade to a certain folder, and grades them there.
 * 
 * Date modified: Sep 15, 2014
 * 
 * @author Andrey Kurenkov
 */
public abstract class DirectoryAssignmentProblemGrader implements AssignmentProblemGrader {
	protected File gradingDirectory;
	protected List<File> directoryFiles;
	protected AssignmentSubmission currentGradingSubmission;
	protected List<File> copiedFiles;

	public DirectoryAssignmentProblemGrader(File dir) {
		this.gradingDirectory = dir;
		if (!dir.exists())
			throw new IllegalArgumentException("Dir " + dir.getName() + " does not exist.");
		directoryFiles = Arrays.asList(dir.listFiles());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.gatech.gradeseer.autograding.AssignmentProblemGrader#gradeQuestion(edu.gatech.gradeseer.gradingmodel.Assignment,
	 * edu.gatech.gradeseer.gradingmodel.AssignmentProblem, edu.gatech.gradeseer.gradingmodel.StudentSubmission)
	 */
	@Override
	public double gradeQuestion(Assignment assignment, AssignmentProblem problem, AssignmentSubmission submission) {
		if (currentGradingSubmission != submission) {
			File[] delete = gradingDirectory.listFiles(new FileFilter() {

				@Override
				public boolean accept(File file) {
					return !directoryFiles.contains(file);
				}

			});
			for (File file : delete)
				file.delete();

			currentGradingSubmission = submission;
			copiedFiles = new ArrayList<File>();
			for (File file : submission.getSubmissionFiles().values()) {
				try {
					copiedFiles.add(FileIOUtil.copyTo(file, new File(gradingDirectory, file.getName())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return gradeCopiedFiles(assignment, problem, submission, copiedFiles);
	}

	public abstract double gradeCopiedFiles(Assignment assignment, AssignmentProblem problem,
			AssignmentSubmission submission, List<File> files);

}
