package edu.gatech.gradeseer.examples.gatech_cs_3600_grading.project3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import edu.gatech.gradeseer.autograding.AssignmentGrader;
import edu.gatech.gradeseer.autograding.AssignmentProblemGrader;
import edu.gatech.gradeseer.examples.gatech_cs_3600_grading.CS3600PacmanAssignmentProblemGrader;
import edu.gatech.gradeseer.examples.gatech_cs_3600_grading.TsquareSubmissionIterator;
import edu.gatech.gradeseer.fileio.printerimpl.TextAndCSVPrinter;
import edu.gatech.gradeseer.gradingmodel.Assignment;
import edu.gatech.gradeseer.gradingmodel.AssignmentProblem;
import edu.gatech.gradeseer.gradingmodel.AssignmentSubmission;

/**
 * 
 * Date modified: Sep 15, 2014
 * 
 * @author Andrey Kurenkov
 */
public class CS3600Project3Grader {

	public static void main(String[] args) {
		File baseFile = new File("/home/andrey/School/2014-2015/Spring/TA/Grading/Project 3");
		File gradeDir = new File(baseFile, "Grading Dir");
		File studentsDir = new File(baseFile, "Submissions");
		String[] codeFiles = { "inference.py", "bustersAgents.py" };
		String[] questions = { "q1", "q2", "q3", "q4", "q5", "q6", "q7" };
		double[] maxPoints = { 3, 4, 3, 3, 4, 4, 4 };
		String[] instructorNames = { "Kurenkov" };

		ArrayList<AssignmentProblem> problems = new ArrayList<AssignmentProblem>();
		CS3600PacmanAssignmentProblemGrader problemGrader = new CS3600PacmanAssignmentProblemGrader(gradeDir, 180.0, true);
		HashMap<AssignmentProblem, AssignmentProblemGrader> map = new HashMap<AssignmentProblem, AssignmentProblemGrader>();
		for (int i = 0; i < questions.length; i++) {
			problems.add(new AssignmentProblem(questions[i], "Pacman problem", maxPoints[i]));
			map.put(problems.get(i), problemGrader);
		}

		final AssignmentProblemGrader q4Grader = map.get(problems.get(3));
		map.put(problems.get(3), new AssignmentProblemGrader() {

			@Override
			public double gradeQuestion(Assignment assignment, AssignmentProblem problem, AssignmentSubmission submission) {
				File inferenceFile = submission.getSubmissionFile("inference.py");
				if (inferenceFile == null)
					return 0;
				try {
					Scanner scan = new Scanner(inferenceFile);
					int line = 0;
					int lineStart = 0;
					while (scan.hasNextLine()) {
						String nextLine = scan.nextLine();
						if (line > 250 && nextLine.contains("def observe(self, observation, gameState):")) {
							lineStart = line;
						}
						if (lineStart != 0 && line - lineStart > 25 && nextLine.contains("legalPositions")) {
							submission.addCommentForProblem(problem, "Used legalPositions! Check");
							break;
						}
						if (nextLine.contains("def elapseTime(self, gameState):") && lineStart != 0)
							break;
						line++;
					}
					scan.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return q4Grader.gradeQuestion(assignment, problem, submission);
			}

		});

		Assignment assignment = new Assignment("Project 3", problems, Arrays.asList(codeFiles));
		TsquareSubmissionIterator iterator = new TsquareSubmissionIterator(studentsDir, instructorNames, assignment);
		TextAndCSVPrinter out = new TextAndCSVPrinter(true, new File(baseFile, "grades.txt"), new File(baseFile,
				"grades.csv"));
		try {
			AssignmentGrader assignmentGrader = new AssignmentGrader(assignment, iterator, map, out);
			assignmentGrader.runGrader();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
