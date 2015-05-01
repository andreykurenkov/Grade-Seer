package edu.gatech.gradeseer.examples.gatech_cs_3600_grading.project2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.gatech.gradeseer.autograding.AssignmentGrader;
import edu.gatech.gradeseer.autograding.AssignmentProblemGrader;
import edu.gatech.gradeseer.examples.gatech_cs_3600_grading.CS3600NonPacmanAssignmentProblemGrader;
import edu.gatech.gradeseer.examples.gatech_cs_3600_grading.TsquareSubmissionIterator;
import edu.gatech.gradeseer.fileio.printerimpl.TextAndCSVPrinter;
import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * 
 * Date modified: Oct 16, 2014
 * 
 * @author Andrey Kurenkov
 */
public class CS3600Project2Grader {

	public static void main(String[] args) {
		File baseFile = new File("/home/andrey/School/2014-2015/Spring/TA/Grading/Project 2");
		File gradeDir = new File(baseFile, "Grading Dir");
		File studentsDir = new File(baseFile, "Submissions");

		String[] codeFiles = { "BinaryCSP.py" };
		String[] questions = { "q1", "q2", "q3", "q4", "q5", "q6", "Extra" };
		double[] maxPoints = { 4, 2, 2, 4, 4, 2, 0 };
		String[] instructorNames = { "Kurenkov", "Wang", "Clark", "Thomaz" };

		ArrayList<AssignmentProblem> problems = new ArrayList<AssignmentProblem>();
		CS3600NonPacmanAssignmentProblemGrader problemGrader = new CS3600NonPacmanAssignmentProblemGrader(gradeDir, 180.0,
				true);
		HashMap<AssignmentProblem, AssignmentProblemGrader> map = new HashMap<AssignmentProblem, AssignmentProblemGrader>();
		// for (int i = 0; i < questions.length - 1; i++) {
		// problems.add(new AssignmentProblem(questions[i], "CSP problem", maxPoints[i]));
		// map.put(problems.get(i), problemGrader);
		// }

		final AssignmentProblem extra = new AssignmentProblem("Extra", "CSP problem", 0);
		problems.add(extra);
		map.put(extra, new AssignmentProblemGrader() {
			@Override
			public double gradeQuestion(Assignment assignment, AssignmentProblem problem, AssignmentSubmission submission) {
				if (Arrays.asList(submission.getSubmissionDirectory().list()).contains("Extra"))
					submission.addCommentForProblem(problem, "Extra dir detected, need to grade manually.");
				System.out.println(Arrays.asList(submission.getSubmissionDirectory().list()));
				return 0;
			}
		});

		Assignment assignment = new Assignment("Project 2", problems, Arrays.asList(codeFiles));
		TsquareSubmissionIterator iterator = new TsquareSubmissionIterator(studentsDir, instructorNames, assignment);
		TextAndCSVPrinter out = new TextAndCSVPrinter(true, new File(baseFile, "gradesExtra.txt"), new File(baseFile,
				"gradesExtra.csv"));
		try {
			AssignmentGrader assignmentGrader = new AssignmentGrader(assignment, iterator, map, out);
			assignmentGrader.runGrader();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
